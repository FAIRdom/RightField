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
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyItem;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;


@SuppressWarnings("serial")
public class PropertyListPanel extends JPanel {
	private final WorkbookManager workbookManager;	
	private static final Logger logger = Logger.getLogger(PropertyListPanel.class);
	private JComboBox comboBox;
	private JCheckBox checkBox;
	
	public PropertyListPanel(WorkbookManager workbookManager) {
		this.workbookManager = workbookManager;
		setLayout(new BorderLayout());			
		comboBox = new JComboBox();
		comboBox.setEnabled(false);
		add(new JLabel("Property"),BorderLayout.NORTH);
		checkBox = new JCheckBox("Add a property?");
		checkBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent event) {
				Object source = event.getItemSelectable();
				if (source == checkBox) {
					boolean selected = checkBox.isSelected();
					logger.debug("Checkbox changed to "+selected);
					comboBox.setEnabled(selected);
					updateEntityModel();
				}								
			}
		});
		
		add(checkBox,BorderLayout.WEST);
		add(comboBox,BorderLayout.SOUTH);
		workbookManager.addListener(new WorkbookManagerListener() {
			
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
				updateModel();
			}			
		});
		comboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {				
				updateEntityModel();
			}			
		});
	}
	
	private void updateEntityModel() {
		OWLPropertyItem item = (OWLPropertyItem)comboBox.getSelectedItem();
		if (item==null || !checkBox.isSelected()) {
			logger.debug("Property item not selected or wanted");
			getWorkbookManager().getEntitySelectionModel().setOWLPropertyItem(null);
		}
		else {
			logger.debug("Property item selected as: "+item.getIRI().toString());
			getWorkbookManager().getEntitySelectionModel().setOWLPropertyItem(item);
		}
		
	}
	
	private WorkbookManager getWorkbookManager() {
		return this.workbookManager;
	}
	
	private void updateModel() {		
		Set<OWLPropertyItem> properties = getWorkbookManager().getAllOWLProperties();
		
		logger.debug("Updating model for properties list with "+properties.size()+" properties");
		comboBox.removeAllItems();
		for (OWLPropertyItem property : properties) {
			comboBox.addItem(property);
		}		
	}
}

//class PropertyListComboModel implements ComboBoxModel {
//	private static final Logger logger = Logger.getLogger(PropertyListComboModel.class);
//	
//	List<OWLDataProperty> dataProperties = new ArrayList<OWLDataProperty>();
//	Object selectedItem = null;
//	List<ListDataListener> listeners = new ArrayList<ListDataListener>();
//	
//	public void updateProperties(Collection<OWLDataProperty> properties) {
//		logger.debug("properties list model updated with "+properties.size()+" properties");
//		dataProperties.clear();
//		dataProperties.addAll(properties);		
//	}
//
//	@Override
//	public void addListDataListener(ListDataListener l) {
//		listeners.add(l);		
//	}
//
//	@Override
//	public Object getElementAt(int index) {
//		return dataProperties.get(index);
//	}
//
//	@Override
//	public int getSize() {
//		return dataProperties.size();
//	}
//
//	@Override
//	public void removeListDataListener(ListDataListener l) {
//		listeners.remove(l);		
//	}
//
//	@Override
//	public Object getSelectedItem() {
//		return selectedItem;
//	}
//
//	@Override
//	public void setSelectedItem(Object anItem) {
//		selectedItem = anItem;		
//	}
//	
//}
