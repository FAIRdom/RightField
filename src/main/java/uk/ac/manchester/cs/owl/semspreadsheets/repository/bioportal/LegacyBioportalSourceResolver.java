/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import au.com.bytecode.opencsv.CSVReader;


/** 
 * Resolves old legacy BioPortal download paths, such as http://rest.bioontology.org/bioportal/virtual/download/1488
 * To the newer format - http://data.bioontology.org/ontologies/JERM/download
 * using a lookup table stored in the csv file /legacy_bioportal_ontology_id_mappings.csv
 * 
 * @author Stuart Owen
 */
public class LegacyBioportalSourceResolver {
	static class LegacyBioportalSourceResolverHolder {
		private static LegacyBioportalSourceResolver INSTANCE = new LegacyBioportalSourceResolver();
	}
	public static LegacyBioportalSourceResolver getInstance() {
		return LegacyBioportalSourceResolverHolder.INSTANCE;
	}

	private static final Logger logger = Logger.getLogger(LegacyBioportalSourceResolver.class);
	
	private static final Map<String,String> mappings = new HashMap<String,String>();
	private static final String MAPPING_FILE = "/legacy_bioportal_ontology_id_mappings.csv";
	private static final String OLD_HOST = "rest.bioontology.org";
	private static final String NEW_BASE = "http://data.bioontology.org/ontologies/";
	
	public LegacyBioportalSourceResolver() {
		if (mappings.isEmpty()) {
			readMappings();
		}
	}
	
	public IRI resolve(IRI iri) {		
		if (matches(iri.toURI())) {
			return IRI.create(switchToNew(iri.toURI()));
		}
		else {
			return iri;
		}
	}
	
	private boolean matches(URI uri) {		
		return (uri!=null && uri.getScheme().startsWith("http") && uri.getHost().equals(OLD_HOST) && uri.getPath().contains("download"));
	}
	
	private URI switchToNew(URI uri) { 
		URI newURI = null;
		logger.info("Old bioportal download uri detected and about to be resolved:"+uri.toString());
		String [] split = uri.getPath().split("/");
		String id = split[split.length-1];
		String acronym = mappings.get(id);
		if (acronym!=null) {					
			newURI=URI.create(NEW_BASE+acronym+"/download");
			logger.info("BioPortal download URI resolved to:"+newURI);
		} else {		
			logger.warn("Expected to be able to resolve "+uri.toString()+" to new api download path\n but the extracted ontology id was not recognised ("+id +")");			
		}
		return newURI;
	}
	
	private void readMappings() {
		URL csvResource = LegacyBioportalSourceResolver.class.getResource(MAPPING_FILE);
		
			try {
				CSVReader reader = new CSVReader(new InputStreamReader(csvResource.openStream()));
				String [] line;
				while ((line = reader.readNext())!=null) {
					String id = line[0];
					String acronym = line[1];
					logger.debug("Bioportal Legacy ID mapping: "+id+" : "+acronym);
					mappings.put(id, acronym);
				}
				reader.close();
			} catch (IOException e) {
				logger.error("Error reading Bioportal legacy mapping file",e);
			}
		
	}
}
