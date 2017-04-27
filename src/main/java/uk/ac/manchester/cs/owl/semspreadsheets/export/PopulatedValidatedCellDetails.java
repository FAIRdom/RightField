/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyItem;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationDescriptor;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Term;

/**
 * Contains the details for a cell in a spreadsheet that has had its annotation selected.
 * Contains details about the actual cell, the text value of the cell, the associated {@link Term}, Entity IRI and the Ontology IRI's. 
 * It also provides full access to the {@link OntologyTermValidation} for additional information if required.
 * 
 * @author Stuart Owen
 * 
 * @see AbstractExporter#getPopulatedValidatedCellDetails()
 *
 */
class PopulatedValidatedCellDetails 
{
	private OntologyTermValidation validation;
	private Cell cell;	
	private Term term;
	private String textValue;
	
	public PopulatedValidatedCellDetails(OntologyTermValidation validation,
			Cell cell, Term term, String textValue) {
		super();
		this.validation = validation;
		this.cell = cell;
		this.term = term;
		this.textValue = textValue;		
	}

	public PopulatedValidatedCellDetails(OntologyTermValidation validation, Cell cell, String textValue) {
		this(validation,cell,null,textValue);
	}
	
	 /**
     * @return whether this validation defines a literal, i.e has a property but is FREETEXT
     * 
     * @see OntologyTermValidationDescriptor#definesLiteral()
     */
	public boolean definesLiteral() {
		return this.term==null && getValidation().getValidationDescriptor().definesLiteral();
	}
	
	public Sheet getSheet() {
		return validation.getRange().getSheet();
	}
	
	public OntologyTermValidation getValidation() {
		return validation;
	}
	
	public OWLPropertyItem getOWLPropertyItem() {
		return getValidation().getValidationDescriptor().getOWLPropertyItem();
	}
	
	public Cell getCell() {
		return cell;
	}
	
	public Term getTerm() {
		return term;
	}
	
	public String getTextValue() {
		return textValue;
	}
	
	public IRI getEntityIRI() {
		return getValidation().getValidationDescriptor().getEntityIRI();
	}
	
	public Set<IRI> getOntologyIRIs() {
		return getValidation().getValidationDescriptor().getOntologyIRIs();
	}
	
	public Set<IRI> getPhysicalIRIs() {
		Set<IRI> iris = new HashSet<IRI>();
		for (IRI ontologyIRI : getOntologyIRIs()) {
			iris.add(getValidation().getValidationDescriptor().getPhysicalIRIForOntologyIRI(ontologyIRI));
		}
		return iris;
	}
}
