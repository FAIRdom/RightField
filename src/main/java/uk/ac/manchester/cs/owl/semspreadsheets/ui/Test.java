package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.io.File;

import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleRenderer;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 18-Sep-2009
 */
public class Test {

    public static void main(String[] args) {
        File file = new File("/Users/matthewhorridge/Desktop/IDFExcelExample2_jerm.xls");

        SimpleRenderer renderer = new SimpleRenderer();
        ShortFormProvider sfp = new SimpleShortFormProvider();
        renderer.setShortFormProvider(sfp);
//            renderer.setPrefix(":", "http://mged.sourceforge.net/ontologies/MGEDOntology.owl#");
        ToStringRenderer.getInstance().setRenderer(renderer);
        WorkbookManager workbookManager = new WorkbookManager();
//        MainPanel mainPanel = new MainPanel(workbookManager);


//
//        catch (OWLOntologyCreationException e) {
//            e.printStackTrace();
//        }

    }
}
