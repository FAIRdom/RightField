/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.aggregates.DataValidityTable;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * This class isolates the interaction with Apache POI that relies on following accessor methods that were added to POI in the
 * patch. Hopefully one day this patching won't be necessary
 * 
 * {@link HSSFSheet#getDataValidityTable()}
 * {@link DataValidityTable#clear()}
 * {@link DVRecord#getFormula1()}
 * {@link DVRecord#getFormula2()}
 * 
 * @author Stuart Owen 
 */
public class PatchedPoi {

	private static PatchedPoi instance = new PatchedPoi();

	private PatchedPoi() {

	}

	public static PatchedPoi getInstance() {
		return instance;
	}

	public List<HSSFDataValidation> getValidationData(final HSSFSheet sheet,
			final HSSFWorkbook workbook) {
		final List<HSSFDataValidation> dataValidation = new ArrayList<HSSFDataValidation>();
		DataValidityTable dvt = sheet.getDataValidityTable();
		dvt.visitContainedRecords(new RecordAggregate.RecordVisitor() {
			/**
			 * /** Implementors may call non-mutating methods on Record r.
			 * 
			 * @param r must not be <code>null</code>
			 *            
			 */
			public void visitRecord(Record r) {
				if (r instanceof DVRecord) {
					DVRecord dvRecord = (DVRecord) r;
					CellRangeAddressList cellRangeAddressList = dvRecord
							.getCellRangeAddress();
					int validationType = dvRecord.getDataType();
					if (validationType == DVConstraint.ValidationType.LIST) {
						Formula f1 = dvRecord.getFormula1();
						String formula1 = getStringFromPtgTokens(
								f1.getTokens(), workbook);
						DVConstraint dvConstraint = DVConstraint
								.createFormulaListConstraint(formula1);
						HSSFDataValidation validation = new HSSFDataValidation(
								cellRangeAddressList, dvConstraint);
						dataValidation.add(validation);
					} else if (validationType == DVConstraint.ValidationType.INTEGER
							|| validationType == DVConstraint.ValidationType.DECIMAL
							|| validationType == DVConstraint.ValidationType.TEXT_LENGTH
							) {
						Formula f1 = dvRecord.getFormula1();
						Formula f2 = dvRecord.getFormula2();
						String formula1 = getStringFromPtgTokens(
								f1.getTokens(), workbook);
						String formula2 = getStringFromPtgTokens(
								f2.getTokens(), workbook);
						int comparison = dvRecord.getConditionOperator();
						DVConstraint dvConstraint = DVConstraint
								.createNumericConstraint(validationType,
										comparison, formula1, formula2);
						HSSFDataValidation validation = new HSSFDataValidation(
								cellRangeAddressList, dvConstraint);
						dataValidation.add(validation);
					} else if (validationType == DVConstraint.ValidationType.FORMULA) {
						Formula f1 = dvRecord.getFormula1();
						
						String formula1 = getStringFromPtgTokens(
								f1.getTokens(), workbook);
						//doesn't contain the full formula, including cell and comparison - but should contain all the information needed. If not, getStringFromPtgTokens
						//needs updating to handle the extra token types
						DVConstraint dvConstraint = DVConstraint.createCustomFormulaConstraint(formula1);
								
						HSSFDataValidation validation = new HSSFDataValidation(
								cellRangeAddressList, dvConstraint);
						dataValidation.add(validation);
					}
					
				}
			}
		});
		return dataValidation;
	}

	public void clearValidationData(HSSFSheet sheet) {
		sheet.getDataValidityTable().clear();
	}

	protected String getStringFromPtgTokens(Ptg[] tokens,
			HSSFWorkbook hssfWorkbook) {

		StringBuilder sb = new StringBuilder();
		for (Ptg token : tokens) {
			if (token instanceof NamePtg) {
				NamePtg namePtg = (NamePtg) token;
				HSSFEvaluationWorkbook wb = HSSFEvaluationWorkbook
						.create(hssfWorkbook);
				sb.append(wb.getNameText(namePtg));
			} else if (token instanceof StringPtg) {
				StringPtg stringPtg = (StringPtg) token;
				sb.append(stringPtg.getValue());
			} else if (token instanceof IntPtg) {
				IntPtg intPtg = (IntPtg) token;
				sb.append(intPtg.getValue());
			}
		}
		return sb.toString();
	}

}
