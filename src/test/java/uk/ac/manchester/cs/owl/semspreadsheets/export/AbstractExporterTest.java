package uk.ac.manchester.cs.owl.semspreadsheets.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
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
		assertEquals(9,exporter.getValidations().size());
	}

	@Test
	public void testInitiWithURI() throws Exception {
		URI uri = jermWorkbookURI();
		AbstractExporter exporter = new AbstractExporterImpl(uri);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
		assertEquals(9,exporter.getValidations().size());
	}
	
	@Test
	public void testInitiWithFile() throws Exception {
		File file = jermWorkbookFile();
		AbstractExporter exporter = new AbstractExporterImpl(file);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());	
		assertEquals(9,exporter.getValidations().size());
	}
	
	@Test
	public void testGetValidations() throws Exception {
		URI uri = jermWorkbookURI();
		AbstractExporter exporter = new AbstractExporterImpl(uri);
		Collection<OntologyTermValidation> vals = exporter.getValidations();
		assertEquals(9,vals.size());
		List<OntologyTermValidation> list = new ArrayList<OntologyTermValidation>(vals);
		Collections.sort(list,new Comparator<OntologyTermValidation>() {
			@Override
			public int compare(OntologyTermValidation o1,
					OntologyTermValidation o2) {
				return o1.getRange().compareTo(o2.getRange());
			}
		});
		OntologyTermValidation val = list.get(0);
		assertEquals("Metadata Template!B17:B17",val.getRange().toString());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#Project",val.getValidationDescriptor().getEntityIRI().toString());
		assertEquals("BaCell",val.getValidationDescriptor().getTerms().iterator().next().getFormattedName());
		
		val = list.get(8);
		assertEquals("Metadata Template!P28:P37",val.getRange().toString());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#organism",val.getValidationDescriptor().getEntityIRI().toString());
		assertEquals("Bacillus subtilis",val.getValidationDescriptor().getTerms().iterator().next().getFormattedName());
		
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
