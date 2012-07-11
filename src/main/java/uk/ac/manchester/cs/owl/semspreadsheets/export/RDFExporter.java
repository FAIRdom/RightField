/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyItem;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class RDFExporter extends AbstractExporter {
	
	private static final Logger logger = Logger.getLogger(RDFExporter.class);
	
	private final IRI rootID;
	
	public static final String DEFAULT_PROPERTY_URI = "http://rightfield.org.uk/RightFieldOntology#contains";

	public RDFExporter(File workbookFile,IRI rootID) throws IOException {
		super(workbookFile);
		this.rootID = rootID;		
	}

	public RDFExporter(URI workbookURI,IRI rootID) throws IOException {
		super(workbookURI);
		this.rootID = rootID;		
	}

	public RDFExporter(WorkbookManager manager,IRI rootID) {
		super(manager);
		this.rootID = rootID;		
	}
	
	private IRI getRootID() {
		return rootID;
	}
	
	private Property getDefaultProperty(Model model) {		
		Property property = model.createProperty(DEFAULT_PROPERTY_URI);
		model.setNsPrefix("rightfield", IRI.create(DEFAULT_PROPERTY_URI).getStart());
		return property;		
	}

	@Override
	public void export(OutputStream outStream) {		
		Model model = ModelFactory.createDefaultModel();
		
		Resource root = model.createResource(getRootID().toString());
		
		for (PopulatedValidatedCellDetails details : getPopulatedValidatedCellDetails()) {
			addNode(root,model,details);
		}
		
		try {
			model.write(new OutputStreamWriter(outStream,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Error writing to stream with UTF-8 encoding",e);
		}
	}
	
	private Property createProperty(Model model,OWLPropertyItem property) {
		if (property==null) {
			return getDefaultProperty(model);
		}
		else {
			Property result = model.createProperty(property.getIRI().toString());
			String start = property.getIRI().getStart(); 
			if (start.equals("http://www.mygrid.org.uk/ontology/JERMOntology#")) {
				model.setNsPrefix("jerm", start);
			}
			return result;
		}
	}
	
	private void addNode(Resource rootResource,Model model,PopulatedValidatedCellDetails cellDetails) {
		if (cellDetails.definesLiteral()) {
			addLiteralNode(rootResource,model,cellDetails);
		}
		else {
			addStatementNode(rootResource, model, cellDetails);
		}
	}
	
	private void addLiteralNode(Resource rootResource,Model model,PopulatedValidatedCellDetails cellDetails) {
		Property property = createProperty(model,cellDetails.getOWLPropertyItem());
		rootResource.addProperty(property, model.createLiteral(cellDetails.getTextValue()));
	}
	
	private void addStatementNode(Resource rootResource,Model model,PopulatedValidatedCellDetails cellDetails) {
		Property property = createProperty(model,cellDetails.getOWLPropertyItem());
		Resource r = model.createResource(cellDetails.getTerm().getIRI().toString());						
		Statement s = model.createStatement(rootResource, property, r);
		model.add(s);
	}

}
