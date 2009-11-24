package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.inference.OWLReasoner;
import org.semanticweb.owlapi.inference.OWLReasonerException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.util.*;
import java.net.URI;
import java.io.File;
import java.io.IOException;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.*;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChange;

import javax.swing.*;
/*
 * Copyright (C) 2009, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 * Manages the current spread sheet, the current ontologies and validations
 */
public class WorkbookManager {

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
        workbook = WorkbookFactory.createWorkbook(uri);
        workbookURI = uri;
        // Extract validation
        ontologyTermValidationManager.readValidationFromWorkbook();
        fireWorkbookLoaded();
        return workbook;
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
                    System.out.println("Could not load ontology: " + e.getMessage());
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

    public OWLOntology loadOntology(URI physicalURI) throws OWLOntologyCreationException {
        this.ontology = manager.loadOntologyFromPhysicalURI(physicalURI);
        updateReasoner();
        setLabelRendering(true);
        fireOntologiesChanged();
        return ontology;
    }

    private void updateReasoner() {
        try {
            getReasoner().clearOntologies();
            getReasoner().loadOntologies(getLoadedOntologies());
        }
        catch (OWLReasonerException e) {
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
            this.reasoner = new StrictlyToldReasoner(manager, getLoadedOntologies());
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
                entities.addAll(ont.getReferencedEntities());
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
                System.out.println("Found validation: " + sheet.getName());
                sheets.add(sheet);
                NamedRange range = parser.parseNamedRange();
                if (range != null) {
                    System.out.println("Found named range associated with sheet: ");
                    System.out.println("\t" + range.getName());
                    System.out.println("\t" + range.getRange());
                }
            }

        }
        return sheets;
    }


}
