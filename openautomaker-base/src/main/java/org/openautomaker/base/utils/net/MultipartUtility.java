package org.openautomaker.base.utils.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.openautomaker.base.utils.PercentProgressReceiver;

/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 *
 * @author www.codejava.net
 *
 */
public class MultipartUtility
{

    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
    private final int connectionTimeout = 3000;
    private final int readTimeout = 40000;
    /**
     * This constructor initializes a new HTTP POST request with content type is
     * set to multipart/form-data
     *
     * @param requestURL
     * @param charset
     * @param authData
     * @throws IOException
     */
    public MultipartUtility(String requestURL,
            String charset,
            String authData)
            throws IOException
    {
        this.charset = charset;

        // creates a unique boundary based on time stamp
        boundary = "-------------------------" + System.currentTimeMillis();

        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
//        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        if (authData != null
                && !authData.equals(""))
        {
            httpConn.setRequestProperty("Authorization", "Basic " + authData);
        }
        httpConn.setRequestProperty("Expect", "100-continue");
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        httpConn.setConnectTimeout(connectionTimeout);
        httpConn.setReadTimeout(readTimeout);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
    }

    /**
     * Adds a form field to the request
     *
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value)
    {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @param progressReceiver
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile,
            PercentProgressReceiver progressReceiver)
            throws IOException

    {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        long fileLength = uploadFile.length();
        
        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        int totalBytesRead = 0;
        while ((bytesRead = inputStream.read(buffer)) != -1)
        {
            totalBytesRead += bytesRead;
            double percentComplete = ((double) totalBytesRead / fileLength) * 100.0;
            progressReceiver.updateProgressPercent(percentComplete);
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     *
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value)
    {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned status
     * OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public List<String> finish() throws IOException
    {
        List<String> response = new ArrayList<String>();

        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK)
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else
        {
            throw new IOException("Server returned non-OK status: " + status);
        }

        return response;
    }
}
