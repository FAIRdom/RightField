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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyItem;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * UI Element that show the lists of properties that can be applied along with the Validation type.
 * 
 * @author Stuart Owen
 *
 */
@SuppressWarnings("serial")
public class PropertyListPanel extends JPanel {
	private final WorkbookManager workbookManager;
	private static final Logger logger = Logger
			.getLogger(PropertyListPanel.class);
	private JComboBox comboBox;
	private JCheckBox checkBox;
	private JTextArea fullPropertyName;	

	public PropertyListPanel(WorkbookManager workbookManager) {

		this.workbookManager = workbookManager;
		setLayout(new BorderLayout());
		comboBox = new JComboBox();
		
		checkBox = new JCheckBox("Include a property");		

		add(checkBox, BorderLayout.NORTH);
		add(comboBox, BorderLayout.CENTER);
		
		fullPropertyName=new JTextArea();
		fullPropertyName.setOpaque(false);
		fullPropertyName.setEditable(false);
		fullPropertyName.setFocusable(false);
		fullPropertyName.setRows(2);
		fullPropertyName.setLineWrap(true);
		fullPropertyName.setWrapStyleWord(false);		
		
		add(fullPropertyName,BorderLayout.SOUTH);
		setupListeners();
		setSelectedStatus(false);		
	}
	
	public void ontologySelected(OWLOntology ontology) {		
		updateModel(ontology);
	}

	private void setupListeners() {		
		getWorkbookManager().getSelectionModel().addCellSelectionListener(
				new CellSelectionListener() {

					@Override
					public void selectionChanged(Range range) {
						updatePropertySelectionFromModel(range);
					}
				});

		comboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {				
				if (event.getStateChange()==ItemEvent.SELECTED) {
					updateEntityModel();	
					updatePropertyDetails();
				}				
			}
		});

		checkBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				Object source = event.getItemSelectable();
				if (source == checkBox) {
					boolean selected = checkBox.isSelected();
					logger.debug("Checkbox changed to " + selected);
					comboBox.setEnabled(selected);	
					fullPropertyName.setEnabled(selected);
					updateEntityModel();					
				}
			}
		});
	}
	
	private void updatePropertyDetails() {
		OWLPropertyItem item = (OWLPropertyItem)comboBox.getSelectedItem();
		String txt = item.getIRI().toString();
		fullPropertyName.setText(txt);
	}

	private void updatePropertySelectionFromModel(Range range) {		
		if(range == null) {
			setEnabledStatusAccordingToSelectedRange(range);
            return;
        }		
		
		Collection<OntologyTermValidation> containingValidations = getWorkbookManager()
				.getOntologyManager().getContainingOntologyTermValidations(
						range);
				
		
		if (containingValidations.size() == 1) {
			OntologyTermValidation validation = containingValidations.iterator().next();
			OWLPropertyItem property = validation.getValidationDescriptor()
					.getOWLPropertyItem();
			if (property == null) {
				setSelectedStatus(false);
			} else {
				comboBox.setSelectedItem(property);
				checkBox.setSelected(true);				
			}
		} else {
			setSelectedStatus(false);
		}				
		setEnabledStatusAccordingToSelectedRange(range);
	}

	private void updateEntityModel() {
		OWLPropertyItem item = (OWLPropertyItem) comboBox.getSelectedItem();
		if (item == null || !checkBox.isSelected()) {
			logger.debug("Property item not selected or wanted");
			getWorkbookManager().getEntitySelectionModel().setOWLPropertyItem(
					null);
		} else {
			logger.debug("Property item selected as: "
					+ item.getIRI().toString());
			getWorkbookManager().getEntitySelectionModel().setOWLPropertyItem(
					item);
		}
	}

	private WorkbookManager getWorkbookManager() {
		return this.workbookManager;
	}
	
	private Set<OWLPropertyItem> getPropertyItems(OWLOntology ontology) {		
		return getWorkbookManager().getOntologyManager().getAllOWLProperties(ontology);		
	}

	private void updateModel(OWLOntology ontology) {
		
		OWLPropertyItem[] sortedProperties = getPropertyItems(ontology).toArray(new OWLPropertyItem[]{});
		Arrays.sort(sortedProperties, new Comparator<OWLPropertyItem>() {
			@Override
			public int compare(OWLPropertyItem o1, OWLPropertyItem o2) {
				return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
			}
		});
		
		logger.debug("Updating model for properties list with "
				+ sortedProperties.length+ " properties");
		
		comboBox.removeAllItems();
		for (OWLPropertyItem property : sortedProperties) {
			comboBox.addItem(property);
		}
		setEnabledStatus();
	}
	
	private void setEnabledStatusAccordingToSelectedRange(Range range) {
		if (range==null) {
			checkBox.setEnabled(false);
			comboBox.setEnabled(checkBox.isSelected());
		}
		else {
			if (!range.isCellSelection() || !isPropertiesAvailable()) {
				checkBox.setEnabled(false);
				comboBox.setEnabled(checkBox.isSelected());
			}
			else {
				Collection<OntologyTermValidation> intersectingValidations = getWorkbookManager().getOntologyManager().getIntersectingOntologyTermValidations(range);
				Collection<OntologyTermValidation> containingValidations = getWorkbookManager().getOntologyManager().getContainingOntologyTermValidations(
						range);
				boolean enabledStatus = containingValidations.size() <= 1 && intersectingValidations.size() == containingValidations.size();
				checkBox.setEnabled(enabledStatus);
				comboBox.setEnabled(checkBox.isSelected());
			}
		}
	}
	private void setEnabledStatus() {		
		if (!isPropertiesAvailable()) {
			comboBox.setEnabled(false);
			checkBox.setEnabled(false);			
		}
		else {
			checkBox.setEnabled(true);
			comboBox.setEnabled(checkBox.isSelected());
		}
			
	}
	
	private boolean isPropertiesAvailable() {
		return getPropertyCount()>0;
	}
	private int getPropertyCount() {
		return comboBox.getItemCount();
	}

	private void setSelectedStatus(boolean selected) {		
		checkBox.setSelected(selected);
		setEnabledStatus();
	}	

}
