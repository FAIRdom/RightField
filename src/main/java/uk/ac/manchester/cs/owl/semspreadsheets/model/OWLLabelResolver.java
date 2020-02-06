package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class OWLLabelResolver {

	private BidirectionalShortFormProviderAdapter shortFormProvider;

	// ensures thread safety getting instance
	private static class InstanceHolder {
		static final OWLLabelResolver intance = new OWLLabelResolver();
	}

	public static OWLLabelResolver getInstance() {
		return InstanceHolder.intance;
	}

	public synchronized Set<OWLEntity> findMatchingOwlEntities(String label) {
		label = label.toLowerCase();
		Set<OWLEntity> result = new HashSet<OWLEntity>();
		for (String shortForms : shortFormProvider.getShortForms()) {
			if (shortForms.toLowerCase().contains(label)) {
				result.addAll(shortFormProvider.getEntities(shortForms));
			}
		}
		return result;
	}

	public synchronized String getLabel(OWLEntity entity) {
		return shortFormProvider.getShortForm(entity);
	}

	public void update(Set<OWLOntology> owlOntologies, OWLOntologyManager ontologyManager) {

		OWLAnnotationProperty prop = ontologyManager.getOWLDataFactory()
				.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		List<OWLAnnotationProperty> props = new ArrayList<OWLAnnotationProperty>();
		props.add(prop);
		ShortFormProvider provider = new AnnotationValueShortFormProvider(props,
				new HashMap<OWLAnnotationProperty, List<String>>(), ontologyManager);

		synchronized (this) {
			shortFormProvider.dispose();
			shortFormProvider = new BidirectionalShortFormProviderAdapter(owlOntologies, provider);
		}
	}

	private OWLLabelResolver() {
		shortFormProvider = new BidirectionalShortFormProviderAdapter(new SimpleShortFormProvider());
	}

}
