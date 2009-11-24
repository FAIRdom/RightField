package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.*;
import uk.ac.manchester.cs.owl.semspreadsheets.change.SetCellValue;

import javax.swing.table.AbstractTableModel;
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
public class SheetTableModel extends AbstractTableModel implements SpreadSheetListener {

    private Sheet sheet;

    public SheetTableModel(Sheet sheet) {
        this.sheet = sheet;
//        sheet.addSpreadSheetListener(this);
    }

    public void sheetAdded(Sheet sheet) {

    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
        return sheet.getMaxRows();
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
        return sheet.getMaxColumns();
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     * @param    rowIndex    the row whose value is to be queried
     * @param    columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return sheet.getCellAt(columnIndex, rowIndex);
    }

    /**
     * Returns false.  This is the default implementation for all cells.
     * @param rowIndex    the row being queried
     * @param columnIndex the column being queried
     * @return false
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    /**
     * This empty implementation is provided so users don't have to implement
     * this method if their data model is not editable.
     * @param aValue      value to assign to cell
     * @param rowIndex    row of cell
     * @param columnIndex column of cell
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Workbook workbook = sheet.getWorkbook();
        Cell cell = sheet.getCellAt(columnIndex, rowIndex);
        Object oldValue = null;
        if(cell != null) {
            oldValue = cell.getValue();
        }
        workbook.applyChange(new SetCellValue(workbook, sheet, columnIndex, rowIndex, oldValue, aValue));
        fireTableCellUpdated(rowIndex, columnIndex);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  Implementation of Spreadsheet listener
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void cellAdded(Sheet sheet, Cell cell) {
        fireTableCellUpdated(cell.getRow(), cell.getColumn());
    }

    public void cellRemoved(Sheet sheet, Cell cell) {
        fireTableCellUpdated(cell.getRow(), cell.getColumn());
    }

    public void allCellsRemoved() {
        fireTableDataChanged();
    }

    public void cellContentsChanged(Sheet sheet, Cell cell) {
        fireTableCellUpdated(cell.getRow(), cell.getColumn());
    }
}
