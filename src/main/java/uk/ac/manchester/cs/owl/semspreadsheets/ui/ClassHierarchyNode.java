package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.NodeFactory;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
public class ClassHierarchyNode extends DefaultMutableTreeNode {

    private WorkbookManager workbookManager;

    public ClassHierarchyNode(WorkbookManager workbookManager) {
        super(NodeFactory.getOWLClassTopNode());
        this.workbookManager = workbookManager;
    }

    public ClassHierarchyNode(WorkbookManager workbookManager, Node<OWLClass> clses) {
        super(clses);
        this.workbookManager = workbookManager;
    }

    public Set<OWLClass> getOWLClasses() {
        return ((Node<OWLClass>) getUserObject()).getEntities();
    }


}
