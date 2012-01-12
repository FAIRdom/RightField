package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.ApplyValidationAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.CancelValidationAction;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 * 
 */
@SuppressWarnings("serial")
public class ValidationInspectorPanel extends JPanel {
	
	private static Logger logger = Logger.getLogger(ValidationInspectorPanel.class);

    private static Font font = new Font("Lucida Grande", Font.BOLD, 11);

    private WorkbookManager workbookManager;

    private JLabel selectedCellAddressLabel = new JLabel("No cells selected");

    private static Color textColor = new Color(96, 110, 128);
    
    private JButton applyButton = new JButton("Apply");
    private JButton cancelButton = new JButton("Cancel");

    public ValidationInspectorPanel(WorkbookFrame frame) {
        workbookManager = frame.getWorkbookManager();
        setLayout(new BorderLayout(14, 14));
        setBorder(BorderFactory.createEmptyBorder(7, 2, 7, 7));
        add(selectedCellAddressLabel, BorderLayout.NORTH);
        JPanel outerPanel = new JPanel(new BorderLayout(7, 7));
        
        add(outerPanel);
        outerPanel.setLayout(new BorderLayout(7, 7));

        ClassHierarchyTreePanel classHierarchyTreePanel = new ClassHierarchyTreePanel(frame);
        classHierarchyTreePanel.setBorder(createTitledBorder("HIERARCHY"));
        outerPanel.add(classHierarchyTreePanel);        
        
        ValidationValuesPanel valuesPanel = new ValidationValuesPanel(frame.getWorkbookManager());
        valuesPanel.setBorder(createTitledBorder("ALLOWED VALUES"));
        
        JPanel innerPanel = new JPanel(new BorderLayout(7, 7));        
        outerPanel.add(innerPanel, BorderLayout.SOUTH);
        ValidationTypeSelectorPanel typeSelectorPanel = new ValidationTypeSelectorPanel(frame.getWorkbookManager());
        
        typeSelectorPanel.setBorder(createTitledBorder("TYPE OF ALLOWED VALUES"));
        innerPanel.add(typeSelectorPanel, BorderLayout.NORTH);
        
        innerPanel.add(valuesPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = setupButtonPanel(classHierarchyTreePanel, typeSelectorPanel);
        
        innerPanel.add(buttonPanel, BorderLayout.SOUTH);        
        
        frame.getWorkbookManager().getSelectionModel().addCellSelectionListener(new CellSelectionListener() {
            public void selectionChanged(Range range) {
                updateSelectionLabel();
            }
        });        
        updateSelectionLabel();
    }

	private JPanel setupButtonPanel(
			ClassHierarchyTreePanel classHierarchyTreePanel,
			ValidationTypeSelectorPanel typeSelectorPanel) {
        
		classHierarchyTreePanel
				.addTreeSelectionListener(new TreeSelectionListener() {
					@Override
					public void valueChanged(TreeSelectionEvent e) {
						logger.debug("ClassHierarchyTree TreeSelectionEvent fired");
						updateApplyButtonState();
					}
				});

		typeSelectorPanel.addRadioButtonActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				logger.debug("Radio Button ActionEvent fired");
				updateApplyButtonState();				
			}
		});

		applyButton.setAction(new ApplyValidationAction(workbookManager));
		applyButton.setEnabled(false);
				
		cancelButton.setAction(new CancelValidationAction(workbookManager));
		cancelButton.setEnabled(false);
		
		JPanel  buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(applyButton);        
        buttonPanel.add(cancelButton);
        
        workbookManager.addListener(new WorkbookManagerListener() {
			
			@Override
			public void workbookLoaded(WorkbookManagerEvent event) {				
				
			}
			
			@Override
			public void workbookChanged(WorkbookManagerEvent event) {
				
			}
			
			@Override
			public void validationAppliedOrCancelled() {
				applyButton.setEnabled(false);
				cancelButton.setEnabled(false);
			}
			
			@Override
			public void ontologiesChanged(WorkbookManagerEvent event) {				
				
			}
		});
        
        return buttonPanel;
	}
	
	private void updateApplyButtonState() {
		boolean state = workbookManager.determineApplyButtonState();
		applyButton.setEnabled(state);
		cancelButton.setEnabled(state);
	}

    private void updateSelectionLabel() {
        Range selectedRange = workbookManager.getSelectionModel().getSelectedRange();
        if (selectedRange.isCellSelection()) {
            selectedCellAddressLabel.setText("CELLS: " + selectedRange.getColumnRowAddress());
        }
        else {
            selectedCellAddressLabel.setText("");
        }
    }

    private static Border createTitledBorder(String title) {
        Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);

        Border titledBorder = BorderFactory.createTitledBorder(border, title,
                TitledBorder.LEFT, TitledBorder.TOP, font, textColor);
        Border innerBorder = BorderFactory.createEmptyBorder(3, 20, 0, 0);
        return BorderFactory.createCompoundBorder(titledBorder, innerBorder);
    }

}
