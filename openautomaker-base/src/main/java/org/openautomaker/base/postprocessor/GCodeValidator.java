package org.openautomaker.base.postprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Ian
 */
public class GCodeValidator
{

	private static final Logger LOGGER = LogManager.getLogger();

    private final String gcodeFileToValidate;
    private final String nozzleControlPatternString = ".*B([\\.\\d]+).*";
    private final String extrusionPatternString = ".*G[01].* E[\\.\\d]+.*";
    private final Pattern nozzleControlPattern;
    private final Pattern extrusionPattern;

    public GCodeValidator(String gcodeFileToValidate)
    {
        this.gcodeFileToValidate = gcodeFileToValidate;

        nozzleControlPattern = Pattern.compile(nozzleControlPatternString);
        extrusionPattern = Pattern.compile(extrusionPatternString);
    }

    public boolean validate()
    {
        boolean fileIsValid = false;

		LOGGER.info("Validating GCode " + gcodeFileToValidate);

        File inputFile = new File(gcodeFileToValidate);

        try
        {
			BufferedReader fileReader = new BufferedReader(new FileReader(inputFile));

            boolean nozzleOpen = false;

            String line;
            int lineNumber = 1;
            boolean safeToStartChecking = false;

            double lastNozzleControlValue = 0;

            while ((line = fileReader.readLine()) != null)
            {
                if (safeToStartChecking)
                {

                    Matcher nozzleControlMatcher = nozzleControlPattern.matcher(line);
                    Matcher extrusionMatcher = extrusionPattern.matcher(line);

                    double nozzleControlValue = 0;
                    boolean nozzleControlFound = false;
                    boolean extrusionFound = false;

                    if (nozzleControlMatcher.find())
                    {
                        nozzleControlFound = true;
                        nozzleControlValue = Float.valueOf(nozzleControlMatcher.group(1));
                    }

                    extrusionFound = extrusionMatcher.find();

                    if (nozzleControlFound)
                    {
                        if (nozzleOpen && nozzleControlValue > lastNozzleControlValue)
                        {
							LOGGER.error("Nozzle opened when it hadn't been closed on line " + lineNumber + " - " + line);
                            fileIsValid = false;
                        }

                        if (nozzleControlValue > 0)
                        {
							LOGGER.trace("Nozzle open on line " + lineNumber + " - " + line);
                            nozzleOpen = true;
                        } else
                        {
							LOGGER.trace("Nozzle closed on line " + lineNumber + " - " + line);
                            nozzleOpen = false;
                        }

                        lastNozzleControlValue = nozzleControlValue;
                    }

                    if (extrusionFound)
                    {
						LOGGER.trace("Extrusion on line " + lineNumber + " - " + line);
                    }

                    if (extrusionFound && !nozzleOpen)
                    {
						LOGGER.error("Extrusion with closed nozzle on line " + lineNumber + " - " + line);
                        fileIsValid = false;
                    }
                } else
                {
                    if (line.contains("; End of Pre print gcode"))
                    {
						LOGGER.info("Commencing validation from line " + lineNumber + " - " + line);
                        safeToStartChecking = true;
                        fileIsValid = true;
                    }
                }

                lineNumber++;
            }

			fileReader.close();

        } catch (IOException ex)
        {
			LOGGER.error("Failure to validate GCode file");
        }

        if (fileIsValid)
        {
			LOGGER.info("GCode file " + gcodeFileToValidate + " is valid");
        } else
        {
			LOGGER.warn("GCode file " + gcodeFileToValidate + " is invalid");
        }

        return fileIsValid;
    }
}
