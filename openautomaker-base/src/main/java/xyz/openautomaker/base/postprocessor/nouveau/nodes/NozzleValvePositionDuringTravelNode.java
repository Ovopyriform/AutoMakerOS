package xyz.openautomaker.base.postprocessor.nouveau.nodes;

import xyz.openautomaker.base.postprocessor.nouveau.nodes.providers.Feedrate;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.providers.FeedrateProvider;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.providers.Movement;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.providers.MovementProvider;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.providers.NozzlePosition;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.providers.NozzlePositionProvider;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class NozzleValvePositionDuringTravelNode extends GCodeEventNode implements NozzlePositionProvider, MovementProvider, FeedrateProvider, Renderable
{

    private boolean fastAsPossible = false;
    private final NozzlePosition nozzlePosition = new NozzlePosition();
    private final Movement movement = new Movement();
    private final Feedrate feedrate = new Feedrate();

    public void setMoveAsFastAsPossible(boolean fastAsPossible)
    {
        this.fastAsPossible = fastAsPossible;
    }

    @Override
    public String renderForOutput()
    {
        StringBuilder stringToOutput = new StringBuilder();
        stringToOutput.append('G');
        if (fastAsPossible)
        {
            stringToOutput.append('0');
        } else
        {
            stringToOutput.append('1');
        }
        stringToOutput.append(' ');

        String feedrateString = feedrate.renderForOutput();
        stringToOutput.append(feedrateString);
        if (feedrateString.length() > 0)
        {
            stringToOutput.append(' ');
        }

        String movementString = movement.renderForOutput();
        stringToOutput.append(movementString);
        if (movementString.length() > 0)
        {
            stringToOutput.append(' ');
        }

        String nozzlePositionString = nozzlePosition.renderForOutput();
        stringToOutput.append(nozzlePositionString);
        if (nozzlePositionString.length() > 0)
        {
            stringToOutput.append(' ');
        }

        stringToOutput.append(getCommentText());

        return stringToOutput.toString().trim();
    }

    @Override
    public NozzlePosition getNozzlePosition()
    {
        return nozzlePosition;
    }

    @Override
    public Movement getMovement()
    {
        return movement;
    }

    @Override
    public Feedrate getFeedrate()
    {
        return feedrate;
    }
}
