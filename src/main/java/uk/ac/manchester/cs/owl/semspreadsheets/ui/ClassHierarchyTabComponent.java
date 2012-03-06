package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeEvent;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

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
		addWorkbookManagerListener();
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	protected void removeOntology() {
		logger.debug("About to remove ontology:" + ontology.toString());
		getWorkbookFrame().removeOntology(ontology);
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

	private void updateTabClosableStatus() {
		
		Collection<IRI> ontologyIRIs = getWorkbookManager()
				.getOntologyTermValidationManager().getOntologyIRIs();
		boolean used = ontologyIRIs.contains(getOntology().getOntologyID()
				.getOntologyIRI());
		logger.debug("Checking wether the ontology is used in the workbook = "+used);
		closeButton.setEnabled(!used);
	}

	private WorkbookFrame getWorkbookFrame() {
		return workbookFrame;
	}

	private WorkbookManager getWorkbookManager() {
		return getWorkbookFrame().getWorkbookManager();
	}
	
	private void addWorkbookManagerListener() {
		getWorkbookManager().getWorkbook().addChangeListener(new WorkbookChangeListener() {			
			@Override
			public void workbookChanged(WorkbookChangeEvent event) {
				updateTabClosableStatus();
			}
			
			@Override
			public void sheetRenamed(String oldName, String newName) {
				updateTabClosableStatus();
			}
			
			@Override
			public void sheetRemoved() {
				
			}
			
			@Override
			public void sheetAdded() {
				updateTabClosableStatus();
			}
		});
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
