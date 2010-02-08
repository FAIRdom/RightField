package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owlapi.util.OWLOntologyChangeVisitorAdapter;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.*;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
/*
 * Copyright (C) 2009, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 */
public class HierarchyTreeModel implements TreeModel, OWLOntologyChangeListener {

    private OWLReasoner reasoner;

    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

    private WorkbookManager workbookManager;

    private TreeHierarchyNode rootNode;

    private Map<OWLClassExpression, Collection<ClassHierarchyNode>> classNodeMap = new HashMap<OWLClassExpression, Collection<ClassHierarchyNode>>();

    private NodeComparator comparator;

    private ChangeProcessor changeProcessor = new ChangeProcessor();

    public HierarchyTreeModel(WorkbookManager workbookManager) {
        this.workbookManager = workbookManager;
        comparator = new NodeComparator();
        this.reasoner = workbookManager.getReasoner();
        rootNode = new RootNode();
        workbookManager.getOntologyManager().addOntologyChangeListener(this);
    }

    /**
     * Disposes of the object that implements this interface.  Note that the contract is that subclasses will call
     * the superclass dispose() method before disposing of themseleves.
     */
    public void dispose() {
        workbookManager.getOntologyManager().removeOntologyChangeListener(this);
    }

    /**
     * Adds a listener for the <code>TreeModelEvent</code>
     * posted after the tree changes.
     * @param l the listener to add
     * @see #removeTreeModelListener
     */
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    /**
     * Returns the child of <code>parent</code> at index <code>index</code>
     * in the parent's
     * child array.  <code>parent</code> must be a node previously obtained
     * from this data source. This should not return <code>null</code>
     * if <code>index</code>
     * is a valid index for <code>parent</code> (that is <code>index >= 0 &&
     * index < getChildCount(parent</code>)).
     * @param parent a node in the tree, obtained from this data source
     * @return the child of <code>parent</code> at index <code>index</code>
     */
    public Object getChild(Object parent, int index) {
        TreeHierarchyNode hierarchyNode = (TreeHierarchyNode) parent;
        return hierarchyNode.getChildren().get(index);
    }

    /**
     * Returns the number of children of <code>parent</code>.
     * Returns 0 if the node
     * is a leaf or if it has no children.  <code>parent</code> must be a node
     * previously obtained from this data source.
     * @param parent a node in the tree, obtained from this data source
     * @return the number of children of the node <code>parent</code>
     */
    public int getChildCount(Object parent) {
        return ((TreeHierarchyNode) parent).getChildren().size();
    }

    /**
     * Returns the index of child in parent.  If either <code>parent</code>
     * or <code>child</code> is <code>null</code>, returns -1.
     * If either <code>parent</code> or <code>child</code> don't
     * belong to this tree model, returns -1.
     * @param parent a note in the tree, obtained from this data source
     * @param child  the node we are interested in
     * @return the index of the child in the parent, or -1 if either
     *         <code>child</code> or <code>parent</code> are <code>null</code>
     *         or don't belong to this tree model
     */
    public int getIndexOfChild(Object parent, Object child) {
        return ((TreeHierarchyNode) parent).getChildren().indexOf(child);
    }

    /**
     * Returns <code>true</code> if <code>node</code> is a leaf.
     * It is possible for this method to return <code>false</code>
     * even if <code>node</code> has no children.
     * A directory in a filesystem, for example,
     * may contain no files; the node representing
     * the directory is not a leaf, but it also has no children.
     * @param node a node in the tree, obtained from this data source
     * @return true if <code>node</code> is a leaf
     */
    public boolean isLeaf(Object node) {
        return ((TreeHierarchyNode) node).getChildren().isEmpty();
    }


    /**
     * Returns the root of the tree.  Returns <code>null</code>
     * only if the tree has no nodes.
     * @return the root of the tree
     */
    public Object getRoot() {
        return rootNode;
    }


