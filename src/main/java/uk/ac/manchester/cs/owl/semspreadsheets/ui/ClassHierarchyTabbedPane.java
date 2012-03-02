package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

@SuppressWarnings("serial")
public class ClassHierarchyTabbedPane extends JTabbedPane {
	
	private static final Logger logger=Logger.getLogger(ClassHierarchyTabbedPane.class);
	
	private final WorkbookManager workbookManager;	


	public ClassHierarchyTabbedPane(WorkbookManager workbookManager) {
		super();
		this.workbookManager = workbookManager;		
		
		workbookManager.addListener(new WorkbookManagerListener() {
			
			@Override
			public void workbookLoaded(WorkbookManagerEvent event) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void workbookChanged(WorkbookManagerEvent event) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void validationAppliedOrCancelled() {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void ontologiesChanged(WorkbookManagerEvent event) {
				updateTabs();			
			}
		});
		
	}
	
	private void updateTabs() {
		removeTabs();
		for (OWLOntology ontology : getLoadedOntologies()) {
			ClassHierarchyTree tree = new ClassHierarchyTree(getWorkbookManager(),ontology);
	        tree.updateModel();
	        JScrollPane sp = new JScrollPane(tree);
	        sp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	        String tabTitle = ontology.getOntologyID().getOntologyIRI().getFragment();
	        logger.debug("Adding tab with title:"+tabTitle+" for ontology "+ ontology.getOntologyID().toString());
	        addTab(tabTitle, sp);
		}		
	}		
	
	private Set<OWLOntology> getLoadedOntologies() {
		return getWorkbookManager().getLoadedOntologies();
	}
	
	private void removeTabs() {
		while(getTabCount()>0) {
			removeTabAt(0);
			logger.debug("Removing tab at 0");
		}
	}
	
	private Font ontologyNotLoadedFont = new Font("Lucida Grande", Font.BOLD, 14);
	
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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
		return workbookManager;
	}

}
