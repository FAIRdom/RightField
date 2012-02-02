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
	
	private URI workbookURI() throws Exception {
		return WorkbookManagerTest.class.getResource("/simple_annotated_book.xls").toURI();
	}
}
