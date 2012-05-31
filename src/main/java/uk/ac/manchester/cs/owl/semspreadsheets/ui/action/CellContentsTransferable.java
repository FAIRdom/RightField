/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
/**
 * This is a Transferable implementation to support the copy and paste of a range of cells, including both cell text values, and the OntologyTermValidationDescriptor.
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
			+ ";class="+SelectedCellDataContainerList.class.getCanonicalName(),"CellContentsList");
	
	private final SelectedCellDataContainerList data;
	
	public CellContentsTransferable(SelectedCellDataContainerList data) {
		super();		
		this.data = data;			
	}
	
	/**
	 * If the flavor is DataFlavor.stringFlavor then a tab separated text value is returned.
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
				data.sort();
				
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
		else if (CellContentsTransferable.dataFlavour.equals(flavor)){
			return data;
		}		
		else {
			throw new UnsupportedFlavorException(flavor);
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
