/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.TestDocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyItem;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyType;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationDescriptor;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class WorkbookManagerTest {
	
	WorkbookManager manager;
	DummyWorkbookManagerListener testListener;
	DummyWorkbookChangeListener testChangeListener;
	
	
	@Before
	public void setUp() {
		manager = new WorkbookManager();		
		testListener=new DummyWorkbookManagerListener();
		testChangeListener=new DummyWorkbookChangeListener();
		manager.addListener(testListener);
		manager.getWorkbook().addChangeListener(testChangeListener);		
	}
	
	@Test
	public void testCreateNewWorkbook() throws Exception {		
		Workbook book=manager.getWorkbook();
		assertNull(manager.getWorkbookURI());
		assertNotNull(book);
		manager.getWorkbookState().changesUnsaved();
		Workbook book2=manager.createNewWorkbook();
		assertTrue(manager.getWorkbookState().isChangesSaved());
		assertTrue(testListener.isWorkbookChangedFired());
		assertNotSame(book2, book);
		assertSame(book2, manager.getWorkbook());
		assertNull(manager.getWorkbookURI());
		
		URI uri = TestDocumentsCatalogue.simpleAnnotatedworkbookURI();
		manager.loadWorkbook(uri);
		manager.createNewWorkbook();
		assertNull(manager.getWorkbookURI());
	}
	
	@Test
	public void testLoadWorkbook() throws Exception {
		URI uri = TestDocumentsCatalogue.simpleAnnotatedworkbookURI();
		manager.getWorkbookState().changesUnsaved();
		Workbook book = manager.loadWorkbook(uri);
		assertNotNull(book);
		assertTrue(manager.getWorkbookState().isChangesSaved());
		assertSame(book, manager.getWorkbook());
		assertNotNull(manager.getWorkbookURI());
		assertEquals(uri,manager.getWorkbookURI());
		assertTrue(testListener.isWorkbookLoadedFired());
	}	
	
	@Test
	public void testInsertSheet() throws Exception {
		Sheet sheet = manager.addSheet();
		assertNotNull(sheet);
		assertEquals("Sheet1",sheet.getName());
		assertTrue(testChangeListener.isSheetAddedFired());
		//FIXME: workbookChanged should also probably be fired		
		assertFalse(testChangeListener.isWorkbookChangedFired());
		assertFalse(testChangeListener.isSheetRemovedFired());
		assertFalse(testChangeListener.isSheetRenamedFired());
		assertEquals(2,manager.getWorkbook().getSheets().size());
	}
	
	@Test
	public void testRemoveSheet() throws Exception {
		manager.deleteSheet("Sheet0");		
		assertTrue(testChangeListener.isSheetRemovedFired());
		//FIXME: workbookChanged should also probably be fired
		assertFalse(testChangeListener.isWorkbookChangedFired());		
		assertFalse(testChangeListener.isSheetAddedFired());
		assertFalse(testChangeListener.isSheetRenamedFired());
		assertEquals(0,manager.getWorkbook().getSheets().size());
	}
	
	@Test
	public void testRenameSheet() throws Exception {
		manager.renameSheet("Sheet0", "fred");
		assertEquals(1,manager.getWorkbook().getSheets().size());
		assertEquals("fred",manager.getWorkbook().getSheet(0).getName());
		assertTrue(testChangeListener.isSheetRenamedFired());
		assertFalse(testChangeListener.isSheetRemovedFired());
		//FIXME: workbookChanged should also probably be fired
		assertFalse(testChangeListener.isWorkbookChangedFired());		
		assertFalse(testChangeListener.isSheetAddedFired());
	}
	
	@Test
	public void testGetOntologyTermValidations() throws Exception {
		WorkbookManager manager=new WorkbookManager();
		manager.loadWorkbook(TestDocumentsCatalogue.bookWithPropertiesURI());
		Collection<OntologyTermValidation> ontologyTermValidations = manager.getOntologyTermValidations();
		assertEquals(2,ontologyTermValidations.size());
		OntologyTermValidation selectedValidation=null;		
		for (OntologyTermValidation validation : ontologyTermValidations) {
			assertTrue(validation.getRange().toFixedAddress().equals("Sheet0!$E$8:$E$8") || validation.getRange().toFixedAddress().equals("Sheet0!$E$10:$E$10"));
			if (validation.getRange().toFixedAddress().equals("Sheet0!$E$8:$E$8")) {
				selectedValidation = validation;
			}
		}
		OntologyTermValidationDescriptor descriptor = selectedValidation.getValidationDescriptor();
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology",descriptor.getOntologyIRIs().iterator().next().toString());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#isAssociatedWith",descriptor.getOWLPropertyItem().getIRI().toString());
		assertEquals(OWLPropertyType.OBJECT_PROPERTY,descriptor.getOWLPropertyItem().getPropertyType());
	}
	
	@Test
	public void testWorkbookChangeListenersRetainedAferNewOrLoad() throws Exception {
		Workbook book = manager.getWorkbook();
		WorkbookChangeListener l1=new DummyWorkbookChangeListener();
		WorkbookChangeListener l2=new DummyWorkbookChangeListener();
		book.addChangeListener(l1);
		book.addChangeListener(l2);
		manager.createNewWorkbook();
		Workbook newBook = manager.getWorkbook();
		assertNotSame(book, newBook);
		assertTrue(newBook.getAllChangeListeners().contains(l1));
		assertTrue(newBook.getAllChangeListeners().contains(l2));
		URI workbookURI = TestDocumentsCatalogue.simpleAnnotatedworkbookURI();
		manager.loadWorkbook(workbookURI);
		Workbook newBook2 = manager.getWorkbook();
		assertNotSame(newBook, newBook2);
		assertTrue(newBook2.getAllChangeListeners().contains(l1));
		assertTrue(newBook2.getAllChangeListeners().contains(l2));
	}
	
	@Test
	public void testLoadOntology() throws Exception {
		URI uri = TestDocumentsCatalogue.jermOntologyURI();
		assertEquals(0,manager.getLoadedOntologies().size());
		manager.loadOntology(IRI.create(uri));
		assertTrue(testListener.isOntologiesChanedFired());
		assertEquals(1,manager.getLoadedOntologies().size());
		assertEquals(manager.getOntologyManager().getOntologies(),manager.getLoadedOntologies());
	}
	
	@Test
	public void testRemoveOntology() throws Exception {
		URI uri = TestDocumentsCatalogue.jermOntologyURI();
		manager.loadOntology(IRI.create(uri));		
		OWLOntology ont = manager.getLoadedOntologies().iterator().next();
		testListener.reset();
		manager.removeOntology(ont);		
		assertEquals(0,manager.getLoadedOntologies().size());		
	}
	
	@Test
	public void testGetDataProperties() throws Exception {
		URI uri = TestDocumentsCatalogue.jermOntologyURI();
		manager.loadOntology(IRI.create(uri));
		Set<OWLPropertyItem> dataProperties = manager.getOWLDataProperties();		
		assertEquals(19,dataProperties.size());
		boolean found=false;
		boolean shouldNotBeFound=false;
		for (OWLPropertyItem property : dataProperties) {
			assertEquals(OWLPropertyType.DATA_PROPERTY,property.getPropertyType());
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#External_supplier_ID")) {
				found=true;
			}
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#technologyUsedIn")) {
				shouldNotBeFound=true;
			}
		}
		assertTrue("Should have found #External_supplier_ID",found);
		assertFalse("Should not have found the #technologyUsedIn as this is an object property",shouldNotBeFound);
	}
	
	@Test
	public void testGetObjectProperties() throws Exception {
		URI uri = TestDocumentsCatalogue.jermOntologyURI();
		manager.loadOntology(IRI.create(uri));
		Set<OWLPropertyItem> objectProperties = manager.getOWLObjectProperties();
		System.out.println(objectProperties);
		assertEquals(18,objectProperties.size());
		boolean found=false;
		boolean shouldNotBeFound=false;
		for (OWLPropertyItem property : objectProperties) {	
			assertEquals(OWLPropertyType.OBJECT_PROPERTY,property.getPropertyType());
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#technologyUsedIn")) {
				found=true;
			}
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#External_supplier_ID")) {
				shouldNotBeFound=true;
			}
		}
		assertTrue("Should have found #technologyUsedIn",found);
		assertFalse("Should not have found #External_supplier_ID  as this is a data property",shouldNotBeFound);
	}
	
	@Test
	public void getAllOWLProperties() throws Exception {
		URI uri = TestDocumentsCatalogue.jermOntologyURI();
		manager.loadOntology(IRI.create(uri));
		Set<OWLPropertyItem> objectProperties = manager.getAllOWLProperties();
		assertEquals(37,objectProperties.size());
		boolean found=false;
		boolean found2=false;
		for (OWLPropertyItem property : objectProperties) {	
			assertTrue(property.getPropertyType().equals(OWLPropertyType.DATA_PROPERTY) || property.getPropertyType().equals(OWLPropertyType.OBJECT_PROPERTY));
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#technologyUsedIn")) {
				found=true;
			}
			if (property.getIRI().toString().equals("http://www.mygrid.org.uk/ontology/JERMOntology#External_supplier_ID")) {
				found2=true;
			}
		}
		assertTrue("Should have found #technologyUsedIn",found);
		assertTrue("Should have found #External_supplier_ID",found2);
	}
	
	
}
