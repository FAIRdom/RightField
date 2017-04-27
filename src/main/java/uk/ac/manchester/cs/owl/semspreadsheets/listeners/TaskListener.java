/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.listeners;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.Task;

/**
 * @author Matthew Horridge
 */
public interface TaskListener {
    
	@SuppressWarnings("rawtypes")
	void messageChanged(Task task);
    
	@SuppressWarnings("rawtypes")
	void lengthChanged(Task task);
    
	@SuppressWarnings("rawtypes")
	void progressChanged(Task task);
}
