package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
public class OntologyTermValidationManager {

    private WorkbookManager workbookManager;

    private Set<OntologyTermValidation> ontologyTermValidations = new HashSet<OntologyTermValidation>();

    private List<OntologyTermValidationListener> listeners = new ArrayList<OntologyTermValidationListener>();


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
        OntologyTermValidationWorkbookParser parser = new OntologyTermValidationWorkbookParser(workbookManager);
        ontologyTermValidations.addAll(parser.readOntologyTermValidations());
        parser.clearOntologyTermValidations();

    }

    protected void writeValidationToWorkbook() {
        OntologyTermValidationWorkbookParser parser = new OntologyTermValidationWorkbookParser(workbookManager);
        parser.writeOntologyTermValidations(ontologyTermValidations);
    }

    public Collection<OntologyTermValidation> getValidations() {
        return new ArrayList<OntologyTermValidation>(ontologyTermValidations);
    }

    public void clearValidations() {
        if (!ontologyTermValidations.isEmpty()) {
            ontologyTermValidations.clear();
            fireValidationsChanged();
        }
    }

    public void clearValidation(Range range) {
        if (ontologyTermValidations.removeAll(getIntersectingValidations(range))) {
            fireValidationsChanged();
        }
    }

    public void addValidation(OntologyTermValidation validation) {
        if(ontologyTermValidations.add(validation)) {
            fireValidationsChanged();
        }
    }

    public void setValidation(Range range, ValidationType type, IRI entityIRI) {
        Collection<OntologyTermValidation> intersectingValidations = getIntersectingValidations(range);
        if (!type.equals(ValidationType.NOVALIDATION)) {
            // Add new validation
            OntologyTermValidationDescriptor descriptor = new OntologyTermValidationDescriptor(type, entityIRI, workbookManager);
            OntologyTermValidation validation = new OntologyTermValidation(descriptor, range);
            ontologyTermValidations.add(validation);
        }
        // Remove validation at intersecting ranges
        ontologyTermValidations.removeAll(intersectingValidations);
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
        for(OntologyTermValidationListener listener : new ArrayList<OntologyTermValidationListener>(listeners)) {
            try {
                listener.validationsChanged();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void removeValidation(Range selectedRange) {
        for(Iterator<OntologyTermValidation> it = ontologyTermValidations.iterator(); it.hasNext(); ) {
            OntologyTermValidation validation = it.next();
            if(validation.getRange().intersectsRange(selectedRange)) {
                it.remove();
            }
        }
        fireValidationsChanged();
    }
}
