package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class ClassHierarchyTreeModel implements TreeModel {	

    private ClassHierarchyNode rootNode;

    private WorkbookManager manager;

    private Map<OWLEntity, Collection<DefaultMutableTreeNode>> cls2NodeMap = new HashMap<OWLEntity, Collection<DefaultMutableTreeNode>>();

    private ClassHierarchyTreeModel.NodeContentComparator nodeContentComparator;

    private IndividualNodeContentComparator individualNodeContentComparator = new IndividualNodeContentComparator();

    public ClassHierarchyTreeModel(WorkbookManager manager) {
        this.manager = manager;
        nodeContentComparator = new NodeContentComparator();
        if (manager.getLoadedOntologies().size() > 0) {
            rootNode = new ClassHierarchyNode();
            put(rootNode.getOWLClasses().iterator().next(), rootNode);
            buildChildren(rootNode);
        }
    }

    private void put(OWLEntity cls, DefaultMutableTreeNode node) {    	
        Collection<DefaultMutableTreeNode> nodes = cls2NodeMap.get(cls);
        if (nodes == null) {
            nodes = new ArrayList<DefaultMutableTreeNode>();
            cls2NodeMap.put(cls, nodes);
        }
        nodes.add(node);
    }

    public Collection<DefaultMutableTreeNode> getNodesForEntity(OWLEntity entity) {
        Collection<DefaultMutableTreeNode> hierarchyNodes = cls2NodeMap.get(entity);
        if (hierarchyNodes == null) {
            return Collections.emptyList();
        }
        else {
            return new ArrayList<DefaultMutableTreeNode>(hierarchyNodes);
        }
    }

    public Collection<TreePath> getTreePathsForEntity(OWLEntity entity) {
        Collection<DefaultMutableTreeNode> nodes = getNodesForEntity(entity);
        List<TreePath> treePaths = new ArrayList<TreePath>();
        for (DefaultMutableTreeNode node : nodes) {
            treePaths.add(new TreePath(node.getPath()));
        }
        return treePaths;
    }

    private void buildChildren(ClassHierarchyNode node) {
        Set<OWLClass> clses = node.getOWLClasses();
        OWLClass representative = clses.iterator().next();
        NodeSet<OWLClass> subs = manager.getReasoner().getSubClasses(representative, true);
        if (!subs.isBottomSingleton()) {
            List<Node<OWLClass>> sortedSubs = new ArrayList<Node<OWLClass>>(subs.getNodes());
            Collections.sort(sortedSubs, nodeContentComparator);
            for (Node<OWLClass> sub : sortedSubs) {
                ClassHierarchyNode childNode = new ClassHierarchyNode(sub);
                node.add(childNode);
                for (OWLClass cls : sub) {
                    put(cls, childNode);
                }
                buildChildren(childNode);
            }
        }
        else {
            NodeSet<OWLNamedIndividual> individuals = manager.getReasoner().getInstances(representative, true);
            List<OWLNamedIndividual> sortedIndividuals = new ArrayList<OWLNamedIndividual>(individuals.getFlattened());
            Collections.sort(sortedIndividuals, individualNodeContentComparator);
            for (OWLNamedIndividual ind : sortedIndividuals) {
                ClassHierarchyIndividualNode childNode = new ClassHierarchyIndividualNode(ind);
                node.add(childNode);
                put(ind, childNode);
            }
        }
    }

    public Object getRoot() {
        return rootNode;
    }

    public Object getChild(Object parent, int index) {
        return ((DefaultMutableTreeNode) parent).getChildAt(index);
    }

    public int getChildCount(Object parent) {
        return ((DefaultMutableTreeNode) parent).getChildCount();
    }

    public boolean isLeaf(Object node) {
        return ((DefaultMutableTreeNode) node).isLeaf();
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public int getIndexOfChild(Object parent, Object child) {
        return ((DefaultMutableTreeNode) parent).getIndex((ClassHierarchyNode) child);
    }

    public void addTreeModelListener(TreeModelListener l) {

    }

    public void removeTreeModelListener(TreeModelListener l) {
    }


    private class NodeContentComparator implements Comparator<Node<OWLClass>> {

        public int compare(Node<OWLClass> o1, Node<OWLClass> o2) {
            OWLClass cls1 = o1.iterator().next();
            OWLClass cls2 = o2.iterator().next();
            String ren1 = manager.getRendering(cls1);
            String ren2 = manager.getRendering(cls2);
            return ren1.compareToIgnoreCase(ren2);
        }
    }

    private class IndividualNodeContentComparator implements Comparator<OWLNamedIndividual> {
        public int compare(OWLNamedIndividual o1, OWLNamedIndividual o2) {
            return manager.getRendering(o1).compareToIgnoreCase(manager.getRendering(o2));
        }
    }
}
