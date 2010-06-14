package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Validation;
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
public class TermSelectionPanel extends JPanel {

    private WorkbookManager workbookManager;
    private JList list;
    private JRadioButton noneRadioButton;
    private JRadioButton subClassesRadioButton;
    private JRadioButton individualsRadioButton;
    private JRadioButton directIndividualsRadioButton;
    private JTree tree;

    public TermSelectionPanel(WorkbookManager man) {
        this.workbookManager = man;
        setLayout(new BorderLayout());
        tree = new JTree(new HierarchyTreeModel(workbookManager));

        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            /**
             * Returns the icon used to represent non-leaf nodes that are not
             * expanded.
             */
            public Icon getClosedIcon() {
                return null;
            }

            /**
             * Returns the icon used to represent leaf nodes.
             */
            public Icon getLeafIcon() {
                return null;
            }

            /**
             * Returns the icon used to represent non-leaf nodes that are expanded.
             */
            public Icon getOpenIcon() {
                return null;
            }
        });
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            /**
             * Called whenever the value of the selection changes.
             * @param e the event that characterizes the change.
             */
            public void valueChanged(TreeSelectionEvent e) {
                refillList();
            }
        });

        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        sp.setResizeWeight(0.8);
        sp.setTopComponent(new JScrollPane(tree));
        list = new JList();
        JPanel listHolder = new JPanel(new BorderLayout());
        listHolder.add(new JScrollPane(list));
        listHolder.setBorder(BorderFactory.createTitledBorder("Values"));
        sp.setBottomComponent(listHolder);
        add(sp);
        sp.setBorder(null);

        JPanel choicePanel = new JPanel(new BorderLayout());
        choicePanel.setBorder(BorderFactory.createTitledBorder("Value type"));
        Box box = new Box(BoxLayout.Y_AXIS);
        choicePanel.add(box);
        ActionListener buttonListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refillList();
            }
        };
        ButtonGroup bg = new ButtonGroup();
        noneRadioButton = new JRadioButton("None", true);
        bg.add(noneRadioButton);
        box.add(noneRadioButton);
        noneRadioButton.addActionListener(buttonListener);
        subClassesRadioButton = new JRadioButton("Subclasses");
        bg.add(subClassesRadioButton);
        box.add(subClassesRadioButton);
        subClassesRadioButton.addActionListener(buttonListener);
        individualsRadioButton = new JRadioButton("Individuals");
        box.add(individualsRadioButton);
        bg.add(individualsRadioButton);
        individualsRadioButton.addActionListener(buttonListener);
        directIndividualsRadioButton = new JRadioButton("Direct individuals");
        box.add(directIndividualsRadioButton);
        bg.add(directIndividualsRadioButton);
        directIndividualsRadioButton.addActionListener(buttonListener);
        add(choicePanel, BorderLayout.SOUTH);

    }

    private void refillList() {
        list.setListData(new Object[0]);
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            Object last = path.getLastPathComponent();
            if (last instanceof HierarchyTreeModel.ClassHierarchyNode) {
                HierarchyTreeModel.ClassHierarchyNode node = (HierarchyTreeModel.ClassHierarchyNode) last;
                Set<? extends OWLClassExpression> expressions = node.getClassExpressions();
                fillList(expressions);
            }
        }
    }

    private void fillList(Set<? extends OWLClassExpression> expressions) {
        Validation validation = null;
        for (OWLClassExpression ce : expressions) {
            if (!ce.isAnonymous()) {
                List<OWLObject> listObjects = new ArrayList<OWLObject>();
                if (noneRadioButton.isSelected()) {
                    validation = null;
                }
                else if (subClassesRadioButton.isSelected()) {
                    listObjects.addAll(workbookManager.getReasoner().getSubClasses(ce, true).getFlattened());
                }
                else if (individualsRadioButton.isSelected()) {
                    Set<OWLNamedIndividual> individuals = workbookManager.getReasoner().getInstances(ce, false).getFlattened();
                    listObjects.addAll(individuals);
//                        validation = new IndividualValuesValidation(col, row, row, ce.asOWLClass(), individuals);
                }
                else if (directIndividualsRadioButton.isSelected()) {
                    listObjects.addAll(workbookManager.getReasoner().getInstances(ce, true).getFlattened());

                }
                Collections.sort(listObjects);
                for (OWLObject o : listObjects) {
                    System.out.println(o);
                }
                list.setListData(listObjects.toArray());
            }
        }
    }
}
