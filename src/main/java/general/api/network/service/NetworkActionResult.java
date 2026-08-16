package general.api.network.service;

public record NetworkActionResult(boolean successful, String message) {

	public static NetworkActionResult success () {
		return new NetworkActionResult(true, "");
	}

	public static NetworkActionResult success (String message) {
		return new NetworkActionResult(true, message);
	}

	public static NetworkActionResult failure (String message) {
		return new NetworkActionResult(false, message);
	}
}
