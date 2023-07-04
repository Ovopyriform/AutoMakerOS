

package celtech.utils.threed.importers.stl;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public enum STLLoadState {

	/**
	 *
	 */
	IDLE,

	/**
	 *
	 */
	ASCII_FILE_STARTED,

	/**
	 *
	 */
	FACET_STARTED,

	/**
	 *
	 */
	LOOP_STARTED,

	/**
	 *
	 */
	LOOP_ENDED,

	/**
	 *
	 */
	FACET_ENDED,

	/**
	 *
	 */
	FILE_ENDED_OK
}
