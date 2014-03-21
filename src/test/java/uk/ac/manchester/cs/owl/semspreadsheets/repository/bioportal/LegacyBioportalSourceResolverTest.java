package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

public class LegacyBioportalSourceResolverTest {
	
	
	@Test
	public void resolve() {
		//normal match
		IRI resolved = LegacyBioportalSourceResolver.getInstance().resolve(IRI.create("http://rest.bioontology.org/bioportal/virtual/download/1488"));
		assertEquals("http://data.bioontology.org/ontologies/JERM/download",resolved.toString());
		
		resolved = LegacyBioportalSourceResolver.getInstance().resolve(IRI.create("http://rest.bioontology.org/bioportal/virtual/download/1132"));
		assertEquals("http://data.bioontology.org/ontologies/NCBITAXON/download",resolved.toString());
		
		//https
		resolved = LegacyBioportalSourceResolver.getInstance().resolve(IRI.create("https://rest.bioontology.org/bioportal/virtual/download/1488"));
		assertEquals("http://data.bioontology.org/ontologies/JERM/download",resolved.toString());
		
		
		
		//not matched
		resolved = LegacyBioportalSourceResolver.getInstance().resolve(IRI.create("http://fish.org/download"));
		assertEquals("http://fish.org/download",resolved.toString());
	
	}

}
