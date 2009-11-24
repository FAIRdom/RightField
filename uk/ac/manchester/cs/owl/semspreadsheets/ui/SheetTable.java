package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.*;

import javax.swing.*;
import javax.swing.table.TableColumn;
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
public class SheetTable extends JTable {

    private Workbook workbook;

    private Sheet sheet;

    private WorkbookManager workbookManager;


    public SheetTable(WorkbookManager ss, Sheet sheet) {
        this.workbookManager = ss;
        this.sheet = sheet;
        workbook = workbookManager.getWorkbook();


        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setModel(new SheetTableModel(sheet));

        setCellSelectionEnabled(true);

        SheetCellRenderer ren = new SheetCellRenderer();
        SpreadSheetCellEditor editor = new SpreadSheetCellEditor(sheet);

        for (int col = 0; col < sheet.getMaxColumns(); col++) {
            TableColumn column = getColumnModel().getColumn(col);
            column.setCellRenderer(ren);
            column.setCellEditor(editor);
            if (col % 2 == 0) {
                column.setPreferredWidth(sheet.getColumnWidth(col));
            }
            else {
                column.setPreferredWidth(sheet.getColumnWidth(col));
            }
        }

        setShowGrid(true);
        setRowMargin(1);
        setGridColor(new Color(206, 206, 206));


    }


    public Sheet getSheet() {
        return sheet;
    }


    private Stroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);

    private AlphaComposite alphaComposite2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f);

    private Color color = new Color(50, 150, 60);

    private Color emptyValidationColor = Color.DARK_GRAY;

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color oldColor = g.getColor();
        Graphics2D g2 = (Graphics2D) g;
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(stroke);
        for (OntologyTermValidation ontologyTermValidation : workbookManager.getOntologyTermValidation()) {
            if (ontologyTermValidation.getRange().getSheet().equals(sheet)) {
                Range validation = ontologyTermValidation.getRange();
                if(ontologyTermValidation.getValidationDescriptor().getTerms().isEmpty()) {
                    g.setColor(emptyValidationColor);
                }
                else {
                    g.setColor(color);
                }
                Rectangle startRect = getCellRect(validation.getFromRow(), validation.getFromColumn(), false);
                Rectangle endRect = getCellRect(validation.getToRow(), validation.getToColumn(), false);
                int x1 = startRect.x + 1;
                int y1 = startRect.y + 1;
                int width = endRect.width + endRect.x - startRect.x - 2;
                int height = endRect.y + endRect.height - startRect.y - 2;
                Rectangle rect = new Rectangle(x1, y1, width, height);
//                g2.drawString(validation.getListName(), x1 + 3, y1 + 10);
                Composite oldComposite = g2.getComposite();
                g2.setComposite(alphaComposite2);
                g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 5, 5);
                g2.setComposite(alphaComposite);
                g2.fill(rect);
                g2.setComposite(oldComposite);

            }
        }

//        for (Validation validation : sheet.getValidations()) {
//
//            Rectangle startRect = getCellRect(validation.getFirstRow(), validation.getFirstColumn(), false);
//            Rectangle endRect = getCellRect(validation.getLastRow(), validation.getLastColumn(), false);
//            int x1 = startRect.x + 1;
//            int y1 = startRect.y + 1;
//            int width = endRect.width + endRect.x - startRect.x - 2;
//            int height = endRect.y + endRect.height - startRect.y - 2;
//            Rectangle rect = new Rectangle(x1, y1, width, height);
//            g2.drawString(validation.getListName(), x1 + 3, y1 + 10);
//            Composite oldComposite = g2.getComposite();
//            g2.setComposite(alphaComposite2);
//            g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 5, 5);
//            g2.setComposite(alphaComposite);
//            g2.fill(rect);
//            g2.setComposite(oldComposite);
//
//        }

        g2.setStroke(oldStroke);
        g.setColor(oldColor);
    }
}
