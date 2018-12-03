package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.net.URI;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;

public class CSVExporterXLSXTest extends GeneralCSVExporterTests {		

	@Override
	protected URI twoOntologiesWorkbookURI() throws Exception {
		return DocumentsCatalogue.twoOntologiesWorkbookXLSXURI();
	}

	@Override
	protected URI bookWithPropertiesURI() throws Exception {
		return DocumentsCatalogue.bookWithPropertiesXLSXURI();
	}

	@Override
	protected URI bookWithLiteralsURI() throws Exception {
		return DocumentsCatalogue.simpleWorkbookWithLiteralsOverRangeXLSXURI();
	}

	@Override
	protected URI bookWithQuotesAndCommasURI() throws Exception {
		return DocumentsCatalogue.simpleXLSXWorkbookForCSVURI();
	}

	@Override
	protected URI bookWithNumericsAndStringsURI() throws Exception {
		return DocumentsCatalogue.bookWithNumericsAndStringsXLSXURI();
	}
}
