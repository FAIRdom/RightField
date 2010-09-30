package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.datatransfer.Clipboard;

public class OntologyValidationsClipboard {
	
	private static Clipboard clipboard = new Clipboard("Ontology Validations");
	
	public static final Clipboard getClipboard() {
		return clipboard;
	}
}
