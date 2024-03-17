package org.openautomaker.base.utils.tasks;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 *
 * @author ianhudson
 */
public class HeadlessTaskExecutor implements TaskExecutor
{

	private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void runTaskAsDaemon(Task task)
    {
        runOnGUIThread(new Runnable()
        {

            @Override
            public void run()
            {
                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();
            }
        });
    }

    @Override
    public void runOnGUIThread(Runnable runnable)
    {
        if (Platform.isFxApplicationThread())
        {
            runnable.run();
        } else
        {
            Platform.runLater(runnable);
        }
    }

    @Override
    public void runOnBackgroundThread(Runnable runnable)
    {
        Thread th = new Thread(runnable);
        th.setDaemon(true);
        th.start();
    }
    
    @Override
    public void runDelayedOnBackgroundThread(Runnable runnable, long delay)
    {
        new Timer().schedule(new TimerTask()
                             {
                                @Override
                                public void run()
                                {
                                    runnable.run();
                                }
                             },
                             delay);
    }

    @Override
    public void respondOnGUIThread(TaskResponder responder, boolean success, String message)
    {
        respondOnGUIThread(responder, success, message, null);
    }

    @Override
    public void respondOnGUIThread(TaskResponder responder, boolean success, String message,
            Object returnedObject)
    {
        if (responder != null)
        {
            TaskResponse taskResponse = new TaskResponse(message);
            taskResponse.setSucceeded(success);

            if (returnedObject != null)
            {
                taskResponse.setReturnedObject(returnedObject);
            }

            responder.taskEnded(taskResponse);
        }
    }

    @Override
    public void respondOnCurrentThread(TaskResponder responder, boolean success, String message)
    {
        if (responder != null)
        {
            TaskResponse taskResponse = new TaskResponse(message);
            taskResponse.setSucceeded(success);

            responder.taskEnded(taskResponse);
        }
    }

    @Override
    public void runAsTask(TaskExecutor.NoArgsVoidFunc action, TaskExecutor.NoArgsVoidFunc successHandler,
            TaskExecutor.NoArgsVoidFunc failureHandler, String taskName)
    {
        Runnable runTask = () ->
        {
            try
            {
                action.run();
                successHandler.run();

            } catch (Exception ex)
            {
				LOGGER.error("Failure running task: ", ex);
                try
                {
                    if (failureHandler != null)
                    {
                        failureHandler.run();
                    } else
                    {
						LOGGER.warn("No failure handler for this case");
                    }
                } catch (Exception ex1)
                {
					LOGGER.error("Error running failure handler!: " + ex);
                }
            }
        };
        Thread taskThread = new Thread(runTask);
        // Setting to Daemon is not strictly necessary if the cancelling logic
        // is implemented correctly, but just in case.
        taskThread.setDaemon(true);
        taskThread.setName(taskName);
        taskThread.start();
    }
}
