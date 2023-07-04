
package xyz.openautomaker.gcodeviewer.engine;

import xyz.openautomaker.gcodeviewer.gcode.GCodeLine;

/**
 *
 * @author Tony
 */
public interface GCodeConsumer {
	public void reset();
	public void processLine(GCodeLine line);
	public void complete();
}
