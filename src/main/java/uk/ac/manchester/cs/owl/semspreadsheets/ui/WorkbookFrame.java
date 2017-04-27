/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.AbstractWorkbookManagerListener;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.OntologyManagerListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.*;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.*;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.FetchBioportalOntologyListTask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.LoadOntologyFromURITask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.LoadRepositoryItemTask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.TaskManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */

public class WorkbookFrame extends JFrame {
	
	private static final long serialVersionUID = 8991252467294969145L; 
		
	private static final String [] APPLICATION_LOGO_FILENAMES = {"/rightfield-logo.png","/rightfield-logo-16x16.png"};

	private static final Logger logger = LogManager.getLogger();			

	private WorkbookManager workbookManager;

	private TaskManager taskManager = new TaskManager(this);
	
	private OWLOntology selectedOntology = null;

	private CloseSelectedOntologyMenuItem removeOntologyMenuItem;

	private FileHandling fileHandling;

	public WorkbookFrame(WorkbookManager manager) {
		this.workbookManager = manager;
		fileHandling = new FileHandling(this, manager, taskManager);
		setTitle("RightField");
		List<Image> iconImages = createIconImages();
		setIconImages(iconImages);		
		
		addMainPanel();		
		
		setupMenuItems();
		
		updateTitleBar();			
		
		workbookManager.getOntologyManager().addListener(new OntologyManagerListener() {
			
			@Override
			public void ontologiesChanged() {
				updateTitleBar();
				workbookManager.getWorkbookState().changesUnsaved();
			}
			@Override
			public void ontologySelected(OWLOntology ontology) {
				if (ontology==null) {
					logger.debug("Selected ontology set to be NULL");
				}
				else {
					logger.debug("Selected ontology updated to be: "+ontology.getOntologyID().toString());
				}
				
				selectedOntology = ontology;
				removeOntologyMenuItem.setSelectedOntology(ontology);
				
			}
		});
		
		workbookManager.addListener(new AbstractWorkbookManagerListener() {
			@Override
			public void workbookCreated() {
				handleNewWorkbook();
			}
			
			@Override
			public void workbookLoaded() {
				handleNewWorkbook();
			}									
			
			@Override
			public void workbookSaved() {
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

	private List<Image> createIconImages() {
		List<Image> iconImages = new ArrayList<Image>();
		for (String logoFilename : APPLICATION_LOGO_FILENAMES) {
			if (WorkbookFrame.class.getResource(logoFilename) != null) {
				iconImages.add(new ImageIcon(WorkbookFrame.class
						.getResource(logoFilename)).getImage());
			} else {
				logger.warn("Unable to find the "+logoFilename+" for the icon");
			}
		}
		return iconImages;
	}

	private void addMainPanel() {
		MainPanel mainPanel = new MainPanel(this);				
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel);
	}	

	private void suggestOpeningWorkbook() {
		String message = "You may open an existing spreadsheet, or start with a fresh empty spreadsheet";
		String [] options = new String[]{"Open a spreadsheet","Start with an empty spreadsheet"};
		int ret=JOptionPane.showOptionDialog(this, 
				message, "Open spreadsheet?", 
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,null);		
		if (ret==0) {
			openWorkbook();
		}
		else {
			createNewWorkbook();
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
		fileMenu.add(new ExportCSVAction(this));
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
		sheetMenu.add(new RemoveSheetAction(this));
		sheetMenu.add(new RenameSheetAction(this));			
				
		JMenu helpMenu = menuBar.add(new JMenu("Help"));		
		helpMenu.setMnemonic(KeyEvent.VK_H);
		
		JMenuItem onlineHelp = helpMenu.add(new OnlineHelpAction(this));
		onlineHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		helpMenu.add(new RegistrationAction(this));
		helpMenu.add(new AboutBoxAction(this));

		JMenu testMenu = menuBar.add(new JMenu("Link"));
		testMenu.add(new AddLinkCellToCellAction(workbookManager, this));
		testMenu.add(new AddLinkCellToTableAction(workbookManager, this));
		testMenu.add(new DeleteLinkCellsAction(workbookManager, this));
		testMenu.add(new DeleteAllLinks(workbookManager, this));

		setJMenuBar(menuBar);
	}
	
	public void exit() {
		processWindowEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
	}
	
	public void closeWorkbook() {
		if (checkSavedState("Close the spreadsheet")) {
			createNewWorkbook();
		}
	}
	
	private void createNewWorkbook() {
		int index=-1;
		WorkbookFormat[] formats = WorkbookFormat.getFormats();
		while(index<0) {
			String question = "Please select the Excel format of the new spreadsheet";
			
			List<String>options = new ArrayList<String>();
			for (WorkbookFormat format : formats) {
				options.add(format.toString());
			}
			index = JOptionPane
					.showOptionDialog(this, question, "Select Excel format",JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null,
							options.toArray(), null);
		}
						
		getWorkbookManager().createNewWorkbook(formats[index]);		
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public void handleNewWorkbook() {
		updateTitleBar();
		OntologyManager ontologyManager = getWorkbookManager().getOntologyManager();
		Collection<IRI> ontologyIRIs = ontologyManager.getOntologyIRIs();		
				
		Set<IRI> missingOntologies = new HashSet<IRI>();		
		
		for (IRI ontologyIRI : ontologyIRIs) {						
			if (!ontologyManager.isOntologyLoaded(ontologyIRI)) {
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
		fileHandling.loadOntology();
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
			RepositoryItem item = RepositoryPanel.showDialog(this,RepositoryManager.getInstance().getBioPortalRepositoryAccessor().getRepository());
			if (item == null) {
				return;
			}
			try {
				taskManager.runTask(new LoadRepositoryItemTask(item));
			}
			catch(OWLOntologyCreationException ex) {
				ErrorHandler.getErrorHandler().handleError(ex, item.getPhysicalIRI());
			}
			
		}
	}

	public void saveWorkbook() throws Exception {
		fileHandling.saveWorkbook();
	}

	public void openWorkbook()  {
		if (checkSavedState("Open a new workbook")) {
			fileHandling.openWorkbook();
		}		
	}

	public void saveWorkbookAs() throws Exception {
		fileHandling.saveWorkbookAs();
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

	private OWLOntology getSelectedOntology() {
		return selectedOntology;
	}

	public void removeOntology(OWLOntology ontology) {
		int res = JOptionPane.showConfirmDialog(this,"Are you sure you wish to remove the '"+ontology.getOntologyID().getOntologyIRI() +"' ontology?","Remove ontology?",JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.YES_OPTION) {			
			getWorkbookManager().getOntologyManager().removeOntology(ontology);			
		}
	}	
}
