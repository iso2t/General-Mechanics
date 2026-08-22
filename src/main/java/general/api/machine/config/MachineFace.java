package general.api.machine.config;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * A face expressed relative to the direction a horizontally-oriented machine
 * considers its front.
 *
 * <p>Left and right are evaluated while looking in the same direction as the
 * machine. For example, a machine facing north has west on its left and east on
 * its right.</p>
 */
public enum MachineFace implements StringRepresentable {

	FRONT(0),
	BACK(1),
	LEFT(2),
	RIGHT(3),
	TOP(4),
	BOTTOM(5);

	private static final MachineFace[] VALUES = values();

	private final int id;

	MachineFace (int id) {
		this.id = id;
	}

	/**
	 * Stable protocol identifier. Unlike {@link #ordinal()}, this value may remain
	 * unchanged if the enum declaration is reorganized later.
	 */
	public int id () {
		return id;
	}

	/**
	 * Resolves this relative face to a direction in the world.
	 *
	 * @param front the horizontal world direction the machine faces
	 */
	public Direction toWorldDirection (Direction front) {
		requireHorizontalFront(front);
		return switch (this) {
			case FRONT -> front;
			case BACK -> front.getOpposite();
			case LEFT -> front.getCounterClockWise();
			case RIGHT -> front.getClockWise();
			case TOP -> Direction.UP;
			case BOTTOM -> Direction.DOWN;
		};
	}

	/**
	 * Resolves a world direction to the corresponding machine-relative face.
	 *
	 * @param front     the horizontal world direction the machine faces
	 * @param worldSide the side being resolved
	 */
	public static MachineFace fromWorldDirection (Direction front, Direction worldSide) {
		requireHorizontalFront(front);
		Objects.requireNonNull(worldSide, "worldSide");
		if (worldSide == Direction.UP) return TOP;
		if (worldSide == Direction.DOWN) return BOTTOM;
		if (worldSide == front) return FRONT;
		if (worldSide == front.getOpposite()) return BACK;
		if (worldSide == front.getCounterClockWise()) return LEFT;
		return RIGHT;
	}

	public static Optional<MachineFace> byId (int id) {
		for (MachineFace face : VALUES) {
			if (face.id == id) return Optional.of(face);
		}
		return Optional.empty();
	}

	public static Optional<MachineFace> byName (String name) {
		if (name == null) return Optional.empty();
		String normalized = name.toLowerCase(Locale.ROOT);
		for (MachineFace face : VALUES) {
			if (face.getSerializedName().equals(normalized)) return Optional.of(face);
		}
		return Optional.empty();
	}

	@Override
	public @NonNull String getSerializedName () {
		return name().toLowerCase(Locale.ROOT);
	}

	private static void requireHorizontalFront (Direction front) {
		Objects.requireNonNull(front, "front");
		if (front.getAxis().isVertical()) {
			throw new IllegalArgumentException("Machine front must be horizontal: " + front);
		}
	}
}
