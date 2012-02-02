package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A wrapped ArrayList that SelectedCellDataContainer. By creating this class it makes setting
 * the class type on the Transferable for copy & paste more explicit. It also avoids having a Serializable
 * List that seems to cause problems with the clipboard on Mac - otherwise it always tries to serialize 
 * even if using the javaJVMLocalObjectMimeType based Data flavor.
 * 
 * @author Stuart Owen
 * @see SelectedCellDataContainer
 * @see CellContentsTransferable
 *
 */
public class SelectedCellDataContainerList implements Iterable<SelectedCellDataContainer> {
	
	private List<SelectedCellDataContainer> innerList = new ArrayList<SelectedCellDataContainer>();
	
	public void sort() {
		Collections.sort(innerList,new Comparator<SelectedCellDataContainer>() {
			@Override
			public int compare(SelectedCellDataContainer o1,
					SelectedCellDataContainer o2) {					
				Integer col1=new Integer(o1.col);
				Integer col2=new Integer(o2.col);
				if (o1.row==o2.row) {
					return col1.compareTo(col2);
				}
				else {
					Integer row1=new Integer(o1.row);
					Integer row2=new Integer(o2.row);
					return row1.compareTo(row2);
				}						
			}
		});
	}
	
	public int size() {
		return innerList.size();
	}
	public Object[] toArray() {
		return innerList.toArray();
	}
	public <T> T[] toArray(T[] a) {
		return innerList.toArray(a);
	}
	public boolean isEmpty() {
		return innerList.isEmpty();
	}
	public Iterator<SelectedCellDataContainer> iterator() {
		return innerList.iterator();
	}
	public boolean add(SelectedCellDataContainer o) {
		return innerList.add(o);
	}
	public void clear() {
		innerList.clear();
	}
	public SelectedCellDataContainer get(int index) {
		return innerList.get(index);
	}	

}
