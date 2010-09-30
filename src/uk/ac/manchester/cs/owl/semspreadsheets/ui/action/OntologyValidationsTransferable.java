package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Collection;

import javax.swing.TransferHandler;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;

public class OntologyValidationsTransferable implements	Transferable {
	
	public static DataFlavor dataFlavour;
	private final Collection<? extends OntologyTermValidation> validations;
	
	public OntologyValidationsTransferable(Collection<? extends OntologyTermValidation> validations) {
		super();
		this.validations = validations;		
		Class c = validations.getClass();
		dataFlavour=new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
				+ c.getCanonicalName(),"OntologyTermValidations");		
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return validations;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{dataFlavour};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return this.dataFlavour.equals(flavor);
	}

}
