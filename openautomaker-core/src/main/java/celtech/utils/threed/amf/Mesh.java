package celtech.utils.threed.amf;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 *
 * @author Ian
 */
public class Mesh {
	@JacksonXmlElementWrapper(localName = "vertices")
	@JacksonXmlProperty(localName = "vertex")
	private List<Vertex> vertices;

	public List<Vertex> getVertices() {
		return vertices;
	}

	public void setVertices(List<Vertex> vertices) {
		this.vertices = vertices;
	}

}