    /**
     * Removes a listener previously added with
     * <code>addTreeModelListener</code>.
     * @param l the listener to remove
     * @see #addTreeModelListener
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    /**
     * Messaged when the user has altered the value for the item identified
     * by <code>path</code> to <code>newValue</code>.
     * If <code>newValue</code> signifies a truly new value
     * the model should post a <code>treeNodesChanged</code> event.
     * @param path     path to the node that the user has altered
     * @param newValue the new value from the TreeCellEditor
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
    }


    public Set<Object> getContainedObjects(Object node) {
        if (!(node instanceof TreeHierarchyNode)) {
            return Collections.emptySet();
        }
        return new HashSet<Object>((((TreeHierarchyNode) node).getObjects()));
    }


    public Collection<ClassHierarchyNode> getNodesForClassExpression(OWLClassExpression ce) {
        Collection<ClassHierarchyNode> nodes = classNodeMap.get(ce);
        if (nodes == null) {
            return Collections.emptyList();
        }
        else {
            return new ArrayList<ClassHierarchyNode>(nodes);
        }
    }

    /**
     * Called when some changes have been applied to various ontologies.  These
     * may be an axiom added or an axiom removed changes.
     * @param changes A list of changes that have occurred.  Each change may be examined
     *                to determine which ontology it was applied to.
     * @throws org.semanticweb.owlapi.model.OWLException
     *
     */
    public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
        for (OWLOntologyChange chg : changes) {
            chg.accept(changeProcessor);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Tree hierarchy node
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public abstract class TreeHierarchyNode<N> {

        private TreeHierarchyNode parent;

        private Set<N> objects = new HashSet<N>();

        private List<TreeHierarchyNode> children = null;

        private String toStringValue = null;


        public TreeHierarchyNode(TreeHierarchyNode parent, Set<? extends N> objects) {
            this.parent = parent;
            List<N> sorted = new ArrayList<N>(objects);
            Collections.sort(sorted, new Comparator<N>() {
                public int compare(N o1, N o2) {
                    String r1 = getRendering(o1);
                    String r2 = getRendering(o2);
                    int diff = r1.length() - r2.length();
                    if (diff != 0) {
                        return diff;
                    }
                    return r1.compareTo(r2);
                }
            });
            this.objects = new LinkedHashSet<N>(sorted);
        }

        public abstract void add(N object);

        public void reset() {
            toStringValue = null;
            children = null;
        }

        protected TreeHierarchyNode getParent() {
            return parent;
        }

        public Set<N> getObjects() {
            return objects;
        }

        public List<TreeHierarchyNode> getPathToRoot() {
            TreeHierarchyNode curParent = this;
            List<TreeHierarchyNode> path = new ArrayList<TreeHierarchyNode>();
            while (curParent != null) {
                path.add(0, curParent);
                curParent = curParent.getParent();
            }
            return path;
        }

        protected abstract List<TreeHierarchyNode> createChildren();

        public List<TreeHierarchyNode> getChildren() {
            if (children == null) {
                children = createChildren();
            }
            return children;
        }

        protected abstract boolean isParentInferred();

        public abstract boolean isRoot();

        protected String getObjectsRendering() {
            StringBuilder sb = new StringBuilder();
            for (Iterator<N> it = getObjects().iterator(); it.hasNext();) {
                sb.append(getRendering(it.next()));
                if (it.hasNext()) {
                    sb.append(" \u2261 ");
                }
            }
            if (isParentInferred()) {
                sb.append(" + ");
            }
            return sb.toString();
        }

        /**
         * Default implementation of the toString method.  This creates a string that is the concatenation of
         * the contained objects, with each object being separated by the \equiv character
         * @return
         */
        public String toString() {
            if (toStringValue == null) {
                toStringValue = getObjectsRendering();
            }
            return toStringValue;
        }

        /**
         * Gets the rendering of a contained object.
         * @param containedObject The object
         * @return The rendering.  The default implementation asks the workspace for the rendering if the object
         *         is an OWLObject, otherwise the object's toString method is used to provider the rendering.
         */
        protected String getRendering(N containedObject) {
            if (containedObject instanceof OWLObject) {
                return workbookManager.getRendering((OWLObject) containedObject);
            }
            else {
                return containedObject.toString();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Root node
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public class RootNode extends TreeHierarchyNode<OWLEntity> {


        public RootNode() {
            super(null, new HashSet<OWLEntity>());
        }

        protected List<TreeHierarchyNode> createChildren() {
            List<TreeHierarchyNode> children = new ArrayList<TreeHierarchyNode>();
            children.add(new ClassHierarchyNode(this, workbookManager.getDataFactory().getOWLThing()));
//            children.add(new ObjectPropertyHierarchyNode(this, spreadSheetManager.getDataFactory().getOWLTopObjectProperty()));
//            children.add(new DataPropertyHierarchyNode(this, spreadSheetManager.getDataFactory().getOWLTopDataProperty()));
            return children;
        }

        protected boolean isParentInferred() {
            return false;
        }

        public boolean isRoot() {
            return true;
        }

        public void add(OWLEntity object) {
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Object property node
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public class ObjectPropertyHierarchyNode extends TreeHierarchyNode<OWLObjectProperty> {

        public ObjectPropertyHierarchyNode(TreeHierarchyNode parent, OWLObjectProperty prop) {
            super(parent, Collections.singleton(prop));
        }

        public ObjectPropertyHierarchyNode(TreeHierarchyNode parent, Set<? extends OWLObjectProperty> objects) {
            super(parent, objects);
        }

        public void add(OWLObjectProperty object) {

        }

        protected List<TreeHierarchyNode> createChildren() {
            List<TreeHierarchyNode> children = new ArrayList<TreeHierarchyNode>();
                if (!getObjects().isEmpty()) {
                    NodeSet<OWLObjectProperty> subs = reasoner.getSubObjectProperties(getObjects().iterator().next(), true);
                    List<Node<OWLObjectProperty>> subsSorted = new ArrayList<Node<OWLObjectProperty>>(subs.getNodes());
                    Collections.sort(subsSorted, comparator);
                    for (Node<OWLObjectProperty> sub : subsSorted) {
                        children.add(new ObjectPropertyHierarchyNode(getParent(), sub.getRepresentativeElement()));
                    }
                }
            return children;
        }

        protected boolean isParentInferred() {
            if (getParent() instanceof ObjectPropertyHierarchyNode) {
                for (OWLObjectProperty prop : getObjects()) {
                    ObjectPropertyHierarchyNode parentNode = (ObjectPropertyHierarchyNode) getParent();
                    for (OWLObjectPropertyExpression supProp : prop.getSuperProperties(workbookManager.getLoadedOntologies())) {
                        if (!supProp.isAnonymous() && parentNode.getObjects().contains(supProp.asOWLObjectProperty())) {
                            return false;
                        }
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }

        public boolean isRoot() {
            return false;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Data property node
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public class DataPropertyHierarchyNode extends TreeHierarchyNode<OWLDataProperty> {

        public DataPropertyHierarchyNode(TreeHierarchyNode parent, OWLDataProperty prop) {
            super(parent, Collections.singleton(prop));
        }

        public DataPropertyHierarchyNode(TreeHierarchyNode parent, Set<? extends OWLDataProperty> objects) {
            super(parent, objects);
        }

        public void add(OWLDataProperty object) {
        }

        protected List<TreeHierarchyNode> createChildren() {
            List<TreeHierarchyNode> children = new ArrayList<TreeHierarchyNode>();
                if (!getObjects().isEmpty()) {
                    NodeSet<OWLDataProperty> subs = reasoner.getSubDataProperties(getObjects().iterator().next(), true);
                    List<Node<OWLDataProperty>> subsSorted = new ArrayList<Node<OWLDataProperty>>(subs.getNodes());
                    Collections.sort(subsSorted, comparator);
                    for (Node<OWLDataProperty> sub : subsSorted) {
                        children.add(new DataPropertyHierarchyNode(getParent(), sub.getRepresentativeElement()));
                    }
                }
            return children;
        }

        protected boolean isParentInferred() {
            if (getParent() instanceof DataPropertyHierarchyNode) {
                for (OWLDataProperty prop : getObjects()) {
                    DataPropertyHierarchyNode parentNode = (DataPropertyHierarchyNode) getParent();
                    for (OWLDataPropertyExpression supProp : prop.getSuperProperties(workbookManager.getLoadedOntologies())) {
                        if (!supProp.isAnonymous() && parentNode.getObjects().contains(supProp.asOWLDataProperty())) {
                            return false;
                        }
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }

        public boolean isRoot() {
            return false;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Class hierarchy node
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public class ClassHierarchyNode extends TreeHierarchyNode<OWLClassExpression> {


        private ClassHierarchyNode(TreeHierarchyNode parent, OWLClassExpression ce) {
            this(parent, Collections.singleton(ce));
        }

        public void add(OWLClassExpression object) {
            ClassHierarchyNode newNode = new ClassHierarchyNode(this, object);
            getChildren().add(newNode);
            for (TreeModelListener lsnr : listeners) {
                TreeModelEvent event = new TreeModelEvent(this, getPathToRoot().toArray(), new int[]{getChildren().size() - 1}, new Object[]{newNode});
                lsnr.treeNodesInserted(event);
            }
        }

        public void remove(OWLClassExpression object) {
            int index = 0;
            for (TreeHierarchyNode node : new ArrayList<TreeHierarchyNode>(getChildren())) {
                if (node.getObjects().contains(object)) {
                    getChildren().remove(node);
                    for (TreeModelListener lsnr : listeners) {
                        TreeModelEvent event = new TreeModelEvent(this, getPathToRoot().toArray(), new int[]{index}, new Object[]{node});
                        lsnr.treeNodesRemoved(event);
                    }
                    index--;
                }
                index++;
            }
        }

        private ClassHierarchyNode(TreeHierarchyNode parent, Set<? extends OWLClassExpression> ces) {
            super(parent, ces);
            for (OWLClassExpression ce : ces) {
                Collection<ClassHierarchyNode> nodes = classNodeMap.get(ce);
                if (nodes == null) {
                    nodes = new ArrayList<ClassHierarchyNode>(3);
                    classNodeMap.put(ce, nodes);
                }
                nodes.add(this);
            }
        }

        public boolean isOWLThing() {
            return getObjects().equals(Collections.singleton(workbookManager.getDataFactory().getOWLThing()));
        }

        public boolean isOWLNothing() {
            return getObjects().equals(Collections.singleton(workbookManager.getDataFactory().getOWLNothing()));
        }

        public boolean isChildOfOWLNothing() {
            return getParent() instanceof ClassHierarchyNode && ((ClassHierarchyNode) getParent()).isOWLNothing();

        }

        public Set<? extends OWLClassExpression> getClassExpressions() {
            return getObjects();
        }

        protected boolean isParentInferred() {
            if (getParent() instanceof ClassHierarchyNode) {
                for (OWLClassExpression cls : getObjects()) {
                    ClassHierarchyNode parentNode = (ClassHierarchyNode) getParent();
                    if (!cls.isAnonymous()) {
                        Set<OWLClassExpression> superClses = cls.asOWLClass().getSuperClasses(workbookManager.getLoadedOntologies());
                        if (superClses.isEmpty()) {
                            return false;
                        }
                        for (OWLClassExpression supCls : superClses) {
                            if (!supCls.isAnonymous() && !supCls.isOWLThing() && parentNode.getObjects().contains(supCls.asOWLClass())) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }

        protected String getRendering(OWLClassExpression containedObject) {
            // We tag on [unsatisfiable class] if the class is unsat, and the count if the class is owl:Nothing
            StringBuilder sb = new StringBuilder(super.getRendering(containedObject));
            if (isOWLNothing() || isChildOfOWLNothing()) {
                if (isOWLNothing()) {
                    sb.append("  [");
                    sb.append(getChildren().size());
                    sb.append(" unsatisfiable classes]");
                }
                else {
                    sb.append(" [unsatisfiable]");
                }
            }
            return sb.toString();
        }

        protected String getObjectsRendering() {
            if (!isOWLNothing() && !isChildOfOWLNothing()) {
                    StringBuilder sb = new StringBuilder(super.getObjectsRendering());


                    Set<OWLClass> supers = new HashSet<OWLClass>();
                    for (OWLClassExpression ce : getObjects()) {
                        supers.addAll(reasoner.getSuperClasses(ce, true).getFlattened());
                    }
                    if (!isRoot()) {
                        supers.removeAll(getParent().getObjects());
                    }
                    if (!supers.isEmpty()) {
                        List<OWLClass> supersSorted = new ArrayList<OWLClass>(supers);
//                        Collections.sort(supersSorted, new WorkspaceObjectComparator(spreadSheetManager));
                        sb.append(" < ");
                        for (Iterator<OWLClass> supIt = supersSorted.iterator(); supIt.hasNext();) {
                            OWLClass sup = supIt.next();
                            sb.append(workbookManager.getRendering(sup));
                            if (supIt.hasNext()) {
                                sb.append(", ");
                            }
                        }
                    }

                    return sb.toString();

            }

            return super.getObjectsRendering();
        }

        public List<TreeHierarchyNode> createChildren() {
            List<TreeHierarchyNode> children;
            if (!getObjects().isEmpty()) {
                children = new ArrayList<TreeHierarchyNode>(0);
                    NodeSet<OWLClass> subs = reasoner.getSubClasses(getObjects().iterator().next(), true);
                    if (isOWLNothing()) {
                        for (OWLClass unsatCls : reasoner.getUnsatisfiableClasses()) {
                            if (!unsatCls.isOWLNothing()) {
                                children.add(new ClassHierarchyNode(this, unsatCls));
                            }
                        }
                    }
                    else if (isOWLThing()) {
                        children.add(new ClassHierarchyNode(this, workbookManager.getDataFactory().getOWLNothing()));
                    }
                    List<Node<OWLClass>> subsSorted = new ArrayList<Node<OWLClass>>(subs.getNodes());
                    Collections.sort(subsSorted, comparator);
                    for (Node<OWLClass> node : subsSorted) {
                        if (!node.contains(workbookManager.getDataFactory().getOWLNothing())) {
                            children.add(new ClassHierarchyNode(this, node.getRepresentativeElement()));
                        }
                    }
            }
            else {
                children = Collections.emptyList();
            }
            return children;

        }

        public boolean isRoot() {
            return false;
        }
    }


    /**
     * A comparator which is used to order nodes in the tree
     */
    private class NodeComparator implements Comparator<Node<? extends OWLLogicalEntity>> {

//        private WorkspaceObjectComparator comparator = new WorkspaceObjectComparator(spreadSheetManager);

        public int compare(Node<? extends OWLLogicalEntity> o1, Node<? extends OWLLogicalEntity> o2) {
            if (o1.getSize() == 0) {
                if (o2.getSize() == 0) {
                    return 0;
                }
                else {
                    return -1;
                }
            }
            else if (o2.getSize() == 0) {
                return 1;
            }
            else {
                OWLObject in1 = o1.iterator().next();
                OWLObject in2 = o2.iterator().next();
                return in1.toString().compareTo(in2.toString());//comparator.compare(in1, in2);
            }
        }
    }


    private class ChangeProcessor extends OWLOntologyChangeVisitorAdapter {

        private AddAxiomProcessor addAxiomProcessor = new AddAxiomProcessor();

        private RemoveAxiomProcessor removeAxiomProcessor = new RemoveAxiomProcessor();

        public void visit(AddAxiom change) {
            change.getAxiom().accept(addAxiomProcessor);
        }

        public void visit(RemoveAxiom change) {
            change.getAxiom().accept(removeAxiomProcessor);
        }
    }

    private class AddAxiomProcessor extends OWLAxiomVisitorAdapter {

        public void visit(OWLDeclarationAxiom axiom) {
                if (axiom.getEntity().isOWLClass()) {
                    OWLClass cls = axiom.getEntity().asOWLClass();
                    if (reasoner.getSuperClasses(cls, true).isEmpty()) {
                        Collection<ClassHierarchyNode> nodes = classNodeMap.get(workbookManager.getDataFactory().getOWLThing());
                        if (nodes != null) {
                            for (ClassHierarchyNode node : nodes) {
                                node.add(cls);
                            }
                        }
                    }

                }
        }

        public void visit(OWLSubClassOfAxiom axiom) {
            if (!axiom.getSuperClass().isAnonymous()) {
                OWLClass superCls = axiom.getSuperClass().asOWLClass();
                Collection<ClassHierarchyNode> nodes = classNodeMap.get(superCls);
                if (nodes != null) {
                    for (ClassHierarchyNode node : nodes) {
                        node.add(axiom.getSubClass());
                    }
                }
                Set<OWLClassExpression> supers = axiom.getSubClass().asOWLClass().getSuperClasses(workbookManager.getLoadedOntologies());
                if (!supers.contains(workbookManager.getDataFactory().getOWLThing())) {
                    for (ClassHierarchyNode node : classNodeMap.get(workbookManager.getDataFactory().getOWLThing())) {
                        node.remove(axiom.getSubClass());
                    }
                }
            }

        }
    }

    private class RemoveAxiomProcessor extends OWLAxiomVisitorAdapter {

        public void visit(OWLDeclarationAxiom axiom) {
        }

        public void visit(OWLSubClassOfAxiom axiom) {
        }
    }

}

