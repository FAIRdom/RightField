package uk.ac.manchester.cs.owl.semspreadsheets.ui.task;

import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 03-Feb-2010
 */
public abstract class AbstractTask<V, E extends Throwable> implements Task<V, E> {


    private WorkbookFrame workbookFrame;

    private int length;

    private int progress;

    private boolean cancelled = false;

    private List<TaskListener> listeners = new ArrayList<TaskListener>();

    public void setup(WorkbookFrame workbookFrame) {
        this.workbookFrame = workbookFrame;
    }

    public WorkbookFrame getWorkbookFrame() {
        return workbookFrame;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void addTaskListener(TaskListener listener) {
        listeners.add(listener);
    }

    public void removeTaskListener(TaskListener listener) {
        listeners.remove(listener);
    }

    public int getLength() {
        return length;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        fireProgressChanged();
    }

    public void setProgressIndeterminate() {
        this.progress = -1;
        fireProgressChanged();
    }

    public void setLength(int length) {
        this.length = length;
        fireLengthChanged();
    }

    protected void fireLengthChanged() {
        for(TaskListener lsnr : listeners) {
            lsnr.lengthChanged(this);
        }
    }

    protected void fireProgressChanged() {
        for(TaskListener lsnr : listeners) {
            lsnr.progressChanged(this);
        }
    }

    protected void fireMessageChanged() {
        for(TaskListener lsnr : listeners) {
            lsnr.messageChanged(this);
        }
    }


}
