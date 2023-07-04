package celtech.webserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

/**
 *
 * @author Ian
 */
public class LocalWebInterface {

	private static final Logger LOGGER = LogManager.getLogger(LocalWebInterface.class.getName());
	private HttpServer server;

	public LocalWebInterface() {
		try {
			server = HttpServer.create(new InetSocketAddress(81), 0);
			HttpContext context = server.createContext("/", new AutoMakerController());
			context.setAuthenticator(new BasicAuthenticator("get") {
				@Override
				public boolean checkCredentials(String user, String pwd) {
					return user.equals("admin") && pwd.equals("password");
				}
			});
		}
		catch (IOException ex) {
			LOGGER.error("Unable to start local web server");
			ex.printStackTrace();
		}
	}

	public void start() {
		server.start();
	}

	public void stop() {
		server.stop(0);
	}
}
