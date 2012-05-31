/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.AbstractTask;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class LoadEmbeddedTermsOntologies extends AbstractTask<Object,RuntimeException> {

    public Object runTask() throws RuntimeException {
        getWorkbookFrame().getWorkbookManager().loadEmbeddedTermOntologies();
        return null;
    }

    public String getTitle() {
        return "Loading embedded ontologies";
    }
}
