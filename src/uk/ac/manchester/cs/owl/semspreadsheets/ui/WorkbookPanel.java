package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.change.SetCellValue;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeEvent;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 31-Oct-2009
 * 
 * Author: Stuart Owen
 * Date: 30-Sep-2010
 */
@SuppressWarnings("serial")
public class WorkbookPanel extends JPanel {
	
	private Logger logger = Logger.getLogger(WorkbookPanel.class);

    private WorkbookManager manager;

    private JTabbedPane tabbedPane;

    private boolean transmittingSelectionToModel = false;

    private boolean updatingSelectionFromModel = false;

    private boolean rebuildingTabs = false;
    private WorkbookChangeListener workbookChangeListener;
    
    public Map<String,SheetPanel> sheetPanels = new HashMap<String,SheetPanel>();


    public WorkbookPanel(WorkbookManager workbookManager) {
        this.manager = workbookManager;
        tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        workbookChangeListener = new WorkbookChangeListener() {
            public void workbookChanged(WorkbookChangeEvent event) {
                logger.debug("Workbook Change: " + event);
                //FIMXE: Don't like this, but it works. Will revisit when I understand the code structure some more.
                if (event.getChange() instanceof SetCellValue) {
                	SetCellValue scv=(SetCellValue)event.getChange();
                	scv.getSheet().getName();
                	SheetPanel sheetPanel = (SheetPanel)tabbedPane.getSelectedComponent();
                	SheetTableModel model = (SheetTableModel)sheetPanel.getSheetTable().getModel();
                	model.fireTableCellUpdated(scv.getRow(),scv.getCol());
                }
            }

            public void sheetAdded() {
                rebuildTabs();
            }

            public void sheetRemoved() {
                rebuildTabs();
            }

            public void sheetRenamed(String oldName, String newName) {
                for(int i = 0; i < tabbedPane.getTabCount(); i++) {
                    if(tabbedPane.getTitleAt(i).equals(oldName)) {
                        tabbedPane.setTitleAt(i, newName);
                    }
                }
            }
        };
        manager.getWorkbook().addChangeListener(workbookChangeListener);
        rebuildTabs();
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                transmitSelectionToModel();
            }
        });
        workbookManager.getSelectionModel().addCellSelectionListener(new CellSelectionListener() {
            public void selectionChanged(Range range) {
                updateSelectionFromModel();
            }
        });
        workbookManager.addListener(new WorkbookManagerListener() {
            public void workbookChanged(WorkbookManagerEvent event) {
                rebuildTabs();
                transmitSelectionToModel();
            }

            public void workbookLoaded(WorkbookManagerEvent event) {
                rebuildTabs();
                transmitSelectionToModel();
            }

            public void ontologiesChanged(WorkbookManagerEvent event) {
            }
        });


    }

    private void rebuildTabs() {
        manager.getWorkbook().addChangeListener(workbookChangeListener);
        rebuildingTabs = true;
        for(int i = 0; i < tabbedPane.getTabCount(); i++) {
            SheetPanel sheetPanel = (SheetPanel) tabbedPane.getComponentAt(i);
            sheetPanel.dispose();
        }
        tabbedPane.removeAll();
        Workbook workbook = manager.getWorkbook();
        for (Sheet sheet : workbook.getSheets()) {
            if (!sheet.isHidden()) {
                SheetPanel sheetPanel = new SheetPanel(manager, sheet);
                tabbedPane.add(sheet.getName(), sheetPanel);                
            }
        }
        rebuildingTabs = false;
    }

    private void updateSelectionFromModel() {
        if(rebuildingTabs || transmittingSelectionToModel) {
            return;
        }
        try {
            updatingSelectionFromModel = true;
            Range range = manager.getSelectionModel().getSelectedRange();
            setSelectedSheet(range.getSheet());
        }
        finally {
            updatingSelectionFromModel = false;
        }
    }

    private SheetPanel getSelectedSheetPanel() {
        return (SheetPanel) tabbedPane.getSelectedComponent();
    }


    private void transmitSelectionToModel() {
        if(rebuildingTabs || updatingSelectionFromModel) {
            return;
        }
        try {
            transmittingSelectionToModel = true;
            Range range = getSelectedSheetPanel().getSelectedRange();
            manager.getSelectionModel().setSelectedRange(range);
        }
        finally {
            transmittingSelectionToModel = false;
        }

    }

    public Sheet getSelectedSheet() {
        SheetPanel sheetPanel = (SheetPanel) tabbedPane.getSelectedComponent();
        return sheetPanel.getSheet();
    }

    public void setSelectedSheet(Sheet sheet) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            SheetPanel sheetPanel = (SheetPanel) tabbedPane.getComponentAt(i);
            if (sheetPanel.getSheet().getName().equals(sheet.getName())) {
                tabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }


}
