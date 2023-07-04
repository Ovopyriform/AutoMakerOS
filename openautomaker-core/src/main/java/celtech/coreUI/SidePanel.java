
package celtech.coreUI;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class SidePanel {

	private HBox sidePanelNode;
	private Object sidePanelController;

	SidePanel(HBox sidePanelNode, Object sidePanelController) {
		this.sidePanelNode = sidePanelNode;
		this.sidePanelController = sidePanelController;
	}

	/**
	 *
	 * @return
	 */
	public HBox getSidePanelNode() {
		return sidePanelNode;
	}

	/**
	 *
	 * @param sidePanelNode
	 */
	public void setSidePanelNode(HBox sidePanelNode) {
		this.sidePanelNode = sidePanelNode;
	}

	/**
	 *
	 * @return
	 */
	public Object getSidePanelController() {
		return sidePanelController;
	}

	/**
	 *
	 * @param sidePanelController
	 */
	public void setSidePanelController(Object sidePanelController) {
		this.sidePanelController = sidePanelController;
	}

	/**
	 *
	 * @return
	 */
	public VBox getSlideOutContainer() {
		VBox returnValue = null;

		for (Node node : sidePanelNode.getChildren()) {
			if (node.getId().equalsIgnoreCase("SlideOut") && node instanceof HBox) {
				for (Node subNode : ((HBox) node).getChildren()) {
					if (subNode.getId().equalsIgnoreCase("Container") && subNode instanceof VBox) {
						returnValue = (VBox) subNode;
						break;
					}
				}
			}
		}

		return returnValue;
	}
}
