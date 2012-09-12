/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.impl.InvalidWorkbookFormatException;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyType;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.ValidationType;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class AbstractExporterTest {		
	
	@Test
	public void testInitWithManager() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		manager.loadWorkbook(DocumentsCatalogue.populatedJermWorkbookURI());
		AbstractExporter exporter = new AbstractExporterTestImpl(manager);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
		assertEquals(9,exporter.getValidations().size());
	}

	@Test
	public void testInitiWithURI() throws Exception {
		URI uri = DocumentsCatalogue.populatedJermWorkbookURI();
		AbstractExporter exporter = new AbstractExporterTestImpl(uri);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
		assertEquals(9,exporter.getValidations().size());
	}
	
	@Test
	public void testInitiWithFile() throws Exception {
		File file = DocumentsCatalogue.populatedJermWorkbookFile();
		AbstractExporter exporter = new AbstractExporterTestImpl(file);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());	
		assertEquals(9,exporter.getValidations().size());
	}
	
	@Test
	public void getPopulatedValidatedCellDetails() throws Exception {
		URI uri = DocumentsCatalogue.partiallyPopulatedWorkbookURI();
		AbstractExporter exporter = new AbstractExporterTestImpl(uri);
		assertEquals(4,exporter.getPopulatedValidatedCellDetails().size());
		List<PopulatedValidatedCellDetails> list = new ArrayList<PopulatedValidatedCellDetails>(exporter.getPopulatedValidatedCellDetails());
		
		String [] textValues = new String [] {"Bacillus_subtilis","cell_size","concentration","dilution_rate"};
		int x=0;
		for (PopulatedValidatedCellDetails pop : list) {
			assertEquals(textValues[x],pop.getTextValue());
			assertFalse(pop.definesLiteral());
			x++;
		}
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#Bacillus_subtilis",list.get(0).getTerm().getIRI().toString());
		assertEquals("cell_size",list.get(1).getTerm().getName());		
		assertEquals(1,list.get(1).getCell().getColumn());
		assertEquals(10,list.get(1).getCell().getRow());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#concentration",list.get(2).getTerm().getIRI().toString());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#FactorsStudied",list.get(3).getEntityIRI().toString());
		assertEquals(1,list.get(2).getOntologyIRIs().size());
	}	
	
	@Test
	public void testPopulatedValidatedCellsForFreeTextProperties() throws Exception {
		URI uri = DocumentsCatalogue.simpleWorkbookWithLiteralsOverRangeURI();
		AbstractExporter exporter = new AbstractExporterTestImpl(uri);
		Collection<PopulatedValidatedCellDetails> cellDetails = exporter.getPopulatedValidatedCellDetails();
		assertEquals(2,cellDetails.size());
		String [] textValues = new String [] {"hello", "world"};
		int x=0;
		for (PopulatedValidatedCellDetails pop : cellDetails) {
			assertTrue(pop.definesLiteral());
			assertEquals(textValues[x],pop.getTextValue());
			assertEquals(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology#ECNumber"),pop.getOWLPropertyItem().getIRI());
			assertEquals(OWLPropertyType.DATA_PROPERTY,pop.getOWLPropertyItem().getPropertyType());
			x++;
		}
		
	}
	
	/**
	 * Uses a spreadsheet created with the latest RightField that does some formatting of terms (i.e. removing underscores and switching with spaces)
	 * and also contains a cell that contains the same text as an annotated cell, but isn't marked up with RightField (so that cell should not be returend).
	 * @throws Exception
	 */
	@Test
	public void getPopulatedValidatedCellDetails2() throws Exception {
		URI uri = AbstractExporterTest.class.getResource("/partially_populated_mged_book.xls").toURI();
		AbstractExporter exporter = new AbstractExporterTestImpl(uri);
		assertEquals(3,exporter.getPopulatedValidatedCellDetails().size());
		List<PopulatedValidatedCellDetails> list = new ArrayList<PopulatedValidatedCellDetails>(exporter.getPopulatedValidatedCellDetails());
		
		String [] textValues = new String [] {"BioAssayData","list of booleans","primary site"};
		int x=0;
		for (PopulatedValidatedCellDetails pop : list) {
			assertEquals(textValues[x],pop.getTextValue());
			assertFalse(pop.definesLiteral());
			x++;
		}
		assertEquals(ValidationType.SUBCLASSES,list.get(0).getValidation().getValidationDescriptor().getType());
		assertEquals(ValidationType.INDIVIDUALS,list.get(1).getValidation().getValidationDescriptor().getType());
		
		assertEquals("primary site",list.get(2).getTerm().getName());
		assertEquals("primary site",list.get(2).getTerm().getFormattedName());
		assertEquals("http://mged.sourceforge.net/ontologies/MGEDOntology.owl#primary_site",list.get(2).getTerm().getIRI().toString());		
		assertEquals("http://mged.sourceforge.net/ontologies/MGEDOntology.owl#CancerSite",list.get(2).getEntityIRI().toString());
		assertEquals(1,list.get(2).getOntologyIRIs().size());
		assertEquals("http://mged.sourceforge.net/ontologies/MGEDOntology.owl",list.get(2).getOntologyIRIs().iterator().next().toString());
		assertEquals("http://rest.bioontology.org/bioportal/virtual/download/1131",list.get(2).getPhysicalIRIs().iterator().next().toString());
	}
	
	
	
	/**
	 * Tests where 2 ontologies are used, and whether the terms correctly link to the correct ontology
	 * @throws Exception
	 */
	@Test
	public void getPopulatedValidatedCellDetails3() throws Exception {
		URI uri = AbstractExporterTest.class.getResource("/two_ontologies.xls").toURI();
		AbstractExporter exporter = new AbstractExporterTestImpl(uri);
		assertEquals(2,exporter.getPopulatedValidatedCellDetails().size());
		List<PopulatedValidatedCellDetails> list = new ArrayList<PopulatedValidatedCellDetails>(exporter.getPopulatedValidatedCellDetails());
		
		String [] textValues = new String [] {"COSMIC","mean and p values"};
		int x=0;
		for (PopulatedValidatedCellDetails pop : list) {
			assertEquals(textValues[x],pop.getTextValue());
			x++;
		}
		assertEquals(ValidationType.DIRECTINDIVIDUALS,list.get(0).getValidation().getValidationDescriptor().getType());
		assertEquals(ValidationType.INDIVIDUALS,list.get(1).getValidation().getValidationDescriptor().getType());
		assertEquals(1,list.get(0).getOntologyIRIs().size());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology",list.get(0).getOntologyIRIs().iterator().next().toString());
		assertEquals(1,list.get(1).getOntologyIRIs().size());
		assertEquals("http://mged.sourceforge.net/ontologies/MGEDOntology.owl",list.get(1).getOntologyIRIs().iterator().next().toString());
		assertEquals("http://rest.bioontology.org/bioportal/virtual/download/1131",list.get(1).getPhysicalIRIs().iterator().next().toString());
	}

	@Test
	public void testGetValidations() throws Exception {
		URI uri = DocumentsCatalogue.populatedJermWorkbookURI();
		AbstractExporter exporter = new AbstractExporterTestImpl(uri);
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
	
	@Test
	public void testExportString() throws Exception {
		Exporter exporter = new AbstractExporterTestImpl(DocumentsCatalogue.populatedJermWorkbookFile());
		assertEquals("this is a string",exporter.export());
	}	
	
	

	/**
	 * A concrete implementation of {@link AbstractExporter} for testing purposes only.
	 */
	private class AbstractExporterTestImpl extends AbstractExporter {
		public AbstractExporterTestImpl(WorkbookManager manager) {
			super(manager);			
		}

		public AbstractExporterTestImpl(File workbookFile) throws IOException,InvalidWorkbookFormatException {
			super(workbookFile);			
		}
		
		public AbstractExporterTestImpl(URI uri) throws IOException,InvalidWorkbookFormatException {
			super(uri);			
		}

		@Override
		public void export(OutputStream stream) {
			PrintWriter writer = new PrintWriter(stream);
			writer.write("this is a string");
			writer.flush();
		}					
	}
}
