package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

import javax.swing.*;
import java.awt.*;
/*
 * Copyright (C) 2009, University of Manchester
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
 * Date: 18-Sep-2009
 */
public class MainPanel extends JPanel {

    private WorkbookManager workbookManager;

    private WorkbookPanel workbookPanel;


    public MainPanel(WorkbookFrame frame) {
        this.workbookManager = frame.getWorkbookManager();
        workbookPanel = new WorkbookPanel(workbookManager);
        setLayout(new BorderLayout());
        JSplitPane sp = new JSplitPane();
        sp.setLeftComponent(workbookPanel);
        sp.setRightComponent(new ValidationInspectorPanel(frame));
        sp.setResizeWeight(0.8);
        add(sp);
    }

  
    public WorkbookManager getSpreadSheetManager() {
        return workbookManager;
    }

}
