package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.util.SimpleRenderer;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.ClearOntologyValuesAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.InsertSheetAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.OpenFromBioPortalAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.OpenOntologyAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.OpenWorkbookAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SaveAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SaveAsAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCopyAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.FetchBioportalOntologyListTask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.LoadOntologyTask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.LoadRepositoryItemTask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.TaskManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 */

public class WorkbookFrame extends JFrame {

	
	private static final long serialVersionUID = 8991252467294969145L;
	
	private static final String[] WORKBOOK_EXT = new String[] { "xls", "xlsx" };
	private static final String[] ONTOLOGY_EXT = new String[] { "obo", "owl",
			"rdf", "rrf" };

	private static final Logger logger = Logger.getLogger(WorkbookFrame.class);

	private WorkbookManager workbookManager;

	private TaskManager taskManager = new TaskManager(this);

	public WorkbookFrame(WorkbookManager manager) {
		this.workbookManager = manager;
		setTitle("RightField");
		if (WorkbookFrame.class.getResource("/rightfield-logo.png") != null) {
			setIconImage(new ImageIcon(WorkbookFrame.class
					.getResource("/rightfield-logo.png")).getImage());
		} else {
			logger.warn("Unable to find the rightfield-logo.png for the icon");
		}
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new MainPanel(this));
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = menuBar.add(new JMenu("File"));
		fileMenu.add(new OpenWorkbookAction(this));
		fileMenu.add(new OpenOntologyAction(this));
		fileMenu.add(new OpenFromBioPortalAction(this));
		fileMenu.addSeparator();
		fileMenu.add(new SaveAction(this));
		fileMenu.add(new SaveAsAction(this));

