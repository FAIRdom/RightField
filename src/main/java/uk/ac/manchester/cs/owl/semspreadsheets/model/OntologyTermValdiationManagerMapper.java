/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalRepository;

/**
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
