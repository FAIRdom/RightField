/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.io.OutputStream;

public interface Exporter {
	
	public void export(OutputStream outStream);
	
	public String export();

}
