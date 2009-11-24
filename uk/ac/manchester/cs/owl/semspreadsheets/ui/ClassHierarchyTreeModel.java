package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeModelListener;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.inference.OWLReasonerException;

import java.util.*;
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
 * Date: 08-Nov-2009
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
            rootNode = new ClassHierarchyNode(manager);
            put(rootNode.getOWLClasses().iterator().next(), rootNode);
            buildChildren(rootNode);
        }
    }

    private void put(OWLEntity cls, DefaultMutableTreeNode node) {
        Collection<DefaultMutableTreeNode> nodes = cls2NodeMap.get(cls);
        if(nodes == null) {
            nodes = new ArrayList<DefaultMutableTreeNode>();
            cls2NodeMap.put(cls, nodes);
        }
        nodes.add(node);
    }

    public Collection<DefaultMutableTreeNode> getNodesForEntity(OWLEntity entity) {
        Collection<DefaultMutableTreeNode> hierarchyNodes = cls2NodeMap.get(entity);
        if(hierarchyNodes == null) {
            return Collections.emptyList();
        }
        else {
            return new ArrayList<DefaultMutableTreeNode>(hierarchyNodes);
        }
    }

    public Collection<TreePath> getTreePathsForEntity(OWLEntity entity) {
        Collection<DefaultMutableTreeNode> nodes = getNodesForEntity(entity);
        List<TreePath> treePaths = new ArrayList<TreePath>();
        for(DefaultMutableTreeNode node : nodes) {
            treePaths.add(new TreePath(node.getPath()));
        }
        return treePaths;
    }

    private void buildChildren(ClassHierarchyNode node) {
        try {
            Set<OWLClass> clses = node.getOWLClasses();
            OWLClass representative = clses.iterator().next();
            Set<Set<OWLClass>> subs = manager.getReasoner().getSubClasses(representative);
            if (!subs.isEmpty()) {
                List<Set<OWLClass>> sortedSubs = new ArrayList<Set<OWLClass>>(subs);
                Collections.sort(sortedSubs, nodeContentComparator);
                for(Set<OWLClass> sub : sortedSubs) {
                    ClassHierarchyNode childNode = new ClassHierarchyNode(manager, sub);
                    node.add(childNode);
                    for(OWLClass cls : sub) {
                        put(cls, childNode);
                    }
                    buildChildren(childNode);
                }
            }
            else {
                Set<OWLNamedIndividual> individuals = manager.getReasoner().getIndividuals(representative, true);
                List<OWLNamedIndividual> sortedIndividuals = new ArrayList<OWLNamedIndividual>(individuals);
                Collections.sort(sortedIndividuals, individualNodeContentComparator);
                for(OWLNamedIndividual ind : sortedIndividuals) {
                    ClassHierarchyIndividualNode childNode = new ClassHierarchyIndividualNode(manager, ind);
                    node.add(childNode);
                    put(ind, childNode);
                }

            }
        }
        catch (OWLReasonerException e) {
            ErrorHandler.getErrorHandler().handleError(e);
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


    private class NodeContentComparator implements Comparator<Set<OWLClass>> {

        public int compare(Set<OWLClass> o1, Set<OWLClass> o2) {
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
