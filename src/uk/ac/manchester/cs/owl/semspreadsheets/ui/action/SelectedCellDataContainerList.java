package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A specialised ArrayList to hold SelectedCellDagtaContainer. By creating this class it makes setting
 * the class type on the Transferable for copy & paste more explicit.
 * 
 * @author Stuart Owen
 * @see SelectedCellDataContainer
 * @see CellContentsTransferable
 *
 */
public class SelectedCellDataContainerList extends ArrayList<SelectedCellDataContainer> implements Serializable {

	private static final long serialVersionUID = 7491188905082713610L;

}
