package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

@SuppressWarnings("serial")
public class OpenOntologyFromURLAction extends WorkbookFrameAction {
	
	public OpenOntologyFromURLAction(WorkbookFrame workbookFrame) {
        super("Open ontology from a URL...", workbookFrame); 
        setAcceleratorKey(KeyEvent.VK_L, true, false);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
            getWorkbookFrame().loadOntologyFromURL();
        }
        catch (Exception e1) {        	
            //will already have been handled inside LoadOntologyFromURITask
        }
	}

}
