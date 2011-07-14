package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

@SuppressWarnings("serial")
public class BioPortalAccessDeniedException extends Exception {	
	public BioPortalAccessDeniedException() {
		super("Invalid API Key");
	}
}
