package xyz.openautomaker.gcodeviewer.engine.renderers;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import xyz.openautomaker.gcodeviewer.engine.RawModel;
import xyz.openautomaker.gcodeviewer.entities.Entity;
import xyz.openautomaker.gcodeviewer.shaders.StaticShader;
import xyz.openautomaker.gcodeviewer.utils.MatrixUtils;
import xyz.openautomaker.gcodeviewer.utils.VectorUtils;

public class StaticRenderer {

	private final StaticShader shader;

	public StaticRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		loadProjectionMatrix(projectionMatrix);
	}

	public final void loadProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(Map<RawModel, List<Entity>> entities,
			boolean showMoves,
			int topLayerToShow, int bottomLayerToShow,
			int firstLineToShow, int lastLineToShow) {
		entities.keySet().stream().forEach(model -> renderModelEntities(model,
				entities.get(model),
				showMoves,
				topLayerToShow,
				bottomLayerToShow,
				firstLineToShow,
				lastLineToShow));
	}

	public void renderModelEntities(RawModel model, List<Entity> modelEntities,
			boolean showMoves,
			int topLayerToShow, int bottomLayerToShow,
			int firstLineToShow, int lastLineToShow) {
		prepareRawModel(model);
		modelEntities.forEach(entity -> {
			if ((showMoves || !entity.isMove()) &&
					entity.getLayer() >= bottomLayerToShow &&
					entity.getLayer() <= topLayerToShow &&
					entity.getLineNumber() >= firstLineToShow &&
					entity.getLineNumber() <= lastLineToShow)
			{
				prepareInstance(entity);
				glDrawElements(GL_TRIANGLES, model.getVertexCount(), GL_UNSIGNED_INT, 0);
			}
		});
		unbindRawModel();
	}

	public void prepareRawModel(RawModel model) {
		glBindVertexArray(model.getVaoId());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
	}

	public void unbindRawModel() {
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}

	public void prepareInstance(Entity entity) {
		Vector3f halfDelta = new Vector3f(entity.getDirection());
		halfDelta.mul(0.5f * entity.getLength());
		Vector3f toPosition = new Vector3f(entity.getPosition()).add(halfDelta);
		Vector3f fromPosition = new Vector3f(entity.getPosition()).sub(halfDelta);
		float angleAroundX = 0.0F;
		float angleAroundY = VectorUtils.calculateRotationAroundYOfVectors(fromPosition, toPosition);
		float angleAroundZ = VectorUtils.calculateRotationAroundZOfVectors(fromPosition, toPosition);

		Matrix4f transformationMatrix = MatrixUtils.createTransformationMatrix(
				entity.getPosition(),
				angleAroundX,
				angleAroundY,
				angleAroundZ,
				entity.getLength(),
				entity.getWidth(),
				entity.getWidth());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadColour(entity.getColour());
	}
}
