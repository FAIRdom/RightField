package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class WorkbookManagerTest {
	
	WorkbookManager manager;
	DummyWorkbookManagerListener testListener;
	
	@Before
	public void setUp() {
		manager = new WorkbookManager();		
		testListener=new DummyWorkbookManagerListener();		
		manager.addListener(testListener);
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
		
		URI uri = workbookURI();
		manager.loadWorkbook(uri);
		manager.createNewWorkbook();
		assertNull(manager.getWorkbookURI());
	}
	
	@Test
	public void testLoadWorkbook() throws Exception {
		URI uri = workbookURI();
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
	public void testLoadOntology() throws Exception {
		URI uri = ontologyURI();
		assertEquals(0,manager.getLoadedOntologies().size());
		manager.loadOntology(IRI.create(uri));
		assertTrue(testListener.isOntologiesChanedFired());
		assertEquals(1,manager.getLoadedOntologies().size());
		assertEquals(manager.getOntologyManager().getOntologies(),manager.getLoadedOntologies());
	}
	
	@Test
	public void testRemoveOntology() throws Exception {
		URI uri = ontologyURI();
		manager.loadOntology(IRI.create(uri));		
		OWLOntology ont = manager.getLoadedOntologies().iterator().next();
		testListener.reset();
		manager.removeOntology(ont);		
		assertEquals(0,manager.getLoadedOntologies().size());		
	}
	
	@Test
	public void testInsertSheet() throws Exception {
		Sheet sheet = manager.addSheet();
		assertNotNull(sheet);
		assertEquals("Sheet1",sheet.getName());
		assertTrue(testListener.isWorkbookChangedFired());
		assertEquals(2,manager.getWorkbook().getSheets().size());
	}
	
	@Test
	public void testRemoveSheet() throws Exception {
		manager.deleteSheet("Sheet0");
		assertTrue(testListener.isWorkbookChangedFired());
		assertEquals(0,manager.getWorkbook().getSheets().size());
	}
	
	private URI ontologyURI() throws Exception {
		return WorkbookManagerTest.class.getResource("/JERM.owl").toURI();
	}
	
	private URI workbookURI() throws Exception {
		return WorkbookManagerTest.class.getResource("/simple_annotated_book.xls").toURI();
	}
}
