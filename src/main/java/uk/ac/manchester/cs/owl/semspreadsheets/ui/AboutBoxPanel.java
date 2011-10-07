package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;


@SuppressWarnings("serial")
public class AboutBoxPanel extends JPanel {
	private static final String WEBSITE = "http://www.rightfield.org.uk";
	private static Logger logger = Logger.getLogger(AboutBoxPanel.class);
	
	public AboutBoxPanel() {
		super(true);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
		JLabel title = new JLabel("RightField");
		title.setFont(title.getFont().deriveFont(40f));
		title.setAlignmentX(CENTER_ALIGNMENT);
		add(rightFieldLogo());
		
		JLabel version = new JLabel("Version "+applicationVersion());
		version.setAlignmentX(CENTER_ALIGNMENT);
		version.setFont(version.getFont().deriveFont(14f));
		add(new JLabel(" "));
		add(version);
		
		add(new JLabel(" "));
		add(new JSeparator());
		add(new JLabel(" "));
		
		JLabel copyright = new JLabel("(c) University of Manchester 2009-2011");
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
	
	private String applicationVersion() {
		String v = AboutBoxPanel.class.getPackage().getImplementationVersion();
		if (v==null) {
			v="Unknown";
		}
		return v;
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


