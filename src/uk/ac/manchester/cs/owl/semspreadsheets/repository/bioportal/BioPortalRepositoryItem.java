package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public class BioPortalRepositoryItem implements RepositoryItem {

    private int ontologyID;

    private String humanReadableName;

    public BioPortalRepositoryItem(int ontologyID, String humanReadableName) {
        this.ontologyID = ontologyID;
        this.humanReadableName = humanReadableName;
    }

    public String getHumanReadableName() {
        return humanReadableName;
    }

    public IRI getOntologyIRI() {
        return IRI.create(BioPortalRepository.BASE + "virtual/download/" + ontologyID);
    }

    public IRI getVersionIRI() {
        return getOntologyIRI();
    }

    public IRI getPhysicalIRI() {
        return getOntologyIRI();
    }
    
}
