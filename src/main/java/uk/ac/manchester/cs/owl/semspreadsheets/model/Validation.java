/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;


/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 */
public interface Validation {

    String getListName();

    Sheet getSheet();

    int getFirstColumn();

    int getLastColumn();

    int getFirstRow();

    int getLastRow();

    Range getRange();

    boolean contains(int col, int row);


}
