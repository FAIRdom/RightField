package uk.ac.manchester.cs.owl.semspreadsheets.ui;


import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.util.SimpleRenderer;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 */
public class WorkbookFrame extends JFrame {

    private WorkbookManager workbookManager;

    private TaskManager taskManager = new TaskManager(this);

    private MainPanel mainPanel;

    public WorkbookFrame(WorkbookManager manager) {
        this.workbookManager = manager;
        setTitle("RightField");
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

    public TaskManager getTaskManager() {
        return taskManager;
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
                taskManager.runTask(new LoadEmbeddedTermsOntologies());
            }
        }
    }

    public void updateTitleBar() {
        StringBuilder sb = new StringBuilder();
        sb.append("RightField");
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
        taskManager.runTask(new LoadOntologyTask(file));
       

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
