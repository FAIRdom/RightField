package uk.ac.manchester.cs.owl.semspreadsheets.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;

import uk.ac.manchester.cs.owl.semspreadsheets.DummyEntitySelectionModelListener;

public class EntitySelectionModelTest {
	
	private OWLEntity thing;
	private EntitySelectionModel model;
	private DummyEntitySelectionModelListener testListener;
	private OWLDataFactory dataFactory;

	@Before
	public void setup() {
		WorkbookManager wbManager = new WorkbookManager();
		dataFactory = wbManager.getOntologyManager().getDataFactory();
		thing = dataFactory.getOWLThing();
		model = new EntitySelectionModel(thing);
		testListener = new DummyEntitySelectionModelListener();
		model.addListener(testListener);
	}

	@Test
	public void testValidationTypeChanged() {
		model.setValidationType(ValidationType.INDIVIDUALS);
		assertTrue(testListener.isValidationTypeChanged());
		assertFalse(testListener.isOwlPropertyChanged());
		assertFalse(testListener.isSelectedEntityChanged());
		assertEquals(ValidationType.INDIVIDUALS,testListener.getSelectedValidationType());
		
		testListener.reset();
		model.setValidationType(ValidationType.INDIVIDUALS);
		assertFalse(testListener.isValidationTypeChanged());
		assertFalse(testListener.isOwlPropertyChanged());
		assertFalse(testListener.isSelectedEntityChanged());
		
		testListener.reset();
		model.setValidationType(ValidationType.SUBCLASSES);
		assertTrue(testListener.isValidationTypeChanged());
		assertFalse(testListener.isOwlPropertyChanged());
		assertFalse(testListener.isSelectedEntityChanged());
		assertEquals(ValidationType.SUBCLASSES,testListener.getSelectedValidationType());
		
		assertEquals(ValidationType.SUBCLASSES,model.getValidationType());	
		
		testListener.reset();
		model.setValidationType(null);
		assertTrue(testListener.isValidationTypeChanged());
	}
	
	@Test
	public void testPropertyItemChanged() {
		OWLPropertyItem item1 = new OWLPropertyItem(IRI.create("prop:item1"), OWLPropertyType.DATA_PROPERTY);
		OWLPropertyItem item2 = new OWLPropertyItem(IRI.create("prop:item2"), OWLPropertyType.DATA_PROPERTY);
		model.setOWLPropertyItem(item1);
		assertTrue(testListener.isOwlPropertyChanged());
		assertFalse(testListener.isSelectedEntityChanged());
		assertFalse(testListener.isValidationTypeChanged());
		assertEquals(item1,testListener.getSelectedProperty());
		
		testListener.reset();
		model.setOWLPropertyItem(item1);
		assertFalse(testListener.isOwlPropertyChanged());
		assertFalse(testListener.isSelectedEntityChanged());
		assertFalse(testListener.isValidationTypeChanged());		
		
		testListener.reset();
		model.setOWLPropertyItem(item2);
		assertTrue(testListener.isOwlPropertyChanged());
		assertFalse(testListener.isSelectedEntityChanged());
		assertFalse(testListener.isValidationTypeChanged());
		assertEquals(item2,testListener.getSelectedProperty());
		
		assertEquals(item2,model.getOWLPropertyItem());
		
		testListener.reset();
		model.setOWLPropertyItem(null);
		assertTrue(testListener.isOwlPropertyChanged());
	}
	
	@Test
	public void testOWLEntityChanged() {
		
		OWLEntity entity1 = dataFactory.getOWLClass(IRI.create("class:entity1"));
		OWLEntity entity2 = dataFactory.getOWLClass(IRI.create("class:entity2"));
		
		model.setSelectedEntity(entity1);
		assertFalse(testListener.isOwlPropertyChanged());
		assertTrue(testListener.isSelectedEntityChanged());
		assertFalse(testListener.isValidationTypeChanged());
		assertEquals(entity1,testListener.getSelectedEntity());
		
		testListener.reset();
		model.setSelectedEntity(entity1);
		assertFalse(testListener.isSelectedEntityChanged());
		assertFalse(testListener.isOwlPropertyChanged());		
		assertFalse(testListener.isValidationTypeChanged());		
		
		testListener.reset();
		model.setSelectedEntity(entity2);
		assertTrue(testListener.isSelectedEntityChanged());
		assertFalse(testListener.isOwlPropertyChanged());		
		assertFalse(testListener.isValidationTypeChanged());
		assertEquals(entity2,testListener.getSelectedEntity());
		
		assertEquals(entity2,model.getSelectedEntity());
		
		testListener.reset();
		model.setSelectedEntity(null);
		assertTrue(testListener.isSelectedEntityChanged());
	}

}
