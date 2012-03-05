package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;


//the component that goes into the tab
@SuppressWarnings("serial")
class ClassHierarchyTabComponent extends JPanel {
		private final ClassHierarchyTabbedPane pane;
		private final WorkbookManager workbookManager;
		private final OWLOntology ontology;
		private static Logger logger = Logger.getLogger(ClassHierarchyTabComponent.class);

		public ClassHierarchyTabComponent(final ClassHierarchyTabbedPane pane,WorkbookManager workbookManager,OWLOntology ontology) {
			super(new FlowLayout(FlowLayout.LEFT, 0, 0));
			this.pane=pane;
			this.workbookManager = workbookManager;
			this.ontology = ontology;	
			setOpaque(false);
			setText();
			setButton();
		}
		
		protected void removeOntology() {
			logger.debug("About to remove ontology:"+ontology.toString());
		}
		
		private void setText() {
			JLabel label = new JLabel() {
	            public String getText() {
	                int i = pane.indexOfTabComponent(ClassHierarchyTabComponent.this);	                
	                if (i != -1) {
	                    return pane.getTitleAt(i);
	                }
	                return null;
	            }
	        };
	        add(label);
		}
		
		private void setButton() {
			JButton button = new TabButton();
			add(button);
		}
		
		private class TabButton extends JButton implements ActionListener {
	        public TabButton() {
	        	super("x");
	            int size = 17;
	            setPreferredSize(new Dimension(size, size));
	            setToolTipText("close this tab");
	            //Make the button looks the same for all Laf's
	            setUI(new BasicButtonUI());
	            //Make it transparent
	            setContentAreaFilled(false);
	            //No need to be focusable
	            setFocusable(false);
	            setBorder(BorderFactory.createEtchedBorder());
	            setBorderPainted(false);
	            //Making nice rollover effect
	            //we use the same listener for all buttons
	            addMouseListener(buttonMouseListener);
	            setRolloverEnabled(true);
	            //Close the proper tab by clicking the button
	            addActionListener(this);	            
	        }
	 
	        public void actionPerformed(ActionEvent e) {
	        	ClassHierarchyTabComponent.this.removeOntology();
	        }
	 
	        //we don't want to update UI for this button
	        public void updateUI() {
	        }		       
		}
		
		private final MouseListener buttonMouseListener = new MouseAdapter() {
	        public void mouseEntered(MouseEvent e) {
	            Component component = e.getComponent();
	            if (component instanceof AbstractButton) {
	                AbstractButton button = (AbstractButton) component;
	                button.setBorderPainted(true);	                
	            }
	        }
	 
	        public void mouseExited(MouseEvent e) {
	            Component component = e.getComponent();
	            if (component instanceof AbstractButton) {
	                AbstractButton button = (AbstractButton) component;
	                button.setBorderPainted(false);	                
	            }
	        }
	    };
	}
