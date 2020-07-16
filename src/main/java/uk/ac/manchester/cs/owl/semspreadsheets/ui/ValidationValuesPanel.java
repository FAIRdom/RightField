/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;


import uk.ac.manchester.cs.owl.semspreadsheets.listeners.CellSelectionListener;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.OntologyTermValidationListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Term;
import uk.ac.manchester.cs.owl.semspreadsheets.model.ValueListItem;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * UI Pane that shows the available terms for the selected Type.
 * 
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class ValidationValuesPanel extends JPanel {

    private static Logger logger = Logger.getLogger(ValidationValuesPanel.class);    
    
    private WorkbookManager workbookManager;
    
    private ValidationInspectorPanel validationInspectorPanel;

    private JList<ValueListItem> termList;

    private static final String NO_VALIDATION_MESSAGE = "Any";

    private static final String EMPTY_VALIDATION = "None";

    public ValidationValuesPanel(WorkbookManager manager, ValidationInspectorPanel validationInspectorPanel) {
        this.workbookManager = manager;
        this.validationInspectorPanel = validationInspectorPanel;
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
        termList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); 
        workbookManager.setTermJListFromValidationValuesPanel(this.termList); 
        ListSelectionModel listSelectionModel = termList.getSelectionModel(); 
        listSelectionModel.addListSelectionListener(new SharedListSelectionHandler()); 
        //ToolTipManager.sharedInstance().registerComponent(termList); //AW removed, not clear where and how this works?
	}

    protected void updateFromPreviewList(List<OntologyTermValidation> previewList) {
        TreeSet<ValueListItem> listData = new TreeSet<ValueListItem>();
        for (OntologyTermValidation validation : previewList) {
            for (Term term : validation.getValidationDescriptor().getTerms()) {
                listData.add(new ValueListItem(term, validation.getValidationDescriptor().getType()));
            }
        }

        termList.setListData(listData.toArray(new ValueListItem[listData.size()]));
        
        setViewAllTermsSelected();
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

        setViewTermsHighlighting();
    }

    private void setViewTermsHighlighting() {
        ListModel model = termList.getModel();
        ListSelectionModel listSelectionModel = termList.getSelectionModel();
        
        for (int i = 0; i < model.getSize(); i++) {
            ValueListItem vli = (ValueListItem) model.getElementAt(i);
            Term term = vli.getTerm();
            if (term.isSelected()) {
                listSelectionModel.addSelectionInterval(i, i);
            }
        }
    }    
    
     private void setViewAllTermsSelected() {
         ListSelectionModel listSelectionModel = termList.getSelectionModel();
         listSelectionModel.setSelectionInterval(0, termList.getModel().getSize() -1);
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

    class SharedListSelectionHandler implements ListSelectionListener { 

        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            if (e.getValueIsAdjusting()) {
                if (!lsm.isSelectionEmpty()) {
                    ListModel<ValueListItem> lm = termList.getModel();           
                    for (int i = 0; i <= lm.getSize() - 1; i++) {
                        ValueListItem vli = (ValueListItem) lm.getElementAt(i);
                        Term term = vli.getTerm();
                        if (lsm.isSelectedIndex(i)) {
                            term.setSelected(true);
                        } else {
                            term.setSelected(false);
                        }
                    }
                }
            }
            validationInspectorPanel.updateApplyButtonState();
        }
        
    }

    
}
