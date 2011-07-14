package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.io.IOException;
import java.net.URI;

import uk.ac.manchester.cs.owl.semspreadsheets.impl.WorkbookHSSFImpl;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 */
public class WorkbookFactory {

    /**
     * Creates an empty workbook
     * @return The workbook
     */
    public static Workbook createWorkbook() {
        return new WorkbookHSSFImpl();
    }

    /**
     * Creates a workbook by parsing an Excel document at a given URI
     * @param uri The URI that points to the Excel workbook
     * @return A representation of the workbook at the specified URI
     * @throws IOException If there was an IO problem loading the workbook
     */
    public static Workbook createWorkbook(URI uri) throws IOException {
        return new WorkbookHSSFImpl(uri);
    }

}
