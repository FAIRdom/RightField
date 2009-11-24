package uk.ac.manchester.cs.owl.semspreadsheets.model;


import java.util.*;
/*
 * Copyright (C) 2009, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 08-Nov-2009
 */
public class OntologyTermValidationWorkbookParser {

    private WorkbookManager workbookManager;

    public OntologyTermValidationWorkbookParser(WorkbookManager workbookManager) {
        this.workbookManager = workbookManager;
    }

    public Collection<OntologyTermValidation> readOntologyTermValidations() {
        Set<OntologyTermValidation> validations = new HashSet<OntologyTermValidation>();
        Workbook workbook = workbookManager.getWorkbook();
        for (Sheet sheet : workbook.getSheets()) {
            OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(workbookManager, sheet);
            if (parser.isValidationSheet()) {
                OntologyTermValidationDescriptor descriptor = parser.parseValidationDescriptor();
                NamedRange namedRange = parser.parseNamedRange();

                for (Sheet sheet2 : workbook.getSheets()) {
                    for (Validation validation : sheet2.getValidations()) {
                        if (validation.getListName().equals(namedRange.getName())) {
                            validations.add(new OntologyTermValidation(descriptor, validation.getRange()));
                        }
                    }
                }
            }
        }
        return validations;
    }

    public void clearOntologyTermValidations() {
        Workbook workbook = workbookManager.getWorkbook();
        Collection<Sheet> validationSheets = new ArrayList<Sheet>();
        for (Sheet sheet : workbook.getSheets()) {
            OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(workbookManager, sheet);
            if (parser.isValidationSheet()) {
                validationSheets.add(sheet);
                NamedRange namedRange = parser.parseNamedRange();
                if (namedRange != null) {
                    workbook.removeName(namedRange.getName());
                }
            }
        }
        for (Sheet sheet2 : workbook.getSheets()) {
            sheet2.clearValidation();
        }
        for (Sheet sheet : validationSheets) {
            workbook.deleteSheet(sheet.getName());
        }
    }

    public void writeOntologyTermValidations(Collection<OntologyTermValidation> ranges) {
        clearOntologyTermValidations();
        Map<OntologyTermValidationDescriptor, Sheet> descriptorSheetMap = new HashMap<OntologyTermValidationDescriptor, Sheet>();
        for (OntologyTermValidation ontologyTermValidation : ranges) {
            OntologyTermValidationDescriptor descriptor = ontologyTermValidation.getValidationDescriptor();
            if(!descriptor.getTerms().isEmpty()) {
                Sheet sheet = descriptorSheetMap.get(descriptor);
                Workbook workbook = workbookManager.getWorkbook();
                if (sheet == null) {
                    sheet = workbook.addVeryHiddenSheet();
                    OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(workbookManager, sheet);
                    parser.createValidationSheet(descriptor);
                    Range namedRangeRange = parser.getTermsShortNameRange();
                    workbook.addName(sheet.getName(), namedRangeRange);
                }
                Range rng = ontologyTermValidation.getRange();
                rng.getSheet().addValidation(sheet.getName(), rng.getFromColumn(), rng.getFromRow(), rng.getToColumn(), rng.getToRow());
            }
        }
    }

}
