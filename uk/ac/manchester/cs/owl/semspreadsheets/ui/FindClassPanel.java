package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLEntity;
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
 * Date: 08-Nov-2009
 */
public class FindClassPanel extends JPanel {

    private WorkbookManager manager;

    private JTextField findField = new JTextField();

    private JList resultList = new JList();

    private JWindow resultWindow;

    private WorkbookFrame frame;

    public FindClassPanel(WorkbookFrame frame) {
        this.frame = frame;
        this.manager = frame.getWorkbookManager();
        setLayout(new BorderLayout());
        findField.putClientProperty("JTextField.variant", "search");
        add(findField, BorderLayout.NORTH);
        findField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                doFind();
            }

            public void removeUpdate(DocumentEvent e) {
                doFind();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });
        resultWindow = new JWindow(frame);
        resultWindow.getContentPane().setLayout(new BorderLayout());
        resultWindow.getContentPane().add(new JScrollPane(resultList));
        resultList.setCellRenderer(new WorkbookManagerCellRenderer(manager));
        resultList.setRequestFocusEnabled(false);
        resultWindow.setFocusable(false);
        resultWindow.setFocusableWindowState(false);
        findField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dismiss();
                }
                else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    navigate();
                }
            }
        });
        findField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_UP) {
                    moveUpList();
                }
                else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    moveDownList();
                }
            }
        });
        findField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                dismiss();
            }
        });
    }


    private void moveUpList() {
        int selIndex = resultList.getSelectedIndex();
        selIndex--;
        if(selIndex < 0) {
            selIndex = resultList.getModel().getSize() - 1;
        }
        if(selIndex >= 0) {
            resultList.setSelectedIndex(selIndex);
            resultList.scrollRectToVisible(resultList.getCellBounds(selIndex, selIndex));
        }
    }

    private void moveDownList() {
        if (resultList.getModel().getSize() > 0) {
            int selIndex = resultList.getSelectedIndex();
            selIndex++;
            if(selIndex > resultList.getModel().getSize() - 1) {
                selIndex = 0;
            }
            resultList.setSelectedIndex(selIndex);
            resultList.scrollRectToVisible(resultList.getCellBounds(selIndex, selIndex));
        }
    }

    private void dismiss() {
        resultWindow.setVisible(false);
    }

    private void navigate() {
        Object o = resultList.getSelectedValue();
        if(o != null) {
            manager.getEntitySelectionModel().setSelection((OWLEntity) o);
        }
        resultWindow.setVisible(false);
    }

    private void doFind() {
        Collection<OWLEntity> entities = manager.getEntitiesForShortForm(findField.getText().trim());
        ArrayList<OWLEntity> sortedEntities = new ArrayList<OWLEntity>();
        for(OWLEntity ent : entities) {
            if(ent.isOWLClass()) {
                sortedEntities.add(ent);
            }
        }
        resultList.setListData(sortedEntities.toArray());
        showResults();
    }

    private void showResults() {
        if (resultList.getModel().getSize() > 0) {
            resultWindow.setSize(findField.getWidth(), 150);
            Point point = new Point(0, 0);
            SwingUtilities.convertPointToScreen(point, findField);
//            SwingUtilities.convertPointFromScreen(point, frame.getContentPane());
            resultWindow.setLocation(point.x, point.y + findField.getHeight() + 3);
            resultWindow.setVisible(true);
            resultList.setSelectedIndex(0);
        }
        else {
            resultWindow.setVisible(false);
        }
    }

}
