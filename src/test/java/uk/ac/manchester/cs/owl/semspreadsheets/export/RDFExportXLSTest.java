package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.io.File;
import java.net.URI;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;

public class RDFExportXLSTest extends GeneralRDFExporterTests {
	
	@Override
	protected URI populatedJERMWorkbookURI() throws Exception {
		return DocumentsCatalogue.populatedJermWorkbookURI();
	}
	
	@Override
	protected File populatedJERMWorkbookFile() throws Exception {
		return DocumentsCatalogue.populatedJermWorkbookFile();
	}
	
	@Override
	protected URI bookWithPropertiesURI() throws Exception {
		return DocumentsCatalogue.bookWithPropertiesURI();
	}

	@Override
	protected URI simpleWorkbookWithLiteralsOverRangeURI() throws Exception {
		return DocumentsCatalogue.simpleWorkbookWithLiteralsOverRangeURI();
	}

}
