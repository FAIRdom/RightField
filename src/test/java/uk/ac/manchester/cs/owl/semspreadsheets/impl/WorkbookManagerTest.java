package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeListener;
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
		URI workbookURI = workbookURI();
		manager.loadWorkbook(workbookURI);
		Workbook newBook2 = manager.getWorkbook();
		assertNotSame(newBook, newBook2);
		assertTrue(newBook2.getAllChangeListeners().contains(l1));
		assertTrue(newBook2.getAllChangeListeners().contains(l2));
	}
	
	private URI ontologyURI() throws Exception {
		return WorkbookManagerTest.class.getResource("/JERM.owl").toURI();
	}
	
	private URI workbookURI() throws Exception {
		return WorkbookManagerTest.class.getResource("/simple_annotated_book.xls").toURI();
	}
}
