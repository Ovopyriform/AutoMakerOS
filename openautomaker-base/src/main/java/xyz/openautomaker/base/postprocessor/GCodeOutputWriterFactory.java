package xyz.openautomaker.base.postprocessor;

import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author Ian
 * @param <T>
 */
public interface GCodeOutputWriterFactory<T extends GCodeOutputWriter>
{
	T create(Path filename) throws IOException;
}
