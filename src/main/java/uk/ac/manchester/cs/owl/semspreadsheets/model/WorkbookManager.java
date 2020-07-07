/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.CellSelectionListener;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.WorkbookManagerListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.SetCellValue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.WorkbookChange;
import uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl.WorkbookXSSFImpl;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.CellSelectionModel;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFormat;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookState;
import uk.org.rightfield.RightField;

/** 
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class WorkbookManager {

	private static final Logger logger = Logger.getLogger(WorkbookManager.class);
    private Workbook workbook;      

    private URI workbookURI;  
    
    private OntologyManager ontologyManager;   
    
    private WorkbookState workbookState = new WorkbookState();

    private CellSelectionModel selectionModel;

    private EntitySelectionModel entitySelectionModel;

    private Set<WorkbookManagerListener> workbookManagerListeners = new HashSet<WorkbookManagerListener>();

    public WorkbookManager() {    	
        
        ontologyManager = new OntologyManager(this);
                                
        entitySelectionModel = new EntitySelectionModel(ontologyManager.getOWLOntologyManager().getOWLDataFactory().getOWLThing());        
        workbook = WorkbookFactory.createWorkbook();
        selectionModel = new CellSelectionModel();
        selectionModel.setSelectedRange(new Range(workbook.getSheet(0)));
        selectionModel.addCellSelectionListener(new CellSelectionListener() {
            public void selectionChanged(Range range) {
                handleCellSelectionChanged(range);
            }
        });
    }     

    public void applyChanges(List<? extends WorkbookChange> changes) {
        for(WorkbookChange change : changes) {
            ((MutableWorkbook) workbook).applyChange(change);
        }
    }

    public void applyChange(WorkbookChange change) {
        ((MutableWorkbook) workbook).applyChange(change);         
    }

    public void addListener(WorkbookManagerListener listener) {
        workbookManagerListeners.add(listener);        
    }

    public void removeListener(WorkbookManagerListener listener) {
        workbookManagerListeners.remove(listener);        
    }
    
    public EntitySelectionModel getEntitySelectionModel() {
        return entitySelectionModel;
    }    

    private List<WorkbookManagerListener> getCopyOfListeners() {
        return new ArrayList<WorkbookManagerListener>(workbookManagerListeners);
    }

    private void handleCellSelectionChanged(Range range) {        
        OWLEntity selEnt = getEntitySelectionModel().getSelectedEntity();
        if (range.isCellSelection()) {
            for (OntologyTermValidation validation : getOntologyManager().getContainingOntologyTermValidations(range)) {
                OWLClass cls = getOntologyManager().getDataFactory().getOWLClass(validation.getValidationDescriptor().getEntityIRI());
                selEnt = cls;
                break;
            }
        }
        getEntitySelectionModel().setSelectedEntity(selEnt);
    }

    private void fireWorkbookCreated() {  
    	List<WorkbookManagerListener> listeners = getCopyOfListeners();
    	logger.debug("firing workbookCreated to "+listeners.size()+" listeners");
        for (WorkbookManagerListener listener : listeners) {
            try {
                listener.workbookCreated();
            }
            catch (Throwable e) {
                ErrorHandler.getErrorHandler().handleError(e);
            }
        }
    }
    
    private void fireWorkbookSaved() { 
    	List<WorkbookManagerListener> listeners = getCopyOfListeners();
    	logger.debug("firing workbookSaved to "+listeners.size()+" listeners");
        for (WorkbookManagerListener listener : listeners) {
            listener.workbookSaved();            
        }
    }
    
    private void fireValidationAppliedOrCancelled() {
    	List<WorkbookManagerListener> listeners = getCopyOfListeners();
    	logger.debug("firing validationAppliedOrCancelled to "+listeners.size()+" listeners");
    	for (WorkbookManagerListener listener : listeners) {            
                listener.validationAppliedOrCancelled();            
        }
    }

    private void fireWorkbookLoaded() {        
    	List<WorkbookManagerListener> listeners = getCopyOfListeners();
    	logger.debug("firing workbookLoaded to "+listeners.size()+" listeners");
        for (WorkbookManagerListener listener : listeners) {
            try {
                listener.workbookLoaded();
            }
            catch (Throwable e) {
                ErrorHandler.getErrorHandler().handleError(e);
            }
        }
    }

    /**
     * Gets the primary workbook that this manager manager.
     * @return The primary workbook as managed by this manager
     */
    public Workbook getWorkbook() {
        return workbook;
    }
    
    public Workbook createNewWorkbook() {
    	return createNewWorkbook(WorkbookFormat.EXCEL97);
    }

    public Workbook createNewWorkbook(WorkbookFormat format) {
    	List<WorkbookChangeListener> existingListeners = workbook.getAllChangeListeners();
    	workbook.clearChangeListeners();
    	getOntologyManager().clearOntologyTermValidations();
    	OntologyTermValidationWorkbookParser.clearOriginalColours();
    	try {
    		workbook = WorkbookFactory.createWorkbook(format);  
    		for (WorkbookChangeListener l : existingListeners) {
    			workbook.addChangeListener(l);
    		}
    		workbookURI=null;
        
    		fireWorkbookCreated();
    	}
    	catch(Exception e) {
    		ErrorHandler.getErrorHandler().handleError(e);
    	}
    	getWorkbookState().changesSaved();
        return workbook;
    }

    public Workbook loadWorkbook(URI uri) throws IOException,InvalidWorkbookFormatException {
        try {
        	//need to preserve the listeners on the workbook
        	List<WorkbookChangeListener> existingListeners = workbook.getAllChangeListeners();
        	workbook.clearChangeListeners(); //to free it and allow it to be garbage collected
        	OntologyTermValidationWorkbookParser.clearOriginalColours();
            workbook = WorkbookFactory.createWorkbook(uri);
            logger.debug("Adding Workbook "+existingListeners.size()+" change listeners to new workbook instance");
            for (WorkbookChangeListener l : existingListeners) {
            	workbook.addChangeListener(l);
            }
            
            workbookURI = uri;
            
            // Extract validation
            logger.debug("About to read validations from workbook");
            getOntologyManager().getOntologyTermValidationManager().readValidationFromWorkbook();
            logger.debug(getOntologyManager().getOntologyTermValidations().size()+" validations after read");            
            fireWorkbookLoaded();            
            getWorkbookState().changesSaved();            
            return workbook;
        }
        catch (IOException e) {
        	if (e.getMessage().contains("Your InputStream was neither an OLE2 stream, nor an OOXML stream")) {
        		throw new InvalidWorkbookFormatException(e, uri);
        	}
        	else {
        		throw e;
        	}            
        }
    }    

    public void loadWorkbook(File file) throws IOException,InvalidWorkbookFormatException {
        loadWorkbook(file.toURI());
    }

    public URI getWorkbookURI() {
        return workbookURI;
    }

    public void saveWorkbook(URI uri) throws Exception {
        // Insert validation    	
    	getOntologyManager().getOntologyTermValidationManager().writeValidationToWorkbook();
    	appendRightFieldComment();
        workbook.saveAs(uri); 
        logger.debug(getOntologyManager().getOntologyTermValidations().size()+" validation recorded");
        
        //to get round a bug in POI - https://issues.apache.org/bugzilla/show_bug.cgi?id=46662
        //the internal workbook state is reloaded after a save, which mean the validations need refreshing according to the workbook
        if (workbook instanceof WorkbookXSSFImpl) {
        	logger.debug("XSSF workbook, reloaded so re-reading validation");        	        	      
        	loadWorkbook(uri);        	        
        }
        else {
        	OntologyTermValidationWorkbookParser workbookParser = new OntologyTermValidationWorkbookParser(this);
           	workbookParser.clearOntologyTermValidations();
           	if (workbookURI == null || !uri.equals(workbookURI)) {            
                workbookURI = uri;
            }            
        }    
        fireWorkbookSaved();
        getWorkbookState().changesSaved(); 
    }
    
    private void appendRightFieldComment() {
    	String comments = workbook.getComments();
    	if (comments==null) {
    		comments = "";
    	}
    	if (!comments.contains("Created by RightField")) {
    		if (comments.length()>0) {
    			comments = comments + "\n";
    		}
    		comments = comments + "Created by RightField (version "+RightField.getApplicationVersion()+")";
    		workbook.setComments(comments);
    	}
    }

    public void previewValidation() {
    	IRI iri = getEntitySelectionModel().getSelectedEntity().getIRI();
    	logger.debug("Entity IRI for preview: "+iri.toString());
    	ValidationType type = getEntitySelectionModel().getValidationType();
    	logger.debug("Type for preview: "+type.getEntityType());
    	OWLPropertyItem owlPropertyItem = getEntitySelectionModel().getOWLPropertyItem();
    	List<Term> terms = getEntitySelectionModel().getTerms();
    	if (terms == null) terms = type.getTerms(ontologyManager, iri);
    	Range range = new Range(workbook.getSheet(0));
    	
    	getOntologyManager().getOntologyTermValidationManager().previewValidation(range,type, iri,owlPropertyItem, terms);
	}	
    
    public void applyValidationChange() {
    	ValidationType type = entitySelectionModel.getValidationType();
    	IRI iri = entitySelectionModel.getSelectedEntity().getIRI();
    	OWLPropertyItem propertyItem = entitySelectionModel.getOWLPropertyItem();
    	List<Term> terms = entitySelectionModel.getTerms();
        if (terms == null) terms = type.getTerms(ontologyManager, iri);
    	logger.debug("Setting validation for IRI "+iri.toString()+", type "+type.toString()+", property "+propertyItem);
        
        Range selectedRange = getSelectionModel().getSelectedRange();
        if(!selectedRange.isCellSelection()) {
            return;
        }
        setValidationAt(selectedRange, type, iri,propertyItem, terms);
        fireValidationAppliedOrCancelled();
    }      
    
    public void cancelValidationChange() {
    	Range selectedRange = getSelectionModel().getSelectedRange();
    	getSelectionModel().setSelectedRange(selectedRange);
    	fireValidationAppliedOrCancelled();
    }

    public void setValidationAt(Range range,ValidationType type, IRI entityIRI, OWLPropertyItem property, List<Term> terms) {
    	Range rangeToApply;
        Collection<OntologyTermValidation> validations = getOntologyManager().getContainingOntologyTermValidations(range);
        
        if(validations.isEmpty()) {
            rangeToApply=range;
        }
        else {
            OntologyTermValidation validation = validations.iterator().next();
            rangeToApply=validation.getRange();            
        }
        OWLOntologyManager owlManager = getOntologyManager().getOWLOntologyManager();
        
        String cellText = "";
        if (type != ValidationType.FREETEXT) {
        	String shortFormName=getOntologyManager().getRendering(owlManager.getOWLDataFactory().getOWLAnnotationProperty(entityIRI));
            cellText = new Term(entityIRI, shortFormName).getFormattedName();
        }
        
        getOntologyManager().setOntologyTermValidation(rangeToApply, type, entityIRI, property, terms);
        
        for(int col = rangeToApply.getFromColumn(); col < rangeToApply.getToColumn() + 1; col++) {
            for(int row = rangeToApply.getFromRow(); row < rangeToApply.getToRow() + 1; row++) {
                Cell cell = rangeToApply.getSheet().getCellAt(col, row);
                if (cell == null) {                	
                	SetCellValue scv=new SetCellValue(rangeToApply.getSheet(),col,row,null,cellText);
                	applyChange(scv);
                	cell = rangeToApply.getSheet().getCellAt(col, row);
                }
                else {
                	SetCellValue scv=new SetCellValue(rangeToApply.getSheet(),col,row,"",cellText);
                	applyChange(scv);
                	cell = rangeToApply.getSheet().getCellAt(col, row);
                }                              
            }
        }
    }
        
    
    /**
     * Determines whether the apply button should be enabled or not depending on if the validation setting differ from the cell.
     * @return the enabled state of the apply button
     */
    public boolean determineApplyButtonState() {
    	ValidationType type = entitySelectionModel.getValidationType();
    	IRI iri = entitySelectionModel.getSelectedEntity().getIRI();
    	OWLPropertyItem property = entitySelectionModel.getOWLPropertyItem();
    	List<Term> terms = entitySelectionModel.getTerms();
        if (terms == null) terms = type.getTerms(ontologyManager, iri);
        Range selectedRange = getSelectionModel().getSelectedRange();
    	Collection<OntologyTermValidation> validations = getOntologyManager().getContainingOntologyTermValidations(selectedRange);
    	boolean result = false;
    	for (OntologyTermValidation validation : validations) {
    		OntologyTermValidationDescriptor validationDescriptor = validation.getValidationDescriptor();

    		//FIXME: shouldn't rely on OWLProperty being NULL - should have a NullPropertyItem type
            boolean propertyChanged=false;
    		if (validationDescriptor.getOWLPropertyItem()==null) {
    			propertyChanged = property!=null;
    		}
    		else {
    			propertyChanged = !validationDescriptor.getOWLPropertyItem().equals(property);
    		}

    		if (!validationDescriptor.getEntityIRI().equals(iri) ||
    				!validationDescriptor.getType().equals(type) ||
                    !validationDescriptor.getTerms().equals(terms) ||
    				propertyChanged) {
    			result=true;
    			break;
    		}
    	}
    	if (validations.isEmpty()) {
    		result = (type!=ValidationType.FREETEXT || property!=null);
    	}
    	logger.debug("Apply button state deterimned as "+result+" for type "+type.toString()+" and IRI "+iri.toString()+" and Property "+property);
    	return result;
    }
    
    
    public void removeValidations(Range range) {
    	if (getOntologyManager().getContainingOntologyTermValidations(range).size()>0)
    	{
    		getOntologyManager().remoteOntologyTermValidations(range);	    	
    	}
    }
    
    public OntologyManager getOntologyManager() {
    	return ontologyManager;
    }

    public CellSelectionModel getSelectionModel() {
        return selectionModel;
    }        

	public WorkbookState getWorkbookState() {
		return workbookState;
	}

	public Sheet addSheet() {
		Sheet sheet = getWorkbook().addSheet();		
		return sheet;
	}

	public void deleteSheet(String name) {
		//cleanup validations linked to this sheet
		Sheet sheet = getWorkbook().getSheet(name);
		if (sheet!=null) {
			getOntologyManager().remoteOntologyTermValidations(sheet);
		}
		else {
			logger.warn("Attempt to delete sheet with unrecognised name:"+name);
		}		
		getWorkbook().deleteSheet(name);				
	}

	public void renameSheet(String oldName, String newName) {
		Sheet sheet = getWorkbook().getSheet(oldName);
		if (sheet!=null) {
			sheet.setName(newName);
		}
	}	
	
	public String getWorkbookFileExtension() {
		if (getWorkbook() instanceof WorkbookXSSFImpl) {
			return "xlsx";
		}		
		else {
			return "xls";
		}
	}

}
