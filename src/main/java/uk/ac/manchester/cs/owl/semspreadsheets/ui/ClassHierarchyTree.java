package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
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
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * @author Matthew Horridge
 * @author Stuart Owen
 *
 */
@SuppressWarnings("serial")
public class ClassHierarchyTree extends JTree {
	
	private static Logger logger = Logger.getLogger(ClassHierarchyTree.class);

    private WorkbookManager workbookManager;

    private boolean transmittingSelectioToModel;

    private boolean updatingSelectionFromModel;

    public ClassHierarchyTree(final WorkbookManager manager) {
        super(new ClassHierarchyTreeModel(manager));
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);        
        this.workbookManager = manager;
        
        addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                previewSelectedClass();
            }
        });
        manager.getEntitySelectionModel().addListener(new EntitySelectionModelListener() {
            public void selectionChanged() {
                updateSelectionFromModel();
            }
        });
        setCellRenderer(new WorkbookManagerCellRenderer(workbookManager));
    }

    public void updateModel() {
    	setModel(new ClassHierarchyTreeModel(workbookManager));
    }
    
    public ClassHierarchyTreeModel getClassHierarchyTreeModel() {
        return (ClassHierarchyTreeModel) super.getModel();
    }

    private void previewSelectedClass() {
    	logger.debug("In previewSelectedEntity");    	
        if (!updatingSelectionFromModel) {
            transmittingSelectioToModel = true;            
            try {
                TreePath [] selectedPaths = getSelectionPaths();
                if(selectedPaths == null) {
                    return;
                }
                Set<OWLEntity> selectedEntities = new HashSet<OWLEntity>();
                //should only be 
                for(TreePath path : selectedPaths) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if(node instanceof ClassHierarchyNode) {
                        selectedEntities.addAll(((ClassHierarchyNode) node).getOWLClasses());
                    }
                    else {
                        selectedEntities.add((OWLNamedIndividual) ((ClassHierarchyIndividualNode) node).getUserObject());
                    }
                }
                workbookManager.getEntitySelectionModel().setSelection(selectedEntities.iterator().next());                
                workbookManager.previewValidation();                
            }
            finally {
                transmittingSelectioToModel = false;
            }
        }
    }

    private void updateSelectionFromModel() {
    	logger.debug("In updateSelectionFromModel");
        if(!transmittingSelectioToModel) {
            try {
                updatingSelectionFromModel = true;
                setSelectedClass((OWLClass) workbookManager.getEntitySelectionModel().getSelection());
            }
            finally {
                updatingSelectionFromModel = false;
            }
        }
    }

    public void setSelectedClass(OWLClass cls) {
        Collection<TreePath> treePaths = getClassHierarchyTreeModel().getTreePathsForEntity(cls);
        clearSelection();
        for(TreePath path : treePaths) {
            addSelectionPath(path);
            scrollPathToVisible(path);
        }

    }   
}
