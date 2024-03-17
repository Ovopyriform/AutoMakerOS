package org.openautomaker.base.configuration;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author ianhudson
 */
public class HeadFileFilter implements FileFilter
{

    /**
     *
     * @param pathname
     * @return
     */
    @Override
	public boolean accept(File pathname) {
		return pathname.getName().endsWith(BaseConfiguration.headFileExtension);

    }

}
