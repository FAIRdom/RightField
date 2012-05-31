/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.Collection;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 */
public interface Sheet {

    String getName();
    
    int getIndex();

    void setName(String name);

    Workbook getWorkbook();

    boolean isHidden();
    
    boolean isVeryHidden();

    void setVeryHidden(boolean b);

    void setHidden(boolean b);

    int getMaxRows();

    int getMaxColumns();

    int getColumnWidth(int col);

    void clearAllCells();    

    Cell getCellAt(int col, int row);

    Cell addCellAt(int col, int row);

    void clearCellAt(int col, int row);

    void addValidation(String namedRange, int firstCol, int firstRow, int lastCol, int lastRow);    

    Collection<Validation> getValidations();

    Collection<Validation> getIntersectingValidations(Range range);

    Collection<Validation> getContainingValidations(Range range);

    void clearValidationData();

}
