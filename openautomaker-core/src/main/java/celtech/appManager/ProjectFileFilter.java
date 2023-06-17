/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package celtech.appManager;

import java.io.File;
import java.io.FilenameFilter;

import celtech.configuration.ApplicationConfiguration;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ProjectFileFilter implements FilenameFilter
{

	/**
	 *
	 * @param dir
	 * @param name
	 * @return
	 */
	@Override
	public boolean accept(File dir, String name)
	{
		boolean accepted = false;
		if (name.endsWith(ApplicationConfiguration.projectFileExtension))
		{
			accepted = true;
		}

		return accepted;
	}

}
