package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import org.semanticweb.owlapi.model.IRI;
import uk.ac.manchester.cs.owl.semspreadsheets.listeners.OntologyTermValidationListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.List;

/**
 * UI Pane
 *
 * @author Mohammed Charrout
 */
@SuppressWarnings("serial")
public class ValidationValuesFilterPanel extends JPanel {

    private WorkbookManager workbookManager;

    private boolean valuesAvailable;

    private JCheckBox checkBox;
    private JButton button;

    public ValidationValuesFilterPanel(WorkbookFrame workbookFrame) {
        this.workbookManager = workbookFrame.getWorkbookManager();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        checkBox = new JCheckBox("Enable filter");
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Object source = e.getItemSelectable();
                if (source == checkBox) {
                    setEnabledStatus();
                    updateEntityModel();
                }
            }
        });
//        add(checkBox, BorderLayout.WEST);

//        add(Box.createHorizontalGlue());

        button = new JButton("Filter");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TermFilterPanel.showDialog(workbookFrame);
                updateEntityModel();
            }
        });
        add(button, BorderLayout.EAST);

        workbookManager.getOntologyManager().addListener(new OntologyTermValidationListener() {
            @Override
            public void validationsChanged() {
                Range range = workbookManager.getSelectionModel().getSelectedRange();
                Collection<OntologyTermValidation> validations = workbookManager.getOntologyManager().getContainingOntologyTermValidations(range);
                valuesAvailable = !validations.isEmpty();
                setEnabledStatus();
            }

            @Override
            public void ontologyTermSelected(List<OntologyTermValidation> previewValidations) {
                valuesAvailable = !previewValidations.isEmpty();
                setEnabledStatus();
            }
        });
    }

    private void setEnabledStatus() {
        if (!valuesAvailable) {
            button.setEnabled(false);
            checkBox.setEnabled(false);
        } else {
            checkBox.setEnabled(true);
            button.setEnabled(true);
//            button.setEnabled(checkBox.isSelected());
        }
        checkBox.setSelected(checkBox.isSelected() && button.isEnabled());
    }

    private void updateEntityModel() {
        Boolean checkBoxSelected = checkBox.isSelected();
        checkBoxSelected = true;
        if (checkBoxSelected) {
            List<Term> terms = workbookManager.getEntitySelectionModel().getTerms();
            if (terms == null) {
                IRI iri = workbookManager.getEntitySelectionModel().getSelectedEntity().getIRI();
                ValidationType type = workbookManager.getEntitySelectionModel().getValidationType();
                terms = type.getTerms(workbookManager.getOntologyManager(), iri);
            }
            workbookManager.getEntitySelectionModel().setTerms(terms);
        } else {
            workbookManager.getEntitySelectionModel().setTerms(null);
        }
    }
}
