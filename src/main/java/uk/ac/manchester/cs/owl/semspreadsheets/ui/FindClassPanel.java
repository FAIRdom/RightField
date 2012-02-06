package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.semanticweb.owlapi.model.OWLEntity;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class FindClassPanel extends JPanel {

	private WorkbookManager manager;

	private JTextField findField = new JTextField();

	private JList resultList = new JList();

	private JWindow resultWindow;	

	public FindClassPanel(WorkbookFrame frame) {		
		this.manager = frame.getWorkbookManager();
		setLayout(new BorderLayout());
		findField.putClientProperty("JTextField.variant", "search");
		add(findField, BorderLayout.NORTH);
		findField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				doFind();
			}

			public void removeUpdate(DocumentEvent e) {
				doFind();
			}

			public void changedUpdate(DocumentEvent e) {
			}
		});
		resultWindow = new JWindow(frame);
		resultWindow.getContentPane().setLayout(new BorderLayout());
		resultWindow.getContentPane().add(new JScrollPane(resultList));
		resultList.setCellRenderer(new WorkbookManagerCellRenderer(manager));
		resultList.setRequestFocusEnabled(false);
		resultWindow.setFocusable(false);
		resultWindow.setFocusableWindowState(false);
		findField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dismiss();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					navigate();
				}
			}
		});
		findField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					moveUpList();
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					moveDownList();
				}
			}
		});
		findField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				dismiss();
			}
		});
		resultList.addMouseListener(new MouseAdapter() {			

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()>1) { //requires double click
					navigate();
				}
			}			
		});
	}

	private void moveUpList() {
		int selIndex = resultList.getSelectedIndex();
		selIndex--;
		if (selIndex < 0) {
			selIndex = resultList.getModel().getSize() - 1;
		}
		if (selIndex >= 0) {
			resultList.setSelectedIndex(selIndex);
			resultList.scrollRectToVisible(resultList.getCellBounds(selIndex,
					selIndex));
		}
	}

	private void moveDownList() {
		if (resultList.getModel().getSize() > 0) {
			int selIndex = resultList.getSelectedIndex();
			selIndex++;
			if (selIndex > resultList.getModel().getSize() - 1) {
				selIndex = 0;
			}
			resultList.setSelectedIndex(selIndex);
			resultList.scrollRectToVisible(resultList.getCellBounds(selIndex,
					selIndex));
		}
	}

	private void dismiss() {
		resultWindow.setVisible(false);
	}

	private void navigate() {
		Object o = resultList.getSelectedValue();
		if (o != null) {
			manager.getEntitySelectionModel().setSelection((OWLEntity) o);
		}
		resultWindow.setVisible(false);
	}

	private void doFind() {
		Collection<OWLEntity> entities = manager
				.getEntitiesForShortForm(findField.getText().trim());
		ArrayList<OWLEntity> sortedEntities = new ArrayList<OWLEntity>();
		for (OWLEntity ent : entities) {
			if (ent.isOWLClass()) {
				sortedEntities.add(ent);
			}
		}
		resultList.setListData(sortedEntities.toArray());
		showResults();
	}

	private void showResults() {
		if (resultList.getModel().getSize() > 0) {
			resultWindow.setSize(findField.getWidth(), 150);
			Point point = new Point(0, 0);
			SwingUtilities.convertPointToScreen(point, findField);
			// SwingUtilities.convertPointFromScreen(point,
			// frame.getContentPane());
			resultWindow.setLocation(point.x, point.y + findField.getHeight()
					+ 3);
			resultWindow.setVisible(true);
			resultList.setSelectedIndex(0);
		} else {
			resultWindow.setVisible(false);
		}
	}

}
