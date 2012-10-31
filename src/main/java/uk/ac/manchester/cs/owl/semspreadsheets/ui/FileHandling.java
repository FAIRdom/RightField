/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.model.InvalidWorkbookFormatException;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.LoadOntologyTask;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.TaskManager;

public class FileHandling {
		
	private static Logger logger = Logger.getLogger(FileHandling.class);
	
	private final WorkbookManager workbookManager;
	private final TaskManager taskManager;
	private final JFrame frame;
	private static final Map<String,List<String>> WORKBOOK_EXT = new HashMap<String,List<String>>();
	
	private static final Map<String,List<String>> ONTOLOGY_EXT = new HashMap<String,List<String>>();

	public FileHandling(JFrame frame,WorkbookManager workbookManager,TaskManager taskManager) {
		this.frame = frame;
		this.workbookManager = workbookManager;
		this.taskManager = taskManager;		
		defineExtensions();
	}	
	
	public void loadOntology() throws OWLOntologyCreationException {
		File file = browseForFile("Load ontology", FileDialog.LOAD,
				ONTOLOGY_EXT,ONTOLOGY_EXT.keySet().iterator().next());
		if (file != null) {
			taskManager.runTask(new LoadOntologyTask(file));
		}						
	}

	public void saveWorkbook() throws Exception {
		URI workbookURI = workbookManager.getWorkbookURI();
		if (workbookURI == null) {
			saveWorkbookAs();
		} else {
			workbookManager.saveWorkbook(workbookURI);
		}
	}
	
	public void saveWorkbookAs() throws Exception {
		Map<String,List<String>> validExtensions = new HashMap<String,List<String>>();
		
		for (String key : WORKBOOK_EXT.keySet()) {
			if (WORKBOOK_EXT.get(key).contains(workbookManager.getWorkbookFileExtension())) {
				validExtensions.put(key,WORKBOOK_EXT.get(key));				
			}			
		}
		
		String defaultFileType=null;
		if (validExtensions.size()>0) {
			defaultFileType=validExtensions.keySet().iterator().next();
		}
		
		File requestedFile = browseForFile("Save spreadsheet as", FileDialog.SAVE,validExtensions,defaultFileType);		
			
		if (requestedFile != null) {
			File file = checkForDefaultExtension(requestedFile);			
			workbookManager.saveWorkbook(file.toURI());
		}
	}

	public void openWorkbook()  {
		File file = browseForFile("Open spreadsheet", FileDialog.LOAD,
				WORKBOOK_EXT,"Excel 97-2003");
		if (file != null) {
			try {
				workbookManager.loadWorkbook(file);
			} catch (IOException e) {
				ErrorHandler.getErrorHandler().handleError(e);
			} catch (InvalidWorkbookFormatException e) {
				ErrorHandler.getErrorHandler().handleError(e);
			}
		}
	}	
		
	private File checkForDefaultExtension(File file) {
		String filename = file.getName();		
		if (!filename.endsWith("." + workbookManager.getWorkbookFileExtension())) {
			file = new File(file.getPath() + "." + workbookManager.getWorkbookFileExtension());			
		}
		return file;
	}		
	
	private File browseForFile(String title, int mode,Map<String,List<String>> filetypesAndExtensions,String defaultFileType) {
		logger.debug("About to browse for file with title: "+title+", and extensions: "+filetypesAndExtensions);
		JFileChooser chooser = new JFileChooser(title);
		FileFilter defaultFilter = null;
		for (String key : filetypesAndExtensions.keySet()) {			
			ExtensionFileFilter filter = new ExtensionFileFilter(key, filetypesAndExtensions.get(key));
			chooser.addChoosableFileFilter(filter);
			logger.debug("Adding filter: "+filter);
			if (key.equals(defaultFileType)) {
				defaultFilter=filter;
			}
		}
		
		if (defaultFilter!=null) {
			logger.debug("Setting filter to "+defaultFilter);
			chooser.setFileFilter(defaultFilter);
		}
		else {
			chooser.setAcceptAllFileFilterUsed(true);
		}

		int retVal;
		if (mode == FileDialog.LOAD) {			
			retVal = chooser.showOpenDialog(frame);
		} else {
			chooser.setAcceptAllFileFilterUsed(false);
			retVal = chooser.showSaveDialog(frame);			
		}
		if (retVal == JFileChooser.APPROVE_OPTION) {			
			return chooser.getSelectedFile();			
		} else {
			return null;
		}
	}

//	private File browseForFileWithFileDialog(String title, int mode, Map<String,List<String>> filetypesAndExtensions) {		
//		FileDialog fileDialog = new FileDialog(frame, title, mode);
//
//		Set<String> allExtensions = new HashSet<String>();
//		for (String key : filetypesAndExtensions.keySet()) {
//			for (String ext : filetypesAndExtensions.get(key)) {
//				allExtensions.add(ext);
//			}
//		}	
//		fileDialog.setFilenameFilter(new ExtensionsFilenameFilter(allExtensions));		
//		fileDialog.setVisible(true);
//		String name = fileDialog.getFile();
//		if (name == null) {
//			return null;
//		}
//		String directory = fileDialog.getDirectory();		
//		return new File(directory + name);
//	}
	
	/** Defines and populates the extensions and filetypes for WORKBOOK_EXT and ONTOLOGY_EXT **/
	private void defineExtensions() {
		WORKBOOK_EXT.put("Excel 2007", Arrays.asList(new String[]{"xlsx"}));
		WORKBOOK_EXT.put("Excel 97-2003", Arrays.asList(new String[]{"xls"}));		
		ONTOLOGY_EXT.put("Ontology",Arrays.asList(new String[] { "obo", "owl","rdf", "rrf" }));
	}
	
	class ExtensionFileFilter extends FileFilter {

		private final String description;
		private final List<String> extensions;

		public ExtensionFileFilter(String description, List<String> extensions) {			
			this.extensions = extensions;
			this.description = description + " ( " + extensionsAsString() + " )";
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
		
		private String extensionsAsString() {
			String str="";
			Iterator<String> it = extensions.iterator();
			while (it.hasNext()) {
				str+="*."+it.next();
				if (it.hasNext()) {
					str+=", ";
				}
			}
			return str;
		}
		
		@Override
		public String toString() {
			return description;
		}
	}

	class ExtensionsFilenameFilter implements FilenameFilter {
		private Set<String> extensions;
		
		public ExtensionsFilenameFilter(Set<String> extensions) {
			this.extensions = extensions;
		}		

		public boolean accept(File dir, String name) {
			for (String ext : extensions) {
				if (name.endsWith("." + ext))
					return true;
			}
			return false;
		}
		
		public String getExtension() {
			return extensions.iterator().next();
		}
	}

}
