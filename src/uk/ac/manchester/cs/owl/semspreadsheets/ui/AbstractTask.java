package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.ArrayList;
import java.util.List;
/*
 * Copyright (C) 2010, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
