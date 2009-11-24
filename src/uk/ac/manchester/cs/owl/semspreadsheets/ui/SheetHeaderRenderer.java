package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import javax.swing.table.TableCellRenderer;
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
 * Date: 31-Oct-2009
 */
public class SheetHeaderRenderer implements TableCellRenderer {


    /**
     * Returns the component used for drawing the cell.  This method is
     * used to configure the renderer appropriately before drawing.
     * @param    table        the <code>JTable</code> that is asking the
     * renderer to draw; can be <code>null</code>
     * @param    value        the value of the cell to be rendered.  It is
     * up to the specific renderer to interpret
     * and draw the value.  For example, if
     * <code>value</code>
     * is the string "true", it could be rendered as a
     * string or it could be rendered as a check
     * box that is checked.  <code>null</code> is a
     * valid value
     * @param    isSelected    true if the cell is to be rendered with the
     * selection highlighted; otherwise false
     * @param    hasFocus    if true, render cell appropriately.  For
     * example, put a special border on the cell, if
     * the cell can be edited, render in the color used
     * to indicate editing
     * @param    row     the row index of the cell being drawn.  When
     * drawing the header, the value of
     * <code>row</code> is -1
     * @param    column     the column index of the cell being drawn
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        renderingPanel.setText(value.toString());
        int colWidth = table.getColumnModel().getColumn(column).getWidth();
        renderingPanel.selected = false;
        for(int selCol : table.getSelectedColumns()) {
            if(column == selCol) {
                renderingPanel.selected = true;
                break;
            }
        }
        renderingPanel.setPreferredSize(new Dimension(colWidth, table.getRowHeight()));
        return renderingPanel;
    }


    private RenderingPanel renderingPanel = new RenderingPanel();

    private class RenderingPanel extends JPanel {

        private String text = "A";

        private boolean selected = false;

        public void setText(String text) {
            this.text = text;
        }

        /**
         * Calls the UI delegate's paint method, if the UI delegate
         * is non-<code>null</code>.  We pass the delegate a copy of the
         * <code>Graphics</code> object to protect the rest of the
         * paint code from irrevocable changes
         * (for example, <code>Graphics.translate</code>).
         * If you override this in a subclass you should not make permanent
         * changes to the passed in <code>Graphics</code>. For example, you
         * should not alter the clip <code>Rectangle</code> or modify the
         * transform. If you need to do these operations you may find it
         * easier to create a new <code>Graphics</code> from the passed in
         * <code>Graphics</code> and manipulate it. Further, if you do not
         * invoker super's implementation you must honor the opaque property,
         * that is
         * if this component is opaque, you must completely fill in the background
         * in a non-opaque color. If you do not honor the opaque property you
         * will likely see visual artifacts.
         * The passed in <code>Graphics</code> object might
         * have a transform other than the identify transform
         * installed on it.  In this case, you might get
         * unexpected results if you cumulatively apply
         * another transform.
         * @param g the <code>Graphics</code> object to protect
         * @see #paint
         * @see javax.swing.plaf.ComponentUI
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            SheetPanel.paintBorderCell(g, text, new Rectangle(0, 0, getWidth() - 1, getHeight() - 1), selected);
            g.setColor(SheetPanel.MARGIN_GRID_COLOR);
            g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1);
            g.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
        }
    }
}
