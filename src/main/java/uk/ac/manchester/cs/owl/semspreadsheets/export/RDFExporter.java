/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import uk.ac.manchester.cs.owl.semspreadsheets.model.InvalidWorkbookFormatException;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyItem;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class RDFExporter extends AbstractExporter {

	private static final Logger logger = Logger.getLogger(RDFExporter.class);

	private final IRI rootID;

	public static final String DEFAULT_PROPERTY_URI = "http://www.mygrid.org.uk/ontology/JERMOntology#hasAssociatedItem";

	private final IRI defaultProperty;

	public RDFExporter(File workbookFile, IRI rootID, IRI defaultProperty)
			throws IOException, InvalidWorkbookFormatException {
		super(workbookFile);
		this.rootID = rootID;
		this.defaultProperty = defaultProperty;
	}

	public RDFExporter(URI workbookURI, IRI rootID, IRI defaultProperty)
			throws IOException, InvalidWorkbookFormatException {
		super(workbookURI);
		this.rootID = rootID;
		this.defaultProperty = defaultProperty;
	}

	public RDFExporter(WorkbookManager manager, IRI rootID, IRI defaultProperty) {
		super(manager);
		this.rootID = rootID;
		this.defaultProperty = defaultProperty;
	}

	public RDFExporter(File workbookFile, IRI rootID) throws IOException, InvalidWorkbookFormatException {
		this(workbookFile, rootID, IRI.create(DEFAULT_PROPERTY_URI));
	}

	public RDFExporter(URI workbookURI, IRI rootID) throws IOException, InvalidWorkbookFormatException {
		this(workbookURI, rootID, IRI.create(DEFAULT_PROPERTY_URI));
	}

	public RDFExporter(WorkbookManager manager, IRI rootID) {
		this(manager, rootID, IRI.create(DEFAULT_PROPERTY_URI));
	}

	private IRI getRootID() {
		return rootID;
	}

	private Property getDefaultProperty(Model model) {
		Property property = model.createProperty(defaultProperty.toString());
		return property;
	}

	@Override
	public void export(OutputStream outStream) {
		Model model = ModelFactory.createDefaultModel();

		Resource root = model.createResource(getRootID().toString());

		List<PopulatedValidatedCellDetails> populatedValidatedCellDetails = getPopulatedValidatedCellDetails();

		// reverse the list, so the graph shows them in the order in the workbook. First
		// node added appears at the end of the graph
		Collections.reverse(populatedValidatedCellDetails);

		for (PopulatedValidatedCellDetails details : populatedValidatedCellDetails) {
			addNode(root, model, details);
		}

		try {
			model.write(new OutputStreamWriter(outStream, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Error writing to stream with UTF-8 encoding", e);
		}
	}

	private Property createProperty(Model model, OWLPropertyItem propertyItem) {
		Property property;
		if (propertyItem == null) {
			property = getDefaultProperty(model);
		} else {
			property = model.createProperty(propertyItem.getIRI().toString());
		}
		setKnownPropertyPrefixes(model, property);
		return property;
	}

	private void setKnownPropertyPrefixes(Model model, Property property) {
		if (!model.getNsPrefixMap().containsKey("jerm")) {
			if (property.getURI().startsWith("http://www.mygrid.org.uk/ontology/JERMOntology#")) {
				model.setNsPrefix("jerm", "http://www.mygrid.org.uk/ontology/JERMOntology#");
			}
		}
	}

	private void addNode(Resource rootResource, Model model, PopulatedValidatedCellDetails cellDetails) {
		if (cellDetails.definesLiteral()) {
			addLiteralNode(rootResource, model, cellDetails);
		} else {
			addStatementNode(rootResource, model, cellDetails);
		}
	}

	private void addLiteralNode(Resource rootResource, Model model, PopulatedValidatedCellDetails cellDetails) {
		Property property = createProperty(model, cellDetails.getOWLPropertyItem());
		rootResource.addProperty(property, model.createLiteral(cellDetails.getTextValue()));
	}

	private void addStatementNode(Resource rootResource, Model model, PopulatedValidatedCellDetails cellDetails) {
		Property property = createProperty(model, cellDetails.getOWLPropertyItem());
		Resource r = model.createResource(cellDetails.getTerm().getIRI().toString());
		Statement s = model.createStatement(rootResource, property, r);
		model.add(s);
	}

}
