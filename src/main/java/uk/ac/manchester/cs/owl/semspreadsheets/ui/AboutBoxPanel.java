/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.org.rightfield.RightField;


@SuppressWarnings("serial")
public class AboutBoxPanel extends JPanel {
	private static final String WEBSITE = "http://www.rightfield.org.uk";
	private static final String COPYRIGHT = "(c) University of Manchester 2009-2014";
	private static Logger logger = LogManager.getLogger(AboutBoxPanel.class);
	
	public AboutBoxPanel() {
		super(true);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
						
		add(rightFieldLogo());
		
		JLabel version = new JLabel("Version "+RightField.getApplicationVersion());
		version.setAlignmentX(CENTER_ALIGNMENT);
		version.setFont(version.getFont().deriveFont(14f));
		add(new JLabel(" "));
		add(version);
		
		add(new JLabel(" "));
		add(new JSeparator());
		add(new JLabel(" "));
		
		JLabel copyright = new JLabel(COPYRIGHT);
		copyright.setFont(copyright.getFont().deriveFont(Font.ITALIC));
		copyright.setAlignmentX(CENTER_ALIGNMENT);
		add(copyright);
		
		JLabel link = new JLabel("http://www.rightfield.org.uk");
		link.setAlignmentX(CENTER_ALIGNMENT);
		
		link.setForeground(Color.BLUE);
		link.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent event) {
				try {
					Desktop.getDesktop().browse(URI.create(WEBSITE));
				} catch (IOException e) {
					logger.error("There was a problem opening the URI: " + WEBSITE, e);
					
				}
			}
			
		});
		
		add(new JLabel(" "));
		add(link);
				
	}
	
	private JLabel rightFieldLogo() {
		JLabel result;
		URL resource = AboutBoxPanel.class.getResource("/rightfield-about-logo.png");
		Icon logo = new ImageIcon(resource);  
		result = new JLabel(logo);
		result.setAlignmentX(CENTER_ALIGNMENT);
		
		return result;
	}
	
		
	
	public static void main(String[] args) {
		AboutBoxPanel panel = new AboutBoxPanel();
		JOptionPane op = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.DEFAULT_OPTION);
		
		op.setOptions(new Object[]{"OK"});
		JDialog dialog = op.createDialog("About RightField");
		dialog.setVisible(true);
	}
}


