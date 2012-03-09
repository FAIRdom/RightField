package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.model.KnownOntologies;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Pane that contains an individual tab for each Ontology. This class handles the displaying and creation of the tabs, and listens to events
 * for when new ontologies are loaded.
 * 
 * @author Stuart Owen
 *
 */

@SuppressWarnings("serial")
public class ClassHierarchyTabbedPane extends JTabbedPane {	
	
	private static final Logger logger = Logger.getLogger(ClassHierarchyTabbedPane.class);	
	
	private List<OWLOntology> knownOntologies = new ArrayList<OWLOntology>();

	private final WorkbookFrame workbookFrame;
	

	public ClassHierarchyTabbedPane(WorkbookFrame workbookFrame) {
		super();
		this.workbookFrame = workbookFrame;		
		
		getWorkbookManager().addListener(new WorkbookManagerListener() {
			
			@Override
			public void workbookLoaded(WorkbookManagerEvent event) {
								
			}
			
			@Override
			public void workbookCreated(WorkbookManagerEvent event) {
								
			}
			
			@Override
			public void validationAppliedOrCancelled() {
								
			}
			
			@Override
			public void ontologiesChanged(WorkbookManagerEvent event) {
				updateTabs();							
			}
			
		});			
		
		addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int index = getSelectedIndex();
				logger.debug("Selected tab index: "+index);
				OWLOntology ontology = null;
				if (index!=-1) {
					ClassHierarchyTabComponent comp = ((ClassHierarchyTabComponent)getTabComponentAt(index));
					if (comp!=null) {
						ontology = comp.getOntology();
					}					
					else {
						logger.debug("Selected tab component was NULL");
					}
				}
				if (ontology!=null) {
					getWorkbookFrame().setSelectedOntology(ontology);
				}								
				else {
					logger.debug("Selected ontology was NULL");
				}
			}
		});
				
	}
	
	public int tabIndexForOntology(OWLOntology ontology) {
		return indexOfTab(tabTitle(ontology));
	}
	
	public String tabTitle(OWLOntology ontology) {
		return ontology.getOntologyID().getOntologyIRI().getFragment();
	}	
	
	private synchronized void updateTabs() {		
		Set<OWLOntology> loadedOntologies = getLoadedOntologies();
		for (OWLOntology ontology : loadedOntologies) {
			if (!ontology.getOntologyID().getOntologyIRI().toString().equals(KnownOntologies.PROTEGE_ONTOLOGY)) {
				if (!knownOntologies.contains(ontology)) {				
					createTab(ontology);
					knownOntologies.add(ontology);
				}	
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
		ClassHierarchyTree tree = new ClassHierarchyTree(getWorkbookManager(),ontology,this);
		tree.updateModel();
		JScrollPane sp = new JScrollPane(tree);
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
	
	private void removeTab(OWLOntology ontology) {
		int i = tabIndexForOntology(ontology);
		knownOntologies.remove(ontology);
		removeTabAt(i);
	}
	
	private Set<OWLOntology> getLoadedOntologies() {
		return getWorkbookManager().getLoadedOntologies();
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

	public WorkbookManager getWorkbookManager() {
		return getWorkbookFrame().getWorkbookManager();
	}

	public WorkbookFrame getWorkbookFrame() {
		return workbookFrame;
	}
}
