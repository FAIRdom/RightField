package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.net.URI;

import org.junit.Ignore;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;

@Ignore("Ignoring XLSX tests until XLSX support is renabled (see xlsx2 branch)")
public class CSVExporterXLSXTest extends GeneralCSVExporterTests {

	@Override
	protected URI twoOntologiesWorkbookURI() throws Exception {
		return DocumentsCatalogue.twoOntologiesWorkbookXLSXURI();
	}

}
