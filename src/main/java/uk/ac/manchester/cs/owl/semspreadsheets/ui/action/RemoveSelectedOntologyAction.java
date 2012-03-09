package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.event.ActionEvent;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

@SuppressWarnings("serial")
public class RemoveSelectedOntologyAction extends WorkbookFrameAction {

	public RemoveSelectedOntologyAction(WorkbookFrame workbookFrame) {
		super("Close selected ontology", workbookFrame);		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getWorkbookFrame().removeOntology();
	}

}
