package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.semspreadsheets.change.SetCellValue;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChange;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalRepository;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.CellSelectionListener;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.CellSelectionModel;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.EntitySelectionModel;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookManagerEvent;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookManagerListener;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookState;

/** 
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class WorkbookManager {

	private static final Logger logger = Logger.getLogger(WorkbookManager.class);
    private Workbook workbook;      

    private URI workbookURI;
    
    private OWLOntologyManager owlManager;

    private OWLReasoner reasoner;
    public Map<OWLOntologyID,OWLReasoner> ontologyReasoners = new HashMap<OWLOntologyID, OWLReasoner>();
    
    private WorkbookState workbookState = new WorkbookState();

    private CellSelectionModel selectionModel;

    private EntitySelectionModel entitySelectionModel;

    private Set<WorkbookManagerListener> workbookManagerListeners = new HashSet<WorkbookManagerListener>();

    private OntologyTermValidationManager ontologyTermValidationManager;

    private BidirectionalShortFormProviderAdapter shortFormProvider;

    public WorkbookManager() {    	
        this.owlManager = OWLManager.createOWLOntologyManager();        
        owlManager.setSilentMissingImportsHandling(true);        
        shortFormProvider = new BidirectionalShortFormProviderAdapter(new SimpleShortFormProvider());
        entitySelectionModel = new EntitySelectionModel(owlManager.getOWLDataFactory().getOWLThing());
        ontologyTermValidationManager = new OntologyTermValidationManager(this);
        workbook = WorkbookFactory.createWorkbook();
        selectionModel = new CellSelectionModel();
        selectionModel.setSelectedRange(new Range(workbook.getSheet(0)));
        selectionModel.addCellSelectionListener(new CellSelectionListener() {
            public void selectionChanged(Range range) {
                handleCellSelectionChanged();
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

    public OntologyTermValidationManager getOntologyTermValidationManager() {
        return ontologyTermValidationManager;
    }

    public EntitySelectionModel getEntitySelectionModel() {
        return entitySelectionModel;
    }

    private List<WorkbookManagerListener> getCopyOfListeners() {
        return new ArrayList<WorkbookManagerListener>(workbookManagerListeners);
    }

    private void handleCellSelectionChanged() {
        Range range = selectionModel.getSelectedRange();
        OWLEntity selEnt = entitySelectionModel.getSelection();
        if (range.isCellSelection()) {
            for (OntologyTermValidation validation : ontologyTermValidationManager.getContainingValidations(range)) {
                OWLClass cls = getDataFactory().getOWLClass(validation.getValidationDescriptor().getEntityIRI());
                selEnt = cls;
                break;
            }
        }
        entitySelectionModel.setSelection(selEnt);
    }

    private void fireWorkbookCreated() {
        WorkbookManagerEvent event = new WorkbookManagerEvent(this);
        for (WorkbookManagerListener listener : getCopyOfListeners()) {
            try {
                listener.workbookCreated(event);
            }
            catch (Throwable e) {
                ErrorHandler.getErrorHandler().handleError(e);
            }
        }
    }
    
    private void fireValidationAppliedOrCancelled() {
    	for (WorkbookManagerListener listener : getCopyOfListeners()) {            
                listener.validationAppliedOrCancelled();            
        }
    }


    private void fireWorkbookLoaded() {
        WorkbookManagerEvent event = new WorkbookManagerEvent(this);
        for (WorkbookManagerListener listener : getCopyOfListeners()) {
            try {
                listener.workbookLoaded(event);
            }
            catch (Throwable e) {
                ErrorHandler.getErrorHandler().handleError(e);
            }
        }
    }

    private void fireOntologiesChanged() {
        WorkbookManagerEvent event = new WorkbookManagerEvent(this);
        for (WorkbookManagerListener listener : getCopyOfListeners()) {
            try {
                listener.ontologiesChanged(event);
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
    	List<WorkbookChangeListener> existingListeners = workbook.getAllChangeListeners();
    	workbook.clearChangeListeners();
        workbook = WorkbookFactory.createWorkbook();  
        for (WorkbookChangeListener l : existingListeners) {
        	workbook.addChangeListener(l);
        }
        workbookURI=null;
        getOntologyTermValidationManager().clearValidations();
        fireWorkbookCreated();
        getWorkbookState().changesSaved();
        return workbook;
    }

    public Workbook loadWorkbook(URI uri) throws IOException {
        try {
        	//need to preserve the listeners on the workbook
        	List<WorkbookChangeListener> existingListeners = workbook.getAllChangeListeners();
        	workbook.clearChangeListeners(); //to free it and allow it to be garbage collected
            workbook = WorkbookFactory.createWorkbook(uri);
            for (WorkbookChangeListener l : existingListeners) {
            	workbook.addChangeListener(l);
            }
            
            workbookURI = uri;
            // Extract validation
            getOntologyTermValidationManager().readValidationFromWorkbook();
            fireWorkbookLoaded();
            getWorkbookState().changesSaved();
            return workbook;
        }
        catch (IOException e) {
            throw new IOException("Could not open spreadsheet: " + e.getMessage());
        }
    }

    public void loadEmbeddedTermOntologies() {
        ontologyTermValidationManager.getOntologyIRIs();
        final Map<IRI, IRI> ontologyIRIMap = ontologyTermValidationManager.getOntology2PhysicalIRIMap();
        OWLOntologyIRIMapper mapper = new OntologyTermValdiationManagerMapper(ontologyTermValidationManager);
        owlManager.addIRIMapper(mapper);
        for(IRI iri : ontologyIRIMap.keySet()) {        	
            if(!owlManager.contains(iri)) {
                try {                	
                    owlManager.loadOntology(iri);
                }
                catch (OWLOntologyCreationException e) {
                	e.printStackTrace();
                	logger.error("Could not load ontology: " + e.getMessage());
                	logger.debug("Error reading ontology",e);
                }
            }
        }
        owlManager.removeIRIMapper(mapper);
        updateStructuralReasoner();
        setLabelRendering(true);
        fireOntologiesChanged();
    }

    public void loadWorkbook(File file) throws IOException {
        loadWorkbook(file.toURI());
    }

    public URI getWorkbookURI() {
        return workbookURI;
    }

    public void saveWorkbook(URI uri) throws IOException {
        // Insert validation
        ontologyTermValidationManager.writeValidationToWorkbook();
        workbook.saveAs(uri);        
        OntologyTermValidationWorkbookParser workbookParser = new OntologyTermValidationWorkbookParser(this);
        workbookParser.clearOntologyTermValidations();
        if (workbookURI == null || !uri.equals(workbookURI)) {
            // TODO: Fire workbook URI changed
            workbookURI = uri;
        }        
        getWorkbookState().changesSaved();
    }

    public void previewValidation() {
    	IRI iri = entitySelectionModel.getSelection().getIRI();
    	ValidationType type = entitySelectionModel.getValidationType();
    	Range range = new Range(workbook.getSheet(0));
    	ontologyTermValidationManager.previewValidation(range,type, iri);
	}	
    
    public void applyValidationChange() {
    	ValidationType type = entitySelectionModel.getValidationType();
    	IRI iri = entitySelectionModel.getSelection().getIRI();
    	logger.debug("Setting validation for IRI "+iri.toString()+", type "+type.toString());    			        
        
        Range selectedRange = getSelectionModel().getSelectedRange();
        if(!selectedRange.isCellSelection()) {
            return;
        }
        setValidationAt(selectedRange, type, iri);
        fireValidationAppliedOrCancelled();
    }      
    
    public void cancelValidationChange() {
    	Range selectedRange = getSelectionModel().getSelectedRange();
    	getSelectionModel().setSelectedRange(selectedRange);
    	fireValidationAppliedOrCancelled();
    }

    public void setValidationAt(Range range,ValidationType type, IRI entityIRI) {
    	Range rangeToApply;
        Collection<OntologyTermValidation> validations = ontologyTermValidationManager.getContainingValidations(range);
        
        if(validations.isEmpty()) {
            rangeToApply=range;
        }
        else {
            OntologyTermValidation validation = validations.iterator().next();
            rangeToApply=validation.getRange();            
        }
        String cellText=getRendering(owlManager.getOWLDataFactory().getOWLAnnotationProperty(entityIRI));
        if (type == ValidationType.NOVALIDATION) {
        	cellText = "";
        }
        
        ontologyTermValidationManager.setValidation(rangeToApply, type, entityIRI);
        
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
                logger.debug("Current cell colour is:"+cell.getBackgroundFill().toString());
                cell.setBackgroundFill(new Color(16777164)); //pale yellow                
            }
        }
    }
        
    
    /**
     * Determines whether the apply button should be enabled or not depending on if the validation setting differ from the cell.
     * @return the enabled state of the apply button
     */
    public boolean determineApplyButtonState() {
    	ValidationType type = entitySelectionModel.getValidationType();
    	IRI iri = entitySelectionModel.getSelection().getIRI();
    	Range selectedRange = getSelectionModel().getSelectedRange();
    	Collection<OntologyTermValidation> validations = ontologyTermValidationManager.getContainingValidations(selectedRange);
    	boolean result = false;
    	for (OntologyTermValidation validation : validations) {
    		OntologyTermValidationDescriptor validationDescriptor = validation.getValidationDescriptor();
    		if (!validationDescriptor.getEntityIRI().equals(iri) || !validationDescriptor.getType().equals(type)) {
    			result=true;
    			break;
    		}
    	}
    	if (validations.isEmpty()) {
    		result = type!=ValidationType.NOVALIDATION;
    	}
    	logger.debug("Apply button state deterimned as "+result+" for type "+type.toString()+" and IRI "+iri.toString());
    	return result;
    }
    
    
    public void removeValidations(Range range) {
    	if (getOntologyTermValidationManager().getContainingValidations(range).size()>0)
    	{
	    	getOntologyTermValidationManager().removeValidations(range);
	    	for(int col = range.getFromColumn(); col < range.getToColumn() + 1; col++) {
	            for(int row = range.getFromRow(); row < range.getToRow() + 1; row++) {
	                Cell cell = range.getSheet().getCellAt(col, row);
	                if (cell!=null) {
	                	//FIXME: the default colour may not white
	                	cell.setBackgroundFill(Color.WHITE);
	                }
	            }
	    	}
    	}
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  Creating, loading and saving ontologies
    ////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public OWLOntologyManager getOntologyManager() {
        return owlManager;
    }

    public void unloadOntology(IRI physicalIRI) {    	
    	OWLOntology loaded = null;
    	
    	for (OWLOntology ontology : getLoadedOntologies()) {    		
    		if (physicalIRI.equals(ontology.getOntologyID().getVersionIRI())) {    			
    			loaded = ontology;
    			break;
    		}
    	}    	
    	if (loaded != null) owlManager.removeOntology(loaded);
    }
    
    public OWLOntology loadOntology(IRI physicalIRI) throws OWLOntologyCreationException {
    	
    	OWLOntologyID newID = null;
    	IRI logIRI = null;
    	
        logger.info("Loading: " + physicalIRI);
        //See if an ontology with such ID had been loaded. If yes, unload it
        unloadOntology(physicalIRI);
                
    	OWLOntology ontology = owlManager.loadOntologyFromOntologyDocument(BioPortalRepository.handleBioPortalAPIKey(physicalIRI));
    	
    	logIRI = ontology.getOntologyID().getOntologyIRI();
    	//Create a new ID and use the physical IRI as a version ID        
        newID = new OWLOntologyID(logIRI,physicalIRI);        
        owlManager.applyChange(new SetOntologyID(ontology, newID));
        updateStructuralReasoner();
        updateStructuralReasoner(ontology);
        setLabelRendering(true);
        fireOntologiesChanged();        
        
        return ontology;
    }        
    
    public void removeOntology(OWLOntology ontology) {
    	owlManager.removeOntology(ontology);    	
    	fireOntologiesChanged();
    }

    public Set<OWLOntology> getLoadedOntologies() {
        return owlManager.getOntologies();
    }

    public CellSelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * Returns a StructuralReasoner that works over all loaded ontologies
     * @see #getStructuralReasoner(OWLOntology) for reasoners over individual ontologies
     * @return
     */
    public OWLReasoner getStructuralReasoner() {
        if (reasoner == null) {
            updateStructuralReasoner();
        }
        return reasoner;
    }
    
    /**
     * returns (and setsup if necessary) a StucturalReasoner that works over the given ontology only
     * @see #getStructuralReasoner()
     * @param ontology
     * @return
     */
    public OWLReasoner getStructuralReasoner(OWLOntology ontology) {
    	OWLReasoner reasoner = null;
    	synchronized (ontologyReasoners) {
    		reasoner = ontologyReasoners.get(ontology.getOntologyID());
    		if (reasoner==null) {
    			reasoner = updateStructuralReasoner(ontology);
    		}
		}
    	return reasoner;
    }

	private OWLReasoner updateStructuralReasoner(OWLOntology ontology) {
		OWLReasoner reasoner;
		reasoner = new StructuralReasoner(ontology, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
		reasoner.precomputeInferences();
		ontologyReasoners.put(ontology.getOntologyID(), reasoner);
		return reasoner;
	}
	
	private OWLReasoner updateStructuralReasoner() {    	
        try {        	
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();            
            OWLOntology root = man.createOntology(IRI.create("owlapi:reasoner"), getLoadedOntologies());
            reasoner = new StructuralReasoner(root, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
            reasoner.precomputeInferences();
        }
        catch (OWLOntologyCreationException e) {
            ErrorHandler.getErrorHandler().handleError(e);
        }
        return reasoner;
    }

    public Collection<OWLEntity> getEntitiesForShortForm(String shortForm) {
        Set<OWLEntity> result = new HashSet<OWLEntity>();
        for(String s : shortFormProvider.getShortForms()) {
            if(s.toLowerCase().contains(shortForm.toLowerCase())) {
                result.addAll(shortFormProvider.getEntities(s));
            }
        }
        return result;
    }

    public String getRendering(OWLObject object) {
        if (object instanceof OWLEntity) {
            return shortFormProvider.getShortForm((OWLEntity) object);
        }
        else {
            return object.toString();
        }
    }

    public void setLabelRendering(boolean b) {
        if(b) {
            IRI iri = OWLRDFVocabulary.RDFS_LABEL.getIRI();
            OWLAnnotationProperty prop = owlManager.getOWLDataFactory().getOWLAnnotationProperty(iri);
            List<OWLAnnotationProperty> props = new ArrayList<OWLAnnotationProperty>();
            props.add(prop);
            ShortFormProvider provider = new AnnotationValueShortFormProvider(props, new HashMap<OWLAnnotationProperty, List<String>>(), owlManager);
            shortFormProvider.dispose();
            shortFormProvider = new BidirectionalShortFormProviderAdapter(owlManager.getOntologies(), provider);
            final Set<OWLEntity> entities = new HashSet<OWLEntity>();
            for(OWLOntology ont : owlManager.getOntologies()) {
                entities.addAll(ont.getSignature());
            }
            shortFormProvider.rebuild(new OWLEntitySetProvider<OWLEntity>() {
                public Set<OWLEntity> getEntities() {
                    return entities;
                }
            });
        }
        else {
            ShortFormProvider provider = new SimpleShortFormProvider();
            shortFormProvider = new BidirectionalShortFormProviderAdapter(owlManager.getOntologies(), provider);
        }
    }

    public OWLDataFactory getDataFactory() {
        return owlManager.getOWLDataFactory();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  Validations
    ////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    public Collection<OntologyTermValidation> getOntologyTermValidations() {
        return ontologyTermValidationManager.getValidations();
    }

    public Collection<OntologyTermValidation> getIntersectingOntologyTermValidations(Range range) {
        return ontologyTermValidationManager.getIntersectingValidations(range);
    }

    public Collection<OntologyTermValidation> getContainingOntologyTermValidations(Range range) {
        return ontologyTermValidationManager.getContainingValidations(range);
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
			getOntologyTermValidationManager().removeValidations(sheet);
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

}
