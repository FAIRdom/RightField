/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.model.KnownOntologies;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.AboutBoxAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.ClearOntologyValuesAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.CloseWorkbookAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.ExitAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.ExportRDFAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.InsertSheetAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.OnlineHelpAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.OpenFromBioPortalAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.OpenOntologyAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.OpenOntologyFromURLAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.OpenWorkbookAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.CloseSelectedOntologyAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.RemoveSheetAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.RenameSheetAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SaveAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SaveAsAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCellClearAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCellCopyAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCellCutAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCellPasteAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.FetchBioportalOntologyListTask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.LoadOntologyFromURITask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.LoadOntologyTask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.LoadRepositoryItemTask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.TaskManager;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */

public class WorkbookFrame extends JFrame {
	
	private static final long serialVersionUID = 8991252467294969145L; 
	
	private static final String[] WORKBOOK_EXT = new String[] { "xls" };
	private static final String[] ONTOLOGY_EXT = new String[] { "obo", "owl",
			"rdf", "rrf" };
	private static final String [] APPLICATION_LOGO_FILENAMES = {"/rightfield-logo.png","/rightfield-logo-16x16.png"};

	private static final Logger logger = Logger.getLogger(WorkbookFrame.class);			

	private WorkbookManager workbookManager;

	private TaskManager taskManager = new TaskManager(this);
	
	private OWLOntology selectedOntology = null;

	private CloseSelectedOntologyMenuItem removeOntologyMenuItem;

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
			@Override
			public void workbookCreated(WorkbookManagerEvent event) {
				handleNewWorkbook();
			}
			
			@Override
			public void workbookLoaded(WorkbookManagerEvent event) {
				handleNewWorkbook();
			}
			
			@Override
			public void ontologiesChanged(WorkbookManagerEvent event) {
				updateTitleBar();
				workbookManager.getWorkbookState().changesUnsaved();				
			}

			@Override
			public void validationAppliedOrCancelled() {				
				
			}
			
			@Override
			public void workbookSaved(WorkbookManagerEvent event) {
				updateTitleBar();
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
		fileMenu.add(new CloseWorkbookAction(this));
		fileMenu.add(new JSeparator());
		fileMenu.add(new OpenOntologyAction(this));
		fileMenu.add(new OpenOntologyFromURLAction(this));
		fileMenu.add(new OpenFromBioPortalAction(this));
		removeOntologyMenuItem = new CloseSelectedOntologyMenuItem(new CloseSelectedOntologyAction(this),getWorkbookManager().getOntologyManager());
		removeOntologyMenuItem.setSelectedOntology(getSelectedOntology());
		fileMenu.add(removeOntologyMenuItem);		
		fileMenu.addSeparator();
		fileMenu.add(new SaveAction(this));
		fileMenu.add(new SaveAsAction(this));
		fileMenu.add(new JSeparator());
		fileMenu.add(new ExportRDFAction(this));
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
	
	public void closeWorkbook() {
		if (checkSavedState("Close the workbook")) {
			getWorkbookManager().createNewWorkbook();
		}
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public void handleNewWorkbook() {
		updateTitleBar();
		Collection<IRI> ontologyIRIs = getWorkbookManager().getOntologyManager().getOntologyIRIs();		
				
		Set<IRI> missingOntologies = new HashSet<IRI>();
		Set<OWLOntology> openOntologies = getWorkbookManager().getOntologyManager().getLoadedOntologies();
		
		for (IRI ontologyIRI : ontologyIRIs) {			
			boolean present = false;
			//need to loop over like this because OWLOntologyManager.contains() seems to rely on being the same instance
			//TODO: check if this is still the case with v3.2.5 when its released.
			for (OWLOntology openOntology : openOntologies) {
				present = openOntology.getOntologyID().getOntologyIRI().equals(ontologyIRI);
				if (present) {
					break;
				}
			}
			if (!present) {
				missingOntologies.add(ontologyIRI);
				logger.debug("Missing ontology detected: "+ontologyIRI);
			}
		}
		if (!missingOntologies.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("<html><body>");
			sb
					.append("The spreadsheet contains information about terms from ontologies which<br>"
							+ "are not loaded:");
					sb.append("<ul>");
					for (IRI missingIRI : missingOntologies) {
						if (!missingIRI.toString().equals(KnownOntologies.PROTEGE_ONTOLOGY)) {
							sb.append("<li>"+missingIRI.toString()+"</li>");
						}						
					}
					sb.append("</ul>");
					sb.append("Would you like to load these ontologies now?");
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
		return workbookManager.addSheet();		
	}
	
	public void removeSheet(Sheet sheet) {
		workbookManager.deleteSheet(sheet.getName());
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
	
	public void loadOntologyFromURL() throws OWLOntologyCreationException {
		String urlStr = JOptionPane.showInputDialog(this,"Enter the URL for the ontology","URL for ontology",JOptionPane.PLAIN_MESSAGE);
		if (urlStr!=null) {
			URI uri;
			try {
				uri = new URL(urlStr).toURI();
				taskManager.runTask(new LoadOntologyFromURITask(uri));				
			} catch (MalformedURLException e) {
				ErrorHandler.getErrorHandler().handleError(e);
			} catch (URISyntaxException e) {
				ErrorHandler.getErrorHandler().handleError(e);
			}			
		}		
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
		if (checkSavedState("Open a new workbook")) {
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

	public File checkForDefaultExtension(File file,String defaultExtension) {
		String filename = file.getName();
		if (!filename.endsWith(defaultExtension)) {
			file = new File(file.getPath()+defaultExtension);
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

	public boolean checkSavedState(String actionName) {
		int res=JOptionPane.YES_OPTION;
		if (!getWorkbookState().isChangesSaved()) {
    		res = JOptionPane.showConfirmDialog(this,"You have unsaved changes. Are you sure you wish to "+actionName+"?","Continue to "+actionName+"?",JOptionPane.YES_NO_OPTION);
    	}
		return (res == JOptionPane.YES_OPTION);
	}
	
	public void removeOntology() {
		OWLOntology ontology = getSelectedOntology();
		if (ontology!=null) {
			removeOntology(ontology);			
		}
		else {
			logger.debug("Selected ontology found to be NULL when attempting to remove it");
		}
	}
	
	public void setSelectedOntology(OWLOntology ontology) {
		if (ontology==null) {
			logger.debug("Selected ontology set to be NULL");
		}
		else {
			logger.debug("Selected ontology updated to be: "+ontology.getOntologyID().toString());
		}
		
		selectedOntology = ontology;
		removeOntologyMenuItem.setSelectedOntology(ontology);
	}
		

	private OWLOntology getSelectedOntology() {
		return selectedOntology;
	}

	public void removeOntology(OWLOntology ontology) {
		int res = JOptionPane.showConfirmDialog(this,"Are you sure you wish to remove the '"+ontology.getOntologyID().getOntologyIRI() +"' ontology?","Remove ontology?",JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.YES_OPTION) {
			setSelectedOntology(null);
			getWorkbookManager().getOntologyManager().removeOntology(ontology);			
		}
	}

	

	

}
