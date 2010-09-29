package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Component;
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
@SuppressWarnings("serial")
public class RepositoryPanel extends JPanel {

//    private WorkbookFrame frame;
//
//    private Repository repository;

    private JList list;      
    
    private JTextField filterTextField;
    
    private FilteredRepositoryItemListModel filteredListModel;        

    public RepositoryPanel(WorkbookFrame frame, Repository repository) {
//        this.repository = repository;
//        this.frame = frame;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
                
        ArrayList<RepositoryItem> items = new ArrayList<RepositoryItem>(repository.getOntologies());
        Collections.sort(items, new RepositoryItemComparator());
        list = new JList();        
        filteredListModel=new FilteredRepositoryItemListModel(items);
        list.setModel(filteredListModel);
        list.setVisibleRowCount(15);
        //list.setListData(items.toArray());
        list.setCellRenderer(new RepositoryItemCellRenderer());                
        add(new JScrollPane(list),BorderLayout.CENTER);        
        
        filterTextField = new JTextField();
        JPanel filterPanel = new JPanel();
        
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.LINE_AXIS));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 2));
        filterPanel.add(new JLabel("Filter: "));
        filterPanel.add(filterTextField);        
        filterTextField.addKeyListener(new KeyAdapter() {
        	public void keyReleased(KeyEvent e) {
				filteredListModel.filterBy(filterTextField.getText());
				//if you are down to 1 item, select it so that the user can hit enter with 
				//selecting with the mouse
				if (filteredListModel.getSize()==1) {
					list.setSelectedIndex(0);					
				}
				else {
					list.clearSelection();
				}				
			}			
		});
        
        add(filterPanel,BorderLayout.SOUTH);        
    }
    
    public RepositoryItem getSelectedItem() {
        return (RepositoryItem) list.getSelectedValue();
    }

    @SuppressWarnings("serial")
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
