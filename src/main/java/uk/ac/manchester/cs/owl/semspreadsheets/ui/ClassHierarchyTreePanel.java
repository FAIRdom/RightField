package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
public class ClassHierarchyTreePanel extends JPanel {

    public ClassHierarchyTreePanel(WorkbookFrame frame) {
        setLayout(new BorderLayout(5, 5));
        add(new FindClassPanel(frame), BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(new ClassHierarchyTree(frame.getWorkbookManager()));
        add(sp);
        sp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }
}
