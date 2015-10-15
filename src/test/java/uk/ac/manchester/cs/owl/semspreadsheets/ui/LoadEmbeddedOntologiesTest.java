package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.DummyOntologyManagerListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class LoadEmbeddedOntologiesTest {
	
	private OntologyManager ontologyManager;
	private DummyOntologyManagerListener testListener;
	private WorkbookManager workbookManager;

	@Before
	public void createOntologyManager() {
		workbookManager = new WorkbookManager();
		ontologyManager = workbookManager.getOntologyManager();		
		testListener=new DummyOntologyManagerListener();		
		ontologyManager.addListener(testListener);
	}
	
	@Ignore("needs to be fixed to run headless for travis")
	@Test
	public void testLoadEmbeddedOntologies() throws Exception {
		LoadEmbeddedTermsOntologies task = new LoadEmbeddedTermsOntologies();
		workbookManager.loadWorkbook(DocumentsCatalogue.twoOntologiesWorkbookURI());
		assertEquals(0, ontologyManager.getLoadedOntologies().size());
		assertEquals(2,ontologyManager.getOntologyIRIs().size());
		assertTrue(ontologyManager.getOntologyIRIs().contains(IRI.create("http://www.mygrid.org.uk/ontology/JERMOntology")));
		assertTrue(ontologyManager.getOntologyIRIs().contains(IRI.create("http://mged.sourceforge.net/ontologies/MGEDOntology.owl")));
		
		task.setup(new WorkbookFrame(workbookManager));
		task.runTask();
		assertEquals(2, ontologyManager.getLoadedOntologies().size());
		assertEquals(3, ontologyManager.getAllOntologies().size());
		
		//now with just properties over free text		
		WorkbookManager manager = new WorkbookManager();
		manager.loadWorkbook(DocumentsCatalogue.simpleWorkbookWithLiteralsOverRangeURI());
		assertEquals(0, manager.getOntologyManager().getLoadedOntologies().size());
		assertEquals(0, manager.getOntologyManager().getAllOntologies().size());
		assertEquals(1,manager.getOntologyManager().getOntologyIRIs().size());
		task = new LoadEmbeddedTermsOntologies();
		
		task.setup(new WorkbookFrame(manager));
		task.runTask();
		
		assertEquals(1, manager.getOntologyManager().getLoadedOntologies().size());
		assertEquals(1, manager.getOntologyManager().getAllOntologies().size());
		
		
	}	

}
