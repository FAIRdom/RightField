package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.io.Serializable;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.skos.SKOSConcept;

@SuppressWarnings("serial")
public final class ValidationEntityType<E> implements Serializable {
	
	public static ValidationEntityType<OWLClass> CLASS = new ValidationEntityType<OWLClass>("Class");
	public static ValidationEntityType<OWLNamedIndividual> NAMED_INDIVIDUAL = new ValidationEntityType<OWLNamedIndividual>("Individual");
	public static ValidationEntityType<SKOSConcept> SKOS_CONCEPT = new ValidationEntityType<SKOSConcept>("SKOS concept");
	
	private final String name;	

	private ValidationEntityType(String name) {
		this.name = name;				
	}
	
	public String getName() {
		return name;
	}

}
