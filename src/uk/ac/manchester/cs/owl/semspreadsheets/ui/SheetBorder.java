package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

import javax.swing.border.Border;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.JTableHeader;
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
 * Date: 30-Oct-2009
 */
public class SheetBorder implements Border, Disposable {


    private static final int WIDTH = 50;

    private static final Insets INSETS = new Insets(0, WIDTH, 0, 0);

    private WorkbookManager sheetManager;

    private SheetPanel sheetPanel;

    private Font font;
    private CellSelectionListener cellSelectionListener;


    public SheetBorder(WorkbookManager sheetManager, SheetPanel panel) {
        this.sheetManager = sheetManager;
        this.sheetPanel = panel;
        font = new Font("verdana", Font.BOLD, 10);
        final JViewport vp = sheetPanel.getViewport();
        if (vp != null) {
            vp.addChangeListener(new ChangeListener() {
                /**
                 * Invoked when the target of the listener has changed its state.
                 * @param e a ChangeEvent object
                 */
                public void stateChanged(ChangeEvent e) {
                    vp.getParent().repaint(0, 0, WIDTH, vp.getHeight());
                }
            });
        }
        cellSelectionListener = new CellSelectionListener() {
            public void selectionChanged(Range range) {
                sheetPanel.repaint(0, 0, WIDTH, sheetPanel.getHeight());
            }
        };
        sheetManager.getSelectionModel().addCellSelectionListener(cellSelectionListener);
    }

    public void dispose() {
        sheetManager.getSelectionModel().removeCellSelectionListener(cellSelectionListener);
    }

    /**
     * Paints the border for the specified component with the specified
     * position and size.
     * @param c      the component for which this border is being painted
     * @param g      the paint graphics
     * @param x      the x position of the painted border
     * @param y      the y position of the painted border
     * @param width  the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        SheetTable table = sheetPanel.getSheetTable();
        JTableHeader header = table.getTableHeader();
        Color oldColor = g.getColor();
        Font oldFont = g.getFont();
        g.setFont(font);
        JViewport vp = (JViewport) table.getParent();
        Rectangle viewRect = vp.getViewRect();

        int rowHeight = table.getRowHeight();
        int firstRowY0 = header.getHeight();        
        int rowOffset = viewRect.y % rowHeight;
        int startRow = viewRect.y / rowHeight;
        int endRow = ((viewRect.y + viewRect.height) - firstRowY0) / rowHeight + 2;
        Sheet sheet = table.getSheet();
        Range range = sheetManager.getSelectionModel().getSelectedRange();
        boolean isSheetSel = range != null && sheet.getName().equals(range.getSheet().getName());

        for(int i = startRow; i < endRow; i++) {
            int rowMargin = table.getRowMargin();
            int viewportRow = i - startRow;
            int rowY0 = firstRowY0 + (rowHeight * viewportRow) -  rowOffset;
            int rowY1 = rowY0 + rowHeight - 2 * rowMargin;
            int rowX0 = x;
            int rowX1 = rowX0 + WIDTH - 1;
            int rowNum = i + 1;
            String str = Integer.toString(rowNum);
            Rectangle rect = new Rectangle(rowX0, rowY0, WIDTH - 1, rowHeight - 2 * rowMargin);

            boolean sel = false;
            if(isSheetSel) {
                int fromRow = range.getFromRow();
                int toRow = range.getToRow();
                sel = fromRow <= i && i <= toRow;
                g.setColor(SheetPanel.SELECTED_CELL_MARGIN_COLOR);
            }
            else {
                g.setColor(SheetPanel.BACK_GROUND_COLOR);
            }
            g.fillRect(rowX0, rowY0, WIDTH, rowHeight);
            SheetPanel.paintBorderCell(g, str, rect, sel);
            g.setColor(SheetPanel.MARGIN_GRID_COLOR);
            g.drawLine(rowX0, rowY1 + 1, rowX1, rowY1 + 1);
            g.drawLine(rowX1, rowY0, rowX1, rowY1 + 1);
        }
        g.setColor(SheetPanel.BACK_GROUND_COLOR);
        int headerHeight = table.getTableHeader().getHeight();
        g.fillRect(0, 0, WIDTH, headerHeight);
        g.setColor(SheetPanel.MARGIN_GRID_COLOR);
        g.drawLine(0, headerHeight - 1, WIDTH - 1, headerHeight - 1);
        g.drawLine(WIDTH - 1, 0, WIDTH - 1, headerHeight - 1);
        g.setColor(SheetPanel.TEXT_COLOR);
//        int centreX = WIDTH / 2;
//        int centreY = headerHeight / 2;
//        g.drawLine(centreX - 7, centreY, centreX, centreY - 5);
//        g.drawLine(centreX, centreY - 5, centreX + 7, centreY);
//        g.drawLine(centreX - 7, centreY, centreX, centreY + 5);
//        g.drawLine(centreX, centreY + 5, centreX + 7, centreY);

        g.setFont(oldFont);
        g.setColor(oldColor);
    }



    /**
     * Returns the insets of the border.
     * @param c the component for which this border insets value applies
     */
    public Insets getBorderInsets(Component c) {
        return INSETS;
    }

    /**
     * Returns whether or not the border is opaque.  If the border
     * is opaque, it is responsible for filling in it's own
     * background when painting.
     */
    public boolean isBorderOpaque() {
        return false;
    }


}
