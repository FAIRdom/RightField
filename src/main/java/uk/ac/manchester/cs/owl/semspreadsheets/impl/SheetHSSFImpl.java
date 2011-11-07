package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.aggregates.DataValidityTable;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Validation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 */
public class SheetHSSFImpl implements Sheet {

    private WorkbookHSSFImpl workbook;

    private HSSFWorkbook hssfWorkbook;

    private HSSFSheet sheet;

    private static final short MAX_ROWS = Short.MAX_VALUE;

    private static final int MAX_COLUMNS = 256;


    public SheetHSSFImpl(WorkbookHSSFImpl workbook, HSSFSheet hssfSheet) {
        this.workbook = workbook;
        this.hssfWorkbook = workbook.getHSSFWorkbook();
        this.sheet = hssfSheet;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setName(String name) {
        String oldName = sheet.getSheetName();
        hssfWorkbook.setSheetName(hssfWorkbook.getSheetIndex(sheet), name);
        workbook.fireSheetRenamed(oldName, name);
    }

    public boolean isHidden() {
        return hssfWorkbook.isSheetHidden(hssfWorkbook.getSheetIndex(sheet));
    }

    public void setHidden(boolean b) {
        hssfWorkbook.setSheetHidden(hssfWorkbook.getSheetIndex(sheet), b);
    }

    public void setVeryHidden(boolean b) {    	
        if (b) {
            hssfWorkbook.setSheetHidden(hssfWorkbook.getSheetIndex(sheet), 2);
        }
        else {
            hssfWorkbook.setSheetHidden(hssfWorkbook.getSheetIndex(sheet), false);
        }
    }
    
    @Override
	public boolean isVeryHidden() {
		return hssfWorkbook.isSheetVeryHidden(hssfWorkbook.getSheetIndex(sheet));
	}

    public boolean equals(Object obj) {
        if (!(obj instanceof SheetHSSFImpl)) {
            return false;
        }
        SheetHSSFImpl other = (SheetHSSFImpl) obj;
        return sheet == other.sheet;
    }

    public HSSFSheet getHSSFSheet() {
        return sheet;
    }

    public int getColumnWidth(int col) {
        int width = (sheet.getColumnWidth(col) / 256) * 6;
        return width;
    }

    public String getName() {
        return hssfWorkbook.getSheetName(hssfWorkbook.getSheetIndex(sheet));
    }

    public int getMaxRows() {
        return MAX_ROWS;
    }

    public int getMaxColumns() {
        return MAX_COLUMNS;
    }

    public void clearAllCells() {
        for(Iterator<Row> it = sheet.rowIterator(); it.hasNext(); ) {
            Row row = it.next();
            sheet.removeRow(row);
        }
    }

    public boolean isCellAt(int col, int row) {
    	//FIXME: is this always false??
        return false;
    }

    public Cell getCellAt(int col, int row) {
        HSSFRow hssfRow = sheet.getRow(row);
        if (hssfRow == null) {
            return null;
        }
        HSSFCell hssfCell = hssfRow.getCell(col);
        if (hssfCell == null) {
            return null;
        }
        else {
            return new CellHSSFImpl(hssfWorkbook, hssfCell);
        }
    }

    public Cell addCellAt(int col, int row) {
        HSSFRow hssfRow = sheet.getRow(row);
        if (hssfRow == null) {
            hssfRow = sheet.createRow(row);
        }
        HSSFCell cell = hssfRow.getCell(col);
        if (cell == null) {
            cell = hssfRow.createCell(col);
        }
        return new CellHSSFImpl(hssfWorkbook, cell);
    }

    public void clearCellAt(int col, int row) {
        HSSFRow theRow = sheet.getRow(row);
        if(theRow != null) {
            HSSFCell theCell = theRow.getCell(col);
            theCell.setCellValue("");
        }
    }            
        

    public Collection<Validation> getIntersectingValidations(Range range) {
        ArrayList<Validation> intersectingValidations = new ArrayList<Validation>();
        for (Validation validation : range.getSheet().getValidations()) {
            if (validation.getRange().intersectsRange(range)) {
                intersectingValidations.add(validation);
            }
        }
        return intersectingValidations;
    }

    public Collection<Validation> getContainingValidations(Range range) {
        ArrayList<Validation> containingValidations = new ArrayList<Validation>();
        for (Validation validation : range.getSheet().getValidations()) {
            if (validation.getRange().containsRange(range)) {
                containingValidations.add(validation);
            }
        }
        return containingValidations;
    }

    public void addValidation(String namedRange, int firstCol, int firstRow, int lastCol, int lastRow) {
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, DVConstraint.createFormulaListConstraint(namedRange));
        sheet.addValidationData(dataValidation);
    }

    public Collection<Validation> getValidations() {
        List<Validation> validationList = new ArrayList<Validation>();
        for (HSSFDataValidation validation : getValidationData()) {
            for (CellRangeAddress address : validation.getRegions().getCellRangeAddresses()) {
                validationList.add(new ValidationImpl(validation.getConstraint().getFormula1(), this, address.getFirstColumn(), address.getLastColumn(), address.getFirstRow(), address.getLastRow()));
            }

        }
        return validationList;
    }   
    
    public List<HSSFDataValidation> getValidationData() {    	    	
    	return PatchedPoi.getInstance().getValidationData(sheet, hssfWorkbook);
    }    
    
    public void clearValidationData() {
        PatchedPoi.getInstance().clearValidationData(sheet);
    }

}
