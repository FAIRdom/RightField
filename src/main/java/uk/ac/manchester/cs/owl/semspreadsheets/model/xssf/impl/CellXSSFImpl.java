package uk.ac.manchester.cs.owl.semspreadsheets.model.xssf.impl;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingConstants;

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

    private static Map<XSSFWorkbook,Map<XSSFFont, Font>> fontCache = new HashMap<XSSFWorkbook,Map<XSSFFont, Font>>();
    
    private static Map<XSSFWorkbook,Map<Color,XSSFCellStyle>> colourStylesForWorkbook = new HashMap<XSSFWorkbook, Map<Color,XSSFCellStyle>>();

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
    
    public XSSFWorkbook getWorkbook() {
    	return workbook;
    }

    public int getRow() {
        return theCell.getRowIndex();
    }

    public int getColumn() {
        return theCell.getColumnIndex();
    }

    public String getComment() {
        XSSFComment xssfComment = theCell.getCellComment();
        if (xssfComment == null) {
            return null;
        }
        else {
            return xssfComment.toString();
        }

    }

    public boolean isStrikeThrough() {
        XSSFFont xssfFont = theCell.getCellStyle().getFont();
        return xssfFont.getStrikeout();
    }

    public boolean isUnderline() {
    	XSSFFont xssfFont = theCell.getCellStyle().getFont();
        return xssfFont.getUnderline() != 0;
    }

    public boolean isItalic() {
    	XSSFFont xssfFont = theCell.getCellStyle().getFont();
        return xssfFont.getItalic();
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
        XSSFFont xssfFont = cellStyle.getFont();
        return getFont(xssfFont);
    }

    private Font getFont(XSSFFont xssfFont) {
    	
        Font font = getFontFromCache(xssfFont);
        if (font == null) {
            String name = xssfFont.getFontName();
            int size = xssfFont.getFontHeightInPoints();
            int style = Font.PLAIN;
            if (xssfFont.getBoldweight() == XSSFFont.BOLDWEIGHT_BOLD) {
                style = Font.BOLD;
                if (xssfFont.getItalic()) {
                    style = style | Font.ITALIC;
                }
            }
            else if (xssfFont.getItalic()) {
                style = Font.ITALIC;
            }
            font = new Font(name, style, size);            
            putFontInCache(xssfFont, font);
        }
        return font;

    }
    
    private Font getFontFromCache(XSSFFont xssfFont) {
    	Map<XSSFFont,Font> cache = fontCache.get(getWorkbook());
    	if (cache==null) {
    		cache = new HashMap<XSSFFont,Font>();
    		fontCache.put(getWorkbook(), cache);
    	}
    	return cache.get(xssfFont);
    }
    
    private void putFontInCache(XSSFFont xssfFont,Font font) {
    	Map<XSSFFont,Font> cache = fontCache.get(getWorkbook());
    	if (cache==null) {
    		cache = new HashMap<XSSFFont,Font>();
    		fontCache.put(getWorkbook(), cache);
    	}
    	cache.put(xssfFont, font);
    }
    
    @Override
	public Color getBackgroundFill() {
    	Color colour = null;
    	XSSFCellStyle cellStyle = theCell.getCellStyle();
        if (cellStyle == null) {
            colour = Color.WHITE;
        }        
        else {
        	XSSFColor xssfColour = cellStyle.getFillForegroundXSSFColor();
    		if (xssfColour == null) {
    			colour = Color.WHITE;
    		}
    		else {
    			colour = translateRGB(xssfColour.getRgb());
    		}
        }
		
        logger.debug("Background fill colour read as: "+colour);
		
		return colour;
    	//return Color.WHITE;
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
		XSSFCellStyle style = getFillStyleForColour(colour);
								
		try {
			theCell.setCellStyle(style);
		}
		catch(Exception e) {
			logger.error("Error setting cell style",e);
		}
	}

	private XSSFCellStyle getFillStyleForColour(Color colour) {
		Map<Color,XSSFCellStyle> styles = colourStylesForWorkbook.get(getWorkbook());
    	if (styles == null) {
    		styles = new HashMap<Color,XSSFCellStyle>();
    		colourStylesForWorkbook.put(getWorkbook(), styles);
    	}
    	XSSFCellStyle style = styles.get(colour);
    	if (style==null) {
    		style = getWorkbook().createCellStyle();
    		XSSFColor col = new XSSFColor(colour);
    		style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND );
    		style.setFillForegroundColor(col);
    		styles.put(colour,style);
    	}
    	return style;
	}
	
    public Color getForeground() {
        if (foreground == null) {
        	XSSFColor colour = theCell.getCellStyle().getFont().getXSSFColor();
        	if (colour!=null) {
        		foreground = translateRGB(colour.getRgb());
        	}        	
        	else {
        		foreground = Color.BLACK;
        	}
        }
        return foreground;
    }    

    public int getAlignment() {
        XSSFCellStyle cellStyle = theCell.getCellStyle();
        if (cellStyle == null) {
            return SwingConstants.LEFT;
        }
        short xssfAlignment = cellStyle.getAlignment();
        if (xssfAlignment == XSSFCellStyle.ALIGN_LEFT) {
            return SwingConstants.LEFT;
        }
        else if (xssfAlignment == XSSFCellStyle.ALIGN_CENTER) {
            return SwingConstants.CENTER;
        }
        else if (xssfAlignment == XSSFCellStyle.ALIGN_RIGHT) {
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
