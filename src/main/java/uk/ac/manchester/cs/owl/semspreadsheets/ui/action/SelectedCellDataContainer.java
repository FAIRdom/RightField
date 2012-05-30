/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.io.Serializable;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationDescriptor;

/**
 * A lightweight container for storing the position on contents of a cell during a copy/cut and paste
 * process.
 * 
 * @author Stuart Owen
 *
 * @see CellContentsTransferable
 */
public class SelectedCellDataContainer implements Serializable {
	
	private static final long serialVersionUID = -367003586271754027L;

	/**
	 * The row of the cell
	 */
	public int row;
	
	/** 
	 * The column of the cell
	 */
	public int col;
	
	/**
	 * The OntologyTermValidationDescriptor for the cell, if it contains validations, otherwise null
	 */
	public OntologyTermValidationDescriptor validationDescriptor;
	
	/**
	 * The text value for the cell
	 */
	public String textValue;	
}
