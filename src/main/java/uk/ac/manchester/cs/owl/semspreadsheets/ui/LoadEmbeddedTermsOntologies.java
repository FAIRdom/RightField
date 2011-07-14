package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.AbstractTask;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 03-Feb-2010
 */
public class LoadEmbeddedTermsOntologies extends AbstractTask<Object,RuntimeException> {


    public Object runTask() throws RuntimeException {
        getWorkbookFrame().getWorkbookManager().loadEmbeddedTermOntologies();
        return null;
    }

    public String getTitle() {
        return "Loading embedded ontologies";
    }

    public void cancelTask() {
    }
}
