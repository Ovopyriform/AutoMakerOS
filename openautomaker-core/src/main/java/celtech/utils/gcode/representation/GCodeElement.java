
package celtech.utils.gcode.representation;

import javafx.scene.shape.Shape;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class GCodeElement {

	private Shape gcodeVisualRepresentation = null;
	private MovementType movementType = null;

	/**
	 *
	 * @param gcodeVisualRepresentation
	 * @param movementType
	 */
	public GCodeElement(Shape gcodeVisualRepresentation, MovementType movementType) {
		this.gcodeVisualRepresentation = gcodeVisualRepresentation;
		this.movementType = movementType;
	}

	/**
	 *
	 * @return
	 */
	public Shape getGcodeVisualRepresentation() {
		return gcodeVisualRepresentation;
	}

	/**
	 *
	 * @return
	 */
	public MovementType getMovementType() {
		return movementType;
	}
}
