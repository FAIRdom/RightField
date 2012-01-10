package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
@SuppressWarnings("serial")
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
        JPanel outerPanel = new JPanel(new BorderLayout(7, 7));
        
        add(outerPanel);
        outerPanel.setLayout(new BorderLayout(7, 7));

        ClassHierarchyTreePanel classHierarchyTreePanel = new ClassHierarchyTreePanel(frame);
        classHierarchyTreePanel.setBorder(createTitledBorder("HIERARCHY"));
        outerPanel.add(classHierarchyTreePanel);        
        
        ValidationValuesPanel valuesPanel = new ValidationValuesPanel(frame.getWorkbookManager());
        valuesPanel.setBorder(createTitledBorder("ALLOWED VALUES"));
        
        JPanel innerPanel = new JPanel(new BorderLayout(7, 7));        
        outerPanel.add(innerPanel, BorderLayout.SOUTH);
        ValidationTypeSelectorPanel typeSelectorPanel = new ValidationTypeSelectorPanel(frame.getWorkbookManager());
        
        typeSelectorPanel.setBorder(createTitledBorder("TYPE OF ALLOWED VALUES"));
        innerPanel.add(typeSelectorPanel, BorderLayout.NORTH);
        
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
