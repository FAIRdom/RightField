package uk.org.rightfield;
/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.export.CSVExporter;
import uk.ac.manchester.cs.owl.semspreadsheets.export.Exporter;
import uk.ac.manchester.cs.owl.semspreadsheets.export.RDFExporter;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.AboutBoxPanel;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class RightField {

	private static Logger logger = LogManager.getLogger();
	
    private static final String WINDOW_X_KEY = "orch.window.x";

    private static final String WINDOW_Y_KEY = "orch.window.y";

    private static final String WINDOW_WIDTH_KEY = "orch.window.width";

    private static final String WINDOW_HEIGHT_KEY = "orch.window.height";

    public static void main(String[] args) {
    	new RightField(args);
    }
    
    public RightField(String [] args) {
    	logger.debug("Intialised");
    	boolean cont=handleArguments(args);
    	
    	if (cont) {
    		startUp();
    	}
    }
    
    public static String getApplicationVersion() {    
		String v = AboutBoxPanel.class.getPackage().getImplementationVersion();
		if (v==null) {
			v="Unknown";
		}
		return v;    	
    }
    
    private boolean handleArguments(String [] args) {
    	RightFieldOptions options = new RightFieldOptions(args);
    	boolean cont=true;
    	if (options.isExport()) {
    		cont=false;
    		try {
    			export(options);    			
    		}
    		catch(Exception e) {
    			logger.error("Error attempting export",e);
    			System.exit(-1);
    		}    		
    	}
    	return cont;
    }
    
    private void export(RightFieldOptions options) throws Exception {
    	logger.debug("Exporting "+options.getFilename());
    	File file = new File(options.getFilename());
    	Exporter exporter;
    	if (options.getExportFormat().equals("rdf")) {
    		if (options.getProperty()==null) {
    			exporter=new RDFExporter(file,IRI.create(options.getId()));
    		}
    		else {
    			exporter=new RDFExporter(file,IRI.create(options.getId()),IRI.create(options.getProperty()));
    		}    		
    	}
    	else if (options.getExportFormat().equals("csv")) {
    		exporter=new CSVExporter(file);
    	}
    	else {
    		throw new Exception("Unrecognised export format");
    	}
    	exporter.export(System.out);    	
    }
    
    private void startUp() {
    	logger.debug("Starting Up UI");
    	setupLookAndFeel();
    	WorkbookManager manager = new WorkbookManager();    	
        final WorkbookFrame frame = new WorkbookFrame(manager);
        Preferences preferences = Preferences.userNodeForPackage(RightField.class);
        int x = preferences.getInt(WINDOW_X_KEY, 50);
        int y = preferences.getInt(WINDOW_Y_KEY, 50);
        int width = preferences.getInt(WINDOW_WIDTH_KEY, 800);
        int height = preferences.getInt(WINDOW_HEIGHT_KEY, 600);
        frame.setLocation(x, y);
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosed(WindowEvent e) {
        		System.exit(0);
        	}
            @Override
            public void windowClosing(WindowEvent e) {            	
            	boolean close = frame.checkSavedState("Exit RightField");            	            	
				if (close) {
					Preferences preferences = Preferences.userNodeForPackage(RightField.class);
	                preferences.putInt(WINDOW_X_KEY, frame.getX());
	                preferences.putInt(WINDOW_Y_KEY, frame.getY());
	                preferences.putInt(WINDOW_WIDTH_KEY, frame.getWidth());
	                preferences.putInt(WINDOW_HEIGHT_KEY, frame.getHeight());
					frame.dispose();
				}                
            }
        });
    }
    
    private void setupLookAndFeel() {
    	String os = System.getProperty("os.name");	
    	logger.debug("OS detected as: " + os);
    	if (os.toLowerCase().indexOf("win") > -1 ||	os.toLowerCase().indexOf("mac") > -1) {
    		try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				logger.error("Error setting look and feel",e);
			} 
    	}
    	else {
    		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    	        if ("Nimbus".equals(info.getName())) {
    	            try {
						UIManager.setLookAndFeel(info.getClassName());
					} catch (Exception e) {
						logger.error("Error setting look and feel",e);
					} 
    	            break;
    	        }
    	    }
    	}
    }
}
