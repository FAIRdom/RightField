/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		assertEquals(expectedCSV3(),csv);
	}
	
	@Test
	public void testQuotesCommasAndSpaces() throws Exception {
		URI uri = bookWithQuotesAndCommasURI();
		Exporter exporter = new CSVExporter(uri);
		String csv = exporter.export();		
		assertEquals(expectedCSV4(),csv);
	}
	
	@Test
	public void testNumericAndStrings() throws Exception {
		URI uri = bookWithNumericsAndStringsURI();
		Exporter exporter = new CSVExporter(uri);
		String csv = exporter.export();		
		assertTrue(csv.contains("\"7.0\",4,1,\"Sheet0\",Text,None"));
		assertTrue(csv.contains("\"Policy\",0,0,\"Sheet0\",Text,None"));
		assertFalse("Shouldn't contain anything for the empty cell",csv.contains("0,1"));
	}
	
	protected abstract URI bookWithNumericsAndStringsURI() throws Exception;
	
	protected abstract URI twoOntologiesWorkbookURI() throws Exception;
	
	protected abstract URI bookWithPropertiesURI() throws Exception;
	
	protected abstract URI bookWithLiteralsURI() throws Exception;
	
	protected abstract URI bookWithQuotesAndCommasURI() throws Exception;
	
	private String expectedCSV() {
		String expected = "text,col,row,sheet,type,term uri,entity uri,property uri,ontology uri,ontology source\n";
		expected+="\"COSMIC\",2,3,\"Sheet0\",Direct instances,\"http://www.mygrid.org.uk/ontology/JERMOntology#COSMIC\",\"http://www.mygrid.org.uk/ontology/JERMOntology#Project\",\"None\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://rest.bioontology.org/bioportal/virtual/download/1488\"\n";
		expected+="\"mean and p values\",1,6,\"Sheet0\",Instances,\"http://mged.sourceforge.net/ontologies/MGEDOntology.owl#mean_and_p_values\",\"http://mged.sourceforge.net/ontologies/MGEDOntology.owl#DerivedBioAssayType\",\"None\",\"http://mged.sourceforge.net/ontologies/MGEDOntology.owl\",\"http://rest.bioontology.org/bioportal/virtual/download/1131\"\n";
		expected+="\"<- JERM\",3,3,\"Sheet0\",Text,None,None,None,None,None\n";
		expected+="\"<-MGED\",2,6,\"Sheet0\",Text,None,None,None,None,None";
		return expected;					
	}
	
	private String expectedCSV2() {
		String expected = "text,col,row,sheet,type,term uri,entity uri,property uri,ontology uri,ontology source\n";
		expected += "\"13C radiolabelling\",4,9,\"Sheet0\",Subclasses,\"http://www.mygrid.org.uk/ontology/JERMOntology#13C_radiolabelling\",\"http://www.mygrid.org.uk/ontology/JERMOntology#TechnologyType\",\"http://www.mygrid.org.uk/ontology/JERMOntology#hasType\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://www.mygrid.org.uk/ontology/JERMOntology\"\n";
		expected += "\"COSMIC\",4,7,\"Sheet0\",Instances,\"http://www.mygrid.org.uk/ontology/JERMOntology#COSMIC\",\"http://www.mygrid.org.uk/ontology/JERMOntology#Project\",\"http://www.mygrid.org.uk/ontology/JERMOntology#isAssociatedWith\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://www.mygrid.org.uk/ontology/JERMOntology\"\n";
		expected+="\"Project\",3,7,\"Sheet0\",Text,None,None,None,None,None\n";
		expected+="\"Techo Type\",3,9,\"Sheet0\",Text,None,None,None,None,None";
		return expected;
	}
	
	private String expectedCSV3() {
		String expected = "text,col,row,sheet,type,term uri,entity uri,property uri,ontology uri,ontology source\n";
		expected += "\"hello\",2,1,\"Sheet0\",Free text,\"None\",\"http://www.w3.org/2002/07/owl#Nothing\",\"http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://rest.bioontology.org/bioportal/virtual/download/1488\"\n";
		expected += "\"world\",3,4,\"Sheet0\",Free text,\"None\",\"http://www.w3.org/2002/07/owl#Nothing\",\"http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://rest.bioontology.org/bioportal/virtual/download/1488\"";
		
		return expected;
	}
	
	private String expectedCSV4() {
		String expected = "text,col,row,sheet,type,term uri,entity uri,property uri,ontology uri,ontology source\n";
		expected+="\"ScaRAB\",0,0,\"Sheet0\",Instances,\"http://www.mygrid.org.uk/ontology/JERMOntology#ScaRAB\",\"http://www.mygrid.org.uk/ontology/JERMOntology#Project\",\"http://www.mygrid.org.uk/ontology/JERMOntology#isAssociatedWith\",\"http://www.mygrid.org.uk/ontology/JERMOntology\",\"http://rest.bioontology.org/bioportal/virtual/download/1488\"\n";
		expected+="\" m \",3,2,\"Sheet0\",Text,None,None,None,None,None\n";
		expected+="\"a\"\"b\",1,3,\"Sheet0\",Text,None,None,None,None,None\n";
		expected+="\"a,b,c\",2,8,\"Sheet0\",Text,None,None,None,None,None";
		return expected;
	}

}
