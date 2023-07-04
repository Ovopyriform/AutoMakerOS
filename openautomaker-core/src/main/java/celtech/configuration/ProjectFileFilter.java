
package celtech.configuration;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author ianhudson
 */
public class ProjectFileFilter implements FileFilter {

	/**
	 *
	 * @param pathname
	 * @return
	 */
	@Override
	public boolean accept(File pathname) {
		if (pathname.getName().endsWith(ApplicationConfiguration.projectFileExtension)) {
			return true;
		}
		else {
			return false;
		}
	}

}
