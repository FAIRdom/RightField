/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.InvalidWorkbookFormatException;
import uk.ac.manchester.cs.owl.semspreadsheets.model.MutableWorkbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.NamedRange;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.SetCellValue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.WorkbookChange;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.WorkbookChangeEvent;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.WorkbookChangeVisitor;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */

public class WorkbookHSSFImpl implements MutableWorkbook, WorkbookChangeVisitor {

    private HSSFWorkbook workbook;
    
    private static final Logger logger = Logger.getLogger(WorkbookHSSFImpl.class);    

    private Set<WorkbookChangeListener> changeListeners = new HashSet<WorkbookChangeListener>();

    public WorkbookHSSFImpl() {
        workbook = new HSSFWorkbook();
        workbook.createSheet();        
    }
    
    public WorkbookHSSFImpl(HSSFWorkbook workbook) {
    	this.workbook=workbook;
    }

    public WorkbookHSSFImpl(URI uri) throws IOException,InvalidWorkbookFormatException {
        InputStream inputStream = uri.toURL().openStream();
        try {
        	workbook = new HSSFWorkbook(new BufferedInputStream(inputStream));
        }
        catch (OfficeXmlFileException e) {
        	throw new InvalidWorkbookFormatException(e,uri);
        }
        catch (IOException e) {
        	if (e.getMessage().toLowerCase().contains("invalid header signature")) {
        		throw new InvalidWorkbookFormatException(e,uri);
        	}
        	else {
        		throw e;
        	}
        }
    }

    public HSSFWorkbook getHSSFWorkbook() {
        return workbook;
    }

    public void applyChange(WorkbookChange change) {
        logger.debug("APPLY CHANGE: " + change);
        change.accept(this);
        for (WorkbookChangeListener listener : new ArrayList<WorkbookChangeListener>(changeListeners)) {
            try {
                listener.workbookChanged(new WorkbookChangeEvent(change));
            }
            catch (Throwable e) {
                logger.error("Error notifying listener",e);
            }
        }
    }

    public Collection<NamedRange> getNamedRanges() {
        Collection<NamedRange> result = new ArrayList<NamedRange>();
        for(int i = 0; i < workbook.getNumberOfNames(); i++) {
            HSSFName name = workbook.getNameAt(i);
            if(!name.isDeleted() && !name.isFunctionName()) {
                NamedRange range = new NamedRangeHSSFImpl(this, name);
                result.add(range);
            }
        }
        return result;
    }

    public void addChangeListener(WorkbookChangeListener changeListener) {
    	changeListeners.add(changeListener);    	
    }        

    public void removeChangeListener(WorkbookChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }

    public boolean containsSheet(String name) {
        return workbook.getSheet(name) != null;
    }

    public void deleteSheet(String name) {
        int index = workbook.getSheetIndex(name);
        if (index != -1) {
            workbook.removeSheetAt(index);
        }
        for(WorkbookChangeListener listener : new ArrayList<WorkbookChangeListener>(changeListeners)) {
            try {
                listener.sheetRemoved();                
            }
            catch (Exception e) {
            	logger.error("Error removing a sheet",e);
            }
        }
    }

    public void addName(String name, Range rng) {
        if(workbook.getName(name) != null) {
            workbook.removeName(name);
        }
        HSSFName hssfName = workbook.createName();
        hssfName.setNameName(name);
        hssfName.setRefersToFormula(rng.toFixedAddress());
    }

    public void removeName(String name) {
        workbook.removeName(name);
    }
        
    public Sheet addSheet() {
        Sheet sheet = createSheet();
        for(WorkbookChangeListener listener : new ArrayList<WorkbookChangeListener>(changeListeners)) {
            try {
                listener.sheetAdded();
            }
            catch (Exception e) {
                logger.error("Error adding a new sheet",e);
            }
        }
        return sheet;
    }
    
    protected Sheet createSheet() { 
    	int x=0;
    	String name = "Sheet" + Integer.toString(x);
    	while (containsSheet(name)) {
    		x++;
    		name = "Sheet" + Integer.toString(x);
    	}
    	HSSFSheet hssfSheet = workbook.createSheet(name);
    	return new SheetHSSFImpl(this, hssfSheet);
    }
    
    public Sheet addHiddenSheet() {
        Sheet sheet = createSheet();
        sheet.setHidden(true);
        return sheet;
    }

    public Sheet addVeryHiddenSheet() {
    	Sheet sheet = createSheet();
        sheet.setVeryHidden(true);
        return sheet;
    }

    protected void fireSheetRenamed(String oldName, String newName) {
        for(WorkbookChangeListener listener : new ArrayList<WorkbookChangeListener>(changeListeners)) {
            try {
                listener.sheetRenamed(oldName, newName);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Sheet> getSheets() {
        List<Sheet> sheets = new ArrayList<Sheet>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheets.add(new SheetHSSFImpl(this, workbook.getSheetAt(i)));
        }
        return sheets;
    }

    public Sheet getSheet(String name) {
        HSSFSheet hssfSheet = workbook.getSheet(name);
        if (hssfSheet == null) {
            return null;
        }
        else {
            return new SheetHSSFImpl(this, hssfSheet);
        }
    }

    public Sheet getSheet(int index) {
        HSSFSheet hssfSheet = workbook.getSheetAt(index);
        if(hssfSheet == null) {
            return null;
        }
        else {
            return new SheetHSSFImpl(this, hssfSheet);
        }
    }

    public void saveAs(URI uri) throws IOException {
        File file = new File(uri);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        workbook.write(stream);        
        stream.close();        
    }        

    public void visit(SetCellValue setCellValue) {
        HSSFSheet hssfSheet = workbook.getSheet(setCellValue.getSheet().getName());
        HSSFRow hssfRow = hssfSheet.getRow(setCellValue.getRow());
        if(hssfRow == null && setCellValue.getNewValue() != null) {
            hssfRow = hssfSheet.createRow(setCellValue.getRow());
        }
        HSSFCell hssfCell = hssfRow.getCell(setCellValue.getCol());
        if (hssfCell == null && setCellValue.getNewValue() != null) {
            hssfCell = hssfRow.createCell(setCellValue.getCol());
        }
        if (hssfCell != null) {
            if (setCellValue.getNewValue() != null) {
                hssfCell.setCellValue(new HSSFRichTextString(setCellValue.getNewValue().toString()));
            }
            else {
                hssfRow.removeCell(hssfCell);
            }
        }
    }

	@Override
	public List<Sheet> getVisibleSheets() {
		List<Sheet> result = new ArrayList<Sheet>();
		for (Sheet sheet : getSheets()) {
			if (!sheet.isHidden() && !sheet.isVeryHidden()) result.add(sheet);			
		}
		return result;
	}

	@Override
	public List<WorkbookChangeListener> getAllChangeListeners() {
		return new ArrayList<WorkbookChangeListener>(changeListeners);
	}

	@Override
	public void clearChangeListeners() {
		changeListeners.clear();
	}

}
