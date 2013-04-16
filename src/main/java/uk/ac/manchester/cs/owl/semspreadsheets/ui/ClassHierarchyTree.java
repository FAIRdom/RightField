/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * @author Matthew Horridge
 * @author Stuart Owen
 *
 */
@SuppressWarnings("serial")
public class ClassHierarchyTree extends JTree implements HierarchyTree {
	
	private static Logger logger = Logger.getLogger(ClassHierarchyTree.class);

    private WorkbookManager workbookManager;    
    
    private OWLOntology ontology;	

    public ClassHierarchyTree(final WorkbookManager manager, OWLOntology ontology) {
        super();
        setModel(createTreeModel(manager.getOntologyManager(),ontology));
        
		this.ontology = ontology;               
        this.workbookManager = manager;
        
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        addTreeSelectionListener(new TreeSelectionListener() {
            @Override
			public void valueChanged(TreeSelectionEvent e) {
                previewSelectedClass();
            }
        });
         
        setupCellRenderer();                 
    }
    
    protected void setupCellRenderer() {
    	setCellRenderer(new OntologyCellRenderer(getWorkbookManager().getOntologyManager()));
    }
       
    protected HierarchyTreeModel createTreeModel(OntologyManager ontologyManager, OWLOntology ontology) {
    	return new ClassHierarchyTreeModel(ontologyManager,ontology);
    }
    
    @Override
    public OWLOntology getOntology() {
    	return ontology;
    }    
    
    public HierarchyTreeModel getHierarchyTreeModel() {
        return (HierarchyTreeModel) super.getModel();
    }         

    @Override
	public void previewSelectedClass() {
		logger.debug("In previewSelectedClass");
		
		OWLEntity selectedEntity = getSelectedEntity();
		if (selectedEntity!=null) {
			if (!getWorkbookManager().getEntitySelectionModel().getSelectedEntity().equals(selectedEntity)) {
				logger.debug("Setting selected entity to "+selectedEntity.getIRI().toString());
				getWorkbookManager().getEntitySelectionModel().setSelectedEntity(selectedEntity);
			}			
			getWorkbookManager().previewValidation();
		}					
	}    		

	@Override
    public boolean containsEntity(OWLEntity entity) {
    	return getOntology().containsEntityInSignature(entity.getIRI());
    }
    
	@Override
    public void setSelectedEntity(OWLEntity entity) {
        Collection<TreePath> treePaths = getHierarchyTreeModel().getTreePathsForEntity(entity);        
        clearSelection();
        if (!treePaths.isEmpty()) {
        	TreePath path = treePaths.iterator().next();
        	addSelectionPath(path);
            scrollPathToVisible(path);        	
        }              
    } 
    
	@Override
    public OWLEntity getSelectedEntity() {
		TreePath[] selectedPaths = getSelectionPaths();
		if (selectedPaths == null) {
			return null;
		}
		Set<OWLEntity> selectedEntities = new HashSet<OWLEntity>();
		
		for (TreePath path : selectedPaths) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			if (node instanceof ClassHierarchyTreeNode) {
				selectedEntities.addAll(((ClassHierarchyTreeNode) node)
						.getOWLClasses());
			} else {
				selectedEntities
						.add((OWLNamedIndividual) ((ClassHierarchyIndividualNode) node)
								.getUserObject());
			}
		}
		OWLEntity selectedEntity = selectedEntities.iterator().next();
		return selectedEntity;
	}
    
    protected WorkbookManager getWorkbookManager() {
    	return workbookManager;
    }    
}
