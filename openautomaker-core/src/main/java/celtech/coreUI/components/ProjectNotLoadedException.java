
package celtech.coreUI.components;

/**
 *
 * @author Ian
 */
public class ProjectNotLoadedException extends Exception {
	private String projectName = null;

	/**
	 *
	 * @param projectName
	 */
	public ProjectNotLoadedException(String projectName) {
		this.projectName = projectName;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getMessage() {
		return "Project " + projectName + " could not be loaded.";
	}
}
