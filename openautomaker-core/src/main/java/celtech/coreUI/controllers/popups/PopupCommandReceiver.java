package celtech.coreUI.controllers.popups;

/**
 *
 * @author Ian
 */
public interface PopupCommandReceiver {

	/**
	 *
	 * @param source
	 */
	public void triggerSaveAs(Object source);

	/**
	 *
	 * @param profileName
	 */
	public void triggerSave(Object profile);
}
