package uk.ac.manchester.cs.owl.semspreadsheets.export;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;

public class CSVExporterXLSTest extends GeneralCSVExporterTests {

	@Override
	protected URI twoOntologiesWorkbookURI() throws Exception {
		return DocumentsCatalogue.twoOntologiesWorkbookURI();
	}

	@Override
	protected URI bookWithPropertiesURI() throws Exception {
		return DocumentsCatalogue.bookWithPropertiesURI();
	}

	@Override
	protected URI bookWithLiteralsURI() throws Exception {
		return DocumentsCatalogue.simpleWorkbookWithLiteralsOverRangeURI();
	}

	@Override
	protected URI bookWithQuotesAndCommasURI() throws Exception {
		return DocumentsCatalogue.simpleWorkbookForCSVURI();
	}

	@Override
	protected URI bookWithNumericsAndStringsURI() throws Exception {
		return DocumentsCatalogue.bookWithNumericsAndStringsURI();
	}
	
	@Test
	public void testProblematicTemplateWithNullCells() throws Exception {
		URI uri = DocumentsCatalogue.class.getResource("/workbooks/microarray_example_newformat-8.xls").toURI();
		Exporter exporter = new CSVExporter(uri);
		String csv = exporter.export();
		
		//just a basic check it has generated some expected csv
		assertEquals(9991,csv.length());
		assertEquals("mged.sourceforge.net/ontologies/MGEDOntology.owl#L",csv.substring(190, 240));
	}
		

}
