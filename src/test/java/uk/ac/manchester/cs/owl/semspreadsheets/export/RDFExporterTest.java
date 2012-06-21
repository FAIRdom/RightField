package uk.ac.manchester.cs.owl.semspreadsheets.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.util.List;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class RDFExporterTest {
	
	private static String rootID="http://files/data/1";	
	
	@Test
	public void testInitWithManager() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		manager.loadWorkbook(DocumentsCatalogue.populatedJermWorkbookURI());
		AbstractExporter exporter = new RDFExporter(manager,rootID);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
		assertEquals(9,exporter.getValidations().size());
	}

	@Test
	public void testInitiWithURI() throws Exception {
		URI uri = DocumentsCatalogue.populatedJermWorkbookURI();
		AbstractExporter exporter = new RDFExporter(uri,rootID);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
		assertEquals(9,exporter.getValidations().size());
	}
	
	@Test
	public void testInitiWithFile() throws Exception {
		File file = DocumentsCatalogue.populatedJermWorkbookFile();
		AbstractExporter exporter = new RDFExporter(file,rootID);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());	
		assertEquals(9,exporter.getValidations().size());
	}

	@Test
	public void testExport() throws Exception {
		URI uri = DocumentsCatalogue.bookWithPropertiesURI();
		Exporter exp = new RDFExporter(uri,rootID);
		String rdf = exp.export();
		
		Model model = ModelFactory.createDefaultModel();
		StringReader reader = new StringReader(rdf);
		
		model.read(reader, "");		
		Resource r = model.getResource(rootID);
		assertNotNull(r);
		assertEquals(rootID,r.getURI());
		List<Statement> statements = r.listProperties().toList();
		assertEquals(2,statements.size());
		
		assertEquals(rootID,statements.get(0).getSubject().getURI());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#hasType",statements.get(0).getPredicate().getURI());
		assertTrue(statements.get(0).getObject().isResource());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#13C_radiolabelling",statements.get(0).getObject().asResource().getURI());
		
		
		assertEquals(rootID,statements.get(1).getSubject().getURI());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#isAssociatedWith",statements.get(1).getPredicate().getURI());
		assertTrue(statements.get(1).getObject().isResource());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#COSMIC",statements.get(1).getObject().asResource().getURI());						
	}
	
	@Test
	public void testExportWithNoProperties() throws Exception {
		URI uri = DocumentsCatalogue.populatedJermWorkbookURI();
		Exporter exp = new RDFExporter(uri,rootID);
		String rdf = exp.export();		
		Model model = ModelFactory.createDefaultModel();
		StringReader reader = new StringReader(rdf);
		
		model.read(reader, "");		
		Resource r = model.getResource(rootID);
		assertNotNull(r);
		assertEquals(rootID,r.getURI());
		List<Statement> statements = r.listProperties().toList();
		assertEquals(6,statements.size());
		
		assertEquals(rootID,statements.get(0).getSubject().getURI());
		assertEquals(RDFExporter.DEFAULT_PROPERTY_URI,statements.get(0).getPredicate().getURI());
		assertTrue(statements.get(0).getObject().isResource());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#Enterococcus_faecalis",statements.get(0).getObject().asResource().getURI());
	}
	
	@Test
	public void testExportWithLiterals() throws Exception {
		URI uri = DocumentsCatalogue.simpleWorkbookWithLiteralsOverRangeURI();
		Exporter exp = new RDFExporter(uri,rootID);
		String rdf = exp.export();
				
		Model model = ModelFactory.createDefaultModel();
		StringReader reader = new StringReader(rdf);
		
		model.read(reader, "");		
		Resource r = model.getResource(rootID);
		assertNotNull(r);
		assertEquals(rootID,r.getURI());
		List<Statement> statements = r.listProperties().toList();
		assertEquals(2,statements.size());
		
		assertEquals(rootID,statements.get(0).getSubject().getURI());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber",statements.get(0).getPredicate().getURI());
		assertTrue(statements.get(0).getObject().isLiteral());
		assertEquals("hello",statements.get(0).getObject().asLiteral().getValue());
		
		
		assertEquals(rootID,statements.get(1).getSubject().getURI());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber",statements.get(1).getPredicate().getURI());
		assertTrue(statements.get(1).getObject().isLiteral());
		assertEquals("world",statements.get(1).getObject().asLiteral().getValue());
	}
	
	

}
