package uk.ac.manchester.cs.owl.semspreadsheets.ui.task;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;


/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 03-Feb-2010
 */
public interface Task<V, E extends Throwable> {

    void setup(WorkbookFrame workbookFrame);

    V runTask() throws E;

    String getTitle();

    int getProgress();

    int getLength();

    void addTaskListener(TaskListener listener);

    void removeTaskListener(TaskListener listener);

    void cancelTask();

}
