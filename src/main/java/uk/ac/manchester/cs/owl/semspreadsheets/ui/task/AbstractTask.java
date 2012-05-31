/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public abstract class AbstractTask<V, E extends Throwable> implements Task<V, E> {

	private static final Logger logger = Logger.getLogger(AbstractTask.class);

    private WorkbookFrame workbookFrame;

    private int length;

    private int progress;

    private boolean cancelled = false;

    private List<TaskListener> listeners = new ArrayList<TaskListener>();

    public void setup(WorkbookFrame workbookFrame) {
        this.workbookFrame = workbookFrame;
    }
    
    public boolean isCancelSupported() {
    	return false;
    }

    public WorkbookFrame getWorkbookFrame() {
        return workbookFrame;
    }

    public boolean isCancelled() {
        return cancelled;
    }
    
    public void cancelTask() {
    	setCancelled(true);
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        if (!isCancelSupported()) {
        	logger.error("Cancel selected for a task that does not support it");
        }
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
