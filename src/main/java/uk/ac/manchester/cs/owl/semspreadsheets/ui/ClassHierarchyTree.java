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
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

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
    
    private OWLOntology ontology;

	private final ClassHierarchyTabbedPane pane;

    public ClassHierarchyTree(final WorkbookManager manager, OWLOntology ontology, final ClassHierarchyTabbedPane pane) {
        super(new ClassHierarchyTreeModel(manager,ontology));
		this.ontology = ontology;
		this.pane = pane;
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);        
        this.workbookManager = manager;
        
        addTreeSelectionListener(new TreeSelectionListener() {
            @Override
			public void valueChanged(TreeSelectionEvent e) {
                previewSelectedClass();
            }
        });
        manager.getEntitySelectionModel().addListener(new EntitySelectionModelListener() {
            @Override
			public void selectionChanged() {
                updateSelectionFromModel();
            }
        });
        setCellRenderer(new WorkbookManagerCellRenderer(workbookManager));
    }

    public void updateModel() {
    	setModel(new ClassHierarchyTreeModel(workbookManager,ontology));
    }
    
    public ClassHierarchyTreeModel getClassHierarchyTreeModel() {
        return (ClassHierarchyTreeModel) super.getModel();
    }

    private void previewSelectedClass() {
    	logger.debug("In previewSelectedEntity");    	
        
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

    private void updateSelectionFromModel() {
    	logger.debug("In updateSelectionFromModel");
        if(!transmittingSelectioToModel) {                            
            setSelectedClass((OWLClass) workbookManager.getEntitySelectionModel().getSelection());                      
        }
    }

    public void setSelectedClass(OWLClass cls) {
        Collection<TreePath> treePaths = getClassHierarchyTreeModel().getTreePathsForEntity(cls);        
        clearSelection();
        for(TreePath path : treePaths) {
            addSelectionPath(path);
            scrollPathToVisible(path);
        }
        if (!treePaths.isEmpty()) {
        	int index = pane.tabIndexForOntology(ontology);
        	if (index!=-1) {
        		pane.setSelectedIndex(index);
        	}
        }
    }         
}
