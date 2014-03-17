/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.AbstractTask;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class LoadEmbeddedTermsOntologies extends AbstractTask<Object,RuntimeException> {
	
	private static final Logger logger = Logger.getLogger(LoadEmbeddedTermsOntologies.class);

    public Object runTask() throws RuntimeException {
        loadEmbeddedTermOntologies();
        return null;
    }
    
    public void loadEmbeddedTermOntologies()  {
    	OntologyTermValidationManager ontologyTermValidationManager = getOntologyManager().getOntologyTermValidationManager();
    	Collection<IRI> ontologyIRIs = ontologyTermValidationManager.getOntologyIRIs();
    	setLength(ontologyIRIs.size());
    	setProgress(0);
        for(IRI ontologyIRI : ontologyIRIs) {        	
            if(!getOntologyManager().isOntologyLoaded(ontologyIRI)) {            	
            	IRI sourceIRI = ontologyTermValidationManager.getOntologyPhysicalIRI(ontologyIRI);
            	if (sourceIRI==null) {
            		sourceIRI=ontologyIRI; //if the physical IRI cannot be found, that as a last resort try the ontology IRI
            	}
                try {
                	logger.info("Loading embedded ontology from source: "+sourceIRI.toString());
                	getOntologyManager().loadOntology(sourceIRI);
				} catch (OWLOntologyCreationException e) {
					if (sourceIRI.equals(ontologyIRI)) {
						ErrorHandler.getErrorHandler().handleError(e,sourceIRI);
					}
					else {
						try {
							logger.info("Unable to load ontology from source: "+sourceIRI.toString()+", using ontology IRI: "+ontologyIRI.toString());
							getOntologyManager().loadOntology(ontologyIRI);
						} catch (OWLOntologyCreationException e1) {
							ErrorHandler.getErrorHandler().handleError(e,ontologyIRI);
						}
					}					
				}				
            }
            setProgress(getProgress()+1);
        }                
    }

    public String getTitle() {
        return "Loading embedded ontologies";
    }
}
