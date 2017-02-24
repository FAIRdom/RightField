/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellAddress;
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

    private Set<String> linkedCells = new HashSet<String>();
    private String tempTo = null;

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
            linkedCells.clear();
            if(sheet != null)
            {

                int indexCell = Integer.parseInt(sheet.getCellAt(0,0).getValue());
                for(int i = 1; i < indexCell + 1; i++)
                {
                    linkedCells.add(sheet.getCellAt(0,i).getValue() + "," + sheet.getCellAt(1,i).getValue());
                }
            }
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
        } else {
            //sheet.clearAllCells();
        }
        sheet.clearCellAt(0,0);
        sheet.addCellAt(0,0).setValue(linkedCells.size() + "");


        int index = 1;
        for(String cellLink : linkedCells)
        {
            String[] fromAndTo = cellLink.split(",");

            sheet.clearCellAt(0,index);
            sheet.clearCellAt(1,index);

            sheet.addCellAt(0,index).setValue(fromAndTo[0]);
            sheet.addCellAt(1,index).setValue(fromAndTo[1]);
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
    public Set<String> getLinkedCells()
    {
        Set<String> tempSet = new HashSet<>();
        Range selectedRange = getSelectionModel().getSelectedRange();
        if(selectedRange.isCellSelection()) {
            String from = getCellString(selectedRange.getFromColumn(),selectedRange.getFromRow());
            String to = getCellString(selectedRange.getToColumn(),selectedRange.getToRow());

            if(!from.equals(to))
            {
                return linkedCells;
            }
            for(String temp : linkedCells)
            {
                if(temp.split(",")[0].equals(from))
                {
                    tempSet.add(temp);
                }
            }
            if(tempSet.size() == 0)
            {
                return linkedCells;
            }
            else
            {
                return tempSet;
            }
        }
        return linkedCells;
    }
    public void addLinkCells(Boolean delete)
    {
        System.out.println("global map version");
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
            for(int i = selectedRange.getFromRow(); i <= selectedRange.getToRow(); i++)
            {
                for(int j = selectedRange.getFromColumn(); j <= selectedRange.getToColumn(); j++)
                {
                    if(delete)
                    {
                        System.out.println("CLICK DREAPTA DELET DATEN MORTI MATI NU STAM LA DISCUTII");
                        linkedCells.remove(tempTo+","+new CellAddress(i,j) + "");
                    }
                    else
                    {
                        System.out.println("ADD");
                        linkedCells.add(tempTo+","+new CellAddress(i,j) + "");
                    }

                }
            }
            tempTo = null;
        }
        System.out.println("map size: " + linkedCells.size());
        fireValidationAppliedOrCancelled();
    }
	public void addLinkCellsss(Boolean delete)
    {
        System.out.println("map version");
        Range selectedRange = getSelectionModel().getSelectedRange();
        if(!selectedRange.isCellSelection()) {
            return;
        }
        String from = getCellString(selectedRange.getFromColumn(),selectedRange.getFromRow());
        String to = getCellString(selectedRange.getToColumn(),selectedRange.getToRow());

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
            sheet.addCellAt(0,0).setValue("0");
        }

        int indexCell = Integer.parseInt(sheet.getCellAt(0,0).getValue());
        int step = sheet.getCellAt(0,indexCell+1) == null || sheet.getCellAt(0,indexCell+1).getValue().equals("")? 1 : 2;
        if(step == 1)
        {
            if(!from.equals(to))
            {
                return;
            }
            sheet.addCellAt(0,indexCell+1).setValue(new CellAddress(selectedRange.getFromRow(), selectedRange.getFromColumn()) + ""/*+ "," +  new CellAddress(selectedRange.getToRow(), selectedRange.getToColumn())*/);
        }
        if(step == 2)
        {
            Set<String> map = new HashSet<String>();
            int what = map.size();
            String mama = sheet.getCellAt(0,indexCell+1).getValue();

            //add in set what is already linked
            for(int i = 1; i < indexCell + 1; i++)
            {
                map.add(sheet.getCellAt(0,i).getValue()+ "," +sheet.getCellAt(1,i).getValue());
            }

            for(int i = selectedRange.getFromRow(); i <= selectedRange.getToRow(); i++)
            {
                for(int j = selectedRange.getFromColumn(); j <= selectedRange.getToColumn(); j++)
                {
                    if(delete)
                    {
                        System.out.println("CLICK DREAPTA DELET DATEN MORTI MATI NU STAM LA DISCUTII");
                        map.remove(mama+","+new CellAddress(i,j) + "");
                    }
                    else
                    {
                        System.out.println("ADD");
                        map.add(mama+","+new CellAddress(i,j) + "");
                    }

                }
            }
            //indexCell = map.size();
            //Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
            int tempIndex = 1;
            for(String s : map)
            {
                String[] temp = s.split(",");
                sheet.addCellAt(0,tempIndex).setValue(temp[0]);
                sheet.addCellAt(1,tempIndex).setValue(temp[1]);
                tempIndex++;
            }
            int test = map.size();
            sheet.getCellAt(0,0).setValue(map.size() + "");
            for(int i = tempIndex; i < indexCell + 2; i++)
            {
                sheet.clearCellAt(0,i);
                sheet.clearCellAt(1,i);
            }



        }
        fireValidationAppliedOrCancelled();
    }
    public void linkCellss() {
        System.out.println("what?");
        Range selectedRange = getSelectionModel().getSelectedRange();
        Sheet workingSheet = selectedRange.getSheet();

        if(!selectedRange.isCellSelection()) {
            return;
        }

        //System.out.println((char)(selectedRange.getToColumn() / 26 + 64));
        //System.out.println((char)(selectedRange.getToColumn() % 26 + 65));
        /*String fromColumn = selectedRange.getFromColumn() < 26 ? (char)(selectedRange.getFromColumn() + 65) + ""
                : Character.toString((char)(selectedRange.getFromColumn() / 26 + 64)) + (char)(selectedRange.getFromColumn() % 26 + 65) + "";
        String toColumn = selectedRange.getToColumn() < 26 ? (char)(selectedRange.getToColumn() + 65) + ""
                : Character.toString((char)(selectedRange.getToColumn() / 26 + 64))+ (char)(selectedRange.getToColumn() % 26 + 65) + "";
        String from = fromColumn + "," + (selectedRange.getFromRow() + 1);
        String to = toColumn + "," +(selectedRange.getToRow() + 1);
        System.out.println(from + "->" + to);*/

        String from = getCellString(selectedRange.getFromColumn(),selectedRange.getFromRow());
        String to = getCellString(selectedRange.getToColumn(),selectedRange.getToRow());

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
            sheet.addCellAt(0,0).setValue("0");
        }

        int indexCell = Integer.parseInt(sheet.getCellAt(0,0).getValue());
        //String newLink = from + "->" + to;
        String newLink = from+to;
        /*if(from.equals(to))
        {
            return;
        }*/
        /*workingSheet.addCellAt(0,0);
        Cell mm = workingSheet.getCellAt(0,0);
        mm.setValue("morti mei");
        mm.setBold(true);
        mm.setBackgroundFill(Color.GREEN);
        mm.setBorders(Color.CYAN);*/
        int step = sheet.getCellAt(0,indexCell+1) == null || sheet.getCellAt(0,indexCell+1).getValue().equals("")? 1 : 2;
        if(step == 1)
        {
            if(!from.equals(to))
            {
                return;
            }
            sheet.addCellAt(0,indexCell+1).setValue(new CellAddress(selectedRange.getFromRow(), selectedRange.getFromColumn()) + ""/*+ "," +  new CellAddress(selectedRange.getToRow(), selectedRange.getToColumn())*/);
        }
        if(step == 2)
        {
            //indexCell++;
            //sheet.addCellAt(1,indexCell).setValue(new CellAddress(selectedRange.getFromRow(), selectedRange.getFromColumn()) + "," + new CellAddress(selectedRange.getToRow(), selectedRange.getToColumn()));
            String mama = sheet.getCellAt(0,indexCell+1).getValue();
            for(int i = selectedRange.getFromRow(); i <= selectedRange.getToRow(); i++)
            {
                for(int j = selectedRange.getFromColumn(); j <= selectedRange.getToColumn(); j++)
                {
                    indexCell++;
                    sheet.addCellAt(0,indexCell).setValue(mama);
                    sheet.addCellAt(1,indexCell).setValue(new CellAddress(i,j) + "");
                }
            }
            sheet.getCellAt(0,0).setValue(indexCell + "");

            Boolean alreadyLinked = false;
            int alreadyLinkedIndex = -1;
            for(int i = 0; i < indexCell-1; i++)
            {
                if(sheet.getCellAt(0,i+1).getValue().equals(sheet.getCellAt(0,indexCell).getValue()) &&
                   sheet.getCellAt(1,i+1).getValue().equals(sheet.getCellAt(1,indexCell).getValue()))
                {
                    alreadyLinked = true;
                    alreadyLinkedIndex = i+1;
                    break;
                }
            }
            if(sheet.getCellAt(0,indexCell).getValue().equals(sheet.getCellAt(1,indexCell).getValue()) || alreadyLinked)
            {
                sheet.clearCellAt(0,indexCell);
                sheet.clearCellAt(1,indexCell);
                indexCell--;
                if(alreadyLinked)
                {
                    indexCell--;
                    sheet.getCellAt(0,alreadyLinkedIndex).setValue(sheet.getCellAt(0,indexCell+1).getValue());
                    sheet.getCellAt(1,alreadyLinkedIndex).setValue(sheet.getCellAt(0,indexCell+1).getValue());
                    sheet.clearCellAt(0,indexCell+1);
                    sheet.clearCellAt(1,indexCell+1);
                }
                sheet.getCellAt(0,0).setValue(indexCell + "");
            }
        }
