package general.api.formula.render;

/**
 * Built-in formula render styles. Resolve to a {@link FormulaRenderer} via
 * {@link FormulaRenderers#byMode(RenderMode)}.
 */
public enum RenderMode {

	/**
	 * ASCII count
	 */
	PLAIN,

	/**
	 * Unicode subscript counts
	 */
	UNICODE,

	/**
	 * Verbose, structural representation for debugging.
	 */
	DEBUG
}
