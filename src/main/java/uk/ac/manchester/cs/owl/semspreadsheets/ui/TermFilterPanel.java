package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Term;
import uk.ac.manchester.cs.owl.semspreadsheets.model.ValidationType;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

import java.awt.*;

/**
 * UI Pane that allows the user to filter the allowed values in a selection.
 *
 * @author Mohammed Charrout
 */
@SuppressWarnings("serial")
public class TermFilterPanel extends JPanel {

    Logger logger = Logger.getLogger(ValidationValuesFilterPanel.class);

    private WorkbookManager workbookManager;

    private JList<Term> availableList;
    private TermListModel availableListModel;
    private JList<Term> allowedList;
    private TermListModel allowedListModel;

    private JButton addButton;
    private JButton removeButton;

    public TermFilterPanel(WorkbookManager manager) {
        this.workbookManager = manager;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

        createLists();
        createButtons();
        Border lb = BorderFactory.createLineBorder(Color.LIGHT_GRAY);

        JPanel availablePanel = new JPanel();
        availablePanel.setLayout(new BoxLayout(availablePanel, BoxLayout.Y_AXIS));
        availablePanel.setBorder(BorderFactory.createTitledBorder(lb, "Available values"));
        availablePanel.add(new JScrollPane(availableList));
        add(availablePanel, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonsPanel.add(Box.createVerticalGlue());
        buttonsPanel.add(addButton);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(Box.createVerticalGlue());
        add(buttonsPanel, BorderLayout.CENTER);

        JPanel allowedPanel = new JPanel();
        allowedPanel.setLayout(new BoxLayout(allowedPanel, BoxLayout.Y_AXIS));
        allowedPanel.setBorder(BorderFactory.createTitledBorder(lb, "Allowed values"));
        allowedPanel.add(new JScrollPane(allowedList));
        add(allowedPanel, BorderLayout.EAST);
    }

    private void createLists() {
        List<Term> selectedTerms = workbookManager.getEntitySelectionModel().getTerms();
        if (selectedTerms == null) selectedTerms = new ArrayList<>();
        ValidationType validationType = workbookManager.getEntitySelectionModel().getValidationType();
        IRI iri = workbookManager.getEntitySelectionModel().getSelectedEntity().getIRI();
        List<Term> allTerms = validationType.getTerms(workbookManager.getOntologyManager(), iri);
        List<Term> availableTerms = new ArrayList<>();

        for (Term term : allTerms) {
            if (selectedTerms.contains(term)) continue;
            availableTerms.add(term);
        }

        allowedList = new JList<>();
        allowedList.setCellRenderer(new TermCellRenderer());
        allowedList.setVisibleRowCount(15);
        allowedListModel = new TermListModel(selectedTerms);
        allowedList.setModel(allowedListModel);

        availableList = new JList<>();
        availableList.setCellRenderer(new TermCellRenderer());
        availableList.setVisibleRowCount(15);
        availableListModel = new TermListModel(availableTerms);
        availableList.setModel(availableListModel);
    }

    private void createButtons() {
        addButton = new JButton("Add >>");
        addButton.addActionListener(e -> {
            List<Term> selected = availableList.getSelectedValuesList();
            availableListModel.removeAll(selected);
            allowedListModel.addAll(selected);
        });

        removeButton = new JButton("<< Remove");
        removeButton.addActionListener(e -> {
            List<Term> selected = allowedList.getSelectedValuesList();
            allowedListModel.removeAll(selected);
            availableListModel.addAll(selected);
        });
    }

    public List<Term> getAllowedTerms() {
        return allowedListModel.terms;
    }

    public static void showDialog(WorkbookFrame frame) {
        final Logger logger = Logger.getLogger(ValidationValuesFilterPanel.class);

        WorkbookManager manager = frame.getWorkbookManager();
        final TermFilterPanel panel = new TermFilterPanel(manager);
        JOptionPane op = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = op.createDialog(frame, "Filter list of allowed values");
        dialog.setResizable(true);

        dialog.setVisible(true);
        if (op.getValue() != null
                && op.getValue().equals(JOptionPane.OK_OPTION)) {
            logger.info("Apply filter with " + panel.getAllowedTerms().size() + " items.");
            manager.getEntitySelectionModel().setTerms(panel.getAllowedTerms());
            manager.previewValidation();
        }
    }

    class TermListModel extends AbstractListModel<Term> {

        private final List<Term> terms;

        public TermListModel(List<Term> terms) {
            this.terms = terms;
        }

        @Override
        public int getSize() {
            return terms.size();
        }

        @Override
        public Term getElementAt(int index) {
            return terms.get(index);
        }

        public void addAll(List<Term> terms) {
            this.terms.addAll(terms);
            fireContentsChanged(this, 0, getSize());
        }

        public void removeAll(List<Term> terms) {
            this.terms.removeAll(terms);
            fireContentsChanged(this, 0, getSize());
        }
    }

    private class TermCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Term term = (Term) value;
            label.setText(term.getName());
            label.setToolTipText(term.getIRI().toString());
            return label;
        }
    }
}
