package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 07-Nov-2009
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
}
