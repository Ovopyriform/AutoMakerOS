package xyz.openautomaker.base.importers.twod.svg;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openautomaker.base.configuration.hardwarevariants.PrinterType;
import xyz.openautomaker.base.importers.twod.svg.metadata.dragknife.StylusMetaPart;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.GCodeEventNode;
import xyz.openautomaker.base.printerControl.comms.commands.GCodeMacros;
import xyz.openautomaker.base.printerControl.comms.commands.MacroLoadException;

/**
 *
 * @author ianhudson
 */
public class StylusMetaToGCodeEngine
{

	private static final Logger LOGGER = LogManager.getLogger();

    private final String outputFilename;
    private final List<StylusMetaPart> metaparts;

    public StylusMetaToGCodeEngine(String outputURIString, List<StylusMetaPart> metaparts)
    {
        this.outputFilename = outputURIString;
        this.metaparts = metaparts;
    }

    public List<GCodeEventNode> generateGCode()
    {
        List<GCodeEventNode> gcodeNodes = new ArrayList<>();
        
        PrintWriter out = null;
        try
        {
            out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilename)));

            //Add a macro header
            try
            {
                List<String> startMacro = GCodeMacros.getMacroContents("stylus_cut_start",
                        Optional.<PrinterType>empty(), null, false, false, false);
                for (String macroLine : startMacro)
                {
                    out.println(macroLine);
                }
            } catch (MacroLoadException ex)
            {
				LOGGER.error("Unable to load stylus cut start macro.", ex);
            }

            String renderResult = null;

            for (StylusMetaPart part : metaparts)
            {
                renderResult = part.renderToGCode();
                if (renderResult != null)
                {
                    out.println(renderResult);
                    gcodeNodes.addAll(part.renderToGCodeNode());
                    renderResult = null;
                }
            }

            //Add a macro footer
            try
            {
                List<String> startMacro = GCodeMacros.getMacroContents("stylus_cut_finish",
                        Optional.<PrinterType>empty(), null, false, false, false);
                for (String macroLine : startMacro)
                {
                    out.println(macroLine);
                }
            } catch (MacroLoadException ex)
            {
				LOGGER.error("Unable to load stylus cut start macro.", ex);
            }
        } catch (IOException ex)
        {
			LOGGER.error("Unable to output SVG GCode to " + outputFilename);
        } finally
        {
            if (out != null)
            {
                out.flush();
                out.close();
            }
        }
        
        return gcodeNodes;
    }
}
