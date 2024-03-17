package org.openautomaker.base.services.printing;

import static org.openautomaker.environment.OpenAutomakerEnv.KEY;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.environment.OpenAutomakerEnv;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

/**
 *
 * @author Tony Aldhous
 */
public class SFTPUtils {

	private static final String PRIVATE_KEY = "automaker-root.ssh";
	private static final int[] pp1 = { 81, 86, 10, 93, 51, 78, 87, 120, 117 };
	private static final int[] pp2 = { -14, 155, 66, 138, 31, 189, 11, 231, 3 };
	private static final String USER = "pi";

	private static final Logger LOGGER = LogManager.getLogger();

	private String hostAddress = null;

	public SFTPUtils(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	/**
	 * Takes the remote directory path and checks/creates elements
	 * 
	 * @param channelSftp
	 * @param remoteDirectory
	 * @throws SftpException
	 */
	private void createRemoteDirectory(ChannelSftp channelSftp, Path remoteDirectory) throws SftpException {
		Path builtPath = Paths.get("/");
		for (Path pathElement : remoteDirectory) {
			try {
				builtPath = builtPath == null ? pathElement : builtPath.resolve(pathElement);

				channelSftp.stat(builtPath.toString());
			}
			catch (SftpException ex) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Creating Directory: " + builtPath.toString());
				channelSftp.mkdir(builtPath.toString());
			}
		}
	}

	// TODO: Look at putting in this method to simplify things
	//	public boolean transferPrint (Path local, SftpProgressMonitor monitor) {
	//		
	//		
	//		
	//		
	//		return true;
	//	}

	public boolean transferToRemotePrinter(File localFile, Path remoteDirectory, Path remoteFile, SftpProgressMonitor monitor) {
		if (localFile == null || !localFile.isFile() || remoteDirectory == null)
			return false;


		// Set the remote file name the same as the file name if one hasn't been provided
		if (remoteFile == null || remoteFile.toString().isBlank())
			remoteFile = Paths.get(localFile.getName());

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Transferring " + localFile.toString() + " to " + remoteDirectory + ":" + remoteFile.toString());

		// Use sftp to transfer file to remote printer.
		boolean transferredOK = false;
		Path remoteFilePath = null;
		try {
			JSch jsch = new JSch();
			LOGGER.info("Connecting to SFTP service");
			String pp = "";
			for (int i = 0; i < 9; ++i)
				pp += Character.toString((char) ((i % 2) == 0 ? pp1[i] + pp2[i] : pp2[i] - pp1[i]));


			jsch.addIdentity(OpenAutomakerEnv.get().getApplicationPath(KEY).resolve(PRIVATE_KEY).toString(), pp);
			Session session = jsch.getSession(USER, hostAddress, 22);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Connected to host \"" + hostAddress + "\"");

			ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();

			createRemoteDirectory(channelSftp, remoteDirectory);

			remoteFilePath = remoteDirectory.resolve(remoteFile);

			if (LOGGER.isDebugEnabled())
				LOGGER.info("Transferring " + localFile.getCanonicalPath() + " to " + remoteFilePath.toString());

			channelSftp.put(localFile.getCanonicalPath(), remoteFilePath.toString(), monitor);

			channelSftp.disconnect();
			session.disconnect();

			transferredOK = true;
		}
		catch (SftpException | JSchException | IOException ex) {
			LOGGER.error("Failed to transfer '" + localFile.getPath() + "' to remote printer file '" + remoteFilePath.toString() + "'", ex);
		}

		return transferredOK;
	}
}
