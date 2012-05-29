package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
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
	
	public OntologyTermValidation getValidation() {
		return validation;
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
}