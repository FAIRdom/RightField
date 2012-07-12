/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.semanticweb.owlapi.model.EntityType;

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

    private WorkbookManager workbookManager;

    private JList termList;

    private static final String NO_VALIDATION_MESSAGE = "Any";

    private static final String EMPTY_VALIDATION = "None";

    public ValidationValuesPanel(WorkbookManager manager) {
        this.workbookManager = manager;
        setLayout(new BorderLayout());
        createTermList();
        JScrollPane sp = new JScrollPane(termList);
        
        add(sp, BorderLayout.CENTER);
        sp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
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
    }

	private void createTermList() {
		termList = new JList() {
            
            private Font nowValuesSpecifiedFont = new Font("Lucida Grande", Font.BOLD, 14);

             @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (termList.getModel().getSize() == 0) {
                    String msg = NO_VALIDATION_MESSAGE;
                    Range selRange = workbookManager.getSelectionModel().getSelectedRange();
                    if (selRange.isCellSelection()) {
                        Collection<OntologyTermValidation> validations = workbookManager.getOntologyManager().getContainingOntologyTermValidations(selRange);
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
        termList.setCellRenderer(new ValueListItemCellRenderer());
	}

    protected void updateFromPreviewList(
			List<OntologyTermValidation> previewList) {
    	TreeSet<ValueListItem> listData = new TreeSet<ValueListItem>();
    	for(OntologyTermValidation validation : previewList) {
            for(Term term : validation.getValidationDescriptor().getTerms()) {
                listData.add(new ValueListItem(term.getFormattedName(), validation.getValidationDescriptor().getType()));
            }
        }
        termList.setListData(listData.toArray());		
	}

	private void updateFromModel(Range range) {		
        termList.setListData(new Object [0]);
        
        if(!range.isCellSelection()) {
            return;
        }
        Collection<OntologyTermValidation> validations = workbookManager.getOntologyManager().getContainingOntologyTermValidations(range);
        TreeSet<ValueListItem> listData = new TreeSet<ValueListItem>();
        for(OntologyTermValidation validation : validations) {
            for(Term term : validation.getValidationDescriptor().getTerms()) {
                listData.add(new ValueListItem(term.getFormattedName(), validation.getValidationDescriptor().getType()));
            }
        }
        termList.setListData(listData.toArray());
    }

    private class ValueListItemCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, ((ValueListItem) value), index, isSelected, cellHasFocus);
            ValueListItem item = (ValueListItem) value;
            EntityType<?> entityType = item.getType().getEntityType();
            label.setIcon(Icons.getOWLEntityIcon(entityType));
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
        
        public ValidationType getType() {
            return type;
        }

        public int compareTo(ValueListItem o) {
            return name.compareTo(o.name);
        }
    }


    
}
