package celtech.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.github.junrar.volume.FileVolumeManager;

import celtech.configuration.ApplicationConfiguration;
import javafx.concurrent.Task;
import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.utils.FileUtilities;
import xyz.openautomaker.base.utils.SystemUtils;

/**
 *
 * @author Ian
 */
public class MyMiniFactoryLoader extends Task<MyMiniFactoryLoadResult>
{

	private static final Logger LOGGER = LogManager.getLogger(MyMiniFactoryLoader.class.getName());

	private String fileURL = null;

	public MyMiniFactoryLoader(String fileURL)
	{
		this.fileURL = fileURL;
	}

	@Override
	protected MyMiniFactoryLoadResult call() throws Exception
	{
		MyMiniFactoryLoadResult result = new MyMiniFactoryLoadResult();

		LOGGER.info("Got download URL of " + fileURL);

		String tempID = SystemUtils.generate16DigitID();
		try
		{
			URL downloadURL = new URL(fileURL);

			String extension = FilenameUtils.getExtension(fileURL);
			final String tempFilename = BaseConfiguration.getApplicationStorageDirectory() + File.separator + tempID + "." + extension;

			URLConnection urlConn = downloadURL.openConnection();

			String file = fileURL.replaceFirst(".*/", "");

			InputStream webInputStream = urlConn.getInputStream();

			if (extension.equalsIgnoreCase("stl"))
			{
				LOGGER.info("Got stl file from My Mini Factory");
				final String targetname = ApplicationConfiguration.getMyMiniFactoryDownloadDirectory() + file;
				FileUtilities.writeStreamToFile(webInputStream, targetname);
				final List<File> filesToLoad = new ArrayList<>();
				filesToLoad.add(new File(targetname));
				result.setFilesToLoad(filesToLoad);
				result.setSuccess(true);
			} else if (extension.equalsIgnoreCase("zip"))
			{
				LOGGER.info("Got zip file from My Mini Factory");
				FileUtilities.writeStreamToFile(webInputStream, tempFilename);
				ZipFile zipFile = new ZipFile(tempFilename);

				final Enumeration<? extends ZipEntry> entries = zipFile.entries();
				final List<File> filesToLoad = new ArrayList<>();
				while (entries.hasMoreElements())
				{
					final ZipEntry entry = entries.nextElement();
					final String tempTargetname = ApplicationConfiguration.getMyMiniFactoryDownloadDirectory() + entry.getName();
					try
					{
						FileUtilities.writeStreamToFile(zipFile.getInputStream(entry), tempTargetname);
						if (entry.getName().toLowerCase().endsWith("stl")
								|| entry.getName().toLowerCase().endsWith("obj"))
						{
							boolean loadModel = true;
							for (File fileToCheck : filesToLoad)
							{
								if (fileToCheck.getName().equals(entry.getName()))
								{
									loadModel = false;
									break;
								}
							}

							if (loadModel)
							{
								filesToLoad.add(new File(tempTargetname));
							}
						}
					} catch (IOException ex)
					{
						LOGGER.error("Error unwrapping zip - " + ex.getMessage());
					}
				}
				result.setFilesToLoad(filesToLoad);
				result.setSuccess(true);

				zipFile.close();
				FileUtils.deleteQuietly(new File(tempFilename));
			} else if (extension.equalsIgnoreCase("rar"))
			{
				LOGGER.info("Got rar file from My Mini Factory");
				FileUtilities.writeStreamToFile(webInputStream, tempFilename);
				File inputfile = new File(tempFilename);
				Archive archive = null;
				try
				{
					archive = new Archive(new FileVolumeManager(inputfile), null, null);
				} catch (RarException e)
				{
					e.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				if (archive != null)
				{
					archive.getMainHeader().print();
					FileHeader fh = archive.nextFileHeader();
					final List<File> filesToLoad = new ArrayList<>();
					boolean ok = true;
					while (fh != null && ok)
					{
						try
						{
							File out = new File(ApplicationConfiguration.getMyMiniFactoryDownloadDirectory()
									+ File.separator
									+ fh.getFileNameString().trim());
							FileOutputStream os = new FileOutputStream(out);
							archive.extractFile(fh, os);
							os.close();
							filesToLoad.add(out);
						} catch (FileNotFoundException e)
						{
							e.printStackTrace();
							ok = false;
						} catch (RarException e)
						{
							e.printStackTrace();
							ok = false;
						} catch (IOException e)
						{
							e.printStackTrace();
							ok = false;
						}
						fh = archive.nextFileHeader();
					}
					if (ok)
					{
						result.setFilesToLoad(filesToLoad);
						result.setSuccess(true);
					} else
					{
						result.setSuccess(false);
					}

					archive.close();
				}
				inputfile.delete();
			}

			webInputStream.close();
		} catch (IOException ex)
		{
			LOGGER.error("Failed to download My Mini Factory file :" + fileURL);
			result.setSuccess(false);
		}

		return result;
	}
}
