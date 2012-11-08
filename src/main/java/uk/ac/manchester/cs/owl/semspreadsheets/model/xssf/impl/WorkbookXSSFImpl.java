package uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.MutableWorkbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.NamedRange;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.SetCellValue;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.WorkbookChange;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.WorkbookChangeEvent;
import uk.ac.manchester.cs.owl.semspreadsheets.model.change.WorkbookChangeVisitor;

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
 * @author Stuart Owen
 * @author Matthew Horridge
 */

public class WorkbookXSSFImpl implements MutableWorkbook, WorkbookChangeVisitor {

    private XSSFWorkbook workbook;
    
    private static final Logger logger = Logger.getLogger(WorkbookXSSFImpl.class);

    private List<WorkbookChangeListener> changeListeners = new ArrayList<WorkbookChangeListener>();

    public WorkbookXSSFImpl() {
        workbook = new XSSFWorkbook();
        workbook.createSheet();
    }
    
    public WorkbookXSSFImpl(XSSFWorkbook workbook) {
    	this.workbook=workbook;
    }

    public WorkbookXSSFImpl(URI uri) throws IOException {
    	readWorkbook(uri);
    }
    
    public void saveAs(URI uri) throws IOException {
        File file = new File(uri);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));        
        workbook.write(stream);        
        stream.close();
        
        //this is a work-around to avoid https://issues.apache.org/bugzilla/show_bug.cgi?id=52233
        for (int i=0; i<workbook.getNumberOfSheets();i++) {
        	workbook.getSheetAt(i).getColumnHelper().cleanColumns();
        }                
    } 
    
    private void readWorkbook(URI uri) throws IOException {
    	InputStream inputStream = uri.toURL().openStream();
        workbook = new XSSFWorkbook(new BufferedInputStream(inputStream));
    }

    public XSSFWorkbook getXSSFWorkbook() {
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
                e.printStackTrace();
            }
        }
    }

    public Collection<NamedRange> getNamedRanges() {
        Collection<NamedRange> result = new ArrayList<NamedRange>();
        for(int i = 0; i < workbook.getNumberOfNames(); i++) {
            XSSFName name = workbook.getNameAt(i);
            if(!name.isDeleted() && !name.isFunctionName()) {
                NamedRange range = new NamedRangeXSSFImpl(this, name);
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
    	logger.debug("Deleting sheet "+name);
        int index = workbook.getSheetIndex(name);
        if (index != -1) {
            workbook.removeSheetAt(index);
            logger.debug("Sheet removed with index "+index);
        }
        else {
        	logger.debug("Sheet not found");
        }
        
        for(WorkbookChangeListener listener : new ArrayList<WorkbookChangeListener>(changeListeners)) {        	
            try {
                listener.sheetRemoved();
            }
            catch (Exception e) {
            	logger.error("Error firing listener for removing a sheet",e);
            }
        }
    }

    public void addName(String name, Range rng) {
        if(workbook.getName(name) != null) {
            workbook.removeName(name);
        }
        Name xssfName = workbook.createName();
        xssfName.setNameName(name);
        xssfName.setRefersToFormula(rng.toFixedAddress());
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
    	XSSFSheet xssfSheet = workbook.createSheet(name);
    	return new SheetXSSFImpl(this, xssfSheet);
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
            sheets.add(new SheetXSSFImpl(this, workbook.getSheetAt(i)));
        }
        return sheets;
    }

    public Sheet getSheet(String name) {
        XSSFSheet xssfSheet = workbook.getSheet(name);
        if (xssfSheet == null) {
            return null;
        }
        else {
            return new SheetXSSFImpl(this, xssfSheet);
        }
    }

    public Sheet getSheet(int index) {
        XSSFSheet xssfSheet = workbook.getSheetAt(index);
        if(xssfSheet == null) {
            return null;
        }
        else {
            return new SheetXSSFImpl(this, xssfSheet);
        }
    }          

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void visit(SetCellValue setCellValue) {
        XSSFSheet xssfSheet = workbook.getSheet(setCellValue.getSheet().getName());
        XSSFRow xssfRow = xssfSheet.getRow(setCellValue.getRow());
        if(xssfRow == null && setCellValue.getNewValue() != null) {
            xssfRow = xssfSheet.createRow(setCellValue.getRow());
        }
        XSSFCell xssfCell = xssfRow.getCell(setCellValue.getCol());
        if (xssfCell == null && setCellValue.getNewValue() != null) {
            xssfCell = xssfRow.createCell(setCellValue.getCol());
        }
        if (xssfCell != null) {
            if (setCellValue.getNewValue() != null) {
                xssfCell.setCellValue(new XSSFRichTextString(setCellValue.getNewValue().toString()));
            }
            else {
                xssfRow.removeCell(xssfCell);
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
