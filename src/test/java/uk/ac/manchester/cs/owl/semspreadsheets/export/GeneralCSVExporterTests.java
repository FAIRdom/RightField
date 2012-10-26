/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.export;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import org.junit.Test;

public abstract class GeneralCSVExporterTests {
	
		
	
	@Test
	public void testExportOutputStream() throws Exception {
		URI uri =  twoOntologiesWorkbookURI();
		Exporter exporter = new CSVExporter(uri);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		exporter.export(outStream);
		outStream.flush();
		String csv = outStream.toString();			
		assertEquals(expectedCSV(),csv);
	}
	
	@Test
	public void testExportString() throws Exception {	
		URI uri =  twoOntologiesWorkbookURI();
		Exporter exporter = new CSVExporter(uri);
		String csv = exporter.export();		
		assertEquals(expectedCSV(),csv);
	}
	
	@Test
	public void testExportWithProperties() throws Exception {
		URI uri = bookWithPropertiesURI();
		Exporter exporter = new CSVExporter(uri);
		String csv = exporter.export();				
		assertEquals(expectedCSV2(),csv);
		
	}
	
	@Test
	public void testExportWithLiterals() throws Exception {
		URI uri = bookWithLiteralsURI();
		Exporter exporter = new CSVExporter(uri);
		String csv = exporter.export();	
		System.out.println(csv);
		assertEquals(expectedCSV3(),csv);
	}
	
	protected abstract URI twoOntologiesWorkbookURI() throws Exception;
	
	protected abstract URI bookWithPropertiesURI() throws Exception;
	
	protected abstract URI bookWithLiteralsURI() throws Exception;
	
	private String expectedCSV() {
		String expected = "text, col, row, sheet, term uri, type, entity uri, ontology uri, ontology source, property uri\n";
		expected+="\"COSMIC\",3,2,\"Sheet0\",\"http://www.mygrid.org.uk/ontology/JERMOntology#COSMIC\",Direct instances,\"http://www.mygrid.org.uk/ontology/JERMOntology#Project\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://rest.bioontology.org/bioportal/virtual/download/1488\",\"None\"\n";
		expected+="\"mean and p values\",6,1,\"Sheet0\",\"http://mged.sourceforge.net/ontologies/MGEDOntology.owl#mean_and_p_values\",Instances,\"http://mged.sourceforge.net/ontologies/MGEDOntology.owl#DerivedBioAssayType\",\"http://mged.sourceforge.net/ontologies/MGEDOntology.owl\",\"http://rest.bioontology.org/bioportal/virtual/download/1131\",\"None\"";
		
		return expected;					
	}
	
	private String expectedCSV2() {
		String expected = "text, col, row, sheet, term uri, type, entity uri, ontology uri, ontology source, property uri\n";
		expected += "\"13C radiolabelling\",9,4,\"Sheet0\",\"http://www.mygrid.org.uk/ontology/JERMOntology#13C_radiolabelling\",Subclasses,\"http://www.mygrid.org.uk/ontology/JERMOntology#TechnologyType\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://www.mygrid.org.uk/ontology/JERMOntology#hasType\"\n";
		expected += "\"COSMIC\",7,4,\"Sheet0\",\"http://www.mygrid.org.uk/ontology/JERMOntology#COSMIC\",Instances,\"http://www.mygrid.org.uk/ontology/JERMOntology#Project\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://www.mygrid.org.uk/ontology/JERMOntology#isAssociatedWith\"";
		
		return expected;
	}
	
	private String expectedCSV3() {
		String expected = "text, col, row, sheet, term uri, type, entity uri, ontology uri, ontology source, property uri\n";
		expected += "\"hello\",1,2,\"Sheet0\",\"None\",Free text,\"http://www.w3.org/2002/07/owl#Nothing\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://rest.bioontology.org/bioportal/virtual/download/1488\",\"http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber\"\n";
		expected += "\"world\",4,3,\"Sheet0\",\"None\",Free text,\"http://www.w3.org/2002/07/owl#Nothing\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://rest.bioontology.org/bioportal/virtual/download/1488\",\"http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber\"";
		
		return expected;
	}

}
