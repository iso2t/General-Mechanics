package general.api.registry;

public final class RegistryString {

	private final String rawString;
	private final String registryName;

	public RegistryString (String input) {
		this.rawString = input;
		this.registryName = input.toLowerCase().replace(" ", "_");
	}

	public String getRawString () {
		return rawString;
	}

	public String getRegistryName () {
		return registryName;
	}

}
