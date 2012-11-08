package uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.PropertyValidationForumlaDefinition;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Validation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.impl.ValidationImpl;

/**
 * @author Stuart Owen
 */
public class SheetXSSFImpl implements Sheet {

    private WorkbookXSSFImpl workbook;

    private XSSFWorkbook hssfWorkbook;       

    private XSSFSheet sheet;

    private static final short MAX_ROWS = Short.MAX_VALUE;

    private static final int MAX_COLUMNS = 256;
    
    private static final Logger logger = Logger.getLogger(SheetXSSFImpl.class);


    public SheetXSSFImpl(WorkbookXSSFImpl workbook, XSSFSheet hssfSheet) {
        this.workbook = workbook;
        this.hssfWorkbook = workbook.getXSSFWorkbook();
        this.sheet = hssfSheet;
    }

    public Workbook getWorkbook() {
        return workbook;
    }
    
    public List<Cell> getCellsWithContent() {
    	List<Cell> cells = new ArrayList<Cell>();
    	int firstRow = sheet.getFirstRowNum();
    	int lastRow = sheet.getLastRowNum();
    	for (int rowIndex = firstRow ; rowIndex <= lastRow; rowIndex++) {
    		XSSFRow row = sheet.getRow(rowIndex);
    		if (row!=null) {
    			int firstCell = row.getFirstCellNum();
        		int lastCell = row.getLastCellNum();
        		for (int cellIndex = firstCell ; cellIndex <= lastCell;cellIndex++) {
        			XSSFCell cell = row.getCell(cellIndex);
        			if (cell!=null && !cell.getStringCellValue().isEmpty()) {
        				cells.add(new CellXSSFImpl(hssfWorkbook, cell));
        			}
        		} 
    		}    		    		
    	}
    	return cells;
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
        if (!(obj instanceof SheetXSSFImpl)) {
            return false;
        }
        SheetXSSFImpl other = (SheetXSSFImpl) obj;
        return sheet == other.sheet;
    }

    public XSSFSheet getHSSFSheet() {
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

    public Cell getCellAt(int col, int row) {
        XSSFRow hssfRow = sheet.getRow(row);
        if (hssfRow == null) {
            return null;
        }
        XSSFCell hssfCell = hssfRow.getCell(col);
        if (hssfCell == null) {
            return null;
        }
        else {
            return new CellXSSFImpl(hssfWorkbook, hssfCell);
        }
    }

    public Cell addCellAt(int col, int row) {
        XSSFRow hssfRow = sheet.getRow(row);
        if (hssfRow == null) {
            hssfRow = sheet.createRow(row);
        }
        XSSFCell cell = hssfRow.getCell(col);
        if (cell == null) {
            cell = hssfRow.createCell(col);
        }
        return new CellXSSFImpl(hssfWorkbook, cell);
    }

    public void clearCellAt(int col, int row) {
        XSSFRow theRow = sheet.getRow(row);
        if(theRow != null) {
            XSSFCell theCell = theRow.getCell(col);
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
    
    /**
     * Creates a custom validation that embeds the hidden sheet name (that contains the ontology details) .
     * e.g
     * =AND(A1<>"propliteral^wksowlv0")
     * this embeds the information, without restricting the use of the field (except the highly unlikely case of wanting to type the encoded string).
     */
    public void addLiteralValidation(String hiddenSheetName, int firstCol, int firstRow, int lastCol, int lastRow) {
    	String encoded = PropertyValidationForumlaDefinition.encode(hiddenSheetName);
    	
    	//the cell title A1 is irrelevant, when the sheet is saved it gets turned into the current cell.
    	String formula="AND(A1<>\""+encoded+"\")";    	
    	
    	CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol); 
    	DataValidationConstraint constraint = sheet.getDataValidationHelper().createCustomConstraint(formula);
    	DataValidation dataValidation = sheet.getDataValidationHelper().createValidation(constraint, addressList);
        sheet.addValidationData(dataValidation);
    }

    public void addValidation(String namedRange, int firstCol, int firstRow, int lastCol, int lastRow) {
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);        
        DataValidationConstraint constraint = sheet.getDataValidationHelper().createFormulaListConstraint(namedRange);
        DataValidation dataValidation = sheet.getDataValidationHelper().createValidation(constraint, addressList);
        sheet.addValidationData(dataValidation);
    }

    public Collection<Validation> getValidations() {
        List<Validation> validationList = new ArrayList<Validation>();
        for (XSSFDataValidation validation : getValidationData()) {
            for (CellRangeAddress address : validation.getRegions().getCellRangeAddresses()) {
            	String formula1=validation.getValidationConstraint().getFormula1();            	
                validationList.add(new ValidationImpl(formula1, this, address.getFirstColumn(), address.getLastColumn(), address.getFirstRow(), address.getLastRow()));
            }
        }
        return validationList;
    }
    
    public int getIndex() {    	
    	for (int index = 0 ; index < getWorkbook().getSheets().size(); index++) {
    		if (getWorkbook().getSheet(index).equals(this)) {
    			return index;
    		}
    	}
    	return -1;
    }
    
    protected List<XSSFDataValidation> getValidationData() {    	    	    	
    	return sheet.getDataValidations();
    }    
    
    public void clearValidationData() {    	
    	if (sheet.getCTWorksheet().getDataValidations() != null) {    		
	    	for (int i=0;i<sheet.getCTWorksheet().getDataValidations().getCount();i++) {
	    		try {
	    			sheet.getCTWorksheet().getDataValidations().removeDataValidation(0);
	    		}
	    		catch(IndexOutOfBoundsException e) {
	    			//FIXME: currently, getCount seems to return 1 when there are no validation, or 1 when there is 1 validation, and so far haven't found
	    			//a way of distinguishing.
	    			logger.debug("Index out of bounds removing validation (probably caused by getCount returning 1 when there are zero");
	    		}
	    	}        
    	}
    }

}
