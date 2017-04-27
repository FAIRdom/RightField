/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.JTableHeader;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.CellSelectionListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
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
