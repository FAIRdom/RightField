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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    	selectedCellAddressLabel.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 0));
        workbookManager = frame.getWorkbookManager();
        setLayout(new BorderLayout(14, 14));        
        setBorder(BorderFactory.createEmptyBorder(7, 2, 7, 7));
        
        JPanel outerPanel = new JPanel();                
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.PAGE_AXIS));

        ClassHierarchyTreePanel classHierarchyTreePanel = new ClassHierarchyTreePanel(frame);
        classHierarchyTreePanel.setBorder(createTitledBorder("ONTOLOGY HIERARCHIES"));
        
        ValidationValuesPanel valuesPanel = new ValidationValuesPanel(frame.getWorkbookManager());
        valuesPanel.setBorder(createTitledBorder("ALLOWED VALUES"));
        
        JPanel validationSelectionPanel = new JPanel(new BorderLayout(7, 7));        
        
        ValidationTypeSelectorPanel typeSelectorPanel = new ValidationTypeSelectorPanel(frame.getWorkbookManager());
        
        typeSelectorPanel.setBorder(createTitledBorder("TYPE OF ALLOWED VALUES"));
        
        JPanel buttonPanel = setupButtonPanel(typeSelectorPanel);                      
        
        frame.getWorkbookManager().getSelectionModel().addCellSelectionListener(new CellSelectionListener() {
            public void selectionChanged(Range range) {
                updateSelectionLabel();
            }
        });     
                
        validationSelectionPanel.add(typeSelectorPanel, BorderLayout.NORTH);        
        validationSelectionPanel.add(valuesPanel, BorderLayout.CENTER);
        validationSelectionPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        outerPanel.add(classHierarchyTreePanel);        
        outerPanel.add(validationSelectionPanel);
       
        add(selectedCellAddressLabel, BorderLayout.NORTH);
        add(outerPanel,BorderLayout.CENTER);
        
        updateSelectionLabel();
    }

	private JPanel setupButtonPanel(ValidationTypeSelectorPanel typeSelectorPanel) {
		
        workbookManager.getEntitySelectionModel().addListener(new EntitySelectionModelListener() {			
			@Override
			public void selectionChanged() {
				logger.debug("ValidationInspectorPanel's EntitySelectionModelListener selectionChanged fired");
				updateApplyButtonState();
			}
		});
		
		typeSelectorPanel.addRadioButtonActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				logger.debug("Radio Button ActionEvent fired");
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
		});
        
        return buttonPanel;
	}
	
	private void updateApplyButtonState() {
		boolean state = workbookManager.determineApplyButtonState();
		applyButton.setEnabled(state);
		cancelButton.setEnabled(state);
	}

    private void updateSelectionLabel() {
        Range selectedRange = workbookManager.getSelectionModel().getSelectedRange();
        if (selectedRange.isCellSelection()) {        	
            selectedCellAddressLabel.setText("Selected cells: " + selectedRange.getColumnRowAddress());
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
