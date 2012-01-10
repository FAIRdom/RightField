package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.NodeFactory;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class ClassHierarchyNode extends DefaultMutableTreeNode {    

    public ClassHierarchyNode(WorkbookManager workbookManager) {
        super(NodeFactory.getOWLClassTopNode());        
    }

    public ClassHierarchyNode(WorkbookManager workbookManager, Node<OWLClass> clses) {
        super(clses);        
    }

    @SuppressWarnings("unchecked")
	public Set<OWLClass> getOWLClasses() {
        return ((Node<OWLClass>) getUserObject()).getEntities();
    }
}
