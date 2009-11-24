package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalRepositoryAccessor;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;

import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.util.SimpleRenderer;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.IRI;
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
 * Date: 18-Sep-2009
 */
public class WorkbookFrame extends JFrame {

    private WorkbookManager workbookManager;

    private MainPanel mainPanel;

    public WorkbookFrame(WorkbookManager manager) {
        this.workbookManager = manager;
        setTitle("Orcha");
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel = new MainPanel(this));
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = menuBar.add(new JMenu("File"));
        fileMenu.add(new OpenWorkbookAction(this));
        fileMenu.add(new OpenOntologyAction(this));
        fileMenu.add(new OpenFromBioPortalAction(this));
        fileMenu.addSeparator();
        fileMenu.add(new SaveAction(this));
        fileMenu.add(new SaveAsAction(this));

        JMenu editMenu = menuBar.add(new JMenu("Edit"));
        editMenu.add(new ClearOntologyValuesAction(this));
        editMenu.add(new SelectDownColumn(workbookManager, this));
//        JMenu viewMenu = menuBar.add(new JMenu("View"));
        JMenu insertMenu = menuBar.add(new JMenu("Insert"));
        insertMenu.add(new InsertSheetAction("Sheet", workbookManager, this));
//        JMenu formatMenu = menuBar.add(new JMenu("Format"));
//        formatMenu.add(new FormatBoldAction(this));
//        JMenu sheetMenu = new JMenu("Sheet");
//        formatMenu.add(sheetMenu);
//        sheetMenu.add(new RenameSheetAction(this));
//        JMenu helpMenu = menuBar.add(new JMenu("Help"));
        setJMenuBar(menuBar);
        updateTitleBar();
        workbookManager.addListener(new WorkbookManagerListener() {
            public void workbookChanged(WorkbookManagerEvent event) {
                handleWorkbookChanged();
            }

            public void workbookLoaded(WorkbookManagerEvent event) {
                handleWorkbookChanged();
            }

            public void ontologiesChanged(WorkbookManagerEvent event) {
                updateTitleBar();
            }
        });
    }

    public void handleWorkbookChanged() {
        updateTitleBar();
        Collection<IRI> ontologyIRIs = workbookManager.getOntologyTermValidationManager().getOntologyIRIs();
        Set<IRI> missingOntologies = new HashSet<IRI>();
        for(IRI ontologyIRI : ontologyIRIs) {
            if(!workbookManager.getOntologyManager().contains(ontologyIRI)) {
                missingOntologies.add(ontologyIRI);
            }
        }
        if(!missingOntologies.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body>");
            sb.append("The workbook contains information about terms from ontologies which<br>" +
                    "are not loaded.  Do you want to load these ontologies now?");
            sb.append("</body></html>");
            int ret = JOptionPane.showConfirmDialog(this, sb.toString(), "Load ontologies?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(ret == JOptionPane.YES_OPTION) {
                workbookManager.loadEmbeddedTermOntologies();
            }
        }
    }

    public void updateTitleBar() {
        StringBuilder sb = new StringBuilder();
        sb.append("Orcha");
        URI uri = workbookManager.getWorkbookURI();
        if(uri != null) {
            sb.append(" - ");
            if(uri.getScheme().equalsIgnoreCase("file")) {
                File file = new File(uri);
                sb.append(file.getPath());
            }
            else {
                sb.append(uri);
            }
        }
        setTitle(sb.toString());

    }

    public WorkbookManager getWorkbookManager() {
        return workbookManager;
    }

    public Sheet addSheet() {
        return workbookManager.getWorkbook().addSheet();
    }

    public void setSelectedSheet(Sheet sheet) {
        workbookManager.getSelectionModel().setSelectedRange(new Range(sheet));
    }

    public void loadOntology() throws OWLOntologyCreationException {
        File file = browseForFile("Load ontology", FileDialog.LOAD, new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.endsWith(".xls");
            }
        });
        if(file == null) {
            return;
        }
        workbookManager.loadOntology(file.toURI());

    }

    public void saveWorkbook() throws IOException {
        URI workbookURI = workbookManager.getWorkbookURI();
        if(workbookURI == null) {
            saveWorkbookAs();
        }
        else {
            workbookManager.saveWorkbook(workbookURI);
        }
    }

    public void openWorkbook() throws IOException {
        File file = browseForFile("Open workbook", FileDialog.LOAD, new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xls");
            }
        });
        if(file != null) {
            workbookManager.loadWorkbook(file);
        }
    }

    public void saveWorkbookAs() throws IOException {
        File file = browseForFile("Save workbook as", FileDialog.SAVE, null);
        if(file != null) {
            workbookManager.saveWorkbook(file.toURI());
        }
    }

    public File browseForFile(String title, int mode, FilenameFilter filenameFilter) {
        FileDialog fileDialog = new FileDialog(this, title, mode);
        if(filenameFilter != null) {
            fileDialog.setFilenameFilter(filenameFilter);
        }
        fileDialog.setVisible(true);
        String name = fileDialog.getFile();
        if(name == null) {
            return null;
        }
        String directory = fileDialog.getDirectory();
        return new File(directory + name);

    }

    public static void main(String[] args) {
//        try {
            WorkbookManager manager = new WorkbookManager();
//            manager.loadWorkbook(URI.create("file:/Users/matthewhorridge/Desktop/IDFExcelExample2_jerm.xls"));
//            manager.loadWorkbook(URI.create("file:/Users/matthewhorridge/Desktop/sdrfExample2.xls"));
//            ArrayList<OWLEntity> inds = new ArrayList<OWLEntity>();
//            for(int i = 0; i < 10; i++) {
//                inds.add(OWLDataFactoryImpl.getInstance().getOWLNamedIndividual(IRI.create("http://myont.com/ont/vals#Value" + 1)));
//            }
//            manager.loadOntology(URI.create("http://mged.sourceforge.net/ontologies/MGEDOntology.owl"));
//            manager.getWorkbook().addValueList(new ValueList("myvals", ValueListType.INDIVIDUALS, inds));
            WorkbookFrame frame = new WorkbookFrame(manager);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setVisible(true);
            SimpleRenderer renderer = new SimpleRenderer();
            renderer.setShortFormProvider(new SimpleShortFormProvider());
            ToStringRenderer.getInstance().setRenderer(renderer);

//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }

    }


}
