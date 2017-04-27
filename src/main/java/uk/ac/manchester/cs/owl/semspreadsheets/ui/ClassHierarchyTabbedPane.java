/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.semspreadsheets.listeners.AbstractEntitySelectionModelListener;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.CellSelectionListener;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.OntologyManagerListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.skos.SKOSDetector;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.skos.SKOSHierarchyTree;

/**
 * Pane that contains an individual tab for each Ontology. This class handles the displaying and creation of the tabs, and listens to events
 * for when new ontologies are loaded.
 * 
 * @author Stuart Owen
 *
 */

@SuppressWarnings("serial")
public class ClassHierarchyTabbedPane extends JTabbedPane {	
	
	private static final Logger logger = LogManager.getLogger();	
	
	private List<OWLOntology> knownOntologies = new ArrayList<OWLOntology>();

	private final WorkbookFrame workbookFrame;
	
	private Map<JScrollPane,HierarchyTree> tabToTreeMap = new HashMap<JScrollPane,HierarchyTree>();
		

	public ClassHierarchyTabbedPane(WorkbookFrame workbookFrame) {
		super();
		this.workbookFrame = workbookFrame;				
		setupListeners();				
	}

	private void setupListeners() {
		getWorkbookManager().getOntologyManager().addListener(new OntologyManagerListener() {
									
			@Override
			public void ontologiesChanged() {
				updateTabs();							
			}

			@Override
			public void ontologySelected(OWLOntology ontology) {				
				
			}						
		});			
		
		addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() instanceof ClassHierarchyTabbedPane) {				
					HierarchyTree selectedHierarchyTree = ((ClassHierarchyTabbedPane)e.getSource()).getSelectedHierarchyTree();					
					if (selectedHierarchyTree!=null) {						
						OWLOntology ontology = selectedHierarchyTree.getOntology();
						fireOntologySelected(ontology);
						selectedHierarchyTree.previewSelectedClass();
					}
					else {
						logger.debug("Selected hierarchy tree is NULL when stateChanged");
					}									
				}								
			}
		});
		
		getWorkbookManager().getSelectionModel().addCellSelectionListener(new CellSelectionListener() {
			
			@Override
			public void selectionChanged(Range range) {
				Collection<OntologyTermValidation> validations = getWorkbookManager().getOntologyManager().getContainingOntologyTermValidations(range);
				if (validations.isEmpty()) {
					clearSelection();
				}
				else {
					OntologyTermValidation validation = validations.iterator().next();
					OWLClass cls = getWorkbookManager().getOntologyManager().getDataFactory().getOWLClass(validation.getValidationDescriptor().getEntityIRI());
					if (validation.getValidationDescriptor().definesLiteral()) {
						clearSelection();
					}
					else {
						updateSelectedTabAndEntity(cls);
					}					
				}				
			}
		});
		
		//as far as I can tell, this is only needed for the FindClassPanel searching. Other selections are made through
		//the CellSelectionListener
		getWorkbookManager().getEntitySelectionModel().addListener(new AbstractEntitySelectionModelListener() {			
			@Override
			public void selectedEntityChanged(OWLEntity entity) {				
				updateSelectedTabAndEntity(entity);
			}
		});
	}
	
	private void fireOntologySelected(OWLOntology ontology) {
		logger.debug("Ontology selected "+ontology);
		getWorkbookManager().getOntologyManager().ontologySelected(ontology);
	}
	
	private void clearSelection() {
		HierarchyTree tree = getSelectedHierarchyTree();
		if (tree!=null) {
			tree.clearSelection();
		}
	}
	
	private void updateSelectedTabAndEntity(OWLEntity selectedEntity) {
		logger.debug("Updating ontology tab for selected entity - "
				+ selectedEntity);

		HierarchyTree tree = getSelectedHierarchyTree();

		// if it contains class select it
		if (tree != null && tree.containsEntity(selectedEntity)) {
			tree.setSelectedEntity(selectedEntity);
		} else {
			// loop and select first tab to contain cls
			int i = 0;
			if (getTabCount() > 0) {
				for (HierarchyTree t : getHierachyTrees()) {
					if (t.containsEntity(selectedEntity)) {
						t.setSelectedEntity(selectedEntity);
						setSelectedIndex(i);
						break;
					}
					i++;
				}
			}
		}			
	}
	
	private List<HierarchyTree> getHierachyTrees() {
		List<HierarchyTree> trees = new ArrayList<HierarchyTree>();
		for (int i=0;i<getComponentCount();i++) {
			Component comp = getComponent(i);
			if (comp instanceof JScrollPane) {
				HierarchyTree tree = tabToTreeMap.get(comp);
				if (tree!=null) {
					trees.add(tree);
				}				
			}
		}
		return trees;
	}
	
	private HierarchyTree getSelectedHierarchyTree() {
		JScrollPane scrollPane = (JScrollPane)getSelectedComponent();
		return tabToTreeMap.get(scrollPane);
	}		
	
	private int tabIndexForOntology(OWLOntology ontology) {
		return indexOfTab(tabTitle(ontology));
	}
	
	private String tabTitle(OWLOntology ontology) {
		String title = getLabelValue(ontology);
		if (title==null) {
			title = ontology.getOntologyID().getOntologyIRI().getFragment();
			if (title.trim().isEmpty()) {
				if (ontology.getOntologyID().getVersionIRI()!=null) {
					title=ontology.getOntologyID().getVersionIRI().getFragment();
				}
				else {
					title=ontology.getOntologyID().toString();
				}
				
			}
		}
		
		return title;
	}	
	
	/**
	 * 
	 * @param ontology
	 * @return looks to see if the ontology has an rdf:label annotation, and returns is, otherwise returns null
	 */
	private String getLabelValue(OWLOntology ontology) {
		OWLAnnotationProperty label = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		for(OWLAnnotation annotation : ontology.getAnnotations()) {
			if (annotation.getProperty() == label) {
				return annotation.getValue().toString();
			}
		}
		return null;
	}
	
	private synchronized void updateTabs() {		
		Set<OWLOntology> loadedOntologies = getLoadedOntologies();
		for (OWLOntology ontology : loadedOntologies) {			
				if (!knownOntologies.contains(ontology)) {				
					createTab(ontology);
					knownOntologies.add(ontology);
				}								
		}
		//remove tabs that are no longer required
		Set<OWLOntology> forRemoval = new HashSet<OWLOntology>();
		for (OWLOntology ontology : knownOntologies) {
			if (!loadedOntologies.contains(ontology)) {
				forRemoval.add(ontology);
			}
		}
		
		for (OWLOntology ontology : forRemoval) {
			removeTab(ontology);
		}				
	}

	private void createTab(OWLOntology ontology) {
		HierarchyTree tree = createTree(ontology);
		JScrollPane sp = new JScrollPane(tree);
		tabToTreeMap.put(sp, tree);
		sp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		if (tabIndexForOntology(ontology)==-1) {
			String title = tabTitle(ontology);
			addTab(title,sp);
			int index = tabIndexForOntology(ontology);
			setTabComponentAt(index, new ClassHierarchyTabComponent(this,getWorkbookFrame(),ontology));			
			setSelectedIndex(index);			
		}
		else {
			logger.warn("Attempting to create duplicate tab for ontology: "+ontology.getOntologyID().toString());
		}				
	}
	
	private HierarchyTree createTree(OWLOntology ontology) {
		if (SKOSDetector.isSKOS(ontology)) {
			return new SKOSHierarchyTree(getWorkbookManager(),ontology);
		}
		else {
			return new ClassHierarchyTree(getWorkbookManager(), ontology);
		}
	}
	
	private void removeTab(OWLOntology ontology) {
		int i = tabIndexForOntology(ontology);
		knownOntologies.remove(ontology);
		Component pane = getComponent(i);
		tabToTreeMap.remove(pane);
		removeTabAt(i);
	}
	
	private Set<OWLOntology> getLoadedOntologies() {
		return getWorkbookManager().getOntologyManager().getLoadedOntologies();
	}				
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Font ontologyNotLoadedFont = new Font("Lucida Grande", Font.BOLD, 14);
        if (getTabCount() == 0) {
            Color oldColor = g.getColor();
            g.setColor(Color.LIGHT_GRAY);
            Font oldFont = g.getFont();
            g.setFont(ontologyNotLoadedFont);
            String msg = "No ontologies loaded";
            Rectangle bounds = g.getFontMetrics().getStringBounds(msg, g).getBounds();
            g.drawString(msg, getWidth() / 2 - bounds.width / 2, getHeight() / 2 - g.getFontMetrics().getAscent());
            g.setFont(oldFont);
            g.setColor(oldColor);
        }
    }

	private WorkbookManager getWorkbookManager() {
		return getWorkbookFrame().getWorkbookManager();
	}

	private WorkbookFrame getWorkbookFrame() {
		return workbookFrame;
	}
}
