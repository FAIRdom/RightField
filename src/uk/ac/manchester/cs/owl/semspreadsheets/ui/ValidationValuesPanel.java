package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.EntityType;
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
public class ValidationValuesPanel extends JPanel {

    private WorkbookManager workbookManager;

    private JList termList;

    private static final String NO_VALIDATION_MESSAGE = "Any";

    private static final String EMPTY_VALIDATION = "None";

    public ValidationValuesPanel(WorkbookManager manager) {
        this.workbookManager = manager;
        setLayout(new BorderLayout());
        termList = new JList() {

            
            private Font nowValuesSpecifiedFont = new Font("Lucida Grande", Font.BOLD, 14);

             @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (termList.getModel().getSize() == 0) {
                    String msg = NO_VALIDATION_MESSAGE;
                    Range selRange = workbookManager.getSelectionModel().getSelectedRange();
                    if (selRange.isCellSelection()) {
                        Collection<OntologyTermValidation> validations = workbookManager.getOntologyTermValidationManager().getContainingValidations(selRange);
                        if(!validations.isEmpty()) {
                            msg = EMPTY_VALIDATION;
                        }

                    }
                    Color oldColor = g.getColor();
                    g.setColor(Color.LIGHT_GRAY);
                    Font oldFont = g.getFont();
                    g.setFont(nowValuesSpecifiedFont);
                    Rectangle bounds = g.getFontMetrics().getStringBounds(msg, g).getBounds();
                    g.drawString(msg, getWidth() / 2 - bounds.width / 2, getHeight() / 2 - g.getFontMetrics().getAscent());
                    g.setFont(oldFont);
                    g.setColor(oldColor);
                }
            }

        };
        JScrollPane sp = new JScrollPane(termList);
        add(sp, BorderLayout.CENTER);
        sp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        workbookManager.getSelectionModel().addCellSelectionListener(new CellSelectionListener() {
            public void selectionChanged(Range range) {
                updateFromModel();
            }
        });
        termList.setCellRenderer(new ValueListItemCellRenderer());
        workbookManager.getOntologyTermValidationManager().addListener(new OntologyTermValidationListener() {
            public void validationsChanged() {
                updateFromModel();
            }
        });
        termList.setFixedCellHeight(20);
        termList.setVisibleRowCount(7);
//        setMinimumSize(new Dimension(10, 200));
//        setMaximumSize(new Dimension(500, 200));
    }

    private void updateFromModel() {
        termList.setListData(new Object [0]);
        Range range = workbookManager.getSelectionModel().getSelectedRange();
        if(!range.isCellSelection()) {
            return;
        }
        Collection<OntologyTermValidation> validations = workbookManager.getContainingOntologyTermValidations(range);
        TreeSet<ValueListItem> listData = new TreeSet<ValueListItem>();
        for(OntologyTermValidation validation : validations) {
            for(Term term : validation.getValidationDescriptor().getTerms()) {
                listData.add(new ValueListItem(term.getName(), validation.getValidationDescriptor().getType()));
            }
        }
        termList.setListData(listData.toArray());
    }

    private class ValueListItemCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, ((ValueListItem) value), index, isSelected, cellHasFocus);
            ValueListItem item = (ValueListItem) value;
            EntityType entityType = item.getType().getEntityType();
            label.setIcon(Icons.getOWLEntityIcon(workbookManager, entityType));
            return label;
        }
    }

    private class ValueListItem implements Comparable<ValueListItem> {

        private String name;

        private ValidationType type;

        private ValueListItem(String name, ValidationType type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getName() {
            return name;
        }

        public ValidationType getType() {
            return type;
        }

        public int compareTo(ValueListItem o) {
            return name.compareTo(o.name);
        }
    }


    
}
