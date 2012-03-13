package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.task.Task;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
@SuppressWarnings("serial")
public class ProgressDialog extends JDialog {    

    private JLabel titleLabel = new JLabel("                                              ");

    private JProgressBar progressBar;

    private String title;

    private AbstractAction cancelAction;

    @SuppressWarnings("rawtypes")
	private Task task;

	private JButton cancelButton;

	private JPanel buttonPanel;

    public ProgressDialog(WorkbookFrame workbookFrame) throws HeadlessException {
        super(workbookFrame != null ? workbookFrame : null, "Task in progress", true);
        getAccessibleContext().setAccessibleName("Task Progress");
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        JPanel progressPanel = new JPanel(new BorderLayout(10, 10));
        progressBar = new JProgressBar();
        progressBar.getAccessibleContext().setAccessibleName("Task progress");
        progressPanel.add(titleLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.SOUTH);
        progressPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        progressPanel.setPreferredSize(new Dimension(500, 80));
        JPanel holderPanel = new JPanel(new BorderLayout());
        holderPanel.add(progressPanel, BorderLayout.NORTH);
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelAction = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent e) {
                setEnabled(false);
                task.cancelTask();
            }
        };
        cancelButton = new JButton(cancelAction);
        buttonPanel.add(cancelButton);
        holderPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(holderPanel);
        progressBar.setIndeterminate(true);
        pack();
    }

    public void setTitle(String title) {
        this.title = title;
        super.setTitle(title);
        getAccessibleContext().setAccessibleName(title);
        getAccessibleContext().setAccessibleDescription(title);
        titleLabel.setText(title);
        pack();
    }    

    public void setLength(int length) {
        progressBar.setMaximum(length);
    }

    public void setProgress(int progress) {
        if(progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(false);
        }
        progressBar.setValue(progress);
        setTitle(title);
    }


    public void setProgressIndeterminate(boolean indeterminate) {
        progressBar.setIndeterminate(indeterminate);
    }

    @SuppressWarnings("rawtypes")
	public void setTask(Task task) {
        this.task = task;
        cancelButton.setVisible(task.isCancelSupported());
        //this is to hide the buttonPanel if it includes no other buttons than the cancel button, and cancel is unsupported.
        if (buttonPanel.getComponentCount()==1 && buttonPanel.getComponents()[0]==cancelButton) {
        	buttonPanel.setVisible(task.isCancelSupported());
        }        
        else {
        	buttonPanel.setVisible(true);
        }
    }

    public void setVisible(boolean b) {
        if(b) {
            cancelAction.setEnabled(true);
            Window w = getOwner();
            Dimension size = getSize();
            setLocation(w.getLocation().x + ((w.getSize().width - size.width) / 2),
                    w.getLocation().y + ((w.getSize().height - size.height) / 2));
        }
        super.setVisible(b);
    }
}

