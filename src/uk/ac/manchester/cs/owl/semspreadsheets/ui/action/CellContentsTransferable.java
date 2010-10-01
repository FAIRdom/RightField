package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Collection;

import javax.swing.TransferHandler;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;

public class CellContentsTransferable implements	Transferable {
	
	public static DataFlavor dataFlavour;
	private final Collection<? extends OntologyTermValidation> validations;
	private final String textValue;
	
	public CellContentsTransferable(String textValue,Collection<? extends OntologyTermValidation> validations) {
		super();
		this.textValue = textValue;
		this.validations = validations;		
		Class c = validations.getClass();
		dataFlavour=new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
				+ c.getCanonicalName(),"OntologyTermValidations");		
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (DataFlavor.stringFlavor.equals(flavor)) {
			return textValue;
		}
		else {
			return new Object[]{textValue,validations};
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
