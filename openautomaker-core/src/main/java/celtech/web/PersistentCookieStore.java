package celtech.web;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.crypto.CryptoFileStore;

/**
 *
 * @author Ian
 */
public class PersistentCookieStore implements CookieStore, Runnable {

	private static final Logger LOGGER = LogManager.getLogger(PersistentCookieStore.class.getName());
	private final String filename = ".nothing.dat";
	private final CryptoFileStore cryptoFileStore;
	private ObjectMapper jsonMapper = new ObjectMapper();
	private CookieStore store;

	public PersistentCookieStore() {
		// get the default in memory cookie store
		store = new CookieManager().getCookieStore();

		cryptoFileStore = new CryptoFileStore(BaseConfiguration.getApplicationStorageDirectory() + filename, "ab54vi'vSDDAS5r433jjk's#a");
		//        LOGGER.info("Reading cookie store");
		String encryptedCookieData = cryptoFileStore.readFile();

		if (encryptedCookieData != null) {
			try {
				List<CookieContainer> cookieContainers = jsonMapper.readValue(encryptedCookieData, jsonMapper.getTypeFactory().constructCollectionType(List.class, CookieContainer.class));

				cookieContainers.stream().forEach(cookieContainer -> {
					try {
						URI uri = new URI(cookieContainer.getUri());
						cookieContainer.revealTheCookies().stream().forEach(cookie -> store.add(uri, cookie));
					}
					catch (URISyntaxException ex) {
						LOGGER.error("Error reading cache");
					}
				});
			}
			catch (IOException ex) {
				LOGGER.error("Error reading cached data");
			}
		}

		// add a shutdown hook to write out the in memory cookies
		Runtime.getRuntime().addShutdownHook(new Thread(this, "cookie saver"));
	}

	@Override
	public void run() {
		List<CookieContainer> cookieContainers = new ArrayList<>();

		store.getURIs()
				.stream()
				.forEach((uri) -> {
					cookieContainers.add(new CookieContainer(uri.toString(), store.getCookies()));
				});

		try {
			String dataToEncrypt = jsonMapper.writeValueAsString(cookieContainers);
			//            LOGGER.info("Writing cookie store");
			cryptoFileStore.writeFile(dataToEncrypt);
		}
		catch (IOException ex) {
			LOGGER.error("Error caching data");
		}
	}

	@Override
	public void add(URI uri, HttpCookie cookie) {
		store.add(uri, cookie);
	}

	@Override
	public List<HttpCookie> get(URI uri) {
		return store.get(uri);
	}

	@Override
	public List<HttpCookie> getCookies() {
		return store.getCookies();
	}

	@Override
	public List<URI> getURIs() {
		return store.getURIs();
	}

	@Override
	public boolean remove(URI uri, HttpCookie cookie) {
		return store.remove(uri, cookie);
	}

	@Override
	public boolean removeAll() {
		return store.removeAll();
	}
}
