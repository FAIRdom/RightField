/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 *  
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/

package uk.ac.manchester.cs.owl.semspreadsheets.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.semspreadsheets.impl.InvalidWorkbookFormatException;

/**
 * A simple tool for exporting rdf from a batch of spreadsheets in a directory. It is a one off tool, created for a specific purpose, but may have future uses
 * and enhancements.
 * 
 * All Excel files in the provided folder are processed, and rdf generated is put into a file with the same name but .rdf extension. A URI is generated as an
 * identifier based upon the filename.
 * 
 * @author Stuart Owen
 *
 */
public class RightFieldBatchExporter {
	
	private static Logger logger = Logger.getLogger(RightFieldBatchExporter.class);
	
	public static void main(String [] args) {
		logger.setLevel(Level.INFO);		
		new RightFieldBatchExporter();		
	}
	
	public RightFieldBatchExporter() {
		logger.info("Starting");
		File directory = browseForDirectory();
		if (directory!=null && directory.isDirectory()) {
			for (File file : directory.listFiles()) {
				if (file.isFile()) {
					if (file.getName().toLowerCase().endsWith(".xls")) {
						logger.info("Processing "+file.getAbsolutePath());											
						try {
							Exporter exporter = new RDFExporter(file, generateRootIRI(file));
							File outFile = getOutputFile(file);
							
							boolean export = !outFile.exists();
							export = export || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "The output file "+outFile.getAbsolutePath()+" already exists. Overwrite?");
							if (export) {
								logger.info("Exporting to "+outFile.getAbsolutePath());
								FileOutputStream stream = new FileOutputStream(outFile);							
								exporter.export(stream);
								stream.flush();
								stream.close();
							}
							else {
								logger.info("Skipping "+outFile.getAbsolutePath());
							}																																																									
						} catch (IOException e) {
							logger.error("Unable to process "+file.getAbsolutePath(),e);
						} catch (URISyntaxException e) {
							logger.error("Error creating the root URI for "+file.getAbsolutePath(),e);
						} catch (InvalidWorkbookFormatException e) {
							logger.error("The format of the workbook file is not supported",e);
						}
						
					}
				}
			}
			logger.info("Done");
		}		
	}
	
	private File browseForDirectory() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.showOpenDialog(null);
		File file = chooser.getSelectedFile();		
		
		return file;
	}
	
	private File getOutputFile(File inFile) {
		String outputFilename = inFile.getAbsolutePath().replace(".xls", ".rdf");
		return new File(outputFilename);
	}	
	
	/**
	 * Creates a URI based upon the filename, without the extension - which becomes rightfield:<filename>
	 * @param inFile
	 * @return rightfield:<filename>
	 * @throws URISyntaxException
	 */
	private IRI generateRootIRI(File inFile) throws URISyntaxException  {
		
		String name = inFile.getName().replace(".xls", "");
		
		URI uri = new URI("rightfield",name,null);
		
		logger.info("Using URI of "+uri.toString());
		return IRI.create(uri);
		
	}
}
