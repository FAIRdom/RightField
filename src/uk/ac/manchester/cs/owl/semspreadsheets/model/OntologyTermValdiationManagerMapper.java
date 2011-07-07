package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalRepository;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 12-Nov-2009
 * 
 * @author Matthew Horridge
 * @author Stuart Owen
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
            return BioPortalRepository.handleBioPortalAPIKey(physicalIRI);
        }
    }
}
