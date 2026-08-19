package general.api.transfer;

/**
 * Runtime automation mode for composing dynamic sided access policies.
 */
public enum ResourceIoMode {
	NONE(false, false),
	INSERT(true, false),
	EXTRACT(false, true),
	BOTH(true, true);

	private final boolean insertion;
	private final boolean extraction;

	ResourceIoMode (boolean insertion, boolean extraction) {
		this.insertion = insertion;
		this.extraction = extraction;
	}

	public boolean allowsInsertion () {
		return insertion;
	}

	public boolean allowsExtraction () {
		return extraction;
	}
}
