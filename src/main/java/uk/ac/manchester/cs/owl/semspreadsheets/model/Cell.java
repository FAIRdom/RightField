/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.awt.Color;
import java.awt.Font;

/**
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public interface Cell {

    int getRow();

    int getColumn();      

    String getValue();

    void setValue(String value);

    Font getFont();

    Color getForeground();
    
    Color getBackgroundFill();

    int getAlignment();

    String getComment();

    boolean isStrikeThrough();

    boolean isUnderline();

    boolean isItalic();

    boolean isBold();

    void setBold(boolean b);
    
    void setBackgroundFill(Color colour);

    void setBorders(Color colour);

    String getValidationListName();
    
    String getSheetName();
    
    int getSheetIndex();

    boolean isEmpty();

    void setCellStyleFormula();

    void setCellFormula(String formula);
}
