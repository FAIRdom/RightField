/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.apache.log4j.Logger;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

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

    private Set<String> linkedCellsSet = new HashSet<String>();
    private CellReference cellTempRef = null;

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
    public void clearLinkingProcess()
    {
        cellTempRef = null;
    }
    public String getLinkString()
    {
        String sheetName = getSelectionModel().getSelectedRange().getSheet().getName();
        Range selectedRange = getSelectionModel().getSelectedRange();
        if(!selectedRange.isCellSelection()) {
            return "No link";
        }
        String from = getCellString(selectedRange.getFromColumn(),selectedRange.getFromRow());
        String to = getCellString(selectedRange.getToColumn(),selectedRange.getToRow());
        if(selectedRange.getFromColumn() == selectedRange.getToColumn() && selectedRange.getFromRow() == selectedRange.getToRow())
        {
            CellReference rangeCF = new CellReference(sheetName, selectedRange.getFromRow(), selectedRange.getFromColumn(), false, false);
            for(String tempString : linkedCellsSet)
            {
                String[] test = tempString.split(",");
                CellReference fromCF = new CellReference(test[0]);
                CellReference toCF = new CellReference(test[1]);
                Boolean isTable = test[2].equals("table");

                if(isTable)
                {
                    int rowOffset = getAreaFirstColumn(toCF).getAllReferencedCells().length;
                    int colOffset = getAreaFirstColumnNoOfColumns(toCF);

                    String tableFrom = getCellString(toCF.getCol(), toCF.getRow());
                    String tableTo = getCellString(toCF.getCol()+colOffset-1, toCF.getRow()+rowOffset-1);

                    if(test[0].equals(rangeCF.formatAsString()) && fromCF.getSheetName().equals(sheetName))
                    {

                        return fromCF.getSheetName() +"!" + getCellString(fromCF.getCol(), fromCF.getRow()) + " linked to " + toCF.getSheetName() + "!" + tableFrom + ":" + tableTo;
                    }
                    //if(test[1].equals(rangeCF.formatAsString()))
                    if(toCF.getRow() <= rangeCF.getRow() && rangeCF.getRow() < toCF.getRow()+rowOffset && toCF.getCol() <= rangeCF.getCol() && rangeCF.getCol() < toCF.getCol()+colOffset && toCF.getSheetName().equals(sheetName))
                    {
                        return fromCF.getSheetName() + "!" + getCellString(fromCF.getCol(), fromCF.getRow()) + " linked to " + toCF.getSheetName() +  "!" + tableFrom + ":" + tableTo;
                    }
                }
                else
                {
                    if(test[0].equals(rangeCF.formatAsString()) || test[1].equals(rangeCF.formatAsString()))
                    {
                        return fromCF.getSheetName() + "!" + getCellString(fromCF.getCol(), fromCF.getRow()) + " linked to " + toCF.getSheetName() + "!" + getCellString(toCF.getCol(), toCF.getRow());
                    }
                }

            }
        }

        return "No link";
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

            List<Sheet> sheets = workbook.getSheets();
            Sheet linkedCellsSheet = null;
            for(int i = 0 ; i < sheets.size(); i++)
            {
                //String temp = sheets.get(i).getName();
                if(sheets.get(i).getName().equals("LinkedCells"))
                {
                    linkedCellsSheet = sheets.get(i);
                    break;
                }
            }

            linkedCellsSet.clear();
            if(linkedCellsSheet != null)
            {

                int indexSheet = Integer.parseInt(linkedCellsSheet.getCellAt(0,0).getValue());
                for(int i = 0; i < indexSheet; i++)
                {
                    String from = linkedCellsSheet.getCellAt(1, i+1).getValue().replaceAll("\\$", "");
                    String to = linkedCellsSheet.getCellAt(2, i+1).getValue().replaceAll("\\$", "");
                    String tableOrCell = linkedCellsSheet.getCellAt(0, i+1).getValue();

                    linkedCellsSet.add(from + "," + to + "," + tableOrCell);
                }
            }
            workbook.deleteSheet("LinkedCells");
            return workbook;
        }
        catch (IOException e) {
            throw new IOException("Could not open spreadsheet: " + e.getMessage());
        }
    }

    public void loadWorkbook(File file) throws IOException,InvalidWorkbookFormatException {
        loadWorkbook(file.toURI());
    }

    public URI getWorkbookURI() {
        return workbookURI;
    }

    public void saveWorkbook(URI uri) throws Exception {


        List<Sheet> sheets = workbook.getSheets();
        Sheet sheet = null;
        for(int i = 0 ; i < sheets.size(); i++)
        {
            if(sheets.get(i).getName().equals("LinkedCells"))
            {
                sheet = sheets.get(i);
                break;
            }
        }

        if(sheet != null)
        {
            workbook.deleteSheet("LinkedCells");

        }
        if(linkedCellsSet.size() != 0)
        {
            sheet = workbook.addSheet("LinkedCells");

            //set true to hide the spreadsheet
            sheet.setVeryHidden(true);

            sheet.addCellAt(0,0).setValue(linkedCellsSet.size() + "");

            int index = 1;
            for(String link : linkedCellsSet)
            {
                String fromCell = link.split(",")[0];
                String toCell = link.split(",")[1];
                String tableOrCell = link.split(",")[2];

                CellReference fromCF = new CellReference(fromCell);
                CellReference toCF = new CellReference(toCell);


                String fromCellFormula = "" + fromCF.getSheetName() + "!$" + (char)('A' + fromCF.getCol()) + "$" + (fromCF.getRow()+1);
                String toCellFormula = "" + toCF.getSheetName() + "!$" + (char)('A' + toCF.getCol()) + "$" + (toCF.getRow()+1);

                sheet.addCellAt(0, index).setValue(tableOrCell);
                sheet.addCellAt(1,index).setCellFormula(fromCellFormula);
                sheet.addCellAt(2,index).setCellFormula(toCellFormula);

                index++;
            }
        }
        //save linked cell set


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
    	Range range = new Range(workbook.getSheet(0));

    	getOntologyManager().getOntologyTermValidationManager().previewValidation(range,type, iri,owlPropertyItem);
	}

    public AreaReference getAreaFirstColumn(CellReference cell)
    {
        List<Sheet> sheets = workbook.getSheets();
        AreaReference tableArea;
        Sheet sheet = null;
        for(int i = 0 ; i < sheets.size(); i++)
        {
            if(sheets.get(i).getName().equals(cell.getSheetName()))
            {
                sheet = sheets.get(i);
                break;
            }
        }
        boolean findNextRow = true;
        int colIndex = cell.getCol();
        int rowIndex = cell.getRow();
        while(findNextRow)
        {
            Cell temp = sheet.getCellAt(cell.getCol(), rowIndex);
            if(temp == null || temp.equals(""))
            {
                findNextRow = false;
                rowIndex--;
            }
            else
            {
                rowIndex++;
            }
        }
        CellReference endCell = new CellReference(cell.getSheetName(), rowIndex, colIndex, false, false);
        tableArea = new AreaReference(cell, endCell);


        return tableArea;
    }
    public int getAreaFirstColumnNoOfColumns(CellReference cell)
    {
        List<Sheet> sheets = workbook.getSheets();
        Sheet sheet = null;
        for(int i = 0 ; i < sheets.size(); i++)
        {
            if(sheets.get(i).getName().equals(cell.getSheetName()))
            {
                sheet = sheets.get(i);
                break;
            }
        }
        boolean findNextColumn = true;
        int colIndex = cell.getCol();
        while(findNextColumn)
        {
            Cell temp = sheet.getCellAt(colIndex, cell.getRow()-1);
            if(temp == null || temp.getValue().equals(""))
            {
                findNextColumn = false;
            }
            else
            {
                colIndex++;
            }
        }
        return colIndex-cell.getCol();
    }
    public Set<String> getLinkedCells(String currentSheetName)
    {
        Set<String> set = new HashSet<>();
        Range selectedRange = getSelectionModel().getSelectedRange();
        String sheetName = getSelectionModel().getSelectedRange().getSheet().getName();
        if(cellTempRef != null && cellTempRef.getSheetName().equals(sheetName))
        {
            set.add(cellTempRef.getCellRefParts()[2]+ cellTempRef.getCellRefParts()[1] + ",YELLOW,1");
        }
        if(selectedRange.isCellSelection())
        {
            for(String entry : linkedCellsSet)
            {
                CellReference fromCF = new CellReference(entry.split(",")[0]);
                CellReference toCF = new CellReference(entry.split(",")[1]);
                Boolean table = entry.split(",")[2].equals("table");

                if(fromCF.getSheetName().equals(currentSheetName))
                {
                    if(table)
                    {
                        set.add(fromCF.getCellRefParts()[2]+fromCF.getCellRefParts()[1] + ",BLUE,1");
                    }
                    else
                    {
                        set.add(fromCF.getCellRefParts()[2]+fromCF.getCellRefParts()[1] + ",CYAN,1");
                    }
                }
                if(toCF.getSheetName().equals(currentSheetName))
                {
                    if(table)
                    {
                        AreaReference area = getAreaFirstColumn(toCF);
                        int noOfColumns = getAreaFirstColumnNoOfColumns(toCF);
                        for(CellReference cellA : area.getAllReferencedCells())
                        {
                            set.add(cellA.getCellRefParts()[2]+cellA.getCellRefParts()[1] + ",RED," + noOfColumns);
                        }
                    }
                    else
                    {
                        set.add(toCF.getCellRefParts()[2]+toCF.getCellRefParts()[1] + ",ORANGE,1");
                    }
                }
            }
        }
        return set;
    }
    public void deleteAllLinked()
    {
        String sheetName = getSelectionModel().getSelectedRange().getSheet().getName();
        Range selectedRange = getSelectionModel().getSelectedRange();
        if(!selectedRange.isCellSelection()) {
            return;
        }
        cellTempRef = new CellReference(sheetName, selectedRange.getFromRow(), selectedRange.getFromColumn(), false, false);

        for(Iterator<String> i = linkedCellsSet.iterator(); i.hasNext();)
        {
            String temp = i.next();
            if(temp.split(",")[0].equals(cellTempRef.formatAsString()))
            {
                i.remove();
            }
        }
        cellTempRef = null;
    }
    public void addLink(Boolean delete, Boolean table)
    {
        String sheetName = getSelectionModel().getSelectedRange().getSheet().getName();
        Range selectedRange = getSelectionModel().getSelectedRange();
        if(!selectedRange.isCellSelection()) {
            return;
        }
        String from = getCellString(selectedRange.getFromColumn(),selectedRange.getFromRow());
        String to = getCellString(selectedRange.getToColumn(),selectedRange.getToRow());
        if(cellTempRef == null)
        {
            if(!from.equals(to))
            {
                return;
            }
            cellTempRef = new CellReference(sheetName, selectedRange.getFromRow(), selectedRange.getFromColumn(), false, false);
        }
        else
        {
            if(!delete)
            {
                if(table)
                {
                    linkedCellsSet.add(cellTempRef.formatAsString() + "," + new CellReference(sheetName, selectedRange.getFromRow(), selectedRange.getFromColumn(), false, false).formatAsString() + ",table");
                }
                else
                {
                    for(int i = selectedRange.getFromRow(); i <= selectedRange.getToRow(); i++)
                    {
                        for(int j = selectedRange.getFromColumn(); j <= selectedRange.getToColumn(); j++)
                        {
                            linkedCellsSet.add(cellTempRef.formatAsString() + "," + new CellReference(sheetName, i, j, false, false).formatAsString() + ",cell");
                        }
                    }
                }
            }
            else
            {
                for(int i = selectedRange.getFromRow(); i <= selectedRange.getToRow(); i++)
                {
                    for(int j = selectedRange.getFromColumn(); j <= selectedRange.getToColumn(); j++)
                    {
                        linkedCellsSet.remove(cellTempRef.formatAsString() + "," + new CellReference(sheetName, i, j, false, false).formatAsString() + ",table");
                        linkedCellsSet.remove(cellTempRef.formatAsString() + "," + new CellReference(sheetName, i, j, false, false).formatAsString() + ",cell");
                    }
                }
            }
            cellTempRef = null;
        }
    }

    private String getCellString(int column, int row)
    {
        String columnString = column < 26 ? (char)(column + 65) + ""
                : Character.toString((char)(column / 26 + 64)) + (char)(column % 26 + 65) + "";
        String rowString = (row + 1) + "";
        return columnString + rowString;
    }
    public void applyValidationChange() {
    	ValidationType type = entitySelectionModel.getValidationType();
    	IRI iri = entitySelectionModel.getSelectedEntity().getIRI();
    	OWLPropertyItem propertyItem = entitySelectionModel.getOWLPropertyItem();
    	logger.debug("Setting validation for IRI "+iri.toString()+", type "+type.toString()+", property "+propertyItem);

        Range selectedRange = getSelectionModel().getSelectedRange();
        if(!selectedRange.isCellSelection()) {
            return;
        }
        setValidationAt(selectedRange, type, iri,propertyItem);
        fireValidationAppliedOrCancelled();
    }

    public void cancelValidationChange() {
    	Range selectedRange = getSelectionModel().getSelectedRange();
    	getSelectionModel().setSelectedRange(selectedRange);
    	fireValidationAppliedOrCancelled();
    }

    public void setValidationAt(Range range,ValidationType type, IRI entityIRI, OWLPropertyItem property) {
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
        String cellText=getOntologyManager().getRendering(owlManager.getOWLDataFactory().getOWLAnnotationProperty(entityIRI));
        if (type == ValidationType.FREETEXT) {
        	cellText = "";
        }

        getOntologyManager().setOntologyTermValidation(rangeToApply, type, entityIRI, property);

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
    	Range selectedRange = getSelectionModel().getSelectedRange();
    	Collection<OntologyTermValidation> validations = getOntologyManager().getContainingOntologyTermValidations(selectedRange);
    	boolean result = false;
    	for (OntologyTermValidation validation : validations) {
    		OntologyTermValidationDescriptor validationDescriptor = validation.getValidationDescriptor();
    		boolean propertyChanged=false;

    		//FIXME: shouldn't rely on OWLProperty being NULL - should have a NullPropertyItem type
    		if (validationDescriptor.getOWLPropertyItem()==null) {
    			propertyChanged = property!=null;
    		}
    		else {
    			propertyChanged = !validationDescriptor.getOWLPropertyItem().equals(property);
    		}
    		if (!validationDescriptor.getEntityIRI().equals(iri) ||
    				!validationDescriptor.getType().equals(type) ||
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
        Set<String> newSet = new HashSet<String>();
		if (sheet!=null) {
			sheet.setName(newName);
            for(String link : linkedCellsSet)
            {
                newSet.add(link.replaceAll(oldName, newName));
            }
            linkedCellsSet.clear();
            linkedCellsSet.addAll(newSet);
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
