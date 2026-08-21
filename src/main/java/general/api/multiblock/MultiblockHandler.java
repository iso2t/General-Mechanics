package general.api.multiblock;

import general.api.definitions.MultiblockDefinition;
import general.api.multiblock.event.MultiblockEvent;
import general.api.transfer.energy.EnergyResourceProvider;
import general.api.transfer.ResourceIoMode;
import general.api.transfer.fluid.FluidResourceProvider;
import general.api.transfer.item.ItemResourceProvider;
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
		Map<BlockPos, HatchKey> hatches = new LinkedHashMap<>();
		Map<HatchKey, Integer> hatchCounts = new HashMap<>();
		for (Map.Entry<BlockPos, MultiblockElement> block : instance.blocks().entrySet()) {
			BlockPos worldPos = block.getKey();
			MultiblockElement element = block.getValue();
			if (!isLoaded(level, worldPos)) {
				return MultiblockValidationResult.unloaded(worldPos, element);
			}
			if (!element.matches(level, worldPos)) {
				return MultiblockValidationResult.invalid(worldPos, element, level.getBlockState(worldPos));
			}

			BlockEntity blockEntity = level.getBlockEntity(worldPos);
			if (!(blockEntity instanceof MultiblockHatch hatch)) continue;
			if (!element.hatchable()) {
				return MultiblockValidationResult.invalidHatch(worldPos, "A multiblock hatch occupies a position that is not hatchable");
			}
			if (hatch.isBound() && !hatch.isBoundTo(instance.anchor())) {
				BlockPos owner = hatch.getBoundController();
				return MultiblockValidationResult.invalidHatch(worldPos, "This hatch is owned by another multiblock at [" + owner.getX() + ", " + owner.getY() + ", " + owner.getZ() + "]");
			}

			List<MultiblockHatchDefinition> matches = definition.get().hatches().stream().filter(candidate -> candidate.matcher().matches(level, worldPos, level.getBlockState(worldPos), hatch)).toList();
			if (matches.isEmpty()) {
				return MultiblockValidationResult.invalidHatch(worldPos, "This hatch type is not registered for the multiblock");
			}
			if (matches.size() > 1) {
				String routes = matches.stream().map(candidate -> candidate.key().name()).sorted().reduce((first, second) -> first + ", " + second).orElse("");
				return MultiblockValidationResult.invalidHatch(worldPos, "Hatch matches multiple routes: " + routes);
			}

			HatchKey route = matches.getFirst().key();
			hatches.put(worldPos.immutable(), route);
			hatchCounts.merge(route, 1, Integer::sum);
		}

		for (MultiblockHatchDefinition hatch : definition.get().hatches()) {
			int count = hatchCounts.getOrDefault(hatch.key(), 0);
			if (!hatch.count().contains(count)) {
				return MultiblockValidationResult.invalidHatchCount("Hatch route '" + hatch.key() + "' requires " + formatCount(hatch.count()) + "; found " + count);
			}
			if (count > 0) {
				String accessError = validateHatchAccess(level.getBlockEntity(instance.anchor()), hatch);
				if (accessError != null) return MultiblockValidationResult.invalidHatchCount(accessError);
			}
		}

		return MultiblockValidationResult.valid(new MultiblockInstance(instance.definition(), instance.anchor(), instance.facing(), instance.blocks(), hatches));
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
	 * position for determinism. An unbound attachment shared by multiple formed
	 * structures uses this order as a stable initial ownership tie-breaker; an
	 * existing binding is never displaced by that ordering.</p>
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
	 * Returns the last validated formed instance for a controller without loading
	 * its chunk.
	 */
	public static Optional<MultiblockInstance> getFormedInstance (ServerLevel level, BlockPos controllerPosition) {
		RuntimeState runtime = RUNTIMES.get(Objects.requireNonNull(level, "level"));
		if (runtime == null) return Optional.empty();
		return Optional.ofNullable(runtime.instances.get(Objects.requireNonNull(controllerPosition, "controllerPosition")));
	}

	/**
	 * Resolves the active route for a hatch whose bound controller is loaded, formed,
	 * and still owns this hatch position.
	 */
	public static Optional<MultiblockHatchContext> getHatchContext (ServerLevel level, MultiblockHatch hatch) {
		Objects.requireNonNull(level, "level");
		Objects.requireNonNull(hatch, "hatch");
		BlockPos controllerPos = hatch.getBoundController();
		if (controllerPos == null || !isLoaded(level, controllerPos)) return Optional.empty();
		if (!(level.getBlockEntity(controllerPos) instanceof MultiblockController controller) || !controller.isMultiblockOperational()) return Optional.empty();

		MultiblockInstance instance = getFormedInstance(level, controllerPos).orElse(null);
		if (instance == null) return Optional.empty();
		BlockPos hatchPos = attachmentPosition(level, hatch);
		HatchKey key = instance.hatchAt(hatchPos);
		if (key == null) return Optional.empty();

		return instance.definition().get().hatch(key).map(definition -> new MultiblockHatchContext(instance, hatchPos, definition));
	}

	/**
	 * Whether every hatch physically present in the instance belongs to this
	 * controller and every route's minimum count is satisfied. Physical hatch counts
	 * are checked during structure validation; this runtime ownership check prevents
	 * a wall-shared hatch from making both machines appear operational.
	 */
	public static boolean hasRequiredBoundHatches (ServerLevel level, MultiblockController controller) {
		Objects.requireNonNull(level, "level");
		Objects.requireNonNull(controller, "controller");
		if (!controller.isMultiblockFormed()) return false;

		BlockPos controllerPos = controllerPosition(controller);
		MultiblockInstance instance = getFormedInstance(level, controllerPos).orElse(null);
		if (instance == null) return false;

		Map<HatchKey, Integer> owned = new HashMap<>();
		for (Map.Entry<BlockPos, HatchKey> entry : instance.hatches().entrySet()) {
			if (!isLoaded(level, entry.getKey())) return false;
			if (!(level.getBlockEntity(entry.getKey()) instanceof MultiblockHatch hatch) || !hatch.isBoundTo(controllerPos)) return false;
			owned.merge(entry.getValue(), 1, Integer::sum);
		}

		for (MultiblockHatchDefinition definition : instance.definition().get().hatches()) {
			if (owned.getOrDefault(definition.key(), 0) < definition.count().minimum()) return false;
		}
		return true;
	}

	/**
	 * Binds an unbound attachment to its first deterministic formed candidate.
	 * Existing bindings and missing candidate sets are left untouched.
	 *
	 * @return {@code true} when a new binding was created
	 */
	public static boolean tryAutoBind (ServerLevel level, MultiblockAttachment attachment) {
		Objects.requireNonNull(level, "level");
		Objects.requireNonNull(attachment, "attachment");
		if (attachment.isBound()) return false;

		BlockPos attachmentPos = attachmentPosition(level, attachment);
		List<MultiblockInstance> candidates = getAttachmentCandidates(level, attachmentPos, attachment);
		boolean changed = !candidates.isEmpty() && attachment.bindController(candidates.getFirst().anchor());
		RuntimeState runtime = RUNTIMES.get(level);
		if (runtime != null) trackAttachmentBinding(runtime, attachmentPos, attachment.getBoundController());
		return changed;
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
		List<MultiblockInstance> candidates = getAttachmentCandidates(level, attachmentPos, attachment);
		BlockPos boundController = attachment.getBoundController();

		if (boundController == null) {
			boolean changed = !candidates.isEmpty() && attachment.bindController(candidates.getFirst().anchor());
			RuntimeState runtime = RUNTIMES.get(level);
			if (runtime != null) trackAttachmentBinding(runtime, attachmentPos, attachment.getBoundController());
			return changed;
		}
		if (candidates.stream().anyMatch(candidate -> candidate.anchor().equals(boundController))) {
			RuntimeState runtime = RUNTIMES.get(level);
			if (runtime != null) trackAttachmentBinding(runtime, attachmentPos, boundController);
			return false;
		}

		RuntimeState runtime = RUNTIMES.get(level);
		if (runtime == null || !isLoaded(level, boundController) || isControllerValidationPending(runtime, boundController)) {
			if (runtime != null) trackAttachmentBinding(runtime, attachmentPos, boundController);
			return false;
		}

		boolean changed = candidates.size() == 1 ? attachment.bindController(candidates.getFirst().anchor()) : attachment.unbindController();
		trackAttachmentBinding(runtime, attachmentPos, attachment.getBoundController());
		return changed;
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
		RuntimeState runtime = runtime(level);
		BlockPos attachmentPos = attachmentPosition(level, attachment);
		runtime.pendingAttachments.add(attachmentPos);
		runtime.attachmentsAwaitingLoadRefresh.add(attachmentPos);
	}

	/**
	 * Restores controller indexing and retries validations that were waiting on this chunk.
	 */
	public static void onChunkLoaded (ServerLevel level, LevelChunk chunk) {
		RuntimeState runtime = runtime(level);
		Set<BlockPos> waiting = runtime.waitingForChunk.remove(chunk.getPos());
		if (waiting != null) runtime.pendingControllers.addAll(waiting);
		scheduleAttachmentsBoundInChunk(runtime, chunk.getPos());

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
					if (revalidateAttachment(level, attachment)) {
						// A real binding change already invalidated capabilities and notified
						// neighbors through MultiblockAttachment's binding listener.
						runtime.attachmentsAwaitingLoadRefresh.remove(attachmentPos);
					}
				} else {
					forgetAttachment(runtime, attachmentPos);
					runtime.attachmentsAwaitingLoadRefresh.remove(attachmentPos);
				}
			}
			refreshLoadedAttachmentConnections(level, runtime);
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
		forgetAttachment(runtime, changedPos);
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

	private static List<MultiblockInstance> getAttachmentCandidates (ServerLevel level, BlockPos attachmentPos, MultiblockAttachment attachment) {
		List<MultiblockInstance> candidates = getFormedCandidates(level, attachmentPos);
		if (!(attachment instanceof MultiblockHatch)) return candidates;
		return candidates.stream().filter(instance -> instance.hatches().containsKey(attachmentPos)).toList();
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

	private static void trackAttachmentBinding (RuntimeState runtime, BlockPos attachmentPos, @Nullable BlockPos controllerPos) {
		forgetAttachment(runtime, attachmentPos);
		if (controllerPos == null) return;
		BlockPos immutableController = controllerPos.immutable();
		BlockPos immutableAttachment = attachmentPos.immutable();
		runtime.controllerByAttachment.put(immutableAttachment, immutableController);
		runtime.attachmentsByController.computeIfAbsent(immutableController, ignored -> new HashSet<>()).add(immutableAttachment);
	}

	private static void forgetAttachment (RuntimeState runtime, BlockPos attachmentPos) {
		BlockPos controllerPos = runtime.controllerByAttachment.remove(attachmentPos);
		if (controllerPos == null) return;
		Set<BlockPos> attachments = runtime.attachmentsByController.get(controllerPos);
		if (attachments == null) return;
		attachments.remove(attachmentPos);
		if (attachments.isEmpty()) runtime.attachmentsByController.remove(controllerPos);
	}

	private static void scheduleAttachmentsBoundInChunk (RuntimeState runtime, ChunkPos chunkPos) {
		for (Map.Entry<BlockPos, Set<BlockPos>> entry : runtime.attachmentsByController.entrySet()) {
			BlockPos controllerPos = entry.getKey();
			if ((controllerPos.getX() >> 4) == chunkPos.x() && (controllerPos.getZ() >> 4) == chunkPos.z()) {
				runtime.pendingAttachments.addAll(entry.getValue());
			}
		}
	}

	/**
	 * Re-announces capabilities whose persistent binding survived a load unchanged.
	 * Capability consumers may have queried an attachment before its controller was
	 * re-indexed, so the initial null result must be invalidated once the attachment
	 * is operational again.
	 */
	private static void refreshLoadedAttachmentConnections (ServerLevel level, RuntimeState runtime) {
		for (BlockPos attachmentPos : Set.copyOf(runtime.attachmentsAwaitingLoadRefresh)) {
			if (!isLoaded(level, attachmentPos)) continue;
			if (!(level.getBlockEntity(attachmentPos) instanceof MultiblockAttachment attachment)) {
				runtime.attachmentsAwaitingLoadRefresh.remove(attachmentPos);
				continue;
			}
			if (!attachment.isBound()) {
				runtime.attachmentsAwaitingLoadRefresh.remove(attachmentPos);
				continue;
			}
			if (!isAttachmentConnectionReady(level, attachmentPos, attachment)) continue;

			runtime.attachmentsAwaitingLoadRefresh.remove(attachmentPos);
			level.invalidateCapabilities(attachmentPos);
			level.updateNeighborsAt(attachmentPos, level.getBlockState(attachmentPos).getBlock());
		}
	}

	private static boolean isAttachmentConnectionReady (ServerLevel level, BlockPos attachmentPos, MultiblockAttachment attachment) {
		BlockPos controllerPos = attachment.getBoundController();
		if (controllerPos == null) return false;
		boolean indexed = getAttachmentCandidates(level, attachmentPos, attachment).stream().anyMatch(instance -> instance.anchor().equals(controllerPos));
		if (!indexed) return false;
		return !(attachment instanceof MultiblockHatch hatch) || getHatchContext(level, hatch).isPresent();
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

	private static String formatCount (HatchCount count) {
		if (count.minimum() == count.maximum()) return "exactly " + count.minimum();
		if (count.maximum() == Integer.MAX_VALUE) return "at least " + count.minimum();
		if (count.minimum() == 0) return "at most " + count.maximum();
		return "between " + count.minimum() + " and " + count.maximum();
	}

	private static @Nullable String validateHatchAccess (@Nullable BlockEntity controller, MultiblockHatchDefinition hatch) {
		HatchAccess access = hatch.access();
		if (access.hasItemAccess()) {
			if (!(controller instanceof ItemResourceProvider provider)) return "Hatch route '" + hatch.key() + "' requires controller item storage";
			for (String slot : access.itemInsertion()) {
				if (!provider.getItemDefinition().genericDefinition().has(slot)) return "Hatch route '" + hatch.key() + "' references unknown item slot '" + slot + "'";
			}
			for (String slot : access.itemExtraction()) {
				if (!provider.getItemDefinition().genericDefinition().has(slot)) return "Hatch route '" + hatch.key() + "' references unknown item slot '" + slot + "'";
			}
		}
		if (access.hasFluidAccess()) {
			if (!(controller instanceof FluidResourceProvider provider)) return "Hatch route '" + hatch.key() + "' requires controller fluid storage";
			for (String tank : access.fluidInsertion()) {
				if (!provider.getFluidDefinition().genericDefinition().has(tank)) return "Hatch route '" + hatch.key() + "' references unknown fluid tank '" + tank + "'";
			}
			for (String tank : access.fluidExtraction()) {
				if (!provider.getFluidDefinition().genericDefinition().has(tank)) return "Hatch route '" + hatch.key() + "' references unknown fluid tank '" + tank + "'";
			}
		}
		if (access.energyMode() != ResourceIoMode.NONE && !(controller instanceof EnergyResourceProvider)) {
			return "Hatch route '" + hatch.key() + "' requires controller energy storage";
		}
		return null;
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
		private final Map<BlockPos, Set<BlockPos>>      attachmentsByController = new HashMap<>();
		private final Map<BlockPos, BlockPos>           controllerByAttachment = new HashMap<>();
		private final Set<BlockPos>                     changedPositions      = new HashSet<>();
		private final Set<BlockPos>                     pendingControllers    = new HashSet<>();
		private final Set<BlockPos>                     pendingAttachments    = new HashSet<>();
		private final Set<BlockPos>                     attachmentsAwaitingLoadRefresh = new HashSet<>();
		private final Map<ChunkPos, Set<BlockPos>>      waitingForChunk       = new HashMap<>();
		private       SearchRange                       searchRange           = SearchRange.ZERO;
	}
}
