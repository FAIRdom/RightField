package uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingConstants;
import javax.swing.text.Style;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;

/**
 * @author Stuart Owen
 */
public class CellXSSFImpl implements Cell {
	
	private static Logger logger = Logger.getLogger(CellXSSFImpl.class);

    public static final Font DEFAULT_FONT = new Font("verdana", Font.PLAIN, 10);

    private static Map<XSSFFont, Font> fontCache = new HashMap<XSSFFont, Font>();

    private XSSFCell theCell;

    private XSSFWorkbook workbook;

    private Color foreground;

    public CellXSSFImpl(XSSFWorkbook workbook, XSSFCell theCell) {
        this.workbook = workbook;
        this.theCell = theCell;        
    }

    public Font getDefaultFont() {
        XSSFFont font = workbook.getFontAt((short) 0);
        if (font == null) {
            return DEFAULT_FONT;
        }
        return getFont(font);
    }

    public Style getStyle() {
        return null;
    }

    public int getRow() {
        return theCell.getRowIndex();
    }

    public int getColumn() {
        return theCell.getColumnIndex();
    }

    public String getComment() {
        XSSFComment hssfComment = theCell.getCellComment();
        if (hssfComment == null) {
            return null;
        }
        else {
            return hssfComment.toString();
        }

    }

    public boolean isStrikeThrough() {
        XSSFFont hssfFont = theCell.getCellStyle().getFont();
        return hssfFont.getStrikeout();
    }

    public boolean isUnderline() {
    	XSSFFont hssfFont = theCell.getCellStyle().getFont();
        return hssfFont.getUnderline() != 0;
    }

    public boolean isItalic() {
    	XSSFFont hssfFont = theCell.getCellStyle().getFont();
        return hssfFont.getItalic();
    }

    public String getValue() {
        if (theCell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
            return "";
        }
        else if (theCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
            return Boolean.toString(theCell.getBooleanCellValue());
        }
        else if (theCell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
            return "<ERROR?>";
        }
        else if (theCell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
            return theCell.getCellFormula();
        }
        else if (theCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            return Double.toString(theCell.getNumericCellValue());
        }
        else if (theCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
            return theCell.getRichStringCellValue().getString();
        }
        return "";
    }

    public void setValue(String value) {
        if (theCell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
            theCell.setCellValue(new XSSFRichTextString(value));
        }
        else if (theCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
            theCell.setCellValue(Boolean.parseBoolean(value));
        }
        else if (theCell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
        }
        else if (theCell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
            theCell.setCellFormula(value);
        }
        else if (theCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            theCell.setCellValue(Double.parseDouble(value));
        }
        else if (theCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
            theCell.setCellValue(new XSSFRichTextString(value));
        }
    }

    public boolean isBold() {
        return getFont().isBold();
    }

    public void setBold(boolean b) {
        XSSFCellStyle cellStyle = theCell.getCellStyle();        
        if (cellStyle == null) {
            cellStyle = workbook.createCellStyle();
            theCell.setCellStyle(cellStyle);
        }
        XSSFFont font = cellStyle.getFont();
        if (font == null) {
            font = workbook.createFont();
            cellStyle.setFont(font);
        }
        if (b) {
            font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        }
        else {
            font.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
        }
        fontCache.clear();
    }

    public Font getFont() {
        XSSFCellStyle cellStyle = theCell.getCellStyle();
        if (cellStyle == null) {
            return getDefaultFont();
        }
        XSSFFont hssfFont = cellStyle.getFont();
        return getFont(hssfFont);
    }

    private Font getFont(XSSFFont hssfFont) {
        Font font = fontCache.get(hssfFont);
        if (font == null) {
            String name = hssfFont.getFontName();
            int size = hssfFont.getFontHeightInPoints();
            int style = Font.PLAIN;
            if (hssfFont.getBoldweight() == XSSFFont.BOLDWEIGHT_BOLD) {
                style = Font.BOLD;
                if (hssfFont.getItalic()) {
                    style = style | Font.ITALIC;
                }
            }
            else if (hssfFont.getItalic()) {
                style = Font.ITALIC;
            }
            font = new Font(name, style, size);
            fontCache.put(hssfFont, font);
        }
        return font;

    }
    
    @Override
	public Color getBackgroundFill() {		
    	XSSFCellStyle cellStyle = theCell.getCellStyle();
        if (cellStyle == null) {
            return Color.WHITE;
        }        
		XSSFColor colour = cellStyle.getFillForegroundXSSFColor();
		if (colour == null) {
			return Color.WHITE;
		}
		
		return translateRGB(colour.getRgb());
	}

    private Color translateRGB(byte[] rgb) {
    	if (rgb == null) {
    		return Color.WHITE;
    	}
    	
    	if (rgb.length>3) {
    		return new Color(rgb[1] & 0xFF,rgb[2] & 0xFF, rgb[3] & 0xFF,rgb[0] & 0xFF);
    	}
    	else {
    		return new Color(rgb[0] & 0xFF,rgb[1] & 0xFF, rgb[2] & 0xFF);
    	}    	    	                         
	}

	@Override
	public void setBackgroundFill(Color colour) {
		XSSFColor col = new XSSFColor(colour);
		
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND );
		cellStyle.setFillForegroundColor(col.getIndexed());			
		theCell.setCellStyle(cellStyle);
		logger.debug("Cell colour changed to "+col.toString());	
	}

    public Color getForeground() {
        if (foreground == null) {
        	XSSFColor colour = theCell.getCellStyle().getFont().getXSSFColor();
        	return translateRGB(colour.getRgb());
        }
        return foreground;
    }    

    public int getAlignment() {
        XSSFCellStyle cellStyle = theCell.getCellStyle();
        if (cellStyle == null) {
            return SwingConstants.LEFT;
        }
        short hssfAlignment = cellStyle.getAlignment();
        if (hssfAlignment == XSSFCellStyle.ALIGN_LEFT) {
            return SwingConstants.LEFT;
        }
        else if (hssfAlignment == XSSFCellStyle.ALIGN_CENTER) {
            return SwingConstants.CENTER;
        }
        else if (hssfAlignment == XSSFCellStyle.ALIGN_RIGHT) {
            return SwingConstants.RIGHT;
        }
        else {
            return SwingConstants.LEFT;
        }
    }

    public String getValidationListName() {
        return null;
    }

    public boolean isEmpty() {
        return false;
    }    
    
    @Override
	public int hashCode() {
		return theCell.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CellXSSFImpl) {
			CellXSSFImpl cell = (CellXSSFImpl)obj;
			return cell.theCell.equals(this.theCell);			
		}
		else {
			return false;
		}
	}
    
    public XSSFCell getInnards() {
    	return theCell;
    }

	@Override
	public String getSheetName() {
		return workbook.getSheetName(getSheetIndex());
	}

	@Override
	public int getSheetIndex() {
		XSSFSheet sheet = theCell.getSheet();
		return workbook.getSheetIndex(sheet);
	}

	

	
}
