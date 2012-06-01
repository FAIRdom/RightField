/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class RightField {

	private static Logger logger = Logger.getLogger(RightField.class);
	
    private static final String WINDOW_X_KEY = "orch.window.x";

    private static final String WINDOW_Y_KEY = "orch.window.y";

    private static final String WINDOW_WIDTH_KEY = "orch.window.width";

    private static final String WINDOW_HEIGHT_KEY = "orch.window.height";

    public static void main(String[] args) {
    	logger.debug("Starting Up");
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
}
