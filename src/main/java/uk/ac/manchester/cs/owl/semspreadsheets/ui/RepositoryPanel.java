/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.Repository;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryAccessor;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItemComparator;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */

@SuppressWarnings("serial")
public class RepositoryPanel extends JPanel {
	
	Logger logger = Logger.getLogger(RepositoryPanel.class);

	private JList list;

	public final JTextField filterTextField;

	private FilteredRepositoryItemListModel filteredListModel;

	public RepositoryPanel(Repository repository) {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

		ArrayList<RepositoryItem> items = new ArrayList<RepositoryItem>(
				repository.getOntologies());
		Collections.sort(items, new RepositoryItemComparator());
		list = new JList();
		filteredListModel = new FilteredRepositoryItemListModel(items);
		list.setModel(filteredListModel);
		list.setVisibleRowCount(15);

		list.setCellRenderer(new RepositoryItemCellRenderer());
		add(new JScrollPane(list), BorderLayout.CENTER);

		filterTextField = new JTextField();
		JPanel filterPanel = new JPanel();

		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.LINE_AXIS));
		filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 2));
		filterPanel.add(new JLabel("Filter: "));
		filterPanel.add(filterTextField);
		filterTextField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				filteredListModel.filterBy(filterTextField.getText());
				// if you are down to 1 item, select it so that the user can hit
				// enter with
				// selecting with the mouse
				if (filteredListModel.getSize() == 1) {
					list.setSelectedIndex(0);
				} else {
					list.clearSelection();
				}
			}
		});

		add(filterPanel, BorderLayout.SOUTH);
	}

	public RepositoryItem getSelectedItem() {
		return (RepositoryItem) list.getSelectedValue();
	}

	private class RepositoryItemCellRenderer extends DefaultListCellRenderer {
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			RepositoryItem item = (RepositoryItem) value;
			return super
					.getListCellRendererComponent(
							list,
							item.getHumanReadableName() + " ("
									+ item.getFormat() + ")", index,
							isSelected, cellHasFocus);
		}
	}

	public static RepositoryItem showDialog(WorkbookFrame frame,
			RepositoryAccessor repositoryAccessor) {
		
		final Logger logger = Logger.getLogger(RepositoryItem.class);
		
		final RepositoryPanel panel = new RepositoryPanel(repositoryAccessor.getRepository());
		JOptionPane op = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		JDialog dlg = op.createDialog(frame, "Open from BioPortal repository");
		dlg.setResizable(true);

		//Required to get focus on the textfield. Its to workaround an issue with JOptionPane buttons stealing focus
		//see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5018574		
		dlg.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
					logger.error(e1);
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						panel.filterTextField.requestFocusInWindow();
					}
				});
			}
		});

		dlg.setVisible(true);
		if (op.getValue() != null
				&& op.getValue().equals(JOptionPane.OK_OPTION)) {
			return panel.getSelectedItem();
		} else {
			return null;
		}
	}

}
