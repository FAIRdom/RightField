package uk.ac.manchester.cs.owl.semspreadsheets.skos;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitor;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.vocab.BuiltInVocabulary;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skosapibinding.SKOSManager;

import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.skos.SKOSRDFVocabulary;

public class SkosMessing {

	@Test
	public void testOpeningSkos() throws Exception {
		SKOSManager manager = new SKOSManager();
		URI uri = DocumentsCatalogue.uriForResourceName("skos/skos-example.rdf");
		SKOSDataset dataset = manager.loadDataset(uri);
		System.out.println(dataset.getSKOSConcepts().size() + " concepts found");
		
	}
	
	@Test
	public void testOpeningSkosWithOntologyManager() throws Exception {
		URI uri = DocumentsCatalogue.uriForResourceName("skos/skos-example.rdf");
		WorkbookManager wm = new WorkbookManager();
		OntologyManager mgr = new OntologyManager(wm);
		OWLOntology ontology = mgr.loadOntology(uri);
		OWLClass owlClass = mgr.getOWLOntologyManager().getOWLDataFactory().getOWLClass(IRI.create(SKOSRDFVocabulary.CONCEPT.getURI()));
		Set<OWLClassAssertionAxiom> classAssertionAxioms = ontology.getClassAssertionAxioms(owlClass);
		for (OWLClassAssertionAxiom ax : classAssertionAxioms) {
			System.out.println(ax.getIndividual().asOWLNamedIndividual().getIRI());
		}
		
	}
	
}
