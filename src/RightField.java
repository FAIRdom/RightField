/*
* Copyright (C) 2009, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge, Stuart Owen<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 24-Nov-2009
 */

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
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Preferences preferences = Preferences.userNodeForPackage(RightField.class);
                preferences.putInt(WINDOW_X_KEY, frame.getX());
                preferences.putInt(WINDOW_Y_KEY, frame.getY());
                preferences.putInt(WINDOW_WIDTH_KEY, frame.getWidth());
                preferences.putInt(WINDOW_HEIGHT_KEY, frame.getHeight());
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
