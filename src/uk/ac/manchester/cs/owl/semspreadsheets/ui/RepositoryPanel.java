package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.Repository;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryAccessor;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItemComparator;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 * 
 * Author: Stuart Owen
 * Date: 15-June-2010
 */
public class RepositoryPanel extends JPanel {

    private WorkbookFrame frame;

    private Repository repository;

    private JList list;

    public RepositoryPanel(WorkbookFrame frame, Repository repository) {
        this.repository = repository;
        this.frame = frame;
        setLayout(new BorderLayout());
        list = new JList();
        list.setVisibleRowCount(15);
        ArrayList<RepositoryItem> items = new ArrayList<RepositoryItem>(repository.getOntologies());
        Collections.sort(items, new RepositoryItemComparator());
        list.setListData(items.toArray());
        list.setCellRenderer(new RepositoryItemCellRenderer());
        add(new JScrollPane(list));
        setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
    }

    public RepositoryItem getSelectedItem() {
        return (RepositoryItem) list.getSelectedValue();
    }

    private class RepositoryItemCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        	RepositoryItem item = (RepositoryItem) value;
            return super.getListCellRendererComponent(list, item.getHumanReadableName()+" ("+item.getFormat()+")", index, isSelected, cellHasFocus);
        }
    }

    public static RepositoryItem showDialog(WorkbookFrame frame, RepositoryAccessor repositoryAccessor) {
        RepositoryPanel panel = new RepositoryPanel(frame, repositoryAccessor.getRepository());
        JOptionPane op = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dlg = op.createDialog(frame, "Open from BioPortal repository");
        dlg.setResizable(true);
        dlg.setVisible(true);
        if(op.getValue() != null && op.getValue().equals(JOptionPane.OK_OPTION)) {
            return panel.getSelectedItem();
        }
        else {
            return null;
        }
    }

}
