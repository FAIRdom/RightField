package uk.ac.manchester.cs.owl.semspreadsheets.repository;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalRepositoryAccessor;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public class RepositoryManager  {

    private static RepositoryManager instance = new RepositoryManager();

    private BioPortalRepositoryAccessor bioPortalRepositoryAccessor;

    private RepositoryManager() {
        bioPortalRepositoryAccessor = new BioPortalRepositoryAccessor();
    }

    public static RepositoryManager getInstance() {
        return instance;
    }


    public BioPortalRepositoryAccessor getBioPortalRepositoryAccessor() {
        return bioPortalRepositoryAccessor;
    }
}
