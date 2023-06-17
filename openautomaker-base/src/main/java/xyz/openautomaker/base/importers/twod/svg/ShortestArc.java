package xyz.openautomaker.base.importers.twod.svg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Ian
 */
public class ShortestArc
{

	private static final Logger LOGGER = LogManager.getLogger();

    private double stepValue = Math.PI / 18;
    private double angularDifference = 0;
    private double targetAngle = 0;
    private double currentAngle = 0;

    public ShortestArc(double startingCurrentAngle, double startingTargetAngle)
    {
        double differenceBetweenVectors = startingTargetAngle - startingCurrentAngle;

        if (differenceBetweenVectors > Math.PI)
        {
            targetAngle = startingTargetAngle - (Math.PI * 2.0);
            currentAngle = startingCurrentAngle;
        } else if (differenceBetweenVectors < -Math.PI)
        {
            targetAngle = startingTargetAngle + (Math.PI * 2.0);
            currentAngle = startingCurrentAngle;
        }
        else
        {
            currentAngle = startingCurrentAngle;
            targetAngle = startingTargetAngle;
        }

        angularDifference = targetAngle - currentAngle;

        if (angularDifference < 0)
        {
            stepValue = -stepValue;
			LOGGER.info("Direction = backwards");
        } else
        {
			LOGGER.info("Direction = forwards");
        }
    }

    public double getAngularDifference()
    {
        return angularDifference;
    }

    public double getStepValue()
    {
        return stepValue;
    }

    public double getCurrentAngle()
    {
        return currentAngle;
    }

    public double getTargetAngle()
    {
        return targetAngle;
    }
}
