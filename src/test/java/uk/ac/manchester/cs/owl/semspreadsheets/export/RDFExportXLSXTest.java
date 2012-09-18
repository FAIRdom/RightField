package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.io.File;
import java.net.URI;

import org.junit.Ignore;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;

@Ignore("Ignoring XLSX tests until XLSX support is renabled (see xlsx2 branch)")
public class RDFExportXLSXTest extends GeneralRDFExporterTests {

	@Override
	protected URI populatedJERMWorkbookURI() throws Exception {
		return DocumentsCatalogue.populatedJermWorkbookXLSXURI();
	}

	@Override
	protected File populatedJERMWorkbookFile() throws Exception {
		return DocumentsCatalogue.populatedJermWorkbookXLSXFile();
	}

	@Override
	protected URI bookWithPropertiesURI() throws Exception {
		return DocumentsCatalogue.bookWithPropertiesXLSXURI();
	}

	@Override
	protected URI simpleWorkbookWithLiteralsOverRangeURI() throws Exception {
		return DocumentsCatalogue.simpleWorkbookWithLiteralsOverRangeXLSXURI();
	}

}
