package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.awt.Color;
import java.awt.Font;

import javax.swing.text.Style;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
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

    Style getStyle();

    String getValidationListName();

    boolean isEmpty();

    boolean isDataValidation();
}
