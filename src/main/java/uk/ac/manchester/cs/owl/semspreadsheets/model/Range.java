/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



/**
 * @author Stuart Owen
 * @author Matthew Horridge
 * 
 */
public class Range implements Comparable<Range> {

    private Sheet sheet;

    private int fromColumn;

    private int fromRow;

    private int toColumn;

    private int toRow;
    
    public long count() {
    	int w=getToRow()-getFromColumn()+1;
    	int h=getToColumn()-getFromColumn()+1;
    	return w*h;
    }

    public Range(Sheet sheet, int fromColumn, int fromRow, int toCol, int toRow) {
        this.sheet = sheet;
        this.fromColumn = fromColumn;
        this.fromRow = fromRow;
        this.toColumn = toCol;
        this.toRow = toRow;
    }

    public Range(Sheet sheet, Cell cell) {
        this.sheet = sheet;
        fromColumn = cell.getColumn();
        fromRow = cell.getRow();
        toColumn = cell.getColumn();
        toRow = cell.getRow();
    }

    public Range(Sheet sheet) {
        this.sheet = sheet;
        fromColumn = -1;
        toColumn = -1;
        fromRow = -1;
        toRow = -1;
    }
    
    /**
     * @return Indicates whether a single cell is selected
     */
    public boolean isSingleCellSelected() {
    	if (isCellSelection()) {
    		return getFromColumn()==getToColumn() && getFromRow()==getToRow();
    	}
    	return false;
    }

    public boolean isCellSelection() {
        return fromColumn != -1 && toColumn != -1 && fromRow != -1 && toRow != -1;
    }
    
	/**
	 * @return Collection of the cells contained in the range
	 */
	public Collection<Cell> getCells() {
		List<Cell> cells = new ArrayList<Cell>();
		for (int r = fromRow; r <= toRow; r++) {
			for (int c = fromColumn; c <= toColumn; c++) {
				cells.add(getSheet().getCellAt(c, r));
			}
		}
		return cells;
	}
    
    public Sheet getSheet() {
        return sheet;
    }

    public int getFromColumn() {
        return fromColumn;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getToColumn() {
        return toColumn;
    }

    public int getToRow() {
        return toRow;
    }

    public boolean intersectsRange(Range range) {
        if(range == null) {
            return false;
        }
        if (!sheet.equals(range.getSheet())) {
            return false;
        }

        if(range.getToColumn() < fromColumn) {
            return false;
        }
        if(range.getFromColumn() > toColumn) {
            return false;
        }
        if(range.getToRow() < fromRow) {
            return false;
        }
        if(range.getFromRow() > toRow) {
            return false;
        }
        return true;

    }

    public boolean containsRange(Range range) {
        if(range == null) {
            return false;
        }
        if(!sheet.equals(range.getSheet())) {
            return false;
        }
        if(range.getFromColumn() < fromColumn) {
            return false;
        }
        if(range.getToColumn() > toColumn) {
            return false;
        }
        if(range.getFromRow() < fromRow) {
            return false;
        }
        if(range.getToRow() > toRow) {
            return false;
        }
        return true;

    }

    public String toString() {
        return sheet.getName() + "!" + getColumnRowAddress();
    }

    public String getColumnRowAddress() {
        return ((char) (fromColumn + 65)) + "" + (fromRow + 1) + ":" + ((char) (toColumn + 65)) + "" + (toRow + 1);
    }

    public String toFixedAddress() {
        return sheet.getName() + "!$" + ((char) (fromColumn + 65)) + "$" + (fromRow + 1) + ":$" + ((char) (toColumn + 65)) + "$" +  (toRow + 1);
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hashtables such as those provided by
     * <code>java.util.Hashtable</code>.
     * The general contract of <code>hashCode</code> is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     * an execution of a Java application, the <tt>hashCode</tt> method
     * must consistently return the same integer, provided no information
     * used in <tt>equals</tt> comparisons on the object is modified.
     * This integer need not remain consistent from one execution of an
     * application to another execution of the same application.
     * <li>If two objects are equal according to the <tt>equals(Object)</tt>
     * method, then calling the <code>hashCode</code> method on each of
     * the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     * according to the {@link Object#equals(Object)}
     * method, then calling the <tt>hashCode</tt> method on each of the
     * two objects must produce distinct integer results.  However, the
     * programmer should be aware that producing distinct integer results
     * for unequal objects may improve the performance of hashtables.
     * </ul>
     * As much as is reasonably practical, the hashCode method defined by
     * class <tt>Object</tt> does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the
     * Java<font size="-2"><sup>TM</sup></font> programming language.)
     * @return a hash code value for this object.
     * @see Object#equals(Object)
     * @see java.util.Hashtable
     */
    @Override
    public int hashCode() {
        int hashCode = sheet.hashCode();
        hashCode = hashCode * 37 + fromColumn;
        hashCode = hashCode * 37 + toColumn;
        hashCode = hashCode * 37 + fromRow;
        hashCode = hashCode * 37 + toRow;
        return hashCode;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * The <code>equals</code> method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     * <code>x</code>, <code>x.equals(x)</code> should return
     * <code>true</code>.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     * <code>x</code> and <code>y</code>, <code>x.equals(y)</code>
     * should return <code>true</code> if and only if
     * <code>y.equals(x)</code> returns <code>true</code>.
     * <li>It is <i>transitive</i>: for any non-null reference values
     * <code>x</code>, <code>y</code>, and <code>z</code>, if
     * <code>x.equals(y)</code> returns <code>true</code> and
     * <code>y.equals(z)</code> returns <code>true</code>, then
     * <code>x.equals(z)</code> should return <code>true</code>.
     * <li>It is <i>consistent</i>: for any non-null reference values
     * <code>x</code> and <code>y</code>, multiple invocations of
     * <tt>x.equals(y)</tt> consistently return <code>true</code>
     * or consistently return <code>false</code>, provided no
     * information used in <code>equals</code> comparisons on the
     * objects is modified.
     * <li>For any non-null reference value <code>x</code>,
     * <code>x.equals(null)</code> should return <code>false</code>.
     * </ul>
     * The <tt>equals</tt> method for class <code>Object</code> implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values <code>x</code> and
     * <code>y</code>, this method returns <code>true</code> if and only
     * if <code>x</code> and <code>y</code> refer to the same object
     * (<code>x == y</code> has the value <code>true</code>).
     * Note that it is generally necessary to override the <tt>hashCode</tt>
     * method whenever this method is overridden, so as to maintain the
     * general contract for the <tt>hashCode</tt> method, which states
     * that equal objects must have equal hash codes.
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj
     *         argument; <code>false</code> otherwise.
     * @see #hashCode()
     * @see java.util.Hashtable
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof Range)) {
            return false;
        }
        Range other = (Range) obj;
        return other.fromColumn == this.fromColumn
                && other.toColumn == this.toColumn
                && other.fromRow == this.fromRow
                && other.toRow == this.toRow
                && other.sheet.equals(this.sheet);
    }

	@Override
	/**
	 * Compares them based upon the sheet index, the from row, and the from column
	 */
	public int compareTo(Range o) {
		int sheetIndex = getSheet().getIndex();
		int fromColumn = getFromColumn();
		int fromRow = getFromRow();
		if (sheetIndex!=o.getSheet().getIndex()) {
			return sheetIndex > getSheet().getIndex() ? 1 : -1;
		}
		if (fromRow!=o.getFromRow()) {
			return fromRow > o.getFromRow() ? 1 : -1;
		}
		if (fromColumn!=o.getFromColumn()) {
			return fromColumn > o.getFromColumn() ? 1 : -1;
		}
		return 0;
	}
}
