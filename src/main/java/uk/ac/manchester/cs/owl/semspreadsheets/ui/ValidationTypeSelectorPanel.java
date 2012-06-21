/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.ValidationType;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * UI Panel for selection of the Validation Type - i.e Free Text, Subclasses, Instances etc.
 * 
 * @author Mathew Horridge
 * @author Stuart Owen
 */
@SuppressWarnings("serial")
public class ValidationTypeSelectorPanel extends JPanel {
       
	private static final Logger logger = Logger.getLogger(ValidationTypeSelectorPanel.class);

    private final WorkbookManager workbookManager;    

    private CellSelectionListener cellSelectionListener;
        
    JComboBox comboBox;

    public ValidationTypeSelectorPanel(final WorkbookManager workbookManager) {
        this.workbookManager = workbookManager;		
        setLayout(new BorderLayout());
                     
        comboBox = new JComboBox();
        add(comboBox,BorderLayout.NORTH);
              
        add(new PropertyListPanel(workbookManager),BorderLayout.SOUTH);                
		
		comboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED && comboBox==e.getSource() && comboBox.getSelectedItem()!=null) {					
					workbookManager.getEntitySelectionModel().setValidationType((ValidationType) comboBox.getSelectedItem());
					previewSelectionInList();
				}				
			}
		});            

        cellSelectionListener = new CellSelectionListener() {
            public void selectionChanged(Range range) {
                updateSelectionFromModel(range);
            }
        };
        workbookManager.getOntologyManager().addListener(new WorkbookManagerListener() {
			
			@Override
			public void workbookLoaded(WorkbookManagerEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void workbookCreated(WorkbookManagerEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void validationAppliedOrCancelled() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void ontologiesChanged(WorkbookManagerEvent event) {
				refreshTypeList(workbookManager.getOntologyManager().getLoadedOntologies().size()>0);				
			}
		});
        
        workbookManager.getSelectionModel().addCellSelectionListener(cellSelectionListener);
        refreshTypeList(false);
        setComboBoxEnabled(false);
        //updateSelectionFromModel();
    }
    
    public void addListItemListener(ItemListener listener) {
    	comboBox.addItemListener(listener);
    }       
    
    private void previewSelectionInList() {    	    	
    	workbookManager.previewValidation();		
	}        

    private void updateSelectionFromModel(Range range) {        
        if(range == null) {
        	setComboBoxEnabled(false);            
            return;
        }        
        setComboBoxEnabled(range.isCellSelection());
        
        Collection<OntologyTermValidation> intersectingValidations = workbookManager.getOntologyManager().getIntersectingOntologyTermValidations(range);
        Collection<OntologyTermValidation> containingValidations = workbookManager.getOntologyManager().getContainingOntologyTermValidations(range);
        setComboBoxEnabled(containingValidations.size() <= 1 && intersectingValidations.size() == containingValidations.size());                       
        
        if(containingValidations.isEmpty()) {            
            comboBox.setSelectedIndex(0);
        }
        else if(containingValidations.size() == 1) {
            OntologyTermValidation validation = containingValidations.iterator().next();
            setSelectedType(validation);            
        }                       
    }

    private void setSelectedType(OntologyTermValidation validation) {    	
        ValidationType type = validation.getValidationDescriptor().getType();
        logger.debug("Setting selected type to "+type);
        comboBox.setSelectedItem(type);        		
    }

    public ValidationType getSelectedType() {
        return (ValidationType)comboBox.getSelectedItem();
    }

    /**
     * Updates the types list in the dropbox box
     * @param allItems - whether all items should be shown. If false, just NOVALIDATION type is included
     */
    private void refreshTypeList(boolean allItems) { 
    	logger.debug("Refereshing validation type list - include all items:"+allItems);
    	if (allItems) {
    		if (comboBox.getItemCount()<=1) {
    			comboBox.removeAllItems();
        		for(ValidationType type : ValidationType.values()) {                
        			comboBox.addItem(type);
                }
    		}    		
    	}
    	else if (comboBox.getItemCount()!=1) {
    		comboBox.removeAllItems();
    		comboBox.addItem(ValidationType.FREETEXT);
    	}    	    	
    }
    
    private void setComboBoxEnabled(boolean enabled) {
    	comboBox.setEnabled(enabled);
    }    
}
