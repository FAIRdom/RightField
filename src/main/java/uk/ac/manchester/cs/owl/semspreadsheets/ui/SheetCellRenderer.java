/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class SheetCellRenderer extends DefaultTableCellRenderer {
   
    public SheetCellRenderer() {

    }

    /**
     * Returns the default table cell renderer.
     * @param table      the <code>JTable</code>
     * @param value      the value to assign to the cell at
     *                   <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus   true if cell has focus
     * @param row        the row of the cell to render
     * @param column     the column of the cell to render
     * @return the default table cell renderer
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Cell cell = (Cell) value;       

        if (cell != null && !cell.isEmpty()) {
            label.setForeground(cell.getForeground());
            label.setFont(cell.getFont());
            label.setText(cell.getValue());
            if (cell.isUnderline() || cell.isStrikeThrough()) {
                StringBuilder sb = new StringBuilder();
                sb.append("<html><body>");
                if (cell.isUnderline()) {
                    sb.append("<u>");
                }
                if (cell.isStrikeThrough()) {
                    sb.append("<s>");
                }
                sb.append(cell.getValue());
                if (cell.isUnderline()) {
                    sb.append("</u>");
                }
                if (cell.isStrikeThrough()) {
                    sb.append("</s>");
                }
                sb.append("</body></html>");
                label.setText(sb.toString());
            }

        }
        if (isSelected) {
            label.setBackground(SheetPanel.SELECTION_HIGHLIGHT);
        }
        else {
            label.setBackground(Color.WHITE);
        }
        return label;
    }
}
