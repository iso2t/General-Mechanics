package general.api.multiblock;

import general.api.definitions.MultiblockDefinition;
import general.api.multiblock.event.MultiblockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class MultiblockHandler {

	private static final Map<ServerLevel, RuntimeState> RUNTIMES = new IdentityHashMap<>();

	private MultiblockHandler () {
	}

	public static MultiblockValidationResult validate (LevelReader level, BlockPos anchor, Direction facing, MultiblockDefinition definition) {
		MultiblockInstance instance = createInstance(anchor, facing, definition);
		for (Map.Entry<BlockPos, MultiblockElement> block : instance.blocks().entrySet()) {
			BlockPos worldPos = block.getKey();
			MultiblockElement element = block.getValue();
			if (!isLoaded(level, worldPos)) {
				return MultiblockValidationResult.unloaded(worldPos, element);
			}
			if (!element.matches(level, worldPos)) {
				return MultiblockValidationResult.invalid(worldPos, element, level.getBlockState(worldPos));
			}
		}

		return MultiblockValidationResult.valid(instance);
	}

	public static MultiblockValidationResult find (LevelReader level, BlockPos anchor, MultiblockDefinition definition) {
		MultiblockValidationResult firstInvalid = null;
		MultiblockValidationResult firstUnloaded = null;

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			MultiblockValidationResult result = validate(level, anchor, direction, definition);
			if (result.valid()) {
				return result;
			}
			if (result.unloaded() && firstUnloaded == null) firstUnloaded = result;
			if (!result.unloaded() && firstInvalid == null) firstInvalid = result;
		}

		return firstUnloaded != null ? firstUnloaded : firstInvalid;
	}

	public static boolean isValid (LevelReader level, BlockPos anchor, Direction facing, MultiblockDefinition definition) {
		return validate(level, anchor, facing, definition).valid();
	}

	/**
	 * Returns a snapshot of the formed multiblocks whose validated patterns contain
	 * {@code position}. Each returned instance's {@link MultiblockInstance#anchor()}
	 * is its controller position.
	 *
	 * <p>This is a runtime-index lookup: it does not load chunks, create runtime
	 * state, or synchronously validate controllers. Results are ordered by controller
	 * position for determinism, but that order must not be treated as an ownership
	 * preference.</p>
	 */
	public static List<MultiblockInstance> getFormedCandidates (ServerLevel level, BlockPos position) {
		RuntimeState runtime = RUNTIMES.get(Objects.requireNonNull(level, "level"));
		if (runtime == null) return List.of();

		Set<BlockPos> controllerPositions = runtime.controllersByPosition.get(Objects.requireNonNull(position, "position"));
		if (controllerPositions == null || controllerPositions.isEmpty()) return List.of();

		List<MultiblockInstance> candidates = new ArrayList<>(controllerPositions.size());
		for (BlockPos controllerPosition : controllerPositions) {
			MultiblockInstance instance = runtime.instances.get(controllerPosition);
			if (instance != null) candidates.add(instance);
		}

		candidates.sort(Comparator.comparingInt((MultiblockInstance instance) -> instance.anchor().getX())
				.thenComparingInt(instance -> instance.anchor().getY())
				.thenComparingInt(instance -> instance.anchor().getZ()));
		return List.copyOf(candidates);
	}

	/**
	 * Binds an unbound attachment when exactly one formed multiblock contains it.
	 * Existing bindings and ambiguous or missing candidate sets are left untouched.
	 *
	 * @return {@code true} when a new binding was created
	 */
	public static boolean tryAutoBind (ServerLevel level, MultiblockAttachment attachment) {
		Objects.requireNonNull(level, "level");
		Objects.requireNonNull(attachment, "attachment");
		if (attachment.isBound()) return false;

		List<MultiblockInstance> candidates = getFormedCandidates(level, attachmentPosition(level, attachment));
		return candidates.size() == 1 && attachment.bindController(candidates.getFirst().anchor());
	}

	/**
	 * Revalidates an attachment against the current formed-multiblock index.
	 *
	 * <p>A binding is retained while its controller or a required structure chunk is
	 * unavailable. Once invalidation is known, the attachment is rebound when exactly
	 * one candidate remains and is otherwise left unbound.</p>
	 *
	 * @return {@code true} when the binding changed
	 */
	public static boolean revalidateAttachment (ServerLevel level, MultiblockAttachment attachment) {
		Objects.requireNonNull(level, "level");
		Objects.requireNonNull(attachment, "attachment");
		BlockPos attachmentPos = attachmentPosition(level, attachment);
		List<MultiblockInstance> candidates = getFormedCandidates(level, attachmentPos);
		BlockPos boundController = attachment.getBoundController();

		if (boundController == null) {
			return candidates.size() == 1 && attachment.bindController(candidates.getFirst().anchor());
		}
		if (candidates.stream().anyMatch(candidate -> candidate.anchor().equals(boundController))) return false;

		RuntimeState runtime = RUNTIMES.get(level);
		if (runtime == null || !isLoaded(level, boundController) || isControllerValidationPending(runtime, boundController)) return false;

		return candidates.size() == 1 ? attachment.bindController(candidates.getFirst().anchor()) : attachment.unbindController();
	}

	/**
	 * Queues a localized controller search after a world block change.
	 */
	public static void onBlockChanged (Level level, BlockPos pos) {
		if (level instanceof ServerLevel serverLevel) {
			runtime(serverLevel).changedPositions.add(pos.immutable());
		}
	}

	/**
	 * Queues validation when a controller is created or otherwise becomes available.
	 */
	public static void onControllerLoaded (ServerLevel level, MultiblockController controller) {
		RuntimeState runtime = runtime(level);
		rememberControllerBounds(runtime, controller);
		runtime.pendingControllers.add(controllerPosition(controller));
	}

	/**
	 * Queues an attachment for unique-candidate binding after controller validation.
	 */
	public static void onAttachmentLoaded (ServerLevel level, MultiblockAttachment attachment) {
		runtime(level).pendingAttachments.add(attachmentPosition(level, attachment));
	}

	/**
	 * Restores controller indexing and retries validations that were waiting on this chunk.
	 */
	public static void onChunkLoaded (ServerLevel level, LevelChunk chunk) {
		RuntimeState runtime = runtime(level);
		Set<BlockPos> waiting = runtime.waitingForChunk.remove(chunk.getPos());
		if (waiting != null) runtime.pendingControllers.addAll(waiting);

		for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
			if (blockEntity instanceof MultiblockController controller) {
				onControllerLoaded(level, controller);
			}
			if (blockEntity instanceof MultiblockAttachment attachment) {
				onAttachmentLoaded(level, attachment);
			}
		}
	}

	/**
	 * Clears runtime-only indexes when a server level unloads.
	 */
	public static void onLevelUnloaded (ServerLevel level) {
		RUNTIMES.remove(level);
	}

	/**
	 * Processes the queued, server-side work once at the end of a level tick.
	 */
	public static void tick (ServerLevel level) {
		RuntimeState runtime = RUNTIMES.get(level);
		if (runtime == null) return;

		if (!runtime.changedPositions.isEmpty()) {
			Set<BlockPos> changed = Set.copyOf(runtime.changedPositions);
			runtime.changedPositions.clear();
			for (BlockPos pos : changed) {
				scheduleAffectedControllers(level, runtime, pos);
			}
		}

		if (!runtime.pendingControllers.isEmpty()) {
			Set<BlockPos> pending = Set.copyOf(runtime.pendingControllers);
			runtime.pendingControllers.clear();
			for (BlockPos controllerPos : pending) {
				if (!isLoaded(level, controllerPos)) continue;

				BlockEntity blockEntity = level.getBlockEntity(controllerPos);
				if (!(blockEntity instanceof MultiblockController controller)) {
					MultiblockInstance destroyed = unindex(runtime, controllerPos);
					if (destroyed != null) postDestroyed(level, null, destroyed);
					continue;
				}

				revalidate(level, runtime, controller);
			}
		}

		if (!runtime.pendingAttachments.isEmpty()) {
			Set<BlockPos> pending = Set.copyOf(runtime.pendingAttachments);
			runtime.pendingAttachments.clear();
			for (BlockPos attachmentPos : pending) {
				if (!isLoaded(level, attachmentPos)) continue;
				if (level.getBlockEntity(attachmentPos) instanceof MultiblockAttachment attachment) {
					revalidateAttachment(level, attachment);
				}
			}
		}
	}

	public static MultiblockValidationResult revalidate (ServerLevel level, MultiblockController controller) {
		return revalidate(level, runtime(level), controller);
	}

	/**
	 * Emits a one-time formation burst across the outside faces of a formed structure.
	 */
	public static void spawnFormationParticles (ServerLevel level, MultiblockInstance instance) {
		if (instance.blocks().isEmpty()) return;

		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int minZ = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		int maxZ = Integer.MIN_VALUE;

		for (BlockPos pos : instance.blocks().keySet()) {
			minX = Math.min(minX, pos.getX());
			minY = Math.min(minY, pos.getY());
			minZ = Math.min(minZ, pos.getZ());
			maxX = Math.max(maxX, pos.getX());
			maxY = Math.max(maxY, pos.getY());
			maxZ = Math.max(maxZ, pos.getZ());
		}

		for (BlockPos pos : instance.blocks().keySet()) {
			if (pos.getX() == minX) spawnFormationParticle(level, pos, Direction.WEST);
			if (pos.getX() == maxX) spawnFormationParticle(level, pos, Direction.EAST);
			if (pos.getY() == minY) spawnFormationParticle(level, pos, Direction.DOWN);
			if (pos.getY() == maxY) spawnFormationParticle(level, pos, Direction.UP);
			if (pos.getZ() == minZ) spawnFormationParticle(level, pos, Direction.NORTH);
			if (pos.getZ() == maxZ) spawnFormationParticle(level, pos, Direction.SOUTH);
		}
	}

	private static MultiblockValidationResult revalidate (ServerLevel level, RuntimeState runtime, MultiblockController controller) {
		rememberControllerBounds(runtime, controller);
		BlockPos controllerPos = controllerPosition(controller);
		MultiblockInstance tracked = runtime.instances.get(controllerPos);
		clearControllerWaiting(runtime, controllerPos);
		MultiblockValidationResult result = validate(level, controllerPos, controller.getMultiblockFacing(), controller.getMultiblockDefinition());

		if (result.unloaded()) {
			BlockPos failedPosition = result.failedPosition();
			runtime.waitingForChunk.computeIfAbsent(new ChunkPos(failedPosition.getX() >> 4, failedPosition.getZ() >> 4), ignored -> new HashSet<>()).add(controllerPos);
			return result;
		}

		if (result.valid()) {
			if (tracked != null && !controller.isMultiblockFormed()) {
				postDestroyed(level, null, tracked);
			}
			index(runtime, controllerPos, result.instance());
			if (!controller.isMultiblockFormed()) {
				controller.setMultiblockFormed(true);
				markChanged(controller);
				controller.onMultiblockFormed(result.instance());
				NeoForge.EVENT_BUS.post(new MultiblockEvent.Formed(level, controller, result.instance()));
			}
			return result;
		}

		MultiblockInstance previous = unindex(runtime, controllerPos);
		boolean wasFormed = controller.isMultiblockFormed();
		if (controller.isMultiblockFormed()) {
			controller.setMultiblockFormed(false);
			markChanged(controller);
			controller.onMultiblockInvalidated();
		}
		if (previous != null || wasFormed) {
			MultiblockInstance instance = previous != null ? previous : createInstance(controllerPos, controller.getMultiblockFacing(), controller.getMultiblockDefinition());
			scheduleAttachments(runtime, instance);
			postDestroyed(level, wasFormed ? controller : null, instance);
		}
		return result;
	}

	private static void scheduleAffectedControllers (ServerLevel level, RuntimeState runtime, BlockPos changedPos) {
		Set<BlockPos> indexed = runtime.controllersByPosition.get(changedPos);
		if (indexed != null) runtime.pendingControllers.addAll(indexed);
		if (isLoaded(level, changedPos) && level.getBlockEntity(changedPos) instanceof MultiblockAttachment) {
			runtime.pendingAttachments.add(changedPos.immutable());
		}

		SearchRange range = runtime.searchRange;
		for (int y = changedPos.getY() - range.vertical; y <= changedPos.getY() + range.vertical; y++) {
			for (int z = changedPos.getZ() - range.horizontal; z <= changedPos.getZ() + range.horizontal; z++) {
				for (int x = changedPos.getX() - range.horizontal; x <= changedPos.getX() + range.horizontal; x++) {
					BlockPos candidate = new BlockPos(x, y, z);
					if (!isLoaded(level, candidate)) continue;
					if (level.getBlockEntity(candidate) instanceof MultiblockController controller) {
						rememberControllerBounds(runtime, controller);
						runtime.pendingControllers.add(controllerPosition(controller));
					}
				}
			}
		}
	}

	private static void rememberControllerBounds (RuntimeState runtime, MultiblockController controller) {
		MultiblockPattern pattern = controller.getMultiblockDefinition().get().pattern();
		BlockPos anchor = pattern.getAnchor();
		int horizontal = Math.max(Math.max(anchor.getX(), pattern.getWidth() - 1 - anchor.getX()), Math.max(anchor.getZ(), pattern.getDepth() - 1 - anchor.getZ()));
		int vertical = Math.max(anchor.getY(), pattern.getHeight() - 1 - anchor.getY());
		runtime.searchRange = runtime.searchRange.include(horizontal, vertical);
	}

	private static BlockPos controllerPosition (MultiblockController controller) {
		BlockPos position = controller.getMultiblockPosition();
		if (controller instanceof BlockEntity blockEntity && !blockEntity.getBlockPos().equals(position)) {
			throw new IllegalStateException("A multiblock controller must use its own block position as the pattern anchor.");
		}
		return position.immutable();
	}

	private static BlockPos attachmentPosition (ServerLevel level, MultiblockAttachment attachment) {
		if (!(attachment instanceof BlockEntity blockEntity)) {
			throw new IllegalStateException("A multiblock attachment must be implemented by a block entity.");
		}
		if (blockEntity.getLevel() != level) {
			throw new IllegalStateException("A multiblock attachment must be queried in its current server level.");
		}
		return blockEntity.getBlockPos().immutable();
	}

	private static void index (RuntimeState runtime, BlockPos controllerPos, MultiblockInstance instance) {
		unindex(runtime, controllerPos);
		runtime.instances.put(controllerPos, instance);
		for (BlockPos pos : instance.blocks().keySet()) {
			runtime.controllersByPosition.computeIfAbsent(pos.immutable(), ignored -> new HashSet<>()).add(controllerPos);
		}
		runtime.controllersByPosition.computeIfAbsent(controllerPos, ignored -> new HashSet<>()).add(controllerPos);
		scheduleAttachments(runtime, instance);
	}

	private static MultiblockInstance unindex (RuntimeState runtime, BlockPos controllerPos) {
		clearControllerWaiting(runtime, controllerPos);
		MultiblockInstance instance = runtime.instances.remove(controllerPos);
		if (instance == null) return null;

		for (BlockPos pos : instance.blocks().keySet()) {
			removeControllerAt(runtime, pos, controllerPos);
		}
		removeControllerAt(runtime, controllerPos, controllerPos);
		scheduleAttachments(runtime, instance);
		return instance;
	}

	private static void scheduleAttachments (RuntimeState runtime, MultiblockInstance instance) {
		runtime.pendingAttachments.addAll(instance.blocks().keySet());
		runtime.pendingAttachments.add(instance.anchor());
	}

	private static boolean isControllerValidationPending (RuntimeState runtime, BlockPos controllerPos) {
		if (runtime.pendingControllers.contains(controllerPos)) return true;
		for (Set<BlockPos> controllers : runtime.waitingForChunk.values()) {
			if (controllers.contains(controllerPos)) return true;
		}
		return false;
	}

	private static void clearControllerWaiting (RuntimeState runtime, BlockPos controllerPos) {
		for (Iterator<Map.Entry<ChunkPos, Set<BlockPos>>> iterator = runtime.waitingForChunk.entrySet().iterator(); iterator.hasNext(); ) {
			Set<BlockPos> controllers = iterator.next().getValue();
			controllers.remove(controllerPos);
			if (controllers.isEmpty()) iterator.remove();
		}
	}

	private static void removeControllerAt (RuntimeState runtime, BlockPos pos, BlockPos controllerPos) {
		Set<BlockPos> controllers = runtime.controllersByPosition.get(pos);
		if (controllers == null) return;
		controllers.remove(controllerPos);
		if (controllers.isEmpty()) runtime.controllersByPosition.remove(pos);
	}

	private static void markChanged (MultiblockController controller) {
		if (controller instanceof BlockEntity blockEntity) blockEntity.setChanged();
	}

	private static void postDestroyed (ServerLevel level, @Nullable MultiblockController controller, MultiblockInstance instance) {
		NeoForge.EVENT_BUS.post(new MultiblockEvent.Destroyed(level, controller, instance));
	}

	private static void spawnFormationParticle (ServerLevel level, BlockPos pos, Direction outward) {
		level.sendParticles(PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0F), pos.getX() + 0.5D + outward.getStepX() * 0.52D, pos.getY() + 0.5D + outward.getStepY() * 0.52D, pos.getZ() + 0.5D + outward.getStepZ() * 0.52D, 1, 0.015D, 0.015D, 0.015D, 0.0D);
	}

	private static boolean isLoaded (LevelReader level, BlockPos pos) {
		int chunkX = SectionPos.blockToSectionCoord(pos.getX());
		int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
		return level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) != null;
	}

	private static RuntimeState runtime (ServerLevel level) {
		return RUNTIMES.computeIfAbsent(level, ignored -> new RuntimeState());
	}

	private static MultiblockInstance createInstance (BlockPos anchor, Direction facing, MultiblockDefinition definition) {
		MultiblockPattern pattern = definition.get().pattern();
		BlockPos patternAnchor = pattern.getAnchor();
		Map<BlockPos, MultiblockElement> blocks = new LinkedHashMap<>();

		for (int y = 0; y < pattern.getHeight(); y++) {
			for (int z = 0; z < pattern.getDepth(); z++) {
				for (int x = 0; x < pattern.getWidth(); x++) {
					MultiblockElement element = pattern.getElementAt(x, y, z);
					if (element == null) continue;

					int relativeX = x - patternAnchor.getX();
					int relativeY = y - patternAnchor.getY();
					int relativeZ = z - patternAnchor.getZ();
					blocks.put(transform(anchor, relativeX, relativeY, relativeZ, facing), element);
				}
			}
		}

		return new MultiblockInstance(definition, anchor.immutable(), facing, Collections.unmodifiableMap(blocks));
	}

	private static BlockPos transform (BlockPos anchor, int x, int y, int z, Direction facing) {
		return switch (facing) {

			case NORTH -> anchor.offset(x, y, z);

			case SOUTH -> anchor.offset(-x, y, -z);

			case EAST -> anchor.offset(-z, y, x);

			case WEST -> anchor.offset(z, y, -x);

			default -> throw new IllegalArgumentException("Multiblock facing must be horizontal.");
		};
	}

	private record SearchRange(int horizontal, int vertical) {
		private static final SearchRange ZERO = new SearchRange(0, 0);

		private SearchRange include (int horizontal, int vertical) {
			return new SearchRange(Math.max(this.horizontal, horizontal), Math.max(this.vertical, vertical));
		}
	}

	private static final class RuntimeState {
		private final Map<BlockPos, Set<BlockPos>>      controllersByPosition = new HashMap<>();
		private final Map<BlockPos, MultiblockInstance> instances             = new HashMap<>();
		private final Set<BlockPos>                     changedPositions      = new HashSet<>();
		private final Set<BlockPos>                     pendingControllers    = new HashSet<>();
		private final Set<BlockPos>                     pendingAttachments    = new HashSet<>();
		private final Map<ChunkPos, Set<BlockPos>>      waitingForChunk       = new HashMap<>();
		private       SearchRange                       searchRange           = SearchRange.ZERO;
	}
}
