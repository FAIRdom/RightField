package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Stroke;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.Style;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 */
public class SheetCellRenderer extends DefaultTableCellRenderer {

    private boolean comment = false;

    private boolean validation = false;

    private static Color COMMENT_COLOR = new Color(221, 8, 6);


    private Stroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);

    private Color color = new Color(50, 150, 60);


    private Style style;

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
        comment = false;
        validation = false;

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
