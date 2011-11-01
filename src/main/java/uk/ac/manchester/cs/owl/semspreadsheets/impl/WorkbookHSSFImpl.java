package uk.ac.manchester.cs.owl.semspreadsheets.impl;

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
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import uk.ac.manchester.cs.owl.semspreadsheets.change.SetCellValue;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChange;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeEvent;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeVisitor;
import uk.ac.manchester.cs.owl.semspreadsheets.model.MutableWorkbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.NamedRange;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;

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
 * Author: Matthew Horridge, Stuart Owen<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 20-Sep-2009
 * 
 * @author Stuart Owen
 * @author Matthew Horridge
 */

public class WorkbookHSSFImpl implements MutableWorkbook, WorkbookChangeVisitor {

    private HSSFWorkbook workbook;
    
    private static final Logger logger = Logger.getLogger(WorkbookHSSFImpl.class);

    private static final String VALUE_LIST_PREFIX = "semanticspreadsheetvalues";

    private List<WorkbookChangeListener> changeListeners = new ArrayList<WorkbookChangeListener>();

    public WorkbookHSSFImpl() {
        workbook = new HSSFWorkbook();
        workbook.createSheet();
    }

    public WorkbookHSSFImpl(URI uri) throws IOException {
        InputStream inputStream = uri.toURL().openStream();
        workbook = new HSSFWorkbook(new BufferedInputStream(inputStream));
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
                e.printStackTrace();
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
        Sheet sheet = new SheetHSSFImpl(this, workbook.createSheet());
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

    public Sheet addHiddenSheet() {
        HSSFSheet hssfSheet = workbook.createSheet();
        workbook.setSheetHidden(workbook.getSheetIndex(hssfSheet.getSheetName()), true);
        return new SheetHSSFImpl(this, hssfSheet);
    }


    public Sheet addVeryHiddenSheet() {
        HSSFSheet hssfSheet = workbook.createSheet();
        workbook.setSheetHidden(workbook.getSheetIndex(hssfSheet.getSheetName()), 2);
        return new SheetHSSFImpl(this, hssfSheet);
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

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
			if (!sheet.isHidden()) result.add(sheet);			
		}
		return result;
	}

}
