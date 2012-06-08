/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Matthew Horridge
 * @author Stuart Owen
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
            sheet2.clearValidationData();
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
