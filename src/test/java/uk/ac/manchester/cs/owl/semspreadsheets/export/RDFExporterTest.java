package uk.ac.manchester.cs.owl.semspreadsheets.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URI;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.TestDocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class RDFExporterTest {
	
	private static String rootID="http://files/data/1";	
	
	@Test
	public void testInitWithManager() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		manager.loadWorkbook(TestDocumentsCatalogue.populatedJermWorkbookURI());
		AbstractExporter exporter = new RDFExporter(manager,rootID);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
		assertEquals(9,exporter.getValidations().size());
	}

	@Test
	public void testInitiWithURI() throws Exception {
		URI uri = TestDocumentsCatalogue.populatedJermWorkbookURI();
		AbstractExporter exporter = new RDFExporter(uri,rootID);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
		assertEquals(9,exporter.getValidations().size());
	}
	
	@Test
	public void testInitiWithFile() throws Exception {
		File file = TestDocumentsCatalogue.populatedJermWorkbookFile();
		AbstractExporter exporter = new RDFExporter(file,rootID);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());	
		assertEquals(9,exporter.getValidations().size());
	}

	@Test
	public void testExport() throws Exception {
		URI uri = TestDocumentsCatalogue.populatedJermWorkbookURI();
		Exporter exp = new RDFExporter(uri,rootID);
		String rdf = exp.export();
		System.out.println(rdf);
	}
	
	@Test
	public void testExportWithProperties() throws Exception {
		URI uri = TestDocumentsCatalogue.bookWithPropertiesURI();
		Exporter exp = new RDFExporter(uri,rootID);
		String rdf = exp.export();
		System.out.println(rdf);
	}
	
	

}
