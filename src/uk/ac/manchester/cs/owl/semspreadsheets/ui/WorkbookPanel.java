package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeEvent;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
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
 * Date: 31-Oct-2009
 */
public class WorkbookPanel extends JPanel {

    private WorkbookManager manager;

    private JTabbedPane tabbedPane;


    private boolean transmittingSelectionToModel = false;

    private boolean updatingSelectionFromModel = false;

    private boolean rebuildingTabs = false;
    private WorkbookChangeListener workbookChangeListener;


    public WorkbookPanel(WorkbookManager workbookManager) {
        this.manager = workbookManager;
        tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        workbookChangeListener = new WorkbookChangeListener() {
            public void workbookChanged(WorkbookChangeEvent event) {
                System.out.println("WB CHG: " + event);
            }

            public void sheetAdded() {
                rebuildTabs();
            }

            public void sheetRemoved() {
                rebuildTabs();
            }

            public void sheetRenamed(String oldName, String newName) {
                for(int i = 0; i < tabbedPane.getTabCount(); i++) {
                    if(tabbedPane.getTitleAt(i).equals(oldName)) {
                        tabbedPane.setTitleAt(i, newName);
                    }
                }
            }
        };
        manager.getWorkbook().addChangeListener(workbookChangeListener);
        rebuildTabs();
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                transmitSelectionToModel();
            }
        });
        workbookManager.getSelectionModel().addCellSelectionListener(new CellSelectionListener() {
            public void selectionChanged(Range range) {
                updateSelectionFromModel();
            }
        });
        workbookManager.addListener(new WorkbookManagerListener() {
            public void workbookChanged(WorkbookManagerEvent event) {
                rebuildTabs();
                transmitSelectionToModel();
            }

            public void workbookLoaded(WorkbookManagerEvent event) {
                rebuildTabs();
                transmitSelectionToModel();
            }

            public void ontologiesChanged(WorkbookManagerEvent event) {
            }
        });


    }

    private void rebuildTabs() {
        manager.getWorkbook().addChangeListener(workbookChangeListener);
        rebuildingTabs = true;
        for(int i = 0; i < tabbedPane.getTabCount(); i++) {
            SheetPanel sheetPanel = (SheetPanel) tabbedPane.getComponentAt(i);
            sheetPanel.dispose();
        }
        tabbedPane.removeAll();
        Workbook workbook = manager.getWorkbook();
        for (Sheet sheet : workbook.getSheets()) {
            if (!sheet.isHidden()) {
                SheetPanel sheetPanel = new SheetPanel(manager, sheet);
                tabbedPane.add(sheet.getName(), sheetPanel);
            }
        }
        rebuildingTabs = false;
    }

    private void updateSelectionFromModel() {
        if(rebuildingTabs || transmittingSelectionToModel) {
            return;
        }
        try {
            updatingSelectionFromModel = true;
            Range range = manager.getSelectionModel().getSelectedRange();
            setSelectedSheet(range.getSheet());
        }
        finally {
            updatingSelectionFromModel = false;
        }
    }

    private SheetPanel getSelectedSheetPanel() {
        return (SheetPanel) tabbedPane.getSelectedComponent();
    }


    private void transmitSelectionToModel() {
        if(rebuildingTabs || updatingSelectionFromModel) {
            return;
        }
        try {
            transmittingSelectionToModel = true;
            Range range = getSelectedSheetPanel().getSelectedRange();
            manager.getSelectionModel().setSelectedRange(range);
        }
        finally {
            transmittingSelectionToModel = false;
        }

    }

    public Sheet getSelectedSheet() {
        SheetPanel sheetPanel = (SheetPanel) tabbedPane.getSelectedComponent();
        return sheetPanel.getSheet();
    }

    public void setSelectedSheet(Sheet sheet) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            SheetPanel sheetPanel = (SheetPanel) tabbedPane.getComponentAt(i);
            if (sheetPanel.getSheet().getName().equals(sheet.getName())) {
                tabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }


}
