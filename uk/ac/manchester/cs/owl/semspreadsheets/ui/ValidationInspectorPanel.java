package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
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
 * Date: 08-Nov-2009
 */
public class ValidationInspectorPanel extends JPanel {

    private static Font font = new Font("Lucida Grande", Font.BOLD, 11);

    private WorkbookManager workbookManager;

    private JLabel selectedCellAddressLabel = new JLabel("No cells selected");

    private static Color textColor = new Color(96, 110, 128);

    public ValidationInspectorPanel(WorkbookFrame frame) {
        workbookManager = frame.getWorkbookManager();
        setLayout(new BorderLayout(14, 14));
        setBorder(BorderFactory.createEmptyBorder(7, 2, 7, 7));
        add(selectedCellAddressLabel, BorderLayout.NORTH);
//        selectedCellAddressLabel.setFont(font.deriveFont(Font.PLAIN, 16.0f));
//        selectedCellAddressLabel.setForeground(textColor);
        JPanel outerPanel = new JPanel(new BorderLayout(7, 7));
        add(outerPanel);
        outerPanel.setLayout(new BorderLayout(7, 7));

        ClassHierarchyTreePanel classHierarchyTreePanel = new ClassHierarchyTreePanel(frame);
        classHierarchyTreePanel.setBorder(createTitledBorder("HIERARCHY"));
        outerPanel.add(classHierarchyTreePanel);

        JPanel innerPanel = new JPanel(new BorderLayout(7, 7));
        outerPanel.add(innerPanel, BorderLayout.SOUTH);
        ValidationTypeSelectorPanel typeSelectorPanel = new ValidationTypeSelectorPanel(frame.getWorkbookManager());
        typeSelectorPanel.setBorder(createTitledBorder("TYPE OF ALLOWED VALUES"));
        innerPanel.add(typeSelectorPanel, BorderLayout.NORTH);
        
        ValidationValuesPanel valuesPanel = new ValidationValuesPanel(frame.getWorkbookManager());
        valuesPanel.setBorder(createTitledBorder("ALLOWED VALUES"));
        innerPanel.add(valuesPanel, BorderLayout.SOUTH);
        frame.getWorkbookManager().getSelectionModel().addCellSelectionListener(new CellSelectionListener() {
            public void selectionChanged(Range range) {
                updateSelectionLabel();
            }
        });
        updateSelectionLabel();
    }

    private void updateSelectionLabel() {
        Range selectedRange = workbookManager.getSelectionModel().getSelectedRange();
        if (selectedRange.isCellSelection()) {
            selectedCellAddressLabel.setText("CELLS: " + selectedRange.getColumnRowAddress());
        }
        else {
            selectedCellAddressLabel.setText("");
        }
    }


    private static Border createTitledBorder(String title) {
        Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);

        Border titledBorder = BorderFactory.createTitledBorder(border, title,
                TitledBorder.LEFT, TitledBorder.TOP, font, textColor);
        Border innerBorder = BorderFactory.createEmptyBorder(3, 20, 0, 0);
        return BorderFactory.createCompoundBorder(titledBorder, innerBorder);

    }

}
