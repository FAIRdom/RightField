/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.Collection;
import java.util.List;

/**
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public interface Sheet {

    String getName();
    
    int getIndex();

    void setName(String name);

    Workbook getWorkbook();

    boolean isHidden();
    
    boolean isVeryHidden();

    void setVeryHidden(boolean b);   

    int getMaxRows();

    int getMaxColumns();

    int getColumnWidth(int col);

    void clearAllCells();    

    Cell getCellAt(int col, int row);

    Cell addCellAt(int col, int row);

    void clearCellAt(int col, int row);
    
    List<Cell> getCellsWithContent();

    /**
     * Creates a validation with a named range, to created a restricted drop down list
     * @param namedRange
     * @param firstCol
     * @param firstRow
     * @param lastCol
     * @param lastRow
     */
    void addValidation(String namedRange, int firstCol, int firstRow, int lastCol, int lastRow); 
    
    /**
     * Create a custom validation embedding property that allows free text. Only affects the RDF generated, there are no restrictions for the spreadsheet user.
     * 
     * @param hiddenSheetName
     * @param firstCol
     * @param firstRow
     * @param lastCol
     * @param lastRow
     */
    void addLiteralValidation(String hiddenSheetName, int firstCol, int firstRow, int lastCol, int lastRow); 

    Collection<Validation> getValidations();

    Collection<Validation> getIntersectingValidations(Range range);

    Collection<Validation> getContainingValidations(Range range);
    
    Collection<?> getValidationData();

    void clearValidationData();

}
