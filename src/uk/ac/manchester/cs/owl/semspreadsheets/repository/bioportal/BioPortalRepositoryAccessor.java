package uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.Repository;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryAccessor;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public class BioPortalRepositoryAccessor implements RepositoryAccessor {

    private BioPortalRepository repository;

    public String getRepositoryName() {
        return BioPortalRepository.NAME;
    }

    public synchronized Repository getRepository() {
        if(repository == null) {
            repository = new BioPortalRepository();
        }
        return repository;
    }
}
