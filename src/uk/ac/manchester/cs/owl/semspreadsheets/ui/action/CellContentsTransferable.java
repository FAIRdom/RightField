package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationDescriptor;
/**
 * This is a Transferable implemenation to support the copy and paste of a range of cells, including both cell text values, and the OntologyTermValidationDescriptor.
 * 
 * It will allow the copy/paste of both from within RightField, and also text values between RightField and other application.
 * 
 * @author Stuart Owen
 *
 */
public class CellContentsTransferable implements Transferable {
	
	/**
	 * The DataFlavor for handling the collection of OntologyTermValidation's and the text value of the cell.
	 */
	public static DataFlavor dataFlavour = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
			+ SelectedCellDataContainer.class.getCanonicalName(),"SelectedCellDataContainer");
	
	private final List<SelectedCellDataContainer> data;
	
	public CellContentsTransferable(List<SelectedCellDataContainer> data) {
		super();		
		this.data = data;			
	}
	
	/**
	 * If the flavor is DataFlavor.stringFlavor then a tab seperated text value is returned.
	 * If the flavor is CellContentsTransferable.dataFlavor then a List of SelectedCellDataContainer is returned.
	 * 
	 * @see SelectedCellDataContainer
	 */
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (DataFlavor.stringFlavor.equals(flavor)) {
			StringBuffer result = new StringBuffer();
			if (data.size()>0) {
				Collections.sort(data,new Comparator<SelectedCellDataContainer>() {
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
				
				int lastRow=data.get(0).row;
				
				for (SelectedCellDataContainer cellData : data) {
					if (cellData.row != lastRow) {
						result.append("\n");
						lastRow=cellData.row;
					}
					else {
						if (data.get(0)!=cellData) { //i.e. not the first one
							result.append("\t");
						}
					}
					result.append(cellData.textValue);					
				}
			}
			return result.toString();
		}
		else {
			return data;
		}		
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {		
		return new DataFlavor[]{dataFlavour,DataFlavor.stringFlavor};		
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return CellContentsTransferable.dataFlavour.equals(flavor) || DataFlavor.stringFlavor.equals(flavor);
	}

}
