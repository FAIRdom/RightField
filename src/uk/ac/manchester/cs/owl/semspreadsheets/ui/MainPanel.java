package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 */
public class MainPanel extends JPanel {

    private WorkbookManager workbookManager;

    private WorkbookPanel workbookPanel;


    public MainPanel(WorkbookFrame frame) {
        this.workbookManager = frame.getWorkbookManager();
        workbookPanel = new WorkbookPanel(workbookManager);
        setLayout(new BorderLayout());
        JSplitPane sp = new JSplitPane();
        sp.setLeftComponent(workbookPanel);
        sp.setRightComponent(new ValidationInspectorPanel(frame));
        sp.setResizeWeight(0.8);
        add(sp);
    }

  
    public WorkbookManager getSpreadSheetManager() {
        return workbookManager;
    }

}
