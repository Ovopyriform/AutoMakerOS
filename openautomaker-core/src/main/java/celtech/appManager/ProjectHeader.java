package celtech.appManager;

import static xyz.openautomaker.environment.OpenAutoMakerEnv.PROJECTS;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import xyz.openautomaker.base.utils.SystemUtils;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 * ProjectHeader is not used except when loading legacy Project files.
 * 
 * @author tony
 */
public class ProjectHeader implements Serializable {

	private static final long serialVersionUID = 1L;
	private final transient SimpleDateFormat formatter = new SimpleDateFormat("-hhmmss-ddMMYY");
	private String projectUUID = null;
	private StringProperty projectNameProperty = null;
	private Path projectPath = null;
	private final ObjectProperty<Date> lastModifiedDate = new SimpleObjectProperty<>();

	public ProjectHeader() {
		projectUUID = SystemUtils.generate16DigitID();
		Date now = new Date();
		projectNameProperty = new SimpleStringProperty(OpenAutoMakerEnv.getI18N().t("projectLoader.untitled")
				+ formatter.format(now));
		projectPath = OpenAutoMakerEnv.get().getUserPath(PROJECTS);
		lastModifiedDate.set(now);
	}

	private void writeObject(ObjectOutputStream out)
			throws IOException {
		out.writeUTF(projectUUID);
		out.writeUTF(projectNameProperty.get());
		out.writeUTF(projectPath.toString());
		out.writeObject(lastModifiedDate.get());
		out.writeObject(new Date());
	}

	private void readObject(ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		projectUUID = in.readUTF();
		projectNameProperty = new SimpleStringProperty(in.readUTF());
		projectPath = Paths.get(in.readUTF());
		Object lastModifiedDate = new SimpleObjectProperty<>((Date) (in.readObject()));
		Object lastSavedDate = new SimpleObjectProperty<>((Date) (in.readObject()));
	}

	private void readObjectNoData()
			throws ObjectStreamException {

	}

}
