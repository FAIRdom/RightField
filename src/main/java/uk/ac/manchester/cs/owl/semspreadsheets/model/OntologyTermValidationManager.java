/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.OntologyTermValidationListener;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class OntologyTermValidationManager {

	private static Logger logger = Logger.getLogger(OntologyTermValidationManager.class);

    private Set<OntologyTermValidation> ontologyTermValidations = new HashSet<OntologyTermValidation>();

    private Set<OntologyTermValidationListener> listeners = new HashSet<OntologyTermValidationListener>();

	private final WorkbookManager workbookManager;

    public OntologyTermValidationManager(WorkbookManager workbookManager) {
		this.workbookManager = workbookManager;
    }

    public void addListener(OntologyTermValidationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OntologyTermValidationListener listener) {
        listeners.remove(listener);
    }

    protected void readValidationFromWorkbook() {
        OntologyTermValidationWorkbookParser parser = new OntologyTermValidationWorkbookParser(getWorkbookManager());
        clearValidations();
        Collection<OntologyTermValidation> validations = parser.readOntologyTermValidations();
        ontologyTermValidations.addAll(validations);
        parser.clearOntologyTermValidations();
        fireValidationsChanged();
    }

    protected void writeValidationToWorkbook() {
        OntologyTermValidationWorkbookParser parser = new OntologyTermValidationWorkbookParser(getWorkbookManager());
        parser.writeOntologyTermValidations(ontologyTermValidations);
    }

    public Collection<OntologyTermValidation> getValidations() {
        return new ArrayList<OntologyTermValidation>(ontologyTermValidations);
    }

    public void clearValidations() {
    	logger.debug("About to clear "+ontologyTermValidations.size()+" validations");
        if (!ontologyTermValidations.isEmpty()) {
            ontologyTermValidations.clear();
            fireValidationsChanged();
        }
        logger.debug("Validations cleared");
    }

    public void clearValidation(Range range) {
        if (ontologyTermValidations.removeAll(getIntersectingValidations(range))) {
            fireValidationsChanged();
        }
    }

    public void previewValidation(Range range, ValidationType type, IRI entityIRI, OWLPropertyItem property, List<Term> terms) {
    	logger.debug("Previewing validation for iri "+entityIRI.toString()+", type "+type.toString()+", property = "+property);
    	List<OntologyTermValidation> previewList = new ArrayList<OntologyTermValidation>();

        Collection<OntologyTermValidation> intersectingValidations = getIntersectingValidations(range);
        previewList.removeAll(intersectingValidations);

        if (!type.equals(ValidationType.FREETEXT)) {
            // Add new validation        	
            OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(type, entityIRI,property, terms, getOntologyManager());
            OntologyTermValidation validation = new OntologyTermValidation(descriptor, range);
            previewList.add(validation);
        }
        fireOntologyTermSelected(previewList);
    }

    public void setValidation(Range range, ValidationType type, IRI entityIRI, OWLPropertyItem property, List<Term> terms) {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Setting validation at "+range.toFixedAddress()+" type:"+type.toString()+", IRI:"+entityIRI+", property:"+property);
    	}

        Collection<OntologyTermValidation> intersectingValidations = getIntersectingValidations(range);
        ontologyTermValidations.removeAll(intersectingValidations);

        if (!type.equals(ValidationType.FREETEXT)) {
            OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(type, entityIRI, property, terms,  getOntologyManager());
            OntologyTermValidation validation = new OntologyTermValidation(descriptor, range);
            ontologyTermValidations.add(validation);
        }
        else if (property!=null) {
        	OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(property,getOntologyManager());
            OntologyTermValidation validation = new OntologyTermValidation(descriptor, range);
            ontologyTermValidations.add(validation);
        }

        fireValidationsChanged();
    }

    public void removeValidation(OntologyTermValidation validation) {
        if(ontologyTermValidations.remove(validation)) {
            fireValidationsChanged();
        }
    }

    public Collection<OntologyTermValidation> getIntersectingValidations(Range range) {
         List<OntologyTermValidation> result = new ArrayList<OntologyTermValidation>();
        for(OntologyTermValidation validation : ontologyTermValidations) {
            if(validation.getRange().intersectsRange(range)) {
                result.add(validation);
            }
        }
        return result;
    }

    public Collection<OntologyTermValidation> getContainingValidations(Range range) {
        List<OntologyTermValidation> result = new ArrayList<OntologyTermValidation>();
        for(OntologyTermValidation validation : ontologyTermValidations) {
            if(validation.getRange().containsRange(range)) {
                result.add(validation);
            }
        }
        return result;
    }

    public Collection<OntologyTermValidation> getValidations(OntologyTermValidationDescriptor descriptor) {
        List<OntologyTermValidation> result = new ArrayList<OntologyTermValidation>();
        for(OntologyTermValidation validation : ontologyTermValidations) {
            if(validation.getValidationDescriptor().equals(descriptor)) {
                result.add(validation);
            }
        }
        return result;
    }

    public Collection<OntologyTermValidationDescriptor> getDescriptors() {
        Set<OntologyTermValidationDescriptor> result = new HashSet<OntologyTermValidationDescriptor>();
        for(OntologyTermValidation validation : ontologyTermValidations) {
            result.add(validation.getValidationDescriptor());
        }
        return result;
    }

    public Collection<IRI> getOntologyIRIs() {
        Set<IRI> result = new HashSet<IRI>();
        for(OntologyTermValidation validation : ontologyTermValidations) {
            result.addAll(validation.getValidationDescriptor().getOntologyIRIs());
        }
        return result;
    }

    /**
     * returns the physical IRI that the ontology was original retrieved from, according to the ontology IRI
     *
     * @param ontologyIRI
     * @return physicalIRI
     */
    public IRI getOntologyPhysicalIRI(IRI ontologyIRI) {
		return getOntology2PhysicalIRIMap().get(ontologyIRI);
	}

    public Map<IRI, IRI> getOntology2PhysicalIRIMap() {
        Map<IRI, IRI> result = new HashMap<IRI, IRI>();
        for(OntologyTermValidation validation : ontologyTermValidations) {
            for(IRI ontologyIRI : validation.getValidationDescriptor().getOntologyIRIs()) {
                result.put(ontologyIRI, validation.getValidationDescriptor().getPhysicalIRIForOntologyIRI(ontologyIRI));
            }
        }
        return result;
    }

    protected void fireValidationsChanged() {
    	logger.debug("firing validations changed to "+listeners.size()+" listeners");
        for(OntologyTermValidationListener listener : new ArrayList<OntologyTermValidationListener>(listeners)) {
            try {
                listener.validationsChanged();
            }
            catch (Throwable e) {
                logger.error("Error firing validation changed",e);
            }
        }
    }

    protected void fireOntologyTermSelected(List<OntologyTermValidation> previewList) {
        for(OntologyTermValidationListener listener : new ArrayList<OntologyTermValidationListener>(listeners)) {
            try {
                listener.ontologyTermSelected(previewList);
            }
            catch (Throwable e) {
            	logger.error("Error firing term set",e);
            }
        }
    }

    public void removeValidations(Range selectedRange) {
        for(Iterator<OntologyTermValidation> it = ontologyTermValidations.iterator(); it.hasNext(); ) {
            OntologyTermValidation validation = it.next();
            if(validation.getRange().intersectsRange(selectedRange)) {
                it.remove();
            }
        }
        fireValidationsChanged();
    }

    //removes validations that match a given sheet
    public void removeValidations(Sheet sheet) {
    	for(Iterator<OntologyTermValidation> it = ontologyTermValidations.iterator(); it.hasNext(); ) {
            OntologyTermValidation validation = it.next();
            if(validation.getRange().getSheet().equals(sheet)) {
                it.remove();
            }
        }
        fireValidationsChanged();
    }

    protected WorkbookManager getWorkbookManager() {
    	return workbookManager;
    }

    protected OntologyManager getOntologyManager() {
    	return getWorkbookManager().getOntologyManager();
    }


}
