/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.ApplyValidationAction;

/**
 * Container panel for selecting or showing the validation and properties that are going to be applied to the cells. 
 * The container includes available class hierarchies for each ontology loaded, the list of validation types, properties and terms.
 * 
 * @author Stuart Owen
 * @author Matthew Horridge
 * 
 */
@SuppressWarnings("serial")
public class ValidationInspectorPanel extends JPanel {
	
	private static Logger logger = Logger.getLogger(ValidationInspectorPanel.class);

    private static Font font = new Font("Lucida Grande", Font.BOLD, 11);

    private WorkbookManager workbookManager;

    private JLabel selectedCellAddressLabel = new JLabel("No cells are currently selected");        

    private static Color textColor = new Color(96, 110, 128);
    
    private JButton applyButton = new JButton("Apply");
    private JButton cancelButton = new JButton("Cancel");

    public ValidationInspectorPanel(WorkbookFrame frame) {
    	
        workbookManager = frame.getWorkbookManager();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(7, 2, 7, 7));  
        
        //selected cell        
        addSelectedCellLabel();        

        //class hierarchy
        addClassHierarchyPanel(frame);        
        
        //validation selection
        addValidationSelectionPanel(frame);
                        
        
        updateSelectionLabel(workbookManager.getSelectionModel().getSelectedRange());
                
    }

	private void addValidationSelectionPanel(WorkbookFrame frame) {
		ValidationTypeSelectorPanel typeSelectorPanel = new ValidationTypeSelectorPanel(frame.getWorkbookManager());        
        typeSelectorPanel.setBorder(createTitledBorder("VALUE TYPE AND PROPERTY"));        
        
        ValidationValuesPanel valuesPanel = new ValidationValuesPanel(frame.getWorkbookManager());
        valuesPanel.setBorder(createTitledBorder("ALLOWED VALUES"));
        
        
        JPanel buttonPanel = setupButtonPanel(typeSelectorPanel);                      
        
        frame.getWorkbookManager().getSelectionModel().addCellSelectionListener(new CellSelectionListener() {
            public void selectionChanged(Range range) {
                updateSelectionLabel(range);
            }
        });     
          
        JPanel validationSelectionPanel = new JPanel(new BorderLayout(7, 7));
        validationSelectionPanel.add(typeSelectorPanel, BorderLayout.NORTH);        
        validationSelectionPanel.add(valuesPanel, BorderLayout.CENTER);
        validationSelectionPanel.add(buttonPanel, BorderLayout.SOUTH);        
        add(validationSelectionPanel);
	}

	private void addClassHierarchyPanel(WorkbookFrame frame) {
		ClassHierarchyTreePanel classHierarchyTreePanel = new ClassHierarchyTreePanel(frame);
        classHierarchyTreePanel.setBorder(createTitledBorder("ONTOLOGY HIERARCHIES")); 
        add(classHierarchyTreePanel);
	}

	private void addSelectedCellLabel() {
		selectedCellAddressLabel.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 0));		
		selectedCellAddressLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        add(selectedCellAddressLabel);
	}

	private JPanel setupButtonPanel(ValidationTypeSelectorPanel typeSelectorPanel) {
		
        workbookManager.getEntitySelectionModel().addListener(new EntitySelectionModelListener() {			
			@Override
			public void selectionChanged() {
				logger.debug("ValidationInspectorPanel's EntitySelectionModelListener selectionChanged fired");
				updateApplyButtonState();
			}
		});
		
        typeSelectorPanel.addListItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				logger.debug("Validation type selected");
				updateApplyButtonState();
			}
		});

		applyButton.setAction(new ApplyValidationAction(workbookManager));
		applyButton.setEnabled(false);
				
		JPanel  buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(applyButton);
		
		//Cancel button is currently not visible, as Katy thought it was confusing and could be
		//confused with an UNDO button to remove previous changes. Leaving the code here for now incase
        //we decide to reintroduce it.
		//cancelButton.setAction(new CancelValidationAction(workbookManager));
		//cancelButton.setEnabled(false);
		//buttonPanel.add(cancelButton);		               
        
        workbookManager.addListener(new WorkbookManagerListener() {
			
			@Override
			public void workbookLoaded(WorkbookManagerEvent event) {				
				
			}
			
			@Override
			public void workbookCreated(WorkbookManagerEvent event) {
				
			}
			
			@Override
			public void validationAppliedOrCancelled() {
				applyButton.setEnabled(false);
				cancelButton.setEnabled(false);
			}
			
			@Override
			public void ontologiesChanged(WorkbookManagerEvent event) {				
				
			}
			
			@Override
			public void workbookSaved(WorkbookManagerEvent event) {
				
			}
		});
        
        return buttonPanel;
	}
	
	private void updateApplyButtonState() {
		boolean state = workbookManager.determineApplyButtonState();
		applyButton.setEnabled(state);
		cancelButton.setEnabled(state);
	}

    private void updateSelectionLabel(Range range) {        
        if (range.isCellSelection()) {        	
            selectedCellAddressLabel.setText("Selected cells: " + range.getColumnRowAddress());
        }
        else {
            selectedCellAddressLabel.setText("No cells are currently selected");
        }
    }

    private static Border createTitledBorder(String title) {
        Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);

        Border titledBorder = BorderFactory.createTitledBorder(border, title,
                TitledBorder.LEFT, TitledBorder.TOP, font, textColor);
        Border innerBorder = BorderFactory.createEmptyBorder(3, 20, 0, 0);
        return BorderFactory.createCompoundBorder(titledBorder, innerBorder);
    }

}
