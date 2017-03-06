/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.apache.log4j.Logger;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellAddress;
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

    private Map<String, HashSet<String>> linkedCells = new HashMap<String, HashSet<String>>();
    private Set<String> map = new HashSet<String>();
    private CellReference tempToRef = null;
    private String tempTo = null;

    public WorkbookManager() {

        ontologyManager = new OntologyManager(this);

        entitySelectionModel = new EntitySelectionModel(ontologyManager.getOWLOntologyManager().getOWLDataFactory().getOWLThing());
        workbook = WorkbookFactory.createWorkbook();
        linkedCells.put(workbook.getSheet(0).getName(), new HashSet<String>());
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
        tempToRef = null;
    }
    public String getLinkString()
    {
        String sheetName = getSelectionModel().getSelectedRange().getSheet().getName();
        Range selectedRange = getSelectionModel().getSelectedRange();
        if(!selectedRange.isCellSelection()) {
            return "No link2";
        }
        String from = getCellString(selectedRange.getFromColumn(),selectedRange.getFromRow());
        String to = getCellString(selectedRange.getToColumn(),selectedRange.getToRow());
        if(selectedRange.getFromColumn() == selectedRange.getToColumn() && selectedRange.getFromRow() == selectedRange.getToRow())
        {
            CellReference fromCF = new CellReference(sheetName, selectedRange.getFromRow(), selectedRange.getFromColumn(), false, false);
            for(String tempString : map)
            {
                String[] test = tempString.split(",");
                if(test[0].equals(fromCF.formatAsString()) || test[1].equals(fromCF.formatAsString()))
                {
                    return test[0] + " linked to " + test[1];
                }
            }
        }

        return "No link1";
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
        linkedCells.clear();
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
    public void addSheetToHashmap(String name)
    {
        linkedCells.put(name, new HashSet<String>());
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
            Sheet sheet = null;
            for(int i = 0 ; i < sheets.size(); i++)
            {
                //String temp = sheets.get(i).getName();
                if(sheets.get(i).getName().equals("LinkedCells"))
                {
                    sheet = sheets.get(i);
                    break;
                }
            }

            //make all sheets visible
            for(int i = 0 ; i < sheets.size(); i++)
            {
                //String temp = sheets.get(i).getName();
                if(sheets.get(i).getName().equals("LinkedCells"))
                {
                    sheets.get(i).setVeryHidden(false);
                    sheets.get(i).setHidden(false);
                }
            }
            linkedCells.clear();
            map.clear();
            if(sheet != null)
            {

                int indexSheet = Integer.parseInt(sheet.getCellAt(0,0).getValue());
                for(int i = 0; i < indexSheet; i++)
                {
                    String from = sheet.getCellAt(1, i+1).getValue().replaceAll("\\$", "");
                    String to = sheet.getCellAt(2, i+1).getValue().replaceAll("\\$", "");
                    //map.add(sheet.getCellAt(0, i+1).getValue());
                    map.add(from + "," + to);

                    /*CellReference fromCF = new CellReference(sheet.getCellAt(1, i).getValue());
                    CellReference toCF = new CellReference(sheet.getCellAt(2, i).getValue());

                    Sheet fromSheet = workbook.getSheet(fromCF.getSheetName());
                    Sheet toSheet = workbook.getSheet(toCF.getSheetName());

                    Cell fromCell = fromSheet.getCellAt(fromCF.getCol(), fromCF.getRow());
                    Cell toCell = toSheet.getCellAt(toCF.getCol(), toCF.getRow());

                    if(fromCell != null && toCell != null && fromCell.getValue().equals(sheet.getCellAt(3,i).getValue()) && toCell.getValue().equals(sheet.getCellAt(4,i).getValue()))
                    {
                        map.add(sheet.getCellAt(0, i+1).getValue());
                    } else
                    {
                        Cell newFromCell = null;
                        Cell newToCell = null;

                        for(Cell tempCell : fromSheet.getCellsWithContent())
                        {
                            if(tempCell.getValue().equals(sheet.getCellAt(3,i).getValue()))
                            {
                                newFromCell = tempCell;
                                break;
                            }
                        }
                        for(Cell tempCell : toSheet.getCellsWithContent())
                        {
                            if(tempCell.getValue().equals(sheet.getCellAt(4,i).getValue()))
                            {
                                newToCell = tempCell;
                                break;
                            }
                        }
                        if(newFromCell != null && newToCell != null)
                        {
                            CellReference newFromCF = new CellReference(fromSheet.getName(), newFromCell.getRow(), newFromCell.getColumn(), false, false);
                            CellReference newToCF = new CellReference(toSheet.getName(), newToCell.getRow(), newToCell.getColumn(), false, false);
                            map.add(newFromCF.formatAsString() + "," + newToCF.formatAsString());
                        }
                        else
                        {
                            System.out.println("Welp we lost it");
                        }
                        System.out.println("cacat, cauta-l");
                    }*/
                    /*int indexCell = Integer.parseInt(sheet.getCellAt(i*2,1).getValue());
                    String sheetName = sheet.getCellAt(i*2,2).getValue();
                    linkedCells.put(sheetName, new HashSet<String>());
                    for(int j = 3; j < indexCell + 3; j++)
                    {
                        String c1 = sheet.getCellAt(i*2,j).getValue();
                        String c2 = sheet.getCellAt(i*2+1,j).getValue();
                        String tempT = c1 + "," + c2;
                        linkedCells.get(sheetName).add(tempT);

                    }*/
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

        //save linked cell set
        List<Sheet> sheets = workbook.getSheets();
        Sheet sheet = null;
        for(int i = 0 ; i < sheets.size(); i++)
        {
            //String temp = sheets.get(i).getName();
            if(sheets.get(i).getName().equals("LinkedCells"))
            {
                sheet = sheets.get(i);
                break;
            }
        }

        if(sheet == null)
        {
            sheet = workbook.addSheet("LinkedCells");
            sheet.setVeryHidden(false);
        } else {
            //sheet.clearAllCells();
        }
        sheet.clearCellAt(0,0);
        sheet.addCellAt(0,0).setValue(map.size() + "");
        int caca = 0;
          /*  for(Map.Entry<String, HashSet<String>> entry : linkedCells.entrySet())
            {

                String sheetName = entry.getKey();
                HashSet<String> sheetSet = entry.getValue();
                //sheet.clearCellAt(i,1);
                sheet.addCellAt(caca,1).setValue(sheetSet.size() + "");
                sheet.addCellAt(caca,2).setValue(sheetName);

                int index = 3;
                for(String cellLink : sheetSet)
                {
                    String[] fromAndTo = cellLink.split(",");

                    if(sheet.getCellAt(caca, index) != null)sheet.clearCellAt(caca,index);
                    if(sheet.getCellAt(caca+1, index) != null)sheet.clearCellAt(caca+1,index);

                    sheet.addCellAt(caca,index).setValue(fromAndTo[0]);
                    sheet.addCellAt(caca+1,index).setValue(fromAndTo[1]);
                    index++;
                }
                caca = caca + 2;
            }*/
            int index = 1;
            for(String link : map)
            {
                String fromCell = link.split(",")[0];
                String toCell = link.split(",")[1];

                CellReference fromCF = new CellReference(fromCell);
                CellReference toCF = new CellReference(toCell);

                Sheet sheetFromCell = workbook.getSheet(fromCF.getSheetName());
                Sheet sheetToCell = workbook.getSheet(toCF.getSheetName());

                Cell cellFrom = sheetFromCell.getCellAt(fromCF.getCol(), fromCF.getRow());
                Cell cellTo = sheetToCell.getCellAt(toCF.getCol(), toCF.getRow());

                //OntologyTermValidation validationFrom = getOntologyManager().getContainingOntologyTermValidations(new Range(sheetCell, cellFrom)).iterator().next();
                //OntologyTermValidation validationTo = getOntologyManager().getContainingOntologyTermValidations(new Range(sheetCell, cellTo)).iterator().next();

                String fromCellFormula = "" + fromCF.getSheetName() + "!$" + (char)('A' + fromCF.getCol()) + "$" + (fromCF.getRow()+1);
                String toCellFormula = "" + toCF.getSheetName() + "!$" + (char)('A' + toCF.getCol()) + "$" + (toCF.getRow()+1);

                sheet.addCellAt(0, index).setValue(link);

                //sheet.addCellAt(1, index).setCellStyleFormula();
                sheet.addCellAt(1,index).setCellFormula(fromCellFormula);

                //sheet.addCellAt(2,index).setCellStyleFormula();
                sheet.addCellAt(2,index).setCellFormula(toCellFormula);

                index++;
            }



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
	public void deleteLinkCells()
    {
        System.out.println("caca pipi");
    }
    public Map<String, HashSet<String>> getAllLinkedCells()
    {
        return linkedCells;
    }
    public boolean isLinked(Cell cell)
    {
        String address = getCellString(cell.getColumn(), cell.getRow());
        for(HashSet<String> set : linkedCells.values())
        {
            for(String t : set)
            {
                if(t.contains(address))
                    return true;
            }
        }
        return false;
    }
    public AreaReference getAreaFirstColumn(CellReference cell)
    {
        List<Sheet> sheets = workbook.getSheets();
        AreaReference area = new AreaReference(cell, cell);
        Sheet sheet = null;
        for(int i = 0 ; i < sheets.size(); i++)
        {
            //String temp = sheets.get(i).getName();
            if(sheets.get(i).getName().equals(cell.getSheetName()))
            {
                sheet = sheets.get(i);
                break;
            }
        }
        boolean test1 = true;
        int colIndex = cell.getCol();
        int rowIndex = cell.getRow();
        test1 = true;
        while(test1)
        {
            Cell temp = sheet.getCellAt(cell.getCol(), rowIndex);
            if(temp == null || temp.equals(""))
            {
                test1 = false;
                rowIndex--;
            }
            else
            {
                rowIndex++;
            }
        }
        CellReference endCell = new CellReference(cell.getSheetName(), rowIndex, colIndex, false, false);
        area = new AreaReference(cell, endCell);


        return area;
    }
    public int getAreaFirstColumnNoOfColumns(CellReference cell)
    {
        List<Sheet> sheets = workbook.getSheets();
        AreaReference area = new AreaReference(cell, cell);
        Sheet sheet = null;
        for(int i = 0 ; i < sheets.size(); i++)
        {
            //String temp = sheets.get(i).getName();
            if(sheets.get(i).getName().equals(cell.getSheetName()))
            {
                sheet = sheets.get(i);
                break;
            }
        }
        boolean test1 = true;
        int colIndex = cell.getCol();
        while(test1)
        {
            Cell temp = sheet.getCellAt(colIndex, cell.getRow()-1);
            if(temp == null || temp.getValue().equals(""))
            {
                test1 = false;
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
        if(tempToRef != null)
        {
            set.add(tempToRef.getCellRefParts()[2]+tempToRef.getCellRefParts()[1] + ",YELLOW,1");
        }
        if(selectedRange.isCellSelection())
        {
            String from = getCellString(selectedRange.getFromColumn(),selectedRange.getFromRow());
            String to = getCellString(selectedRange.getToColumn(),selectedRange.getToRow());
            for(String entry : map)
            {
                CellReference fromCF = new CellReference(entry.split(",")[0]);
                CellReference toCF = new CellReference(entry.split(",")[1]);

                if(fromCF.getSheetName().equals(currentSheetName))
                {
                    set.add(fromCF.getCellRefParts()[2]+fromCF.getCellRefParts()[1] + ",BLUE,1");
                }
                if(toCF.getSheetName().equals(currentSheetName))
                {
                    AreaReference area = getAreaFirstColumn(toCF);
                    int noOfColumns = getAreaFirstColumnNoOfColumns(toCF);
                    for(CellReference cellA : area.getAllReferencedCells())
                    {
                        set.add(cellA.getCellRefParts()[2]+cellA.getCellRefParts()[1] + ",RED," + noOfColumns);

                    }
                }
            }
        }
        return set;
    }
   /* public Set<String> getLinkedCellsOld(String currentSheetName)
    {
        //String currentSheetName = getWorkbook().getActiveSheetName();
        if(!linkedCells.containsKey(currentSheetName))
        {
            return new HashSet<String>();
        }
        Set<String> tempSet = new HashSet<>();
        Range selectedRange = getSelectionModel().getSelectedRange();
        if(selectedRange.isCellSelection())
        {
            String from = getCellString(selectedRange.getFromColumn(),selectedRange.getFromRow());
            String to = getCellString(selectedRange.getToColumn(),selectedRange.getToRow());

            if(!from.equals(to))
            {
                return linkedCells.get(currentSheetName);
            }

            for(String temp : linkedCells.get(currentSheetName))
            {
                if(temp.split(",")[0].equals(from))
                {
                    tempSet.add(temp);
                }
            }
            if(tempSet.size() == 0)
            {
                return linkedCells.get(currentSheetName);
            }
            else
            {
                return tempSet;
            }
        }
        return linkedCells.get(currentSheetName);
    }*/
    public void addLinkCells(Boolean delete, Boolean rowLinking)
    {
        System.out.print("cell to table");
        String sheetName = getSelectionModel().getSelectedRange().getSheet().getName();
        Range selectedRange = getSelectionModel().getSelectedRange();
        if(!selectedRange.isCellSelection()) {
            return;
        }
        String from = getCellString(selectedRange.getFromColumn(),selectedRange.getFromRow());
        String to = getCellString(selectedRange.getToColumn(),selectedRange.getToRow());
        if(tempToRef == null)
        {
            if(!from.equals(to))
            {
                return;
            }
            tempToRef = new CellReference(sheetName, selectedRange.getFromRow(), selectedRange.getFromColumn(), false, false);
        }
        else
        {
            if(!delete)
                map.add(tempToRef.formatAsString() + "," + new CellReference(sheetName, selectedRange.getFromRow(), selectedRange.getFromColumn(), false, false).formatAsString());
            else
                map.remove(tempToRef.formatAsString() + "," + new CellReference(sheetName, selectedRange.getFromRow(), selectedRange.getFromColumn(), false, false).formatAsString());
            tempToRef = null;
        }
    }
   /* public void addLinkCellsDA(Boolean delete, Boolean rowLinking)
    {
        System.out.println("global map version");
        String sheetName = getSelectionModel().getSelectedRange().getSheet().getName();
        Range selectedRange = getSelectionModel().getSelectedRange();
        if(!selectedRange.isCellSelection()) {
            return;
        }
        String from = getCellString(selectedRange.getFromColumn(),selectedRange.getFromRow());
        String to = getCellString(selectedRange.getToColumn(),selectedRange.getToRow());


        if(tempTo == null)
        {
            if(!from.equals(to))
            {
                return;
            }
            tempTo = new CellAddress(selectedRange.getFromRow(), selectedRange.getFromColumn()) + "";
        }
        else
        {
            if(!linkedCells.containsKey(sheetName))
            {
                linkedCells.put(sheetName, new HashSet<String>());
            }
            for(int i = selectedRange.getFromRow(); i <= selectedRange.getToRow(); i++)
            {
                for(int j = selectedRange.getFromColumn(); j <= selectedRange.getToColumn(); j++)
                {
                    if(delete)
                    {
                        System.out.println("CLICK DREAPTA DELET DATEN MORTI MATI NU STAM LA DISCUTII");
                        linkedCells.get(sheetName).remove(tempTo+","+new CellAddress(i,j) + "");
                        linkedCells.get(sheetName).remove(tempTo+","+new CellAddress(i,j) + "!");
                    }
                    else
                    {
                        System.out.println("ADD");
                        linkedCells.get(sheetName).add(tempTo+","+new CellAddress(i,(rowLinking ? 0 : j)) + (rowLinking ? "!" : ""));
                    }

                }
            }
            tempTo = null;
        }
        System.out.println("map size: " + linkedCells.size());
        fireValidationAppliedOrCancelled();
    }*/

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
            for(String link : map)
            {
                newSet.add(link.replaceAll(oldName, newName));
            }
            map.clear();
            map.addAll(newSet);
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
