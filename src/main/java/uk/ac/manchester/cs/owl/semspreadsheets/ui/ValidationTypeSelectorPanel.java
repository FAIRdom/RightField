/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.ValidationType;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * @author Mathew Horridge
 * @author Stuart Owen
 */
@SuppressWarnings("serial")
public class ValidationTypeSelectorPanel extends JPanel {


    private Map<JRadioButton, ValidationType> values = new LinkedHashMap<JRadioButton, ValidationType>();    

    private final WorkbookManager workbookManager;    

    private CellSelectionListener cellSelectionListener;
    
    ButtonGroup buttonGroup;

    public ValidationTypeSelectorPanel(final WorkbookManager workbookManager) {
        this.workbookManager = workbookManager;		
        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        add(box, BorderLayout.NORTH);                
        buttonGroup = new ButtonGroup();      
        add(new PropertyListPanel(workbookManager),BorderLayout.SOUTH);
        
        ItemListener checkboxActionListener = new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				workbookManager.getEntitySelectionModel().setValidationType(values.get(e.getSource()));
				previewSelectionInList();				
			}
		};	

        for(final ValidationType type : ValidationType.values()) {
            JRadioButton button = new JRadioButton(type.toString());            
            box.add(button);
            button.putClientProperty("JRadioButton.size", "small");
            box.add(Box.createVerticalStrut(1));
            if(values.isEmpty()) {
                button.setSelected(true);
            }
            values.put(button, type);
            buttonGroup.add(button);                       
            button.addItemListener(checkboxActionListener);            
        }

        cellSelectionListener = new CellSelectionListener() {
            public void selectionChanged(Range range) {
                updateSelectionFromModel();
            }
        };
        workbookManager.getSelectionModel().addCellSelectionListener(cellSelectionListener);
        updateSelectionFromModel();
    }
    
    public void addRadioButtonActionListener(ActionListener listener) {
    	Enumeration<AbstractButton> buttons = buttonGroup.getElements();
    	while(buttons.hasMoreElements()) {
    		buttons.nextElement().addActionListener(listener);     		    
    	}    	
    }
    
    private void previewSelectionInList() {    	    	
    	workbookManager.previewValidation();		
	}        

    private void updateSelectionFromModel() {
        Range range = workbookManager.getSelectionModel().getSelectedRange();
        if(range == null) {
            setRadioButtonsEnabled(false);
            return;
        }
        setRadioButtonsEnabled(range.isCellSelection());
        Collection<OntologyTermValidation> intersectingValidations = workbookManager.getIntersectingOntologyTermValidations(range);
        Collection<OntologyTermValidation> containingValidations = workbookManager.getContainingOntologyTermValidations(range);
        setRadioButtonsEnabled(containingValidations.size() <= 1 && intersectingValidations.size() == containingValidations.size());
        if(containingValidations.isEmpty()) {
            values.keySet().iterator().next().setSelected(true);
        }
        else if(containingValidations.size() == 1) {
            OntologyTermValidation validation = containingValidations.iterator().next();
            setSelected(validation);
        }
        if (workbookManager.getLoadedOntologies().isEmpty()) {
            setNonEmptyRadioButtonsEnabled(false);
        }
    }

    private void setSelected(OntologyTermValidation validation) {
        ValidationType type = validation.getValidationDescriptor().getType();
        for(JRadioButton button : values.keySet()) {
            ValidationType buttonType = values.get(button);
            if(type.equals(buttonType)) {
                button.setSelected(true);
            }
        }
    }

    public ValidationType getSelectedType() {
        for(JRadioButton radioButton : values.keySet()) {
            if(radioButton.isSelected()) {
                return values.get(radioButton);
            }
        }
        return ValidationType.NOVALIDATION;
    }

    private void setNonEmptyRadioButtonsEnabled(boolean b) {
        for(JRadioButton button : values.keySet()) {
            ValidationType type = values.get(button);
            if(!type.equals(ValidationType.NOVALIDATION)) {
                button.setEnabled(b);
            }
        }
    }

    private void setRadioButtonsEnabled(boolean b) {
        for(JRadioButton button : values.keySet()) {
            if (b != button.isEnabled()) {
            	button.setEnabled(b);
            }
        }
    }
}
