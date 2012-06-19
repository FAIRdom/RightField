/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public class OntologyTermValidationWorkbookParser {
	
	private static final Logger logger = Logger.getLogger(OntologyTermValidationWorkbookParser.class);

    private final WorkbookManager workbookManager;

    public OntologyTermValidationWorkbookParser(WorkbookManager workbookManager) {
        this.workbookManager = workbookManager;
    }      

    public Collection<OntologyTermValidation> readOntologyTermValidations() {
        Set<OntologyTermValidation> validations = new HashSet<OntologyTermValidation>();
        Workbook workbook = getWorkbookManager().getWorkbook();
        validations.addAll(readPropertyOnlyValidations(workbook));
        for (Sheet sheet : workbook.getSheets()) {
            OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(getWorkbookManager(), sheet);
            if (parser.isValidationSheet()) {
                OntologyTermValidationDescriptor descriptor = parser.parseValidationDescriptor();
                NamedRange namedRange = parser.parseNamedRange();
                if (namedRange!=null) {
                	for (Sheet sheet2 : workbook.getVisibleSheets()) {
                        for (Validation validation : sheet2.getValidations()) {
                            if (validation.getFormula().equals(namedRange.getName())) {
                                validations.add(new OntologyTermValidation(descriptor, validation.getRange()));
                            }
                        }
                    }
                }                
            }
        }
        return validations;
    }
    
    private Collection<OntologyTermValidation> readPropertyOnlyValidations(Workbook workbook) {
    	Set<OntologyTermValidation> validations = new HashSet<OntologyTermValidation>();
    	
    	for (Sheet sheet : workbook.getSheets()) {    		
    		for (Validation validation : sheet.getValidations()) {
    			if (!validation.isDataValidation()) {    				    				
    				OntologyTermValidation termValidation = PropertyFormulaEncoder.constructFromValidation(validation,getWorkbookManager().getOntologyManager());
    				if (termValidation!=null) {
    					validations.add(termValidation);
    				}        			    				    			
    			}
    		}
    	}
    	
    	return validations;
    }

    public void clearOntologyTermValidations() {
        Workbook workbook = getWorkbookManager().getWorkbook();
        Collection<Sheet> validationSheets = new ArrayList<Sheet>();
        for (Sheet sheet : workbook.getSheets()) {
            OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(getWorkbookManager(), sheet);
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
        for (OntologyTermValidation ontologyTermValidation : ranges) {            	 
            if (ontologyTermValidation.getValidationDescriptor().isDefinesLiteral()) {
            	writeFreeTextDataValidation(ontologyTermValidation);
            }
            else {
            	writeDropdownDataValidation(ontologyTermValidation);
            }            
        }
    }
    
    private WorkbookManager getWorkbookManager() {
    	return workbookManager;
    }
    
    /**
     * Creates a standard dropdown validation for a list of available terms. This is achieved using a named range, with the range of available values, mapped to their terms,
     * stored in a 'very' hidden sheet
     * @param ontologyTermValidation
     */
	private void writeDropdownDataValidation(
			OntologyTermValidation ontologyTermValidation) {
		logger.debug("Writing dropdown selection based data validation to Workbook for "+ontologyTermValidation.getValidationDescriptor().toString());
		OntologyTermValidationDescriptor descriptor = ontologyTermValidation
				.getValidationDescriptor();
		if (!descriptor.getTerms().isEmpty()) {
			Workbook workbook = getWorkbookManager().getWorkbook();
			Sheet sheet = workbook.addVeryHiddenSheet();
			OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(
					getWorkbookManager(), sheet);
			parser.createValidationSheet(descriptor);
			Range namedRangeRange = parser.getTermsShortNameRange();
			workbook.addName(sheet.getName(), namedRangeRange);

			Range rng = ontologyTermValidation.getRange();
			rng.getSheet().addValidation(sheet.getName(), rng.getFromColumn(),
					rng.getFromRow(), rng.getToColumn(), rng.getToRow());
		}
	}
	
	/**
	 * Creates a free text based validation, the applies a property, but not specific terms to a field. The cell allows free text to be written, which is then treated
	 * as a literal with the property applied. This is achieved by a using a custom formula on the cell, with the formula (almost) gaurenteed to pass but containing the property
	 * IRI.
	 * 
	 * @param ontologyTermValidation
	 */
	private void writeFreeTextDataValidation(OntologyTermValidation ontologyTermValidation) {
		logger.debug("Writing free text based formula validation to Workbook for "+ontologyTermValidation.getValidationDescriptor().toString());
		OntologyTermValidationDescriptor descriptor = ontologyTermValidation.getValidationDescriptor();
		Workbook workbook = getWorkbookManager().getWorkbook();
		Sheet sheet = workbook.addVeryHiddenSheet();
		OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(
				getWorkbookManager(), sheet);
		parser.createValidationSheet(descriptor);
		Range rng = ontologyTermValidation.getRange();
		rng.getSheet().addValidation(sheet.getName(),ontologyTermValidation.getValidationDescriptor().getOWLPropertyItem(), rng.getFromColumn(),
				rng.getFromRow(), rng.getToColumn(), rng.getToRow());
		
	}

}
