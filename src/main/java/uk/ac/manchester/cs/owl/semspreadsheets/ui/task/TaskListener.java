/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.task;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 03-Feb-2010
 */
public interface TaskListener {
    
	@SuppressWarnings("rawtypes")
	void messageChanged(Task task);
    
	@SuppressWarnings("rawtypes")
	void lengthChanged(Task task);
    
	@SuppressWarnings("rawtypes")
	void progressChanged(Task task);
}
