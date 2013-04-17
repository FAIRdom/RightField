/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyManager;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class ClassHierarchyTreeModel implements HierarchyTreeModel {	

    private ClassHierarchyTreeNode rootNode;

	private Map<IRI, Collection<DefaultMutableTreeNode>> iri2NodeMap = new HashMap<IRI, Collection<DefaultMutableTreeNode>>();

    private ClassHierarchyTreeModel.NodeContentComparator nodeContentComparator;

    private IndividualNodeContentComparator individualNodeContentComparator = new IndividualNodeContentComparator();

	private final OWLOntology ontology;

	private final OntologyManager ontologyManager;  
	
	private static final Logger logger = Logger.getLogger(ClassHierarchyTreeNode.class);
	private List<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();

	public ClassHierarchyTreeModel(OntologyManager ontologyManager, OWLOntology ontology) {
        this.ontologyManager = ontologyManager;		
		this.ontology = ontology;
        nodeContentComparator = new NodeContentComparator();
        if (ontologyManager.getLoadedOntologies().size() > 0) {
            buildTreeModel();
        }        
    }
	
	@Override
    public Collection<TreePath> getTreePathsForEntity(OWLEntity entity) {
        Collection<DefaultMutableTreeNode> nodes = getNodesForIRI(entity.getIRI());
        List<TreePath> treePaths = new ArrayList<TreePath>();
        for (DefaultMutableTreeNode node : nodes) {
            treePaths.add(new TreePath(node.getPath()));
        }
        return treePaths;
    }
	
	 @Override
    public Object getRoot() {
        return rootNode;
    }

	@Override
    public Object getChild(Object parent, int index) {
        return ((DefaultMutableTreeNode) parent).getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((DefaultMutableTreeNode) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((DefaultMutableTreeNode) node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((DefaultMutableTreeNode) parent).getIndex((TreeNode)child);
    }

    @Override
	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.add(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.remove(l);		
	}
    
    protected OWLOntology getOntology() {
		return ontology;
	}  

	protected void buildTreeModel() {
		rootNode = new ClassHierarchyTreeNode();
        storeIRIForNode(rootNode.getOWLClasses().iterator().next().getIRI(), rootNode);
        buildChildren(rootNode);
	}    

    protected Collection<DefaultMutableTreeNode> getNodesForIRI(IRI iri) {
        Collection<DefaultMutableTreeNode> hierarchyNodes = iri2NodeMap.get(iri);
        if (hierarchyNodes == null) {
            return Collections.emptyList();
        }
        else {
            return new ArrayList<DefaultMutableTreeNode>(hierarchyNodes);
        }
    }    
    
    protected void storeIRIForNode(IRI iri, DefaultMutableTreeNode node) {    	
        Collection<DefaultMutableTreeNode> nodes = iri2NodeMap.get(iri);
        if (nodes == null) {
            nodes = new ArrayList<DefaultMutableTreeNode>();
            iri2NodeMap.put(iri, nodes);
        }
        if (!nodes.contains(node)) {
        	nodes.add(node);
        }        
    }

    private void buildChildren(ClassHierarchyTreeNode node) {
    	logger.debug("Building class hierarchy tree");
        Set<OWLClass> clses = node.getOWLClasses();
        OWLClass representative = clses.iterator().next();
        NodeSet<OWLClass> subs = getReasoner().getSubClasses(representative, true);
        if (!subs.isBottomSingleton()) {
            List<Node<OWLClass>> sortedSubs = new ArrayList<Node<OWLClass>>(subs.getNodes());
            Collections.sort(sortedSubs, nodeContentComparator);
            for (Node<OWLClass> sub : sortedSubs) {
                ClassHierarchyTreeNode childNode = new ClassHierarchyTreeNode(sub);
                node.add(childNode);
                for (OWLClass cls : sub) {
                    storeIRIForNode(cls.getIRI(), childNode);
                    logger.debug("Adding subclass: "+cls);
                }
                buildChildren(childNode);
            }
        }
        else {
            NodeSet<OWLNamedIndividual> individuals = getReasoner().getInstances(representative, true);
            List<OWLNamedIndividual> sortedIndividuals = new ArrayList<OWLNamedIndividual>(individuals.getFlattened());
            Collections.sort(sortedIndividuals, individualNodeContentComparator);
            for (OWLNamedIndividual ind : sortedIndividuals) {
                ClassHierarchyIndividualNode childNode = new ClassHierarchyIndividualNode(ind);
                node.add(childNode);
                storeIRIForNode(ind.getIRI(), childNode);
            }
        }
    }              
    
    private OWLReasoner getReasoner() {
    	return getOntologyManager().getStructuralReasoner(getOntology());    	
    }

    private class NodeContentComparator implements Comparator<Node<OWLClass>> {

        public int compare(Node<OWLClass> o1, Node<OWLClass> o2) {
            OWLClass cls1 = o1.iterator().next();
            OWLClass cls2 = o2.iterator().next();
            String ren1 = getOntologyManager().getRendering(cls1);
            String ren2 = getOntologyManager().getRendering(cls2);
            return ren1.compareToIgnoreCase(ren2);
        }
    }

    private class IndividualNodeContentComparator implements Comparator<OWLNamedIndividual> {
        public int compare(OWLNamedIndividual o1, OWLNamedIndividual o2) {
            return getOntologyManager().getRendering(o1).compareToIgnoreCase(getOntologyManager().getRendering(o2));
        }
    }
    
    protected OntologyManager getOntologyManager() {
    	return ontologyManager;
    }

	
}
