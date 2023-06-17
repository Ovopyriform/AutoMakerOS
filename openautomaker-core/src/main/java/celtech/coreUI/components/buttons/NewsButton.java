package celtech.coreUI.components.buttons;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.math3.util.FastMath;

import celtech.appManager.NewsBot;
import celtech.appManager.NewsListener;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import xyz.openautomaker.environment.OpenAutoMakerEnv;
import xyz.openautomaker.environment.MachineType;

/**
 * There should only be one instance of this news button...
 *
 * @author Ian
 */
//TODO: No news any more
public class NewsButton extends GraphicButton implements NewsListener {

	private static final String fxmlFileName = "newsButton";
	private NewsBot newsBot = null;
	private final Tooltip tooltip = new Tooltip();
	private static final String allAutoMakerNewsFlashesURL = "https://www.cel-uk.com/category/automakernewsflash";

	@FXML
	private Text newsCounter;
	@FXML
	private StackPane newsCounterContainer;

	public NewsButton() {
		super(fxmlFileName);

		newsCounterContainer.setVisible(false);

		final MachineType machineType = OpenAutoMakerEnv.get().getMachineType();

		setOnAction((event) -> {

			if (!Desktop.isDesktopSupported())
				return;

			if (machineType == MachineType.LINUX) {
				try {
					if (Runtime.getRuntime().exec(new String[] {
							"which", "xdg-open"
					}).getInputStream().read() != -1) {
						Runtime.getRuntime().exec(new String[] {
								"xdg-open", allAutoMakerNewsFlashesURL
						});
					}
				}
				catch (IOException ex) {
					System.err.println("Failed to run linux-specific browser command");
				}
				return;
			}

			try {
				newsBot.allNewsHasBeenRead();
				URI linkToVisit = new URI(allAutoMakerNewsFlashesURL);
				Desktop.getDesktop().browse(linkToVisit);
			}
			catch (IOException | URISyntaxException ex) {
				System.err.println("Error when attempting to browse to "
						+ allAutoMakerNewsFlashesURL);
			}

			//            ApplicationStatus.getInstance().setMode(ApplicationMode.NEWS);
		});

		newsBot = NewsBot.getInstance();
		newsBot.registerListener(this);
	}

	@Override
	public void hereIsTheNews(List<NewsBot.NewsArticle> newsArticles) {
		if (newsArticles.isEmpty()) {
			newsCounterContainer.setVisible(false);
			Tooltip.uninstall(this, tooltip);
		}
		else {
			newsCounterContainer.setVisible(true);
			int numberOfItemsToUse = FastMath.min(newsArticles.size(), 99);
			newsCounter.setText(String.valueOf(numberOfItemsToUse));

			StringBuilder tooltipBuilder = new StringBuilder();
			tooltipBuilder.append("--- AutoMaker News ---");
			newsArticles.forEach((article) -> {
				if (tooltipBuilder.length() > 0) {
					tooltipBuilder.append("\n");
				}
				tooltipBuilder.append(article.getTitle());
			});

			tooltip.setText(tooltipBuilder.toString());
			Tooltip.install(this, tooltip);
		}
	}
}
