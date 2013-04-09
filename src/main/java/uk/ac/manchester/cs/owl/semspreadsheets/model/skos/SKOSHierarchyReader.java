package uk.ac.manchester.cs.owl.semspreadsheets.model.skos;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skosapibinding.SKOSManager;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;

/**
 * A simple reader to fetch the heirarchy of broader/narrower terms based upon the annotations.
 * This is a naive implementation reading the annotations directly, for use until problems getting the SKOSReasoner working
 * as expected are solved, afterwhich the internals can be replaced to use the Reasoner without affecting the rest of the integration of SKOS
 * 
 * @author Stuart Owen
 *
 */
public class SKOSHierarchyReader {
			
	private SKOSManager skosManager;
	private Map<URI,Set<SKOSConcept>> broader = new HashMap<URI, Set<SKOSConcept>>();
	private Map<URI,Set<SKOSConcept>> narrower = new HashMap<URI, Set<SKOSConcept>>();
	private final OWLOntology skosDocument;

	//takes the ontology manager and OWLOntology holding the skos.
	//although strictly speaking the SKOS isn't an OWL ontology, the SKOS api it built upon the OWL-API, and can be used together in this way
	public SKOSHierarchyReader(OntologyManager ontologyManager, OWLOntology skosDocument) {		
		this.skosDocument = skosDocument;
		skosManager = new SKOSManager(ontologyManager.getOWLOntologyManager());	
		buildGraph(skosDocument);
	}
	
	public Set<SKOSConcept> getTopConcepts() {
		Set<SKOSConcept> top = new SKOSHashSet();
		for (SKOSConcept concept : selectDataset(skosDocument).getSKOSConcepts()) {
			if (getBroaderThan(concept).isEmpty()) {
				top.add(concept);
			}
		}
		return top;
	}
	
	public SKOSConcept getSKOSConcept(URI uri) {
		return skosManager.getSKOSDataFactory().getSKOSConcept(uri);		
	}
	
	public Set<SKOSConcept> getBroaderThan(SKOSConcept concept) {
		Set<SKOSConcept> result = this.broader.get(concept.getURI());
		if (result == null) {
			result = new SKOSHashSet();
		}
		return result;
	}
	
	public Set<SKOSConcept> getNarrowerThan(SKOSConcept concept) {
		Set<SKOSConcept> result = this.narrower.get(concept.getURI());
		if (result == null) {
			result = new SKOSHashSet();			
		}
		return result;
	}
	
	private void buildGraph(OWLOntology ontology) {
		SKOSDataset dataset = selectDataset(ontology);
		for (SKOSConcept concept : dataset.getSKOSConcepts()) {
			Set<SKOSConcept> narrower = parseNarrower(concept,dataset);
			Set<SKOSConcept> broader = parseBroader(concept,dataset);
			
			addToBroader(concept.getURI(),broader);
			addToNarrower(concept.getURI(),narrower);
		}
	}
	
	private void addToNarrower(URI parentURI,Set<SKOSConcept> concepts) {
		for (SKOSConcept concept : concepts) {
			addToNarrower(parentURI,concept);
		}
	}
	
	private void addToBroader(URI parentURI,Set<SKOSConcept> concepts) {
		for (SKOSConcept concept : concepts) {
			addToBroader(parentURI,concept);
		}
	}
	
	private void addToNarrower(URI parentURI,SKOSConcept concept) {
		Set<SKOSConcept> set = narrower.get(parentURI);
		if (set==null) {
			set=new SKOSHashSet();
		}
		set.add(concept);
		narrower.put(parentURI, set);
				
		SKOSConcept parentConcept = getSKOSConcept(parentURI);
		if (!getBroaderThan(concept).contains(parentConcept)) {
			addToBroader(concept.getURI(),getSKOSConcept(parentURI));
		}
	}
	
	private void addToBroader(URI parentURI,SKOSConcept concept) {
		Set<SKOSConcept> set = broader.get(parentURI);
		if (set==null) {
			set=new SKOSHashSet();
		}
		set.add(concept);
		broader.put(parentURI, set);
		SKOSConcept parentConcept = getSKOSConcept(parentURI);
		if (!getNarrowerThan(concept).contains(parentConcept)) {
			addToNarrower(concept.getURI(),getSKOSConcept(parentURI));
		}		
	}
	
	private Set<SKOSConcept> parseNarrower(SKOSConcept concept,SKOSDataset dataset) {
		return annotationValuesByURI(concept,URI.create("http://www.w3.org/2004/02/skos/core#narrower"),dataset);
	}
	
	private Set<SKOSConcept> parseBroader(SKOSConcept concept,SKOSDataset dataset) {
		return annotationValuesByURI(concept,URI.create("http://www.w3.org/2004/02/skos/core#broader"),dataset);
	}
	
	private Set<SKOSConcept> annotationValuesByURI(SKOSConcept concept,URI annotation,SKOSDataset dataset) {
		Set<SKOSConcept> values = new HashSet<SKOSConcept>();
		for (SKOSAnnotation an : concept.getSKOSAnnotationsByURI(dataset, annotation)) {
			values.add(getSKOSConcept(an.getAnnotationValue().getURI()));
		}
		return values;
	}
	
	private SKOSDataset selectDataset(OWLOntology ontology) {
		SKOSDataset result = null;
		for (SKOSDataset dataset : skosManager.getSKOSDataSets()) {
			if (dataset.getURI().equals(ontology.getOntologyID().getOntologyIRI().toURI())) {
				result = dataset;
				break;
			}
		}
		return result;
	}
		
}

/**
 * Specialised HashSet, creating to specifically treat concepts with the same URI being regarded as equal
 * 
 * @author Stuart Owen
 *
 */
class SKOSHashSet extends HashSet<SKOSConcept> {
	
	private static final long serialVersionUID = -1115862678232624477L;

	SKOSHashSet() {
		super();
	}
	
	SKOSHashSet(Collection<SKOSConcept>c) {
		super(c);
	}
	
	@Override
	public boolean add(SKOSConcept e) {
		if (!contains(e)) {
			return super.add(e);
		}
		return false;
	}

	@Override
	public boolean contains(Object o) {
		if (super.contains(o)) {
			return true;
		}
		if (o==null) {
			return false;
		}
		if (!(o instanceof SKOSConcept)) {
			return false;
		}
		for (Iterator<SKOSConcept> i = iterator() ; i.hasNext() ;) {
			SKOSConcept c = i.next();
			if (c.getURI().equals(((SKOSConcept)o).getURI())) {
				return true;
			}
		}
		return false;
	}
	
	
}