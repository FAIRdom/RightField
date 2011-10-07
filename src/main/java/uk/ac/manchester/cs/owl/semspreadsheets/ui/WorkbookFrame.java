package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
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
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.AboutBoxAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.ClearOntologyValuesAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.ExitAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.InsertSheetAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.OnlineHelpAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.OpenFromBioPortalAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.OpenOntologyAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.OpenWorkbookAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.RemoveSheetAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.RenameSheetAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SaveAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SaveAsAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCellClearAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCellCopyAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCellCutAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCellPasteAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.FetchBioportalOntologyListTask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.LoadOntologyTask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.LoadRepositoryItemTask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.TaskManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 * 
 * Author: Stuart Owen
 * Date: 30 Sep 2010
 */

public class WorkbookFrame extends JFrame {
	
	private static final long serialVersionUID = 8991252467294969145L; 
	
	private static final String[] WORKBOOK_EXT = new String[] { "xls" };
	private static final String[] ONTOLOGY_EXT = new String[] { "obo", "owl",
			"rdf", "rrf" };
	private static final String [] APPLICATION_LOGO_FILENAMES = {"/rightfield-logo.png","/rightfield-logo-16x16.png"};

	private static final Logger logger = Logger.getLogger(WorkbookFrame.class);	
	
	//used for tracking whether there are unsaved changes
	private boolean changeUnsaved=false;

	private WorkbookManager workbookManager;

	private TaskManager taskManager = new TaskManager(this);

	public WorkbookFrame(WorkbookManager manager) {
		this.workbookManager = manager;
		setTitle("RightField");
		List<Image> iconImages = new ArrayList<Image>();
		for (String logoFilename : APPLICATION_LOGO_FILENAMES) {
			if (WorkbookFrame.class.getResource(logoFilename) != null) {
				iconImages.add(new ImageIcon(WorkbookFrame.class
						.getResource(logoFilename)).getImage());
			} else {
				logger.warn("Unable to find the "+logoFilename+" for the icon");
			}
		}
		setIconImages(iconImages);		
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new MainPanel(this));
		
		setupMenuItems();
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
				workbookManager.getWorkbookState().changesUnsaved();
			}
		});
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				suggestOpeningWorkbook();
			}			
		});
	}	

	private void suggestOpeningWorkbook() {
		String message = "<html><center><p>Would you like to start by opening an existing spreadsheet you have already created?</p>" +
						 "<p></p>" +
						 "<p>If not, you will begin by editing a new spreadsheet which you can save as a new file.</p></center></html>";
		int ret=JOptionPane.showOptionDialog(this, message, "Open spreadsheet?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,null,null);
		if (ret==JOptionPane.YES_OPTION) {
			openWorkbook();
		}
	}

	private void setupMenuItems() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = menuBar.add(new JMenu("File"));
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.add(new OpenWorkbookAction(this));
		fileMenu.add(new OpenOntologyAction(this));
		fileMenu.add(new OpenFromBioPortalAction(this));
		fileMenu.addSeparator();
		fileMenu.add(new SaveAction(this));
		fileMenu.add(new SaveAsAction(this));
		fileMenu.add(new JSeparator());
		fileMenu.add(new ExitAction(this));
		
		JMenu editMenu = menuBar.add(new JMenu("Edit"));
		editMenu.setMnemonic(KeyEvent.VK_E);
		
		editMenu.add(new SheetCellCutAction(workbookManager,getToolkit()));
		editMenu.add(new SheetCellCopyAction(workbookManager,getToolkit()));
		editMenu.add(new SheetCellPasteAction(workbookManager,getToolkit()));
		JMenuItem menuItem = editMenu.add(new SheetCellClearAction(workbookManager));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		editMenu.add(new JSeparator());
		
		editMenu.add(new ClearOntologyValuesAction(this));

		JMenu sheetMenu = menuBar.add(new JMenu("Sheet"));
		sheetMenu.setMnemonic(KeyEvent.VK_S);
		sheetMenu.add(new InsertSheetAction(workbookManager, this));
		sheetMenu.add(new RemoveSheetAction(this));
		sheetMenu.add(new RenameSheetAction(this));			
				
		JMenu helpMenu = menuBar.add(new JMenu("Help"));		
		helpMenu.setMnemonic(KeyEvent.VK_H);
		
		JMenuItem onlineHelp = helpMenu.add(new OnlineHelpAction(this));
		onlineHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		helpMenu.add(new AboutBoxAction(this));
		
		
						
		setJMenuBar(menuBar);
	}
	
	public void exit() {
		processWindowEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
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
					.append("The spreadsheet contains information about terms from ontologies which<br>"
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
	
	public WorkbookState getWorkbookState() {
		return getWorkbookManager().getWorkbookState();
	}

	public WorkbookManager getWorkbookManager() {
		return workbookManager;
	}

	public Sheet addSheet() {
		return workbookManager.getWorkbook().addSheet();		
	}
	
	public void removeSheet(Sheet sheet) {
		workbookManager.getWorkbook().deleteSheet(sheet.getName());
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

	public void openWorkbook()  {
		File file = browseForFile("Open spreadsheet", FileDialog.LOAD, "Excel spreadsheet",
				WORKBOOK_EXT);
		if (file != null) {
			try {
				workbookManager.loadWorkbook(file);
			} catch (IOException e) {
				ErrorHandler.getErrorHandler().handleError(e);
			}
		}
	}

	public void saveWorkbookAs() throws IOException {
		File file = browseForFile("Save spreadsheet as", FileDialog.SAVE,
				"Excel spreadsheet", WORKBOOK_EXT);		
		if (file != null) {
			file = checkForDefaultExtension(file);
			workbookManager.saveWorkbook(file.toURI());
		}
	}
	
	
	private File checkForDefaultExtension(File file) {
		return checkForDefaultExtension(file,".xls");
	}

	private File checkForDefaultExtension(File file,String defaultExtension) {
		String filename = file.getName();
		if (!filename.endsWith(".xls")) {
			file = new File(file.getPath()+".xls");
		}
		return file;
	}

	public File browseForFile(String title, int mode, final String filetype,
			final String[] extensions) {
		String os = System.getProperty("os.name");
		String fileTypeWithExt = filetype + " (";
		for (String ext : extensions) {
			fileTypeWithExt+="."+ext+", ";
		}
		fileTypeWithExt=fileTypeWithExt.substring(0,fileTypeWithExt.length()-2); //chop off last ", "
		fileTypeWithExt+=")";
		
		logger.debug("OS detected as: " + os);
		// uses FileDialog for Mac and Windows, as this is preferred.
		// but JFileChooser for Linux and the other unix's, as FileDialog is awful on those platforms
		if (os.toLowerCase().indexOf("win") > -1 || os.toLowerCase().indexOf("mac") > -1) {			
			return browseForFileWithFileDialog(title, mode, extensions);
		} else {
			return browseForFileWithJChooser(title, mode, fileTypeWithExt, extensions);
		}
	}

	private File browseForFileWithJChooser(String title, int mode,
			final String filetype, final String[] extensions) {
		JFileChooser chooser = new JFileChooser(title);
		if (extensions != null && extensions.length > 0) {
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

		if (extensions != null && extensions.length > 0) {			
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
