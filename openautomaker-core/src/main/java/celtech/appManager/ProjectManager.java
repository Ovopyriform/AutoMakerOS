package celtech.appManager;

import static xyz.openautomaker.environment.OpenAutoMakerEnv.PROJECTS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import celtech.Lookup;
import celtech.configuration.ApplicationConfiguration;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ProjectManager implements Savable, Serializable {

	private static final long serialVersionUID = 4714858633610290041L;
	private static ProjectManager instance = null;
	private static List<Project> openProjects = new ArrayList<>();
	private final static String openProjectFileName = "projects.dat";
	private static final Logger LOGGER = LogManager.getLogger(ProjectManager.class.getName());
	private final static ProjectFileFilter fileFilter = new ProjectFileFilter();

	private ProjectManager() {
	}

	public static ProjectManager getInstance() {
		if (instance == null) {
			ProjectManager pm = loadState();
			if (pm != null) {
				instance = pm;
			}
			else {
				instance = new ProjectManager();
			}
		}

		return instance;
	}

	private static ProjectManager loadState() {
		ProjectManager pm = null;

		Path projectPath = OpenAutoMakerEnv.get().getUserPath(PROJECTS);
		Path openProjectsDataPath = projectPath.resolve(openProjectFileName);

		if (!Files.exists(openProjectsDataPath))
			return pm;

		try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(openProjectsDataPath.toFile()))) {

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Load open projects from " + openProjectsDataPath.toString());

			pm = new ProjectManager();
			int numberOfOpenProjects = reader.readInt();
			for (int counter = 0; counter < numberOfOpenProjects; counter++) {
				Path projectPathData = Paths.get(reader.readUTF());
				//TODO: Don't want this call to use a string
				Project project = loadProject(projectPathData.toString());

				if (project == null) {
					LOGGER.warn("Project Manager could not open " + projectPathData.toString());
					continue;
				}

				pm.projectOpened(project);
			}
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return pm;
	}

	public static Project loadProject(String projectPath) {
		String basePath = projectPath.substring(0, projectPath.lastIndexOf('.'));
		return Project.loadProject(basePath);
	}

	@Override
	public boolean saveState() {
		boolean savedSuccessfully = false;

		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(OpenAutoMakerEnv.get().getUserPath(PROJECTS).resolve(openProjectFileName).toFile()))) {
			int numberOfProjectsWithModels = 0;

			for (Project candidateProject : openProjects)
				if (candidateProject.getNumberOfProjectifiableElements() > 0)
					numberOfProjectsWithModels++;

			out.writeInt(numberOfProjectsWithModels);

			for (Project project : openProjects)
				if (project.getNumberOfProjectifiableElements() > 0)
					out.writeUTF(project.getAbsolutePath().toString());

			savedSuccessfully = true;
		}
		catch (FileNotFoundException ex) {
			LOGGER.error("Failed to save project state");
		}
		catch (IOException ex) {
			LOGGER.error("Couldn't write project manager state to file");
		}

		return savedSuccessfully;
	}

	public void projectOpened(Project project) {
		if (!openProjects.contains(project)) {
			openProjects.add(project);
		}
	}

	public void projectClosed(Project project) {
		project.close();
		openProjects.remove(project);
		Lookup.removeProjectReferences(project);
	}

	public List<Project> getOpenProjects() {
		return openProjects;
	}

	private Set<String> getAvailableProjectNames() {
		Set<String> availableProjectNames = new HashSet<>();

		File projectDir = OpenAutoMakerEnv.get().getUserPath(PROJECTS).toFile();
		File[] projectFiles = projectDir.listFiles(fileFilter);
		for (File file : projectFiles) {
			String fileName = file.getName();
			String projectName = fileName.replace(ApplicationConfiguration.projectFileExtension, "");
			availableProjectNames.add(projectName);
		}
		return availableProjectNames;
	}

	public Set<String> getOpenAndAvailableProjectNames() {
		Set<String> openAndAvailableProjectNames = new HashSet<>();
		for (Project project : openProjects) {
			openAndAvailableProjectNames.add(project.getProjectName());
		}
		openAndAvailableProjectNames.addAll(getAvailableProjectNames());
		return openAndAvailableProjectNames;
	}

	public Optional<Project> getProjectIfOpen(String projectName) {
		return openProjects.stream().filter((p) -> {
			return p.getProjectName().equals(projectName);
		}).findAny();
	}
}