		JMenu editMenu = menuBar.add(new JMenu("Edit"));
		editMenu.add(new ClearOntologyValuesAction(this));
		editMenu.add(new SelectDownColumn(workbookManager, this));
		editMenu.add(new SheetCopyAction(workbookManager,getToolkit()));
		// JMenu viewMenu = menuBar.add(new JMenu("View"));
		JMenu insertMenu = menuBar.add(new JMenu("Insert"));
		insertMenu.add(new InsertSheetAction("Sheet", workbookManager, this));
		// JMenu formatMenu = menuBar.add(new JMenu("Format"));
		// formatMenu.add(new FormatBoldAction(this));
		// JMenu sheetMenu = new JMenu("Sheet");
		// formatMenu.add(sheetMenu);
		// sheetMenu.add(new RenameSheetAction(this));
		// JMenu helpMenu = menuBar.add(new JMenu("Help"));
		setJMenuBar(menuBar);
		updateTitleBar();
		workbookManager.addListener(new WorkbookManagerListener() {
			public void workbookChanged(WorkbookManagerEvent event) {
				handleWorkbookChanged();
			}

			public void workbookLoaded(WorkbookManagerEvent event) {
				handleWorkbookChanged();
			}

			public void ontologiesChanged(WorkbookManagerEvent event) {
				updateTitleBar();
			}
		});
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public void handleWorkbookChanged() {
		updateTitleBar();
		Collection<IRI> ontologyIRIs = workbookManager
				.getOntologyTermValidationManager().getOntologyIRIs();
		Set<IRI> missingOntologies = new HashSet<IRI>();
		for (IRI ontologyIRI : ontologyIRIs) {
			if (!workbookManager.getOntologyManager().contains(ontologyIRI)) {
				missingOntologies.add(ontologyIRI);
			}
		}
		if (!missingOntologies.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("<html><body>");
			sb
					.append("The workbook contains information about terms from ontologies which<br>"
							+ "are not loaded.  Do you want to load these ontologies now?");
			sb.append("</body></html>");
			int ret = JOptionPane.showConfirmDialog(this, sb.toString(),
					"Load ontologies?", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (ret == JOptionPane.YES_OPTION) {
				taskManager.runTask(new LoadEmbeddedTermsOntologies());
			}
		}
	}

	public void updateTitleBar() {
		StringBuilder sb = new StringBuilder();
		sb.append("RightField");
		URI uri = workbookManager.getWorkbookURI();
		if (uri != null) {
			sb.append(" - ");
			if (uri.getScheme().equalsIgnoreCase("file")) {
				File file = new File(uri);
				sb.append(file.getPath());
			} else {
				sb.append(uri);
			}
		}
		setTitle(sb.toString());

	}

	public WorkbookManager getWorkbookManager() {
		return workbookManager;
	}

	public Sheet addSheet() {
		return workbookManager.getWorkbook().addSheet();
	}

	public void setSelectedSheet(Sheet sheet) {
		workbookManager.getSelectionModel().setSelectedRange(new Range(sheet));
	}

	public void loadOntology() throws OWLOntologyCreationException {

		File file = browseForFile("Load ontology", FileDialog.LOAD, "Ontology",
				ONTOLOGY_EXT);
		if (file == null) {
			return;
		}
		taskManager.runTask(new LoadOntologyTask(file));
	}

	public void loadBioportalOntology() throws Exception {
		Collection<RepositoryItem> ontologies = taskManager.runTask(new FetchBioportalOntologyListTask());
		if (!ontologies.isEmpty()) {
			RepositoryItem item = RepositoryPanel.showDialog(this,RepositoryManager.getInstance().getBioPortalRepositoryAccessor());
			if (item == null) {
				return;
			}
			taskManager.runTask(new LoadRepositoryItemTask(item));
		}
	}

	public void saveWorkbook() throws IOException {
		URI workbookURI = workbookManager.getWorkbookURI();
		if (workbookURI == null) {
			saveWorkbookAs();
		} else {
			workbookManager.saveWorkbook(workbookURI);
		}
	}

	public void openWorkbook() throws IOException {
		File file = browseForFile("Open workbook", FileDialog.LOAD, "Workbook",
				WORKBOOK_EXT);
		if (file != null) {
			workbookManager.loadWorkbook(file);
		}
	}

	public void saveWorkbookAs() throws IOException {
		File file = browseForFile("Save workbook as", FileDialog.SAVE,
				"Workbook", WORKBOOK_EXT);
		if (file != null) {
			workbookManager.saveWorkbook(file.toURI());
		}
	}

	public File browseForFile(String title, int mode, final String filetype,
			final String[] extensions) {
		String os = System.getProperty("os.name");
		logger.debug("OS detected as: " + os);
		//uses FileDialog for Mac and Windows, as this is preferred.
		// but JFileChooser for Linux and the other unix's, as FileDialog is awful on those platforms
		if (os.toLowerCase().indexOf("win") > -1 || os.toLowerCase().indexOf("mac") > -1) {			
			return browseForFileWithFileDialog(title, mode, extensions);
		} else {
			return browseForFileWithJChooser(title, mode, filetype, extensions);
		}
	}

	private File browseForFileWithJChooser(String title, int mode,
			final String filetype, final String[] extensions) {
		JFileChooser chooser = new JFileChooser(title);
		if (extensions != null && extensions.length > 1) {
			chooser.setFileFilter(new ExtensionFileFilter(filetype, extensions));
		}

		int retVal;
		if (mode == FileDialog.LOAD) {
			retVal = chooser.showOpenDialog(this);
		} else {
			retVal = chooser.showSaveDialog(this);
		}
		if (retVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

	private File browseForFileWithFileDialog(String title, int mode,
			final String[] extensions) {
		FileDialog fileDialog = new FileDialog(this, title, mode);

		if (extensions != null && extensions.length > 1) {			
			fileDialog.setFilenameFilter(new ExtensionsFilenameFilter(
					extensions));
		}

		fileDialog.setVisible(true);
		String name = fileDialog.getFile();
		if (name == null) {
			return null;
		}
		String directory = fileDialog.getDirectory();
		return new File(directory + name);
	}

	public static void main(String[] args) {
		WorkbookManager manager = new WorkbookManager();
		WorkbookFrame frame = new WorkbookFrame(manager);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
		SimpleRenderer renderer = new SimpleRenderer();
		renderer.setShortFormProvider(new SimpleShortFormProvider());
		ToStringRenderer.getInstance().setRenderer(renderer);
	}

	class ExtensionFileFilter extends FileFilter {

		private final String description;
		private final List<String> extensions;

		public ExtensionFileFilter(String description, String [] extensions) {
			this.description = description;
			this.extensions = Arrays.asList(extensions);
		}

		@Override
		public boolean accept(File f) {
			String ext = getExtension(f);
			return f.isDirectory() || (ext != null && extensions.contains(ext));
		}

		@Override
		public String getDescription() {
			return this.description;
		}

		private String getExtension(File f) {
			if (f != null) {
				String filename = f.getName();
				int i = filename.lastIndexOf('.');
				if (i > 0 && i < filename.length() - 1) {
					return filename.substring(i + 1).toLowerCase();
				}
			}
			return null;
		}
	}

	class ExtensionsFilenameFilter implements FilenameFilter {
		private String[] extensions;

		public ExtensionsFilenameFilter(String[] extensions) {
			this.extensions = extensions;
		}

		public boolean accept(File dir, String name) {
			for (String ext : extensions) {
				if (name.endsWith("." + ext))
					return true;
			}
			return false;
		}
	}

}
