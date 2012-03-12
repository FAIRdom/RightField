package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCellClearAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCellCopyAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCellCutAction;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.action.SheetCellPasteAction;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class SheetPanel extends JPanel {

    public static final Color MARGIN_GRID_COLOR = new Color(150, 167, 180);

    public static final Color TEXT_COLOR = new Color(68, 81, 94);

    public static final Color BACK_GROUND_COLOR = new Color(235, 235, 235);

    public static final Color SHADOW_COLOR = new Color(228, 228, 228);

    public static final Color HIGHLIGHT1 = new Color(242, 242, 242);

    public static final Color HIGHLIGHT2 = new Color(246, 246, 246);

    public static final Color SELECTED_CELL_MARGIN_COLOR = new Color(109, 127, 142);

    public static final Color SELECTED_CELL_TEXT_COLOR = new Color(241, 241, 241);

    public static final Color SELECTION_HIGHLIGHT = new Color(230, 241, 255);

    public static final Font FONT = new Font("verdana", Font.BOLD, 10);

    private WorkbookManager workbookManager;

    private SheetTable table;
    private JScrollPane scrollPane;
    private CellSelectionListener cellSelectionListener;

    private Collection<Disposable> disposables = new ArrayList<Disposable>();
    private OntologyTermValidationListener ontologyTermValidationListener;

    public SheetPanel(WorkbookManager workbookManager, Sheet sheet) {
        this.workbookManager = workbookManager;
        setLayout(new BorderLayout());
        table = new SheetTable(workbookManager, sheet);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scrollPane = new JScrollPane(table);
        SheetBorder sheetBorder = new SheetBorder(workbookManager, this);
        disposables.add(sheetBorder);
        scrollPane.setBorder(sheetBorder);
        add(scrollPane, BorderLayout.CENTER);
        SheetHeaderRenderer headerRenderer = new SheetHeaderRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setHeaderRenderer(headerRenderer);
        }
        ListSelectionListener selectionListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                    transmitSelectionToModel();
                   
            }
        };
        table.getSelectionModel().addListSelectionListener(selectionListener);
        table.getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);
        cellSelectionListener = new CellSelectionListener() {
            public void selectionChanged(Range range) {
                updateSelectionFromModel(range);
            }
        };
        workbookManager.getSelectionModel().addCellSelectionListener(cellSelectionListener);
        scrollPane.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                handleRowSelectionRequest(e);
            }
        });
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                handleColumnSelectionRequest(e);
            }
        });
        
        ontologyTermValidationListener = new OntologyTermValidationListener() {
        	@Override
            public void validationsChanged() {
                table.repaint();
            }			

			@Override
			public void ontologyTermSelected(
					List<OntologyTermValidation> previewList) {						
			}
        };
        
        //Add copy/paste/cut actions   
        table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0),"delete");
        table.getActionMap().put("copy",new SheetCellCopyAction(workbookManager,getToolkit()));
        table.getActionMap().put("paste",new SheetCellPasteAction(workbookManager, getToolkit()));
        table.getActionMap().put("cut",new SheetCellCutAction(workbookManager, getToolkit()));
        table.getActionMap().put("delete",new SheetCellClearAction(workbookManager));

        workbookManager.getOntologyTermValidationManager().addListener(ontologyTermValidationListener);
    }

    public void dispose() {
        workbookManager.getSelectionModel().removeCellSelectionListener(cellSelectionListener);
        workbookManager.getOntologyTermValidationManager().removeListener(ontologyTermValidationListener);
        for(Disposable disposable : disposables) {
            disposable.dispose();
        }
    }

    public JViewport getViewport() {
        return scrollPane.getViewport();
    }

    public Sheet getSheet() {
        return table.getSheet();
    }

    public SheetTable getSheetTable() {
        return table;
    }

    private void handleRowSelectionRequest(MouseEvent e) {
        int y = e.getY() - table.getTableHeader().getHeight();
        int row = table.rowAtPoint(new Point(1, y));
        if(row < 0) {
            return;
        }
        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
    }

    private void handleColumnSelectionRequest(MouseEvent e) {
        int x = e.getX();
        int col = table.columnAtPoint(new Point(x, 1));
        if(col < 0) {
            return;
        }
        table.setColumnSelectionInterval(col, col);
        table.setRowSelectionInterval(0, table.getRowCount() - 1);
    }

    private boolean updatingSelectionFromModel = false;

    private boolean transmittingSelectionToModel = false;

    private void updateSelectionFromModel(Range range) {
        if(transmittingSelectionToModel) {
            return;
        }
        try {
            updatingSelectionFromModel = true;
            table.clearSelection();
            if(range != null && range.isCellSelection()) {
                table.addRowSelectionInterval(range.getFromRow(), range.getToRow());
                table.addColumnSelectionInterval(range.getFromColumn(), range.getToColumn());
            }
        }
        finally {
            updatingSelectionFromModel = false;
        }
    }

    private void transmitSelectionToModel() {
        if(updatingSelectionFromModel) {
            return;
        }
        try {
            transmittingSelectionToModel = true;
            Range selRange = getSelectedRange();
            workbookManager.getSelectionModel().setSelectedRange(selRange);
        }
        finally {
            transmittingSelectionToModel = false;
        }
    }

    public Range getSelectedRange() {
        int[] selRows = table.getSelectedRows();
        int[] selCols = table.getSelectedColumns();
        int fromCol = Integer.MAX_VALUE;
        int toCol = 0;
        for (int i = 0; i < selCols.length; i++) {
            if (selCols[i] < fromCol) {
                fromCol = selCols[i];
            }
            if (selCols[i] > toCol) {
                toCol = selCols[i];
            }
        }
        int fromRow = Integer.MAX_VALUE;
        int toRow = 0;

        for (int i = 0; i < selRows.length; i++) {
            if (selRows[i] < fromRow) {
                fromRow = selRows[i];
            }
            if (selRows[i] > toRow) {
                toRow = selRows[i];
            }
        }
        Range range = null;
        if (fromCol != Integer.MAX_VALUE && fromRow != Integer.MAX_VALUE) {
            range = new Range(table.getSheet(), fromCol, fromRow, toCol, toRow);
        }
        else {
            range = new Range(table.getSheet());
        }
        return range;
    }


    public static void paintBorderCell(Graphics g, String str, Rectangle rect, boolean selected) {
        if (selected) {
            g.setColor(SELECTED_CELL_MARGIN_COLOR);
        }
        else {
            g.setColor(BACK_GROUND_COLOR);
        }
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        if (!selected) {
            g.setColor(SHADOW_COLOR);
            g.drawLine(rect.x, rect.y + rect.height, rect.width, rect.y + rect.height);
            g.drawLine(rect.width, rect.y, rect.width, rect.y + rect.height);
            g.setColor(HIGHLIGHT2);
            g.drawLine(rect.x, rect.y, rect.x + rect.width, rect.y);
            g.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height);
            g.setColor(HIGHLIGHT1);
            g.drawLine(rect.x + 1, rect.y + 1, rect.x + rect.width - 1, rect.y + 1);
            g.drawLine(rect.x + 1, rect.y + 1, rect.x + 1, rect.y + rect.height - 1);
        }
        if (selected) {
            g.setColor(SELECTED_CELL_TEXT_COLOR);
        }
        else {
            g.setColor(TEXT_COLOR);
        }
        Rectangle bounds = g.getFontMetrics().getStringBounds(str, g).getBounds();
        Font oldFont = g.getFont();
        g.setFont(FONT);
        g.drawString(str, (rect.width / 2) - (bounds.width / 2), g.getFontMetrics().getAscent() + rect.y);
        g.setFont(oldFont);
    }
}
