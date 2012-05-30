package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.io.OutputStream;

public interface Exporter {
	
	public void export(OutputStream outStream);
	
	public String export();

}
