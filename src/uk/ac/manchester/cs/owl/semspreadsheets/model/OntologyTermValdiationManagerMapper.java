package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 12-Nov-2009
 */
public class OntologyTermValdiationManagerMapper implements OWLOntologyIRIMapper {

    private OntologyTermValidationManager manager;

    public OntologyTermValdiationManagerMapper(OntologyTermValidationManager manager) {
        this.manager = manager;
    }

    public IRI getDocumentIRI(IRI ontologyIRI) {
        IRI physicalIRI = manager.getOntology2PhysicalIRIMap().get(ontologyIRI);
        if(physicalIRI == null) {
            return null;
        }
        else {
            return physicalIRI;
        }
    }
}
