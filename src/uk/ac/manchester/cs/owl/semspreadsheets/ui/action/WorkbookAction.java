package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public abstract class WorkbookAction extends AbstractAction {

	private WorkbookManager workbookManager;

	/**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    protected WorkbookAction(String name, WorkbookManager workbookManager) {
        super(name);
		this.workbookManager = workbookManager;        
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

    public WorkbookManager getWorkbookManager() {
        return workbookManager;
    }
}
