package xyz.openautomaker.root;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 *
 * @author ianhudson
 */
public class Utils {
	// save uploaded file to new location
	public void writeToFile(InputStream uploadedInputStream, Path uploadedFileLocation) throws IOException {
		int read;
		final int BUFFER_LENGTH = 1024;
		final byte[] buffer = new byte[BUFFER_LENGTH];
		OutputStream out = new FileOutputStream(uploadedFileLocation.toFile());
		while ((read = uploadedInputStream.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		out.flush();
		out.close();
	}

	public static String secondsToHoursMinutesSecondsString(int secondsInput) {
		int minutes = secondsInput / 60;
		int hours = minutes / 60;
		minutes = minutes - (60 * hours);
		int seconds = secondsInput - (minutes * 60) - (hours * 3600);
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public static String cleanInboundJSONString(String input) {
		String output = input.replaceAll("^\"", "");
		output = output.replaceAll("\"$", "");

		return output;
	}
}
