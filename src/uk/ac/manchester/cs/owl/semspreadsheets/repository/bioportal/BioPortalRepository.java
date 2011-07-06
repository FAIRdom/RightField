package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.Repository;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public class BioPortalRepository implements Repository {

    public static final String NAME = "BioPortal";

    public static final String BASE = "http://rest.bioontology.org/bioportal/";

    public static final String ONTOLOGY_LIST = BASE + "ontologies/";

    public static final String ONTOLOGY_IRI_BASE = BASE + "ontologies/";

    public static final String API_KEY = readAPIKey();

    private Collection<RepositoryItem> repositoryItems = new ArrayList<RepositoryItem>();

    public BioPortalRepository() {
        OntologyListAccessor accessor = new OntologyListAccessor();
            repositoryItems.addAll(accessor.getOntologies());
    }

    public String getName() {
        return NAME;
    }

    public Collection<RepositoryItem> getOntologies() {
        return repositoryItems;
    }
    
    private static String readAPIKey() {
    	return "xxx-xxx-xxx";
    }

    
}
