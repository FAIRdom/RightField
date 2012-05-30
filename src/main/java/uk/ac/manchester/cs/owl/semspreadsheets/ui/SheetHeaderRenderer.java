/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
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

    @SuppressWarnings("serial")
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
