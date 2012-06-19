/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;


/**
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public interface Validation {

    String getFormula();

    Sheet getSheet();

    int getFirstColumn();

    int getLastColumn();

    int getFirstRow();

    int getLastRow();
    
    boolean isDataValidation();

    Range getRange();

    boolean contains(int col, int row);


}
