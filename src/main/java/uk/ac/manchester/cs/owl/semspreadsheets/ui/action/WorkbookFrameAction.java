/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public abstract class WorkbookFrameAction extends AbstractAction {

    private WorkbookFrame workbookFrame;

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    protected WorkbookFrameAction(String name, WorkbookFrame workbookFrame) {
        super(name);
        this.workbookFrame = workbookFrame;
    }

    protected void setAcceleratorKey(int keyCode) {
        setAcceleratorKey(keyCode, false, false);
    }

    protected void setAcceleratorKey(int keyCode, boolean shift, boolean ctrl) {
        int modifiers = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if(shift) {
            modifiers = modifiers | KeyEvent.SHIFT_MASK;
        }
        if(ctrl) {
            modifiers = modifiers | KeyEvent.CTRL_MASK;
        }
        KeyStroke ks = KeyStroke.getKeyStroke(keyCode, modifiers);
        putValue(Action.ACCELERATOR_KEY, ks);
    }

    public WorkbookFrame getWorkbookFrame() {
        return workbookFrame;
    }
    
    public WorkbookManager getWorkbookManager() {
    	return workbookFrame.getWorkbookManager();
    }
    
    public OntologyManager getOntologyManager() {
    	return getWorkbookManager().getOntologyManager();
    }
}