/*
        sheet.addCellAt(selectedRange.getFromColumn(),selectedRange.getFromRow());
        Cell che = sheet.getCellAt(selectedRange.getFromColumn(),selectedRange.getFromRow());
        che.setValue("TEST");
        che.setBorders(Color.BLUE);*/

       /* boolean foundExistingLink = false;
        for(int i = 0; i < indexCell; i++)
        {
            String[] temp2 = newLink.split("->");
            String temp3 = temp2[1] + "->" + temp2[0];
            String pi = sheet.getCellAt(0,i+1).getValue();
            if(pi.equals(newLink))
            {

                //reverse
                /*int fromColumnSheet = Integer.parseInt(sheet.getCellAt(1,i+1).getValue());
                int fromRowSheet = Integer.parseInt(sheet.getCellAt(2,i+1).getValue());
                int toColumnSheet = Integer.parseInt(sheet.getCellAt(3,i+1).getValue());
                int toRowSheet = Integer.parseInt(sheet.getCellAt(4,i+1).getValue());

                String[] temp = sheet.getCellAt(0,i+1).getValue().split("->");
                sheet.getCellAt(0,i+1).setValue(temp[1] + "->" + temp[0]);
                sheet.getCellAt(1,i+1).setValue(Integer.toString(toColumnSheet));
                sheet.getCellAt(2,i+1).setValue(Integer.toString(toRowSheet));
                sheet.getCellAt(3,i+1).setValue(Integer.toString(fromColumnSheet));
                sheet.getCellAt(4,i+1).setValue(Integer.toString(fromRowSheet));
                foundExistingLink = true;
                break;
            }
            else if(temp3.equals(pi))
            {
                sheet.getCellAt(0, 0).setValue(Integer.toString(indexCell - 1));

                sheet.getCellAt(0,i+1).setValue(sheet.getCellAt(0,indexCell).getValue());
                sheet.getCellAt(1,i+1).setValue(sheet.getCellAt(1,indexCell).getValue());
                sheet.getCellAt(2,i+1).setValue(sheet.getCellAt(2,indexCell).getValue());
                sheet.getCellAt(3,i+1).setValue(sheet.getCellAt(3,indexCell).getValue());
                sheet.getCellAt(4,i+1).setValue(sheet.getCellAt(4,indexCell).getValue());

                sheet.clearCellAt(0,indexCell);
                sheet.clearCellAt(1,indexCell);
                sheet.clearCellAt(2,indexCell);
                sheet.clearCellAt(3,indexCell);
                sheet.clearCellAt(4,indexCell);




                foundExistingLink = true;
                break;
            }


            }*/
/*
        if(!foundExistingLink) {
            sheet.getCellAt(0, 0).setValue(Integer.toString(indexCell + 1));
            sheet.addCellAt(0, indexCell + 1).setValue(from + to);
            /*sheet.addCellAt(0, indexCell + 1).setValue(from + "->" + to);
            sheet.addCellAt(1, indexCell + 1).setValue(Integer.toString(selectedRange.getFromColumn()));
            sheet.addCellAt(2, indexCell + 1).setValue(Integer.toString(selectedRange.getFromRow()));
            sheet.addCellAt(3, indexCell + 1).setValue(Integer.toString(selectedRange.getToColumn()));
            sheet.addCellAt(4, indexCell + 1).setValue(Integer.toString(selectedRange.getToRow()));
        }*/





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
