package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
/*
 * Copyright (C) 2010, University of Manchester
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
 * Date: 03-Feb-2010
 */
public class ProgressDialog extends JDialog {

    private WorkbookManager workbookManager;

    private JLabel titleLabel = new JLabel("                                              ");

    private JProgressBar progressBar;

    private String title;

    private AbstractAction cancelAction;

    private Task task;

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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelAction = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent e) {
                setEnabled(false);
                task.cancelTask();
            }
        };
        buttonPanel.add(new JButton(cancelAction));
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

    private String getTitleText() {
        if(progressBar.isIndeterminate()) {
            return title;
        }
        else {
            return title + " - " + ((progressBar.getValue() * 100) / progressBar.getMaximum());
        }
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

    public void setTask(Task task) {
        this.task = task;
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

