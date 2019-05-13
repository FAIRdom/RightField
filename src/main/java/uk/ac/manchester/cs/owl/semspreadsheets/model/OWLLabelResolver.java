package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

public class OWLLabelResolver {

	private BidirectionalShortFormProviderAdapter shortFormProvider;

	// ensures thread safety getting instance
	private static class InstanceHolder {
		static final OWLLabelResolver intance = new OWLLabelResolver();
	}

	public static OWLLabelResolver getInstance() {
		return InstanceHolder.intance;
	}

	public synchronized Set<OWLEntity> fingMatchingOwlEntities(String label) {
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
		DefaultPrefixManager provider = new DefaultPrefixManager();
		for (OWLOntology ontology : owlOntologies) {
			OWLOntologyFormat format = ontologyManager.getOntologyFormat(ontology);
			if (format instanceof PrefixOWLOntologyFormat) {
				PrefixOWLOntologyFormat prefixFormat = (PrefixOWLOntologyFormat) format;
				for (String prefixName : prefixFormat.getPrefixName2PrefixMap().keySet()) {
					provider.setPrefix(prefixName, prefixFormat.getPrefix(prefixName));					
				}
			}
		}

		synchronized (this) {
			shortFormProvider.dispose();
			shortFormProvider = new BidirectionalShortFormProviderAdapter(owlOntologies, provider);
		}
	}

	private OWLLabelResolver() {
		shortFormProvider = new BidirectionalShortFormProviderAdapter(new SimpleShortFormProvider());
	}

}
