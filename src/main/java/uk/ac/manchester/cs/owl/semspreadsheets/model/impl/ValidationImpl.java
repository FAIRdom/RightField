/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model.impl;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationSheetParser;
import uk.ac.manchester.cs.owl.semspreadsheets.model.PropertyValidationForumlaDefinition;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Validation;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class ValidationImpl implements Validation {
	
	private static final Logger logger = Logger.getLogger(ValidationImpl.class);

    private String formula;

    private Sheet sheet;

    private int fromColumn;

    private int toColumn;

    private int fromRow;

    private int toRow;

    public ValidationImpl(String formula, Sheet sheet, int fromColumn, int toColumn, int fromRow, int toRow) {
    	logger.debug("Creating ValidationImpl with formula:'"+formula+"' for sheet: "+sheet.getIndex());
        this.formula = formula;
        this.sheet = sheet;
        this.fromColumn = fromColumn;
        this.toColumn = toColumn;
        this.fromRow = fromRow;
        this.toRow = toRow;
    }
    
    public boolean isDataValidation() {
    	return (formula!=null && formula.startsWith(OntologyTermValidationSheetParser.VALIDATION_SHEET_PREFIX));
    }
    
    public boolean isLiteralValidation() {
    	return (formula!=null && PropertyValidationForumlaDefinition.valid(formula));
    }

    public Range getRange() {
        return new Range(sheet, fromColumn, fromRow, toColumn, toRow);
    }

    public Sheet getSheet() {
        return sheet;
    }

    public String getFormula() {
        return formula;
    }

    public int getFirstColumn() {
        return fromColumn;
    }

    public int getLastColumn() {
        return toColumn;
    }

    public int getFirstRow() {
        return fromRow;
    }

    public int getLastRow() {
        return toRow;
    }

    public boolean contains(int col, int row) {
        return fromColumn >= col && row <= toColumn && row >= fromRow && row <= toRow;
    }
}
