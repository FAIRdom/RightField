package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Matthew Horridge
 * @author Stuart Owen
 */

@SuppressWarnings("serial")
public class ClassHierarchyTreePanel extends JPanel {
	
	private ClassHierarchyTree tree;

    public ClassHierarchyTreePanel(WorkbookFrame frame) {
        setLayout(new BorderLayout(5, 5));
        add(new FindClassPanel(frame), BorderLayout.NORTH);
        tree = new ClassHierarchyTree(frame.getWorkbookManager());
        JScrollPane sp = new JScrollPane(tree);
        add(sp);
        sp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }      
}
