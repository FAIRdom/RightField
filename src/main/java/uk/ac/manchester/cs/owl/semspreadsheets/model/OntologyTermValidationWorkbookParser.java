/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public class OntologyTermValidationWorkbookParser {
	
	private static final Logger logger = LogManager.getLogger();

    private final WorkbookManager workbookManager;
    
    private static final Color highlightColour = new Color(16777164); //pale yellow
    
    private static Map<Cell,Color> originalColours = new HashMap<Cell, Color>();

    public OntologyTermValidationWorkbookParser(WorkbookManager workbookManager) {
        this.workbookManager = workbookManager;
    }      
    
    public static void clearOriginalColours() {
    	logger.debug("Clearing original colours");
    	originalColours.clear();
    }

    public Collection<OntologyTermValidation> readOntologyTermValidations() {
    	logger.debug("Reading validations from workbook");
        Set<OntologyTermValidation> validations = new HashSet<OntologyTermValidation>();
        Workbook workbook = getWorkbookManager().getWorkbook();
        Map<String,Validation> literalValidations = collectLiteralValidations(workbook);
        for (Sheet sheet : workbook.getSheets()) {
            OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(getWorkbookManager(), sheet);
            if (parser.isValidationSheet()) {
            	OntologyTermValidationDescriptor descriptor = parser.parseValidationDescriptor();
            	if (literalValidations.containsKey(sheet.getName())) {
            		Validation validation = literalValidations.get(sheet.getName());
            		validations.add(new OntologyTermValidation(descriptor, validation.getRange()));
            	}
            	else {
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
        }
        logger.debug("Finished reading validations, "+validations.size()+" found.");
        return validations;
    }
    
    private Map<String,Validation> collectLiteralValidations(Workbook workbook) {
    	Map<String,Validation> validations = new HashMap<String,Validation>();
    	
    	for (Sheet sheet : workbook.getSheets()) {    		
    		for (Validation validation : sheet.getValidations()) {
    			if (validation.isLiteralValidation()) {    				    				
    				validations.put(PropertyValidationForumlaDefinition.decode(validation.getFormula()),validation);
    			}
    		}
    	}
    	
    	return validations;
    }

    public void clearOntologyTermValidations() {
    	logger.debug("Clearing validations from workbook");
        Workbook workbook = getWorkbookManager().getWorkbook();
        Collection<Sheet> validationSheets = new ArrayList<Sheet>();
        restoreCellBackgroundColours();
        for (Sheet sheet : workbook.getSheets()) {
        	logger.debug("Clearing validations for sheet:"+sheet.getName());
            OntologyTermValidationSheetParser parser = new OntologyTermValidationSheetParser(getWorkbookManager(), sheet);
            if (parser.isValidationSheet()) {
                validationSheets.add(sheet);
                NamedRange namedRange = parser.parseNamedRange();
                if (namedRange != null) {
                    workbook.removeName(namedRange.getName());
                }
            }
            logger.debug("Finished clearing validations for sheet:"+sheet.getName());
        }
        for (Sheet sheet : workbook.getSheets()) {
        	logger.debug("Clearing validation data for sheet "+sheet.getName());
            sheet.clearValidationData();
            logger.debug("Finished clearing validation data for sheet "+sheet.getName());
        }
        for (Sheet sheet : validationSheets) {
        	String name = sheet.getName();
        	logger.debug("Deleting validation sheet "+name);
            workbook.deleteSheet(sheet.getName());
            logger.debug("Finished deleting validation sheet "+name);
        }
        logger.debug("Finished clearing validations from workbook");
    }

    public void writeOntologyTermValidations(Collection<OntologyTermValidation> ontologyTermValidations) {
        clearOntologyTermValidations();        
        highlightCells(ontologyTermValidations);
        for (OntologyTermValidation ontologyTermValidation : ontologyTermValidations) {            	 
            if (ontologyTermValidation.getValidationDescriptor().definesLiteral()) {
            	writeFreeTextDataValidation(ontologyTermValidation);
            }
            else {
            	writeDropdownDataValidation(ontologyTermValidation);
            }            
        }
    }
    
    private void highlightCells(Collection<OntologyTermValidation> ontologyTermValidations) {
    	Set<Cell> cells = new HashSet<Cell>();
    	for (OntologyTermValidation v : ontologyTermValidations) {
    		cells.addAll(v.getRange().getCells());
    	}
    	for (Cell cell : cells) {
    		logger.debug("Highlighting cell - Current cell colour is:"+cell.getBackgroundFill().toString());
    		if (!originalColours.containsKey(cell)) {
    			originalColours.put(cell, cell.getBackgroundFill());
    		}    		
            cell.setBackgroundFill(highlightColour);  
    	}
    }
    
    private void restoreCellBackgroundColours() {
    	for (Cell cell : originalColours.keySet()) {    		
    		cell.setBackgroundFill(originalColours.get(cell));
    	}
    	originalColours.clear();
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
		rng.getSheet().addLiteralValidation(sheet.getName(), rng.getFromColumn(),
				rng.getFromRow(), rng.getToColumn(), rng.getToRow());
		
	}

}
