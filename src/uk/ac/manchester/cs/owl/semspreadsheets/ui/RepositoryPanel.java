package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.Repository;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItemComparator;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryAccessor;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.*;
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
 * Date: 11-Nov-2009
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
            return super.getListCellRendererComponent(list, ((RepositoryItem) value).getHumanReadableName(), index, isSelected, cellHasFocus);
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
