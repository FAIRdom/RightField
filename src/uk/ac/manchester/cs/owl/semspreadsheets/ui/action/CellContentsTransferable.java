package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
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

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (DataFlavor.stringFlavor.equals(flavor)) {
			if (data.size()!=1) {
				throw new UnsupportedFlavorException(DataFlavor.stringFlavor);
			}
			return data.get(0).textValue;
		}
		else {
			return data;
		}		
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		if (data.size()==1) {
			return new DataFlavor[]{dataFlavour,DataFlavor.stringFlavor};
		}
		else {
			return new DataFlavor[]{dataFlavour};
		}
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return CellContentsTransferable.dataFlavour.equals(flavor) || (DataFlavor.stringFlavor.equals(flavor) && data.size()==1);
	}

}
