/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.AbstractEntitySelectionModelListener;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.CellSelectionListener;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.OntologyTermValidationListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Term;
import uk.ac.manchester.cs.owl.semspreadsheets.model.ValidationType;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * UI Pane that shows the available terms for the selected Type.
 *
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class ValidationValuesPanel extends JPanel {

    Logger logger = Logger.getLogger(ValidationValuesPanel.class);

    private WorkbookManager workbookManager;

    private JList<ValueListItem> termList;

    private static final String NO_VALIDATION_MESSAGE = "Any";

    private static final String EMPTY_VALIDATION = "None";

    public ValidationValuesPanel(WorkbookFrame frame) {
        this.workbookManager = frame.getWorkbookManager();
        setLayout(new BorderLayout());

        createTermList();
        JScrollPane sp = new JScrollPane(termList);
        add(sp, BorderLayout.CENTER);
        sp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        add(new ValidationValuesFilterPanel(frame), BorderLayout.SOUTH);

        workbookManager.getSelectionModel().addCellSelectionListener(new CellSelectionListener() {
            public void selectionChanged(Range range) {
                updateFromModel(range);
            }
        });

        workbookManager.getOntologyManager().addListener(new OntologyTermValidationListener() {
            @Override
            public void validationsChanged() {
                updateFromModel(workbookManager.getSelectionModel().getSelectedRange());
            }

            @Override
            public void ontologyTermSelected(
                    List<OntologyTermValidation> previewList) {
                updateFromPreviewList(previewList);

            }
        });

        workbookManager.getEntitySelectionModel().addListener(new AbstractEntitySelectionModelListener() {
            @Override
            public void termsChanged(List<Term> terms) {
                updateFromSelectionModel(terms);
            }
        });
    }

    private void createTermList() {
        termList = new JList<ValueListItem>() {

            private Font nowValuesSpecifiedFont = new Font("Lucida Grande", Font.BOLD, 14);

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (termList.getModel().getSize() == 0) {
                    String msg = NO_VALIDATION_MESSAGE;
                    Range selRange = workbookManager.getSelectionModel().getSelectedRange();
                    if (selRange.isCellSelection()) {
                        Collection<OntologyTermValidation> validations = workbookManager.getOntologyManager().getContainingOntologyTermValidations(selRange);
                        if (!validations.isEmpty()) {
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
        termList.setCellRenderer(new ValueListItemCellRenderer());
        ToolTipManager.sharedInstance().registerComponent(termList);
    }

    protected void updateFromPreviewList(
            List<OntologyTermValidation> previewList) {
        TreeSet<ValueListItem> listData = new TreeSet<ValueListItem>();
        for (OntologyTermValidation validation : previewList) {
            for (Term term : validation.getValidationDescriptor().getTerms()) {
                listData.add(new ValueListItem(term, validation.getValidationDescriptor().getType()));
            }
        }
        termList.setListData(listData.toArray(new ValueListItem[listData.size()]));
    }

    private void updateFromModel(Range range) {
        termList.setListData(new ValueListItem[0]);

        if (!range.isCellSelection()) {
            return;
        }
        Collection<OntologyTermValidation> validations = workbookManager.getOntologyManager().getContainingOntologyTermValidations(range);
        TreeSet<ValueListItem> listData = new TreeSet<ValueListItem>();
        for (OntologyTermValidation validation : validations) {
            for (Term term : validation.getValidationDescriptor().getTerms()) {
                listData.add(new ValueListItem(term, validation.getValidationDescriptor().getType()));
            }
        }
        termList.setListData(listData.toArray(new ValueListItem[listData.size()]));
    }

    private void updateFromSelectionModel(List<Term> terms) {
        termList.setListData(new ValueListItem[0]);

        ValidationType type = workbookManager.getEntitySelectionModel().getValidationType();
        List<ValueListItem> listItems = new ArrayList<>();

        if (terms != null) {
            for (Term term : terms) {
                listItems.add(new ValueListItem(term, type));
            }
        }

        termList.setListData(listItems.toArray(new ValueListItem[listItems.size()]));
    }

    private class ValueListItemCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, ((ValueListItem) value), index, isSelected, cellHasFocus);
            ValueListItem item = (ValueListItem) value;
            ValidationEntityType<?> entityType = item.getType().getEntityType();
            label.setIcon(Icons.getOWLEntityIcon(entityType));
            label.setToolTipText(item.getEntityIRI().toString());
            return label;
        }
    }

    private class ValueListItem implements Comparable<ValueListItem> {

        private Term term;


        private ValidationType type;

        private ValueListItem(Term term, ValidationType type) {
            this.term = term;
            this.type = type;
        }

        @Override
        public String toString() {
            return getName();
        }

        public String getName() {
            return term.getFormattedName();
        }

        public IRI getEntityIRI() {
            return term.getIRI();
        }

        public ValidationType getType() {
            return type;
        }

        protected Term getTerm() {
            return term;
        }

        public int compareTo(ValueListItem o) {
            return getTerm().compareTo(o.getTerm());
        }
    }


}
