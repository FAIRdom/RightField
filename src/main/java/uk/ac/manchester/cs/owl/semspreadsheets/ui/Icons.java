/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.Icon;

import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class Icons {

    private static OWLClassIcon OWLCLASS_ICON = new OWLClassIcon();

    private static Icon OWLINDIVIDUAL_ICON = new OWLIndividualIcon();

    private static Icon OWLPROPERTY_ICON = new OWLPropertyIcon();

    private static Icon ANNOTATION_PROPERTY_ICON = new OWLAnnotationPropertyIcon();

    private static Icon DATATYPE_ICON = new OWLDatatypeIcon();

    public static Icon getOWLEntityIcon(WorkbookManager manager, OWLEntity entity) {
        if (entity.isOWLClass()) {
            return getOWLClassIcon(manager, entity.asOWLClass());
        }
        else if (entity.isOWLNamedIndividual()) {
            return getOWLNamedIndividualIcon(manager, entity.asOWLNamedIndividual());
        }
        else if (entity.isOWLObjectProperty()) {
            return getOWLObjectPropertyIcon(manager, entity.asOWLObjectProperty());
        }
        else if (entity.isOWLDataProperty()) {
            return getOWLDataPropertyIcon(manager, entity.asOWLDataProperty());
        }
        else if (entity.isOWLAnnotationProperty()) {
            return ANNOTATION_PROPERTY_ICON;
        }
        else if (entity.isOWLDatatype()) {
            return DATATYPE_ICON;
        }
        else {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
	public static Icon getOWLEntityIcon(WorkbookManager manager, EntityType entity) {
        if (entity.equals(EntityType.CLASS)) {
            return OWLCLASS_ICON;
        }
        else if (entity.equals(EntityType.NAMED_INDIVIDUAL)) {
            return OWLINDIVIDUAL_ICON;
        }
        else if (entity.equals(EntityType.OBJECT_PROPERTY)) {
            return OWLPROPERTY_ICON;
        }
        else if (entity.equals(EntityType.DATA_PROPERTY)) {
            return OWLPROPERTY_ICON;
        }
        else if (entity.equals(EntityType.ANNOTATION_PROPERTY)) {
            return ANNOTATION_PROPERTY_ICON;
        }
        else if (entity.equals(EntityType.DATATYPE)) {
            return DATATYPE_ICON;
        }
        else {
            return null;
        }
    }

    public static Icon getOWLClassIcon(WorkbookManager manager, OWLClass cls) {
        return OWLCLASS_ICON;
    }

    public static Icon getOWLObjectPropertyIcon(WorkbookManager manager, OWLObjectProperty prop) {
        return OWLPROPERTY_ICON;
    }

    public static Icon getOWLDataPropertyIcon(WorkbookManager manager, OWLDataProperty prop) {
        return OWLPROPERTY_ICON;
    }

    public static Icon getOWLNamedIndividualIcon(WorkbookManager manager, OWLNamedIndividual ind) {
        return OWLINDIVIDUAL_ICON;
    }


    private static class OWLClassIcon implements Icon {

        public final static int DIMENSION = 20;

        public final static Color COLOR = new Color(120, 140, 160);

        public final static Stroke STROKE = new BasicStroke(2.0f);

        private boolean satisfiable = true;

        /**
         * Draw the icon at the specified location.  Icon implementations
         * may use the Component argument to get properties useful for
         * painting, e.g. the foreground or background color.
         */
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color oldColor = g.getColor();
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(x, y);
            Stroke oldStroke = g2.getStroke();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (satisfiable) {
                g2.setColor(COLOR);
            }
            else {
                g2.setColor(Color.RED);
            }
            g2.setStroke(STROKE);
            g2.drawOval(4, 4, DIMENSION - 8, DIMENSION - 8);
            g2.setColor(oldColor);
            g2.setStroke(oldStroke);
            g2.translate(-x, -y);
        }       

        /**
         * Returns the icon's width.
         * @return an int specifying the fixed width of the icon.
         */
        public int getIconWidth() {
            return DIMENSION;
        }

        /**
         * Returns the icon's height.
         * @return an int specifying the fixed height of the icon.
         */
        public int getIconHeight() {
            return DIMENSION;
        }
    }

    private static class OWLDatatypeIcon implements Icon {

        public final static int DIMENSION = 20;

        public final static Color COLOR = new Color(120, 140, 160);

        public final static Stroke STROKE = new BasicStroke(2.0f);

        /**
         * Draw the icon at the specified location.  Icon implementations
         * may use the Component argument to get properties useful for
         * painting, e.g. the foreground or background color.
         */
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color oldColor = g.getColor();
            Graphics2D g2 = (Graphics2D) g;
            Stroke oldStroke = g2.getStroke();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR);
            g2.setStroke(STROKE);
            g2.drawOval(4, 4, DIMENSION - 8, DIMENSION - 8);
            g2.setColor(oldColor);
            g2.setStroke(oldStroke);
            g2.translate(-x, -y);
        }       

        /**
         * Returns the icon's width.
         * @return an int specifying the fixed width of the icon.
         */
        public int getIconWidth() {
            return DIMENSION;
        }

        /**
         * Returns the icon's height.
         * @return an int specifying the fixed height of the icon.
         */
        public int getIconHeight() {
            return DIMENSION;
        }
    }

    private static class OWLIndividualIcon implements Icon {

        public final static int DIMENSION = 20;

        public final static Color COLOR = new Color(120, 140, 160);

        /**
         * Draw the icon at the specified location.  Icon implementations
         * may use the Component argument to get properties useful for
         * painting, e.g. the foreground or background color.
         */
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color oldColor = g.getColor();
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR);
            g2.fillOval(6, 6, DIMENSION - 12, DIMENSION - 12);
            g2.setColor(oldColor);
            g2.translate(-x, -y);
        }

        /**
         * Returns the icon's width.
         * @return an int specifying the fixed width of the icon.
         */
        public int getIconWidth() {
            return DIMENSION;
        }

        /**
         * Returns the icon's height.
         * @return an int specifying the fixed height of the icon.
         */
        public int getIconHeight() {
            return DIMENSION;
        }
    }

    private static class OWLPropertyIcon implements Icon {

        public final static int DIMENSION = 20;

        public final static Color COLOR = new Color(120, 140, 160);

        public final static Stroke STROKE = new BasicStroke(2.0f);

        /**
         * Draw the icon at the specified location.  Icon implementations
         * may use the Component argument to get properties useful for
         * painting, e.g. the foreground or background color.
         */
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color oldColor = g.getColor();
            Graphics2D g2 = (Graphics2D) g;
            Stroke oldStroke = g2.getStroke();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR);
            g2.setStroke(STROKE);
            g2.drawRoundRect(4, 7, DIMENSION - 8, DIMENSION - 14, 2, 2);
            g2.setColor(oldColor);
            g2.setStroke(oldStroke);
            g2.translate(-x, -y);
        }

        /**
         * Returns the icon's width.
         * @return an int specifying the fixed width of the icon.
         */
        public int getIconWidth() {
            return DIMENSION;
        }

        /**
         * Returns the icon's height.
         * @return an int specifying the fixed height of the icon.
         */
        public int getIconHeight() {
            return DIMENSION;
        }
    }


    private static class OWLAnnotationPropertyIcon implements Icon {

        public final static int DIMENSION = 20;

        public final static Color COLOR = new Color(120, 140, 160);

        public final static Stroke STROKE = new BasicStroke(2.0f);

        /**
         * Draw the icon at the specified location.  Icon implementations
         * may use the Component argument to get properties useful for
         * painting, e.g. the foreground or background color.
         */
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color oldColor = g.getColor();
            Graphics2D g2 = (Graphics2D) g;
            Stroke oldStroke = g2.getStroke();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR);
            g2.setStroke(STROKE);
            g2.drawLine(5, DIMENSION / 2, DIMENSION - 5, DIMENSION / 2);
            g2.fillOval(3, DIMENSION / 2 - 2, 5, 5);
            g2.fillOval(DIMENSION - 5, DIMENSION / 2 - 2, 5, 5);
            g2.setColor(oldColor);
            g2.setStroke(oldStroke);
            g2.translate(-x, -y);
        }

        /**
         * Returns the icon's width.
         * @return an int specifying the fixed width of the icon.
         */
        public int getIconWidth() {
            return DIMENSION;
        }

        /**
         * Returns the icon's height.
         * @return an int specifying the fixed height of the icon.
         */
        public int getIconHeight() {
            return DIMENSION;
        }
    }
}
