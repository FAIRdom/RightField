/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;

/**
 * @author Stuart Owen
 * @author Matthew Horrige
 */
public class SpreadSheetCellEditor implements TableCellEditor {

    private JTextField editorField = new JTextField();

    private Cell cellBeingEdited = null;    

    private List<CellEditorListener> listeners = new ArrayList<CellEditorListener>();

    public SpreadSheetCellEditor() {        
        editorField.setBorder(BorderFactory.createEmptyBorder());        
    }

    /**
     * Sets an initial <code>value</code> for the editor.  This will cause
     * the editor to <code>stopEditing</code> and lose any partially
     * edited value if the editor is editing when this method is called. <p>
     * Returns the component that should be added to the client's
     * <code>Component</code> hierarchy.  Once installed in the client's
     * hierarchy this component will then be able to draw and receive
     * user input.
     * @param table      the <code>JTable</code> that is asking the
     *                   editor to edit; can be <code>null</code>
     * @param value      the value of the cell to be edited; it is
     *                   up to the specific editor to interpret
     *                   and draw the value.  For example, if value is
     *                   the string "true", it could be rendered as a
     *                   string or it could be rendered as a check
     *                   box that is checked.  <code>null</code>
     *                   is a valid value
     * @param isSelected true if the cell is to be rendered with
     *                   highlighting
     * @param row        the row of the cell being edited
     * @param column     the column of the cell being edited
     * @return the component for editing
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Rectangle rect = table.getCellRect(row, column, false);
        editorField.setSize(rect.width, rect.height + 4);
        cellBeingEdited = (Cell) value;        
        if (cellBeingEdited != null) {
            editorField.setText(cellBeingEdited.getValue());
            editorField.setFont(cellBeingEdited.getFont());
            // TODO: Font etc.
        }
        else {
            editorField.setText("");
        }
        return editorField;
    }

    /**
     * Returns the value contained in the editor.
     * @return the value contained in the editor
     */
    public Object getCellEditorValue() {
        return editorField.getText();
    }

    /**
     * Asks the editor if it can start editing using <code>anEvent</code>.
     * <code>anEvent</code> is in the invoking component coordinate system.
     * The editor can not assume the Component returned by
     * <code>getCellEditorComponent</code> is installed.  This method
     * is intended for the use of client to avoid the cost of setting up
     * and installing the editor component if editing is not possible.
     * If editing can be started this method returns true.
     * @param anEvent the event the editor should use to consider
     *                whether to begin editing or not
     * @return true if editing can be started
     * @see #shouldSelectCell
     */
    public boolean isCellEditable(EventObject anEvent) {
        if(anEvent instanceof MouseEvent) {
            return ((MouseEvent) anEvent).getClickCount() == 2;
        }
        else if (anEvent instanceof KeyEvent) {
        	return ((KeyEvent)anEvent).getKeyCode()!=157;
        }
        else {
        	return false;
        }
    }

    /**
     * Returns true if the editing cell should be selected, false otherwise.
     * Typically, the return value is true, because is most cases the editing
     * cell should be selected.  However, it is useful to return false to
     * keep the selection from changing for some types of edits.
     * eg. A table that contains a column of check boxes, the user might
     * want to be able to change those checkboxes without altering the
     * selection.  (See Netscape Communicator for just such an example)
     * Of course, it is up to the client of the editor to use the return
     * value, but it doesn't need to if it doesn't want to.
     * @param anEvent the event the editor should use to start
     *                editing
     * @return true if the editor would like the editing cell to be selected;
     *         otherwise returns false
     * @see #isCellEditable
     */
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    /**
     * Tells the editor to stop editing and accept any partially edited
     * value as the value of the editor.  The editor returns false if
     * editing was not stopped; this is useful for editors that validate
     * and can not accept invalid entries.
     * @return true if editing was stopped; false otherwise
     */
    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }

    /**
     * Tells the editor to cancel editing and not accept any partially
     * edited value.
     */
    public void cancelCellEditing() {
        fireEditingCancelled();
    }

    /**
     * Adds a listener to the list that's notified when the editor
     * stops, or cancels editing.
     * @param l the CellEditorListener
     */
    public void addCellEditorListener(CellEditorListener l) {
        listeners.add(l);
    }

    /**
     * Removes a listener from the list that's notified
     * @param l the CellEditorListener
     */
    public void removeCellEditorListener(CellEditorListener l) {
        listeners.remove(l);
    }

    private void fireEditingCancelled() {
        for (CellEditorListener listener : new ArrayList<CellEditorListener>(listeners)) {
            listener.editingCanceled(new ChangeEvent(this));
        }
    }

    private void fireEditingStopped() {
        for (CellEditorListener listener : new ArrayList<CellEditorListener>(listeners)) {
            listener.editingStopped(new ChangeEvent(this));
        }
    }

}
