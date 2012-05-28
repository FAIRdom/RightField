package uk.ac.manchester.cs.owl.semspreadsheets.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class AbstractExporterTest {
		
	
	@Test
	public void testInitWithManager() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		manager.loadWorkbook(jermWorkbookURI());
		AbstractExporter exporter = new AbstractExporterImpl(manager);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
	}

	@Test
	public void testInitiWithURI() throws Exception {
		URI uri = jermWorkbookURI();
		AbstractExporter exporter = new AbstractExporterImpl(uri);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
	}
	
	@Test
	public void testInitiWithFile() throws Exception {
		File file = jermWorkbookFile();
		AbstractExporter exporter = new AbstractExporterImpl(file);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());		
	}
	
	//test helper methods
	
	private File jermWorkbookFile() throws Exception {
		String filename = AbstractExporterTest.class.getResource("/populated_JERM_template.xls").getFile();
		return new File(filename);
	}
	
	private URI jermWorkbookURI() throws Exception {
		return AbstractExporterTest.class.getResource("/populated_JERM_template.xls").toURI();
	}
	

	/**
	 * A concrete implementation of {@link AbstractExporter} for testing purposes only.
	 */
	private class AbstractExporterImpl extends AbstractExporter {
		public AbstractExporterImpl(WorkbookManager manager) {
			super(manager);			
		}

		public AbstractExporterImpl(File workbookFile) throws IOException {
			super(workbookFile);			
		}
		
		public AbstractExporterImpl(URI uri) throws IOException {
			super(uri);			
		}				
	}
}
