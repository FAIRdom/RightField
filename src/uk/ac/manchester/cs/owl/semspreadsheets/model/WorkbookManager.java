package uk.ac.manchester.cs.owl.semspreadsheets.model;

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
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
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

import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChange;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.CellSelectionListener;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.CellSelectionModel;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.EntitySelectionModel;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookManagerEvent;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookManagerListener;

/**
 * Author: Matthew Horridge, Stuart Owen<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 * Manages the current spread sheet, the current ontologies and validations
 * 
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class WorkbookManager {

	private static final Logger logger = Logger.getLogger(WorkbookManager.class);
    private Workbook workbook;

    private URI workbookURI;

    private OWLOntologyManager manager;

    private OWLOntology ontology;

    private OWLReasoner reasoner;


    private CellSelectionModel selectionModel;

    private EntitySelectionModel entitySelectionModel;

    private List<WorkbookManagerListener> workbookManagerListeners = new ArrayList<WorkbookManagerListener>();

    private OntologyTermValidationManager ontologyTermValidationManager;

    private BidirectionalShortFormProviderAdapter shortFormProvider;

    public WorkbookManager() {
        this.manager = OWLManager.createOWLOntologyManager();
        manager.setSilentMissingImportsHandling(true);
        shortFormProvider = new BidirectionalShortFormProviderAdapter(new SimpleShortFormProvider());
        entitySelectionModel = new EntitySelectionModel(manager.getOWLDataFactory().getOWLThing());
        ontologyTermValidationManager = new OntologyTermValidationManager(this);
        workbook = WorkbookFactory.createWorkbook();
        selectionModel = new CellSelectionModel(this);
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
                listener.workbookChanged(event);
            }
            catch (Throwable e) {
                ErrorHandler.getErrorHandler().handleError(e);
            }
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
//        workbook.createNewSpreadSheet();
        workbook = WorkbookFactory.createWorkbook();
        fireWorkbookCreated();
        return workbook;
    }

    public Workbook loadWorkbook(URI uri) throws IOException {
        try {
            workbook = WorkbookFactory.createWorkbook(uri);
            workbookURI = uri;
            // Extract validation
            ontologyTermValidationManager.readValidationFromWorkbook();
            fireWorkbookLoaded();
            return workbook;
        }
        catch (IOException e) {
            throw new IOException("Could not open workbook: " + e.getMessage());
        }
    }

    public void loadEmbeddedTermOntologies() {
        ontologyTermValidationManager.getOntologyIRIs();
        final Map<IRI, IRI> ontologyIRIMap = ontologyTermValidationManager.getOntology2PhysicalIRIMap();
        OWLOntologyIRIMapper mapper = new OntologyTermValdiationManagerMapper(ontologyTermValidationManager);
        manager.addIRIMapper(mapper);
        for(IRI iri : ontologyIRIMap.keySet()) {
            if(!manager.contains(iri)) {
                try {
                    manager.loadOntology(iri);
                }
                catch (OWLOntologyCreationException e) {
                	logger.error("Could not load ontology: " + e.getMessage());
                	logger.debug("Error reading ontology",e);
                }
            }
        }
        manager.removeIRIMapper(mapper);
        updateReasoner();
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
    }

    public void setValidationType(ValidationType type) {
        setValidationType(type, entitySelectionModel.getSelection().getIRI());
    }

    /**
     * Sets the validation type for the currently selected cells.
     * @param type
     */
    public void setValidationType(ValidationType type, IRI entityIRI) {
        Range selectedRange = getSelectionModel().getSelectedRange();
        if(!selectedRange.isCellSelection()) {
            return;
        }
        Collection<OntologyTermValidation> validations = ontologyTermValidationManager.getContainingValidations(selectedRange);
        if(validations.isEmpty()) {
            ontologyTermValidationManager.setValidation(selectedRange, type, entityIRI);
        }
        else {
            OntologyTermValidation validation = validations.iterator().next();
            ontologyTermValidationManager.setValidation(validation.getRange(), type, entityIRI);
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
        return manager;
    }

    public OWLOntology loadOntology(IRI physicalIRI) throws OWLOntologyCreationException {
        logger.info("Loading: " + physicalIRI);
        this.ontology = manager.loadOntologyFromOntologyDocument(physicalIRI);
        updateReasoner();
        setLabelRendering(true);
        fireOntologiesChanged();
        return ontology;
    }

    private void updateReasoner() {
        try {
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology root = man.createOntology(IRI.create("owlapi:reasoner"), getLoadedOntologies());
            reasoner = new StructuralReasoner(root, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
            reasoner.prepareReasoner();
        }
        catch (OWLOntologyCreationException e) {
            ErrorHandler.getErrorHandler().handleError(e);
        }
    }

    public Set<OWLOntology> getLoadedOntologies() {
        return manager.getOntologies();
    }


    public CellSelectionModel getSelectionModel() {
        return selectionModel;
    }

    public OWLReasoner getReasoner() {
        if (reasoner == null) {
            updateReasoner();
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
            OWLAnnotationProperty prop = manager.getOWLDataFactory().getOWLAnnotationProperty(iri);
            List<OWLAnnotationProperty> props = new ArrayList<OWLAnnotationProperty>();
            props.add(prop);
            ShortFormProvider provider = new AnnotationValueShortFormProvider(props, new HashMap<OWLAnnotationProperty, List<String>>(), manager);
            shortFormProvider.dispose();
            shortFormProvider = new BidirectionalShortFormProviderAdapter(manager.getOntologies(), provider);
            final Set<OWLEntity> entities = new HashSet<OWLEntity>();
            for(OWLOntology ont : manager.getOntologies()) {
                entities.addAll(ont.getSignature());
            }
            shortFormProvider.rebuild(new OWLEntitySetProvider() {
                public Set<OWLEntity> getEntities() {
                    return entities;
                }
            });
        }
        else {
            ShortFormProvider provider = new SimpleShortFormProvider();
            shortFormProvider = new BidirectionalShortFormProviderAdapter(manager.getOntologies(), provider);
        }
    }

    public OWLDataFactory getDataFactory() {
        return manager.getOWLDataFactory();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  Validations
    ////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    public Collection<OntologyTermValidation> getOntologyTermValidation() {
        return ontologyTermValidationManager.getValidations();
    }

    public Collection<OntologyTermValidation> getIntersectingOntologyTermValidations(Range range) {
        return ontologyTermValidationManager.getIntersectingValidations(range);
    }

    public Collection<OntologyTermValidation> getContainingOntologyTermValidations(Range range) {
        return ontologyTermValidationManager.getContainingValidations(range);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  Internal stuff dealing with validations lists
    ////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Gets the sheets in the current workbook that are set up to be data validation sheets.  These sheets
     * have a specific format.  They describe the type of ontology term validation (subclasses of a class,
     * direct subclasses of a class, individuals of a class, direct individuals of a class), the ontology (IRI and
     * version IRI (optional), physical IRI) and the terms.  The sheet also has a named range associated with it
     * that is used to populate drop down boxes in Excel.
     * @return A list of sheets that conform the above description
     */
    private List<Sheet> getValidationSheets() {
        List<Sheet> sheets = new ArrayList<Sheet>();
        for (Sheet sheet : getWorkbook().getSheets()) {
            OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(this, sheet);
            if (parser.isValidationSheet()) {
                logger.info("Found validation: " + sheet.getName());
                sheets.add(sheet);
                NamedRange range = parser.parseNamedRange();
                if (range != null) {
                    logger.debug("Found named range associated with sheet: ");
                    logger.debug("\t" + range.getName());
                    logger.debug("\t" + range.getRange());
                }
            }

        }
        return sheets;
    }


}
