/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.AbstractEntitySelectionModelListener;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.CellSelectionListener;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.OntologyManagerListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.ValidationType;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.skos.SKOSDetector;

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
        
    JComboBox<ValidationType> comboBox;

	private PropertyListPanel propertyListPanel;

    public ValidationTypeSelectorPanel(final WorkbookManager workbookManager) {
        this.workbookManager = workbookManager;		
        setLayout(new BorderLayout());
                     
        comboBox = new JComboBox<ValidationType>();
        add(comboBox,BorderLayout.NORTH);
        propertyListPanel = new PropertyListPanel(workbookManager);      
        add(propertyListPanel,BorderLayout.SOUTH);                
		
		setupListeners();
        
        workbookManager.getSelectionModel().addCellSelectionListener(cellSelectionListener);
        workbookManager.getEntitySelectionModel().addListener(new AbstractEntitySelectionModelListener() {

			@Override
			public void validationTypeChanged(ValidationType type) {
				setSelectedType(type);
			}
		});
			
			
        refreshTypeList(null);
    }        

	private void setupListeners() {
		comboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED && comboBox==e.getSource() && comboBox.getSelectedItem()!=null) {					
					getWorkbookManager().getEntitySelectionModel().setValidationType((ValidationType) comboBox.getSelectedItem());
					previewSelectionInList();
				}				
			}
		});            

        cellSelectionListener = new CellSelectionListener() {
            public void selectionChanged(Range range) {
                updateSelectionFromModel(range);
            }
        };
        getWorkbookManager().getOntologyManager().addListener(new OntologyManagerListener() {						
			
			@Override
			public void ontologiesChanged() {
				if (getWorkbookManager().getOntologyManager().getLoadedOntologies().size()<1) {
					refreshTypeList(null);
				}
			}

			@Override
			public void ontologySelected(OWLOntology ontology) {
				logger.debug("Ontology selected: "+ontology.getOntologyID().getOntologyIRI());
				refreshTypeList(ontology);
			}			
		});
	}
    
    public void addListItemListener(ItemListener listener) {
    	comboBox.addItemListener(listener);
    } 
    
    private WorkbookManager getWorkbookManager() {
    	return this.workbookManager;
    }
    
    private void previewSelectionInList() {    	    	
    	getWorkbookManager().previewValidation();		
	}        

    private void updateSelectionFromModel(Range range) {        
        if(range == null) {
        	setComboBoxEnabled(false);            
            return;
        }        
        setComboBoxEnabled(range.isCellSelection());
        
        Collection<OntologyTermValidation> intersectingValidations = getWorkbookManager().getOntologyManager().getIntersectingOntologyTermValidations(range);
        Collection<OntologyTermValidation> containingValidations = getWorkbookManager().getOntologyManager().getContainingOntologyTermValidations(range);
        setComboBoxEnabled(containingValidations.size() <= 1 && intersectingValidations.size() == containingValidations.size());                       
        
        if(containingValidations.isEmpty()) {            
            comboBox.setSelectedIndex(0);
        }
        else if(containingValidations.size() == 1) {
            OntologyTermValidation validation = containingValidations.iterator().next();
            setSelectedType(validation.getValidationDescriptor().getType());            
        }                       
    }
    
    private void setSelectedType(ValidationType type) {    	        
        logger.debug("Setting selected type to "+type);
        comboBox.setSelectedItem(type);        		
    }

    /**
     * Updates the types list in the dropbox box, selecting the right values according to the ontology passed, which can be null to indicate no ontology
     */
    private void refreshTypeList(OWLOntology ontology) { 
    	logger.debug("Refereshing validation type list");
    	if (ontology==null) {
    		logger.debug("No ontology selected");
    		updateComboBoxItems(ValidationType.valuesNoOntologies());
    	}
    	else if (SKOSDetector.isSKOS(ontology)) {
    		logger.debug("SKOS taxonomy selected");
    		updateComboBoxItems(ValidationType.valuesForSKOS());
    	}
    	else {
    		logger.debug("OWL ontology selected");
    		updateComboBoxItems(ValidationType.valuesForOWL());
    	}    	    	    
    }
    
    private void updateComboBoxItems(ValidationType [] items) {
    	comboBox.removeAllItems();
    	for (ValidationType item : items) {
    		comboBox.addItem(item);
    	}
    }
    
    private void setComboBoxEnabled(boolean enabled) {
    	comboBox.setEnabled(enabled);
    }    
}
