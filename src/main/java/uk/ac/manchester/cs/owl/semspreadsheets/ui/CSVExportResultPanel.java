package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class CSVExportResultPanel extends JPanel {
	
	private final static Logger logger = Logger.getLogger(CSVExportResultPanel.class);
	
	private JTextArea csvTextArea;

	private final WorkbookFrame workbookFrame;

	private final JDialog parent;

	//FIXME: this is almost identical to RDFExportResultPanel - maybe they could be generalised and consolidated
	public CSVExportResultPanel(JDialog parent,WorkbookFrame workbookFrame,final String csv) {
		super();
		this.parent = parent;
		this.workbookFrame = workbookFrame;
		setPreferredSize(new Dimension(700,400));
		
		csvTextArea = new JTextArea();
		csvTextArea.setEditable(false);
		csvTextArea.setText(csv);						
		csvTextArea.setCaretPosition(0);		
		JScrollPane scrollPane = new JScrollPane(csvTextArea);
		
		setLayout(new BorderLayout());		
		add(scrollPane,BorderLayout.CENTER);		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton closeButton = new JButton(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				close();				
			}
		});
		
		JButton saveButton = new JButton(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				save(csv);			
			}
		});
		saveButton.setText("Save");
		closeButton.setText("Close");
		buttonPanel.add(closeButton);
		buttonPanel.add(saveButton);
		add(buttonPanel,BorderLayout.SOUTH);				
	}
	
	private WorkbookFrame getWorkbookFrame() {
		return workbookFrame;
	}
	
	private void close() {
		parent.dispose();
	}
	
	private void save(String csv) {
		//FIXME: workbookFrame is not a suitable place for these file based methods, but have already been refactored
		//to another class in the xlsx branch.
		File file = getWorkbookFrame().browseForFile("Save CSV as", FileDialog.SAVE,
				"CSV File",new String [] {"csv"});
		if (file!=null) {
			file = getWorkbookFrame().checkForDefaultExtension(file, ".csv");
			PrintWriter writer;
			try {
				writer = new PrintWriter(file);
				writer.write(csv);
				writer.close();
			} catch (FileNotFoundException e) {
				ErrorHandler.getErrorHandler().handleError(e);
			}
		}				
	}
	
	public static void showDialog(WorkbookFrame frame,String csv) {
		logger.debug("About to show dialog for CSV export");
		logger.debug(csv);
		JDialog dialog = new JDialog(frame,"Generated CSV");
		
		CSVExportResultPanel panel = new CSVExportResultPanel(dialog,frame,csv);		
		dialog.add(panel);
		dialog.pack();
		dialog.setVisible(true);
	}

}
