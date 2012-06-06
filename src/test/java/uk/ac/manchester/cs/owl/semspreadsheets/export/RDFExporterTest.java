package uk.ac.manchester.cs.owl.semspreadsheets.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URI;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class RDFExporterTest {
	
	private static String rootID="http://files/data/1";	
	
	@Test
	public void testInitWithManager() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		manager.loadWorkbook(jermWorkbookURI());
		AbstractExporter exporter = new RDFExporter(manager,rootID);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
		assertEquals(9,exporter.getValidations().size());
	}

	@Test
	public void testInitiWithURI() throws Exception {
		URI uri = jermWorkbookURI();
		AbstractExporter exporter = new RDFExporter(uri,rootID);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
		assertEquals(9,exporter.getValidations().size());
	}
	
	@Test
	public void testInitiWithFile() throws Exception {
		File file = jermWorkbookFile();
		AbstractExporter exporter = new RDFExporter(file,rootID);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());	
		assertEquals(9,exporter.getValidations().size());
	}

	@Test
	public void testExport() throws Exception {
		URI uri = jermWorkbookURI();
		Exporter exp = new RDFExporter(uri,rootID);
		String rdf = exp.export();
		System.out.println(rdf);
	}
	
	private File jermWorkbookFile() throws Exception {
		String filename = AbstractExporterTest.class.getResource("/populated_JERM_template.xls").getFile();
		return new File(filename);
	}
	
	private URI jermWorkbookURI() throws Exception {
		return AbstractExporterTest.class.getResource("/populated_JERM_template.xls").toURI();
	}

}
