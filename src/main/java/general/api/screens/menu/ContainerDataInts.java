package general.api.screens.menu;

/**
 * Encodes 32-bit values into the 16-bit words carried by vanilla container-data
 * packets. Use adjacent low/high data indices and reconstruct them on the client.
 */
public final class ContainerDataInts {

	private static final int WORD_MASK = 0xFFFF;

	private ContainerDataInts () {
	}

	public static int lowWord (int value) {
		return value & WORD_MASK;
	}

	public static int highWord (int value) {
		return value >>> Short.SIZE & WORD_MASK;
	}

	public static int combineWords (int lowWord, int highWord) {
		return lowWord & WORD_MASK | (highWord & WORD_MASK) << Short.SIZE;
	}
}
