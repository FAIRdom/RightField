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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.AbstractEntitySelectionModelListener;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.CellSelectionListener;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.OntologyManagerListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyItem;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.ValidationType;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * UI Element that show the lists of properties that can be applied along with
 * the Validation type.
 * 
 * @author Stuart Owen
 * 
 */
@SuppressWarnings("serial")
public class PropertyListPanel extends JPanel {
	private final WorkbookManager workbookManager;
	private static final Logger logger = LogManager.getLogger();
	private JComboBox<OWLPropertyItem> comboBox;
	private JCheckBox checkBox;	
	private OWLOntology selectedOntology;
	private DefaultComboBoxModel<OWLPropertyItem> propertyListModel;

	public PropertyListPanel(WorkbookManager workbookManager) {

		this.workbookManager = workbookManager;
		setLayout(new BorderLayout());
		
		addCheckBox();
				
		addDropDownBox();	
		
		setupListeners();
		setSelectedStatus(false);
	}	

	private void addDropDownBox() {
		comboBox = new JComboBox<OWLPropertyItem>();
		propertyListModel = new DefaultComboBoxModel<OWLPropertyItem>();
		comboBox.setModel(propertyListModel);
		add(comboBox, BorderLayout.CENTER);		
	}

	private void addCheckBox() {
		checkBox = new JCheckBox("Include a property");				
		add(checkBox, BorderLayout.NORTH);
	}

	private void setupListeners() {
		getWorkbookManager().getOntologyManager().addListener(
				new OntologyManagerListener() {

					@Override
					public void ontologySelected(OWLOntology ontology) {
						selectedOntology = ontology;
						updateModel();
					}

					@Override
					public void ontologiesChanged() {

					}
				});
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
				if (event.getStateChange() == ItemEvent.SELECTED) {
					updateEntityModel();
					updatePropertyDetailsToolTip();
				}
			}
		});

		getWorkbookManager().getEntitySelectionModel().addListener(
				new AbstractEntitySelectionModelListener() {

					@Override
					public void validationTypeChanged(ValidationType type) {
						updateModel();
					}
				});

		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				Object source = event.getItemSelectable();
				if (source == checkBox) {
					setEnabledStatus();
					updateEntityModel();
				}
			}
		});
	}

	private void updatePropertyDetailsToolTip() {
		OWLPropertyItem item = (OWLPropertyItem) propertyListModel
				.getSelectedItem();
		String txt = item.getIRI().toString();		
		comboBox.setToolTipText(txt);
	}

	private void updatePropertySelectionFromModel(Range range) {
		if (range == null) {
			setEnabledStatusAccordingToSelectedRange(range);
			return;
		}

		Collection<OntologyTermValidation> containingValidations = getWorkbookManager()
				.getOntologyManager().getContainingOntologyTermValidations(
						range);

		if (containingValidations.size() == 1) {
			OntologyTermValidation validation = containingValidations
					.iterator().next();
			OWLPropertyItem property = validation.getValidationDescriptor()
					.getOWLPropertyItem();
			if (property == null) {
				setSelectedStatus(false);
			} else {
				propertyListModel.setSelectedItem(property);
				checkBox.setSelected(true);
			}
		} else {
			setSelectedStatus(false);
		}
		setEnabledStatusAccordingToSelectedRange(range);
	}

	private void updateEntityModel() {
		OWLPropertyItem item = (OWLPropertyItem) propertyListModel
				.getSelectedItem();
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

	private synchronized void updateModel() {

		if (selectedOntology == null) {
			propertyListModel.removeAllElements();
		} else {
			ValidationType type = getWorkbookManager()
					.getEntitySelectionModel().getValidationType();
			Set<OWLPropertyItem> properties = getWorkbookManager()
					.getOntologyManager().getAllOWLProperties(selectedOntology,
							type);
			OWLPropertyItem[] sortedProperties = properties
					.toArray(new OWLPropertyItem[] {});
			Arrays.sort(sortedProperties, new Comparator<OWLPropertyItem>() {
				@Override
				public int compare(OWLPropertyItem o1, OWLPropertyItem o2) {
					return o1.toString().toLowerCase()
							.compareTo(o2.toString().toLowerCase());
				}
			});

			logger.debug("Updating model for properties list with "
					+ sortedProperties.length + " properties");
			
			propertyListModel.removeAllElements();
			for (OWLPropertyItem property : sortedProperties) {
				propertyListModel.addElement(property);
			}
			
		}
		setEnabledStatus();			
	}

	private void setEnabledStatusAccordingToSelectedRange(Range range) {
		if (range == null) {
			checkBox.setEnabled(false);
			comboBox.setEnabled(checkBox.isSelected());
		} else {
			if (!range.isCellSelection() || !isPropertiesAvailable()) {
				checkBox.setEnabled(false);
				comboBox.setEnabled(checkBox.isSelected());
			} else {
				Collection<OntologyTermValidation> intersectingValidations = getWorkbookManager()
						.getOntologyManager()
						.getIntersectingOntologyTermValidations(range);
				Collection<OntologyTermValidation> containingValidations = getWorkbookManager()
						.getOntologyManager()
						.getContainingOntologyTermValidations(range);
				boolean enabledStatus = containingValidations.size() <= 1
						&& intersectingValidations.size() == containingValidations
								.size();
				checkBox.setEnabled(enabledStatus);
				comboBox.setEnabled(checkBox.isSelected());
			}
		}
	}

	private void setEnabledStatus() {
		if (!isPropertiesAvailable()) {
			comboBox.setEnabled(false);
			checkBox.setEnabled(false);
		} else {
			checkBox.setEnabled(true);
			comboBox.setEnabled(checkBox.isSelected());
		}		
		checkBox.setSelected(checkBox.isSelected() && comboBox.isEnabled());
	}

	private boolean isPropertiesAvailable() {
		return getPropertyCount() > 0;
	}

	private int getPropertyCount() {
		return propertyListModel.getSize();
	}

	private void setSelectedStatus(boolean selected) {
		checkBox.setSelected(selected && getPropertyCount()>0);
		setEnabledStatus();
	}

}
