/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationListener;

/**
 * @author Stuart Owen
 * 
 *         Component that appears on the tab for the tabbed panel, and in
 *         particular handles the closing of ontologies.
 */

@SuppressWarnings("serial")
class ClassHierarchyTabComponent extends JPanel {
	private final ClassHierarchyTabbedPane pane;
	private final OWLOntology ontology;
	private static Logger logger = Logger
			.getLogger(ClassHierarchyTabComponent.class);
	
	JButton closeButton;
	private final WorkbookFrame workbookFrame;
	
	private OntologyTermValidationListener ontologyValidationListener;
	
	public ClassHierarchyTabComponent(final ClassHierarchyTabbedPane pane,
			WorkbookFrame workbookFrame, OWLOntology ontology) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));		
		this.pane = pane;
		this.workbookFrame = workbookFrame;
		this.ontology = ontology;		
		setOpaque(false);		
		setText();
		add(new JLabel(" "));
		setButton();					
		addOntologyTermValidationListener();		
		updateTabClosableStatus();
	}	
	
	public OWLOntology getOntology() {
		return ontology;
	}

	protected void removeOntology() {
		logger.debug("About to remove ontology:" + ontology.toString());
		getWorkbookFrame().removeOntology(ontology);
		getOntologyManager().removeListener(ontologyValidationListener);		
	}

	private String getTitle() {
		int i = pane.indexOfTabComponent(ClassHierarchyTabComponent.this);
		if (i != -1) {
			return pane.getTitleAt(i);
		}
		return null;
	}

	private void setText() {
		JLabel label = new JLabel() {
			public String getText() {
				return getTitle();
			}
		};
		add(label);
	}

	private void setButton() {
		closeButton = new TabButton();
		add(closeButton);
	}
	
	protected OntologyManager getOntologyManager() {
		return getWorkbookFrame().getWorkbookManager().getOntologyManager();
	}

	private void updateTabClosableStatus() {
		boolean used = getOntologyManager().isOntologyInUse(getOntology());
		
		logger.debug("Checking wether the ontology is used in the workbook = "+used);
		closeButton.setEnabled(!used);
	}

	private WorkbookFrame getWorkbookFrame() {
		return workbookFrame;
	}	
	
	private void addOntologyTermValidationListener() {
		ontologyValidationListener = new OntologyTermValidationListener() {
			
			@Override
			public void validationsChanged() {
				updateTabClosableStatus();
			}
			
			@Override
			public void ontologyTermSelected(List<OntologyTermValidation> previewList) {
				
			}
		};
		getOntologyManager().addListener(ontologyValidationListener);		
	}

	private class TabButton extends JButton implements ActionListener {
		
		public TabButton() {
			super("x");
			int size = 17;
			setPreferredSize(new Dimension(size, size));
			setToolTipText("close this tab");
			// Make the button looks the same for all Laf's
			setUI(new BasicButtonUI());
			// Make it transparent
			setContentAreaFilled(false);
			// No need to be focusable
			setFocusable(false);
			setBorder(BorderFactory.createEtchedBorder());
			setBorderPainted(false);
			// Making nice rollover effect
			// we use the same listener for all buttons
			addMouseListener(buttonMouseListener);
			setRolloverEnabled(true);
			// Close the proper tab by clicking the button
			addActionListener(this);
		}		

		public void actionPerformed(ActionEvent e) {
			ClassHierarchyTabComponent.this.removeOntology();
		}

		// we don't want to update UI for this button
		public void updateUI() {
		}
	}

	private final MouseListener buttonMouseListener = new MouseAdapter() {
		public void mouseEntered(MouseEvent e) {
			Component component = e.getComponent();
			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(true);
			}
		}

		public void mouseExited(MouseEvent e) {
			Component component = e.getComponent();
			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(false);
			}
		}
	};	
}
