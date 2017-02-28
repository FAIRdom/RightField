/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellAddress;
import org.apache.xmlbeans.impl.values.XmlValueDisconnectedException;
import uk.ac.manchester.cs.owl.semspreadsheets.model.*;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class SheetTable extends JTable {
	
	private static Logger logger = Logger.getLogger(SheetTable.class);
    
    private Sheet sheet;

    private WorkbookManager workbookManager;
    
    private SpreadSheetCellEditor editor;    

	public SheetTable(WorkbookManager ss, Sheet sheet) {
        this.workbookManager = ss;
        this.sheet = sheet;        

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableModel model = new SheetTableModel(sheet);
        setModel(model);                      

        setCellSelectionEnabled(true);

        SheetCellRenderer ren = new SheetCellRenderer();
        editor = new SpreadSheetCellEditor();

        for (int col = 0; col < sheet.getMaxColumns(); col++) {        	
            TableColumn column = getColumnModel().getColumn(col);
            column.setCellRenderer(ren);
            column.setCellEditor(editor);
            column.setPreferredWidth(sheet.getColumnWidth(col));            
        }
        setShowGrid(true);
        setRowMargin(1);
        setGridColor(new Color(206, 206, 206));
    }    

    public Sheet getSheet() {
        return sheet;
    }
    
    public boolean stopCellEditing() {
		return editor.stopCellEditing();
	}

    private Stroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);

    private AlphaComposite alphaComposite2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f);

    private Color validationAppliedColour = new Color(50, 150, 60);

    private Color emptyValidationColour = Color.DARK_GRAY;

    protected void paintComponent(Graphics g) {
    	try {
    		super.paintComponent(g);
    	}
    	catch(XmlValueDisconnectedException e) {
    		logger.warn("XmlValueDisconnectedException whilst repainting table");
    	}
        Color oldColor = g.getColor();
        Graphics2D g2 = (Graphics2D) g;
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(stroke);
        for (OntologyTermValidation ontologyTermValidation : workbookManager.getOntologyManager().getOntologyTermValidations()) {
            if (ontologyTermValidation.getRange().getSheet().equals(sheet)) {
                Range validation = ontologyTermValidation.getRange();
                OntologyTermValidationDescriptor validationDescriptor = ontologyTermValidation.getValidationDescriptor();
                if(!validationDescriptor.definesLiteral() && validationDescriptor.getTerms().isEmpty()) {
                    g.setColor(emptyValidationColour);
                }
                else {
                    g.setColor(validationAppliedColour);
                }
                Rectangle startRect = getCellRect(validation.getFromRow(), validation.getFromColumn(), false);
                Rectangle endRect = getCellRect(validation.getToRow(), validation.getToColumn(), false);
                int x1 = startRect.x + 1;
                int y1 = startRect.y + 1;
                int width = endRect.width + endRect.x - startRect.x - 2;
                int height = endRect.y + endRect.height - startRect.y - 2;
                Rectangle rect = new Rectangle(x1, y1, width, height);
                Composite oldComposite = g2.getComposite();
                g2.setComposite(alphaComposite2);
                g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 5, 5);
                g2.setComposite(alphaComposite);
                g2.fill(rect);
                g2.setComposite(oldComposite);
            }
        }

        java.util.List<Sheet> sheets = workbookManager.getWorkbook().getSheets();
        Sheet sheet = workbookManager.getSelectionModel().getSelectedRange().getSheet();

        if(sheet != null)
        {
            for(String cellLocation : workbookManager.getLinkedCells(sheet.getName()))
            {
                Boolean isTo = true;
                for(String caca : cellLocation.split(","))
                {
                    Range validation;
                    if(caca.contains("!"))
                    {
                        CellAddress da = new CellAddress(caca.substring(0, caca.length()-1));
                        //CellAddress endRow = new CellAddress(da.getRow(),"IV");
                        for(int index = 0; index <= 50; index++)
                        {
                            if(sheet.getCellAt(index, da.getRow()) == null)
                            {
                                sheet.addCellAt(index, da.getRow());
                            }
                        }
                        validation = new Range(sheet, 0,da.getRow(),50,da.getRow());
                    }else {
                        CellAddress da = new CellAddress(caca);
                        if(sheet.getCellAt(da.getColumn(), da.getRow()) == null)
                        {
                            sheet.addCellAt(da.getColumn(), da.getRow());
                        }
                        validation = new Range(sheet, sheet.getCellAt(da.getColumn(), da.getRow()));
                    }



                    g.setColor(isTo ? Color.BLUE : Color.RED);
                    Rectangle startRect = getCellRect(validation.getFromRow(), validation.getFromColumn(), false);
                    Rectangle endRect = getCellRect(validation.getToRow(), validation.getToColumn(), false);
                    int x1 = startRect.x + 1;
                    int y1 = startRect.y + 1;
                    int width = endRect.width + endRect.x - startRect.x - 2;
                    int height = endRect.y + endRect.height - startRect.y - 2;
                    Rectangle rect = new Rectangle(x1, y1, width, height);
                    Composite oldComposite = g2.getComposite();
                    g2.setComposite(alphaComposite2);
                    g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 5, 5);
                    g2.setComposite(alphaComposite);
                    //g2.fill(rect);
                    g2.setComposite(oldComposite);
                    isTo = false;
                }
            }
        }
        g2.setStroke(oldStroke);
        g.setColor(oldColor);
    }
}
