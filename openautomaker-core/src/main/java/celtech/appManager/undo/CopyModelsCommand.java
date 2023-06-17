/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import celtech.appManager.Project;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.modelcontrol.TranslateableTwoD;

/**
 *
 * @author tony
 */
public class CopyModelsCommand extends Command
{

	private static final Logger LOGGER = LogManager.getLogger(
			CopyModelsCommand.class.getName());

	Project project;
	Set<ProjectifiableThing> modelContainers;
	Set<ProjectifiableThing> newProjectifiableThings;

	public CopyModelsCommand(Project project, Set<ProjectifiableThing> modelContainers)
	{
		this.project = project;
		this.modelContainers = modelContainers;
	}

	@Override
	public void do_()
	{
		newProjectifiableThings = new HashSet<>();
		for (ProjectifiableThing modelContainer : modelContainers)
		{
			ProjectifiableThing newModel = modelContainer.makeCopy();
			if (TranslateableTwoD.class.isInstance(newModel))
			{
				((TranslateableTwoD) newModel).translateBy(20, 20);
			}
			newProjectifiableThings.add(newModel);
		}
		redo();
	}

	@Override
	public void undo()
	{
		project.removeModels(newProjectifiableThings);
	}

	@Override
	public void redo()
	{
		for (ProjectifiableThing modelContainer : newProjectifiableThings)
		{
			project.addModel(modelContainer);
		}
	}

	@Override
	public boolean canMergeWith(Command command)
	{
		return false;
	}

	@Override
	public void merge(Command command)
	{
		throw new UnsupportedOperationException("Should never be called.");
	}

}
