package uk.ac.manchester.cs.owl.semspreadsheets.repository;

import org.semanticweb.owlapi.model.IRI;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public interface RepositoryItem {

    String getHumanReadableName();

    IRI getOntologyIRI();

    IRI getVersionIRI();

    IRI getPhysicalIRI();
}
