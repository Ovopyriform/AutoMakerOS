/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import celtech.appManager.Project;
import celtech.modelcontrol.ProjectifiableThing;

/**
 *
 * @author tony
 */
public class DeleteModelsCommand extends Command
{
	private static final Logger LOGGER = LogManager.getLogger(DeleteModelsCommand.class.getName());

	Project project;
	Set<ProjectifiableThing> modelContainers;

	public DeleteModelsCommand(Project project, Set<ProjectifiableThing> modelContainers)
	{
		this.project = project;
		this.modelContainers = modelContainers;
	}

	@Override
	public void do_()
	{
		project.removeModels(modelContainers);
	}

	@Override
	public void undo()
	{
		for (ProjectifiableThing modelContainer : modelContainers)
		{
			project.addModel(modelContainer);
		}

	}

	@Override
	public void redo()
	{
		project.removeModels(modelContainers);
	}

	@Override
	public boolean canMergeWith(Command command)
	{
		return false;
	}

	@Override
	public void merge(Command command)
	{
		throw new UnsupportedOperationException("Should never be called");
	}

}
