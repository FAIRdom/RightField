/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.listeners;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;

/**
 * @author Matthew Horridge
 */
public interface CellSelectionListener {

    void selectionChanged(Range range);
}
