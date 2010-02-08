package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryManager;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalRepositoryAccessor;

import java.awt.event.ActionEvent;
import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
/*
 * Copyright (C) 2009, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 11-Nov-2009
 */
public class OpenFromBioPortalAction extends WorkbookFrameAction {

    public OpenFromBioPortalAction(WorkbookFrame workbookFrame) {
        super("Open from BioPortal...", workbookFrame);
    }

    public void actionPerformed(ActionEvent e) {
        BioPortalRepositoryAccessor bioPortalRepositoryAccessor = RepositoryManager.getInstance().getBioPortalRepositoryAccessor();
        if (!bioPortalRepositoryAccessor.getRepository().getOntologies().isEmpty()) {
            RepositoryItem item =RepositoryPanel.showDialog(getWorkbookFrame(), bioPortalRepositoryAccessor);
            if(item != null) {
                try {
                    getWorkbookFrame().getWorkbookManager().loadOntology(item.getPhysicalIRI());
                }
                catch (OWLOntologyCreationException e1) {
                    ErrorHandler.getErrorHandler().handleError(e1);
                }
            }
        }
    }
}
