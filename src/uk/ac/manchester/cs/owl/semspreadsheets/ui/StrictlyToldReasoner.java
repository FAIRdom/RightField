package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import org.semanticweb.owlapi.inference.OWLReasoner;
import org.semanticweb.owlapi.inference.OWLReasonerException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.util.*;
/*
 * Copyright (C) 2009, University of Manchester
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
 * Date: 18-Sep-2009
 */
public class StrictlyToldReasoner implements OWLReasoner {

    private OWLOntologyManager manager;

    private Set<OWLOntology> ontologies = new HashSet<OWLOntology>();

    private Set<OWLClass> roots = new HashSet<OWLClass>();

    private Set<OWLObjectProperty> objectPropertyRoots = new HashSet<OWLObjectProperty>();

    private Set<OWLDataProperty> dataPropertyRoots = new HashSet<OWLDataProperty>();


    public StrictlyToldReasoner(OWLOntologyManager manager, Set<OWLOntology> ontologies) {
        this.manager = manager;
        this.ontologies.addAll(ontologies);
        computeRoots();

    }

    private void computeRoots() {
        roots.clear();
        objectPropertyRoots.clear();
        dataPropertyRoots.clear();
        for (OWLOntology ont : ontologies) {
            for (OWLClass cls : ont.getReferencedClasses()) {
                if (ont.getSubClassAxiomsForSubClass(cls).isEmpty()) {
                    if (!cls.isOWLThing()) {
                        roots.add(cls);
                    }
                }
            }
            for (OWLObjectProperty prop : ont.getReferencedObjectProperties()) {
                if (prop.getSuperProperties(ontologies).isEmpty()) {
                    objectPropertyRoots.add(prop);
                }
            }
            for (OWLDataProperty prop : ont.getReferencedDataProperties()) {
                if (prop.getSuperProperties(ontologies).isEmpty()) {
                    dataPropertyRoots.add(prop);
                }
            }
        }
    }


    public boolean isConsistent(OWLOntology ontology) throws OWLReasonerException {
        return true;
    }


    public void loadOntologies(Set<OWLOntology> ontologies) throws OWLReasonerException {
        this.ontologies.addAll(ontologies);
        classify();
    }


    public boolean isClassified() throws OWLReasonerException {
        return true;
    }


    public void classify() throws OWLReasonerException {
        computeRoots();
    }


    public boolean isRealised() throws OWLReasonerException {
        return true;
    }


    public void realise() throws OWLReasonerException {
    }


    public boolean isDefined(OWLClass cls) throws OWLReasonerException {
        return true;
    }


    public boolean isDefined(OWLObjectProperty prop) throws OWLReasonerException {
        return true;
    }


    public boolean isDefined(OWLDataProperty prop) throws OWLReasonerException {
        return true;
    }


    public boolean isDefined(OWLIndividual ind) throws OWLReasonerException {
        return true;
    }


    public Set<OWLOntology> getLoadedOntologies() {
        return new HashSet<OWLOntology>(ontologies);
    }


    public void unloadOntologies(Set<OWLOntology> ontologies) throws OWLReasonerException {
        this.ontologies.removeAll(ontologies);
        classify();
    }


    public void clearOntologies() throws OWLReasonerException {
        ontologies.clear();
        classify();
    }


    public void dispose() throws OWLReasonerException {
    }


    public boolean isAsserted(OWLAxiom ax) {
        for (OWLOntology ont : ontologies) {
            if (ont.containsAxiom(ax)) {
                return true;
            }
        }
        return false;
    }


    public Set<Set<OWLClass>> getEquivalenceClasses(Set<OWLClassExpression> clses) {
        Set<Set<OWLClass>> result = new HashSet<Set<OWLClass>>();
        for (OWLClassExpression cls : clses) {
            if (!cls.isAnonymous()) {
                Set<OWLClass> node = new TreeSet<OWLClass>();
                node.add(cls.asOWLClass());
                for (OWLClassExpression eq : cls.asOWLClass().getEquivalentClasses(ontologies)) {
                    if (!eq.isAnonymous()) {
                        node.add(eq.asOWLClass());
                    }
                }
                result.add(node);
            }
        }
        return result;
    }


    public OWLDataFactory getDataFactory() {
        return manager.getOWLDataFactory();
    }


    public boolean isSubClassOf(OWLClassExpression clsC, OWLClassExpression clsD) throws OWLReasonerException {
        OWLAxiom ax = manager.getOWLDataFactory().getOWLSubClassOfAxiom(clsC, clsD);
        return isAsserted(ax);
    }


    public boolean isEquivalentClass(OWLClassExpression clsC, OWLClassExpression clsD) throws OWLReasonerException {
        return isAsserted(manager.getOWLDataFactory().getOWLEquivalentClassesAxiom(clsC, clsD));
    }


    public Set<Set<OWLClass>> getSuperClasses(OWLClassExpression clsC) throws OWLReasonerException {
        if (clsC.isAnonymous()) {
            return Collections.emptySet();
        }
        return getEquivalenceClasses(clsC.asOWLClass().getSuperClasses(ontologies));
    }


    public Set<Set<OWLClass>> getAncestorClasses(OWLClassExpression clsC) throws OWLReasonerException {

        return Collections.emptySet();
    }


    public Set<Set<OWLClass>> getSubClasses(OWLClassExpression clsC) throws OWLReasonerException {
        if (clsC.isAnonymous()) {
            return Collections.emptySet();
        }
        Set<OWLClassExpression> subs = clsC.asOWLClass().getSubClasses(ontologies);
        if (clsC.isOWLThing()) {
            subs.addAll(roots);
        }

        return getEquivalenceClasses(subs);
    }


    public Set<Set<OWLClass>> getDescendantClasses(OWLClassExpression clsC) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLClass> getEquivalentClasses(OWLClassExpression clsC) throws OWLReasonerException {
        Set<OWLClass> result = new HashSet<OWLClass>();
        for (OWLClassExpression ce : clsC.asOWLClass().getEquivalentClasses(ontologies)) {
            if (!ce.isAnonymous()) {
                result.add(ce.asOWLClass());
            }
        }
        return result;
    }


    public Set<OWLClass> getUnsatisfiableClasses() throws OWLReasonerException {
        Set<OWLClass> result = new HashSet<OWLClass>();
        for (OWLClassExpression ce : getDataFactory().getOWLNothing().getSubClasses(ontologies)) {
            if (!ce.isAnonymous()) {
                result.add(ce.asOWLClass());
            }
        }
        return result;
    }


    public boolean isSatisfiable(OWLClassExpression classExpression) throws OWLReasonerException {
        return true;
    }


    public Set<Set<OWLClass>> getTypes(OWLNamedIndividual individual, boolean direct) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLNamedIndividual> getIndividuals(OWLClassExpression clsC, boolean direct) throws OWLReasonerException {
        if (clsC.isAnonymous()) {
            return Collections.emptySet();
        }
        else {
            Set<OWLNamedIndividual> result = new HashSet<OWLNamedIndividual>();
            addIndividuals(clsC.asOWLClass(), new HashSet<OWLClass>(), result, direct);
            return result;
        }
    }

    private void addIndividuals(OWLClass cls, Set<OWLClass> processed, Set<OWLNamedIndividual> result, boolean direct) {
        if(processed.contains(cls)) {
            return;
        }
        processed.add(cls);
        for (OWLIndividual ind : cls.getIndividuals(ontologies)) {
            if (!ind.isAnonymous()) {
                result.add(ind.asNamedIndividual());
            }
        }
        if (!direct) {
            for (OWLClassExpression ce : cls.getSubClasses(ontologies)) {
                if(!ce.isAnonymous()) {
                    addIndividuals(ce.asOWLClass(), processed, result, direct);
                }
            }
        }

    }


    public Map<OWLObjectProperty, Set<OWLNamedIndividual>> getObjectPropertyRelationships(OWLNamedIndividual individual) throws OWLReasonerException {
        return Collections.emptyMap();
    }


    public Map<OWLDataProperty, Set<OWLLiteral>> getDataPropertyRelationships(OWLNamedIndividual individual) throws OWLReasonerException {
        return Collections.emptyMap();
    }


    public boolean hasType(OWLNamedIndividual individual, OWLClassExpression type, boolean direct) throws OWLReasonerException {
        return false;
    }


    public boolean hasObjectPropertyRelationship(OWLNamedIndividual subject, OWLObjectPropertyExpression property, OWLNamedIndividual object) throws OWLReasonerException {
        return false;
    }


    public boolean hasDataPropertyRelationship(OWLNamedIndividual subject, OWLDataPropertyExpression property, OWLLiteral object) throws OWLReasonerException {
        return false;
    }


    public Set<OWLNamedIndividual> getRelatedIndividuals(OWLNamedIndividual subject, OWLObjectPropertyExpression property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLLiteral> getRelatedValues(OWLNamedIndividual subject, OWLDataPropertyExpression property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLObjectProperty>> getSuperProperties(OWLObjectProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLObjectProperty>> getSubProperties(OWLObjectProperty property) throws OWLReasonerException {
        Set<Set<OWLObjectProperty>> props = new HashSet<Set<OWLObjectProperty>>();
        if (property.getIRI().equals(OWLRDFVocabulary.OWL_TOP_OBJECT_PROPERTY.getURI())) {
            for (OWLObjectProperty rootProp : objectPropertyRoots) {
                props.add(Collections.singleton(rootProp));
            }
        }
        for (OWLObjectPropertyExpression sub : property.getSubProperties(ontologies)) {
            if (!sub.isAnonymous()) {
                props.add(Collections.singleton(sub.asOWLObjectProperty()));
            }
        }
        return props;
    }


    public Set<Set<OWLObjectProperty>> getAncestorProperties(OWLObjectProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLObjectProperty>> getDescendantProperties(OWLObjectProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLObjectProperty>> getInverseProperties(OWLObjectProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLObjectProperty> getEquivalentProperties(OWLObjectProperty property) throws OWLReasonerException {
        Set<OWLObjectProperty> props = new HashSet<OWLObjectProperty>();
        for (OWLObjectPropertyExpression equiv : property.getEquivalentProperties(ontologies)) {
            if (!equiv.isAnonymous()) {
                props.add(equiv.asOWLObjectProperty());
            }
        }
        return props;
    }


    public Set<Set<OWLClassExpression>> getDomains(OWLObjectProperty property) throws OWLReasonerException {
        Set<Set<OWLClassExpression>> result = new HashSet<Set<OWLClassExpression>>();
        for (OWLClassExpression domain : property.getDomains(ontologies)) {
            result.add(Collections.singleton(domain));
        }
        return result;
    }


    public Set<OWLClassExpression> getRanges(OWLObjectProperty property) throws OWLReasonerException {
        return property.getRanges(ontologies);
    }


    public boolean isFunctional(OWLObjectProperty property) throws OWLReasonerException {
        return isAsserted(getDataFactory().getOWLFunctionalObjectPropertyAxiom(property));
    }


    public boolean isInverseFunctional(OWLObjectProperty property) throws OWLReasonerException {
        return property.isInverseFunctional(ontologies);
    }


    public boolean isSymmetric(OWLObjectProperty property) throws OWLReasonerException {
        return property.isSymmetric(ontologies);
    }


    public boolean isTransitive(OWLObjectProperty property) throws OWLReasonerException {
        return property.isTransitive(ontologies);
    }


    public boolean isReflexive(OWLObjectProperty property) throws OWLReasonerException {
        for (OWLOntology ont : ontologies) {
            if (!ont.getReflexiveObjectPropertyAxioms(property).isEmpty()) {
                return true;
            }
        }
        return false;
    }


    public boolean isIrreflexive(OWLObjectProperty property) throws OWLReasonerException {
        for (OWLOntology ont : ontologies) {
            if (!ont.getIrreflexiveObjectPropertyAxioms(property).isEmpty()) {
                return true;
            }
        }
        return false;
    }


    public boolean isAsymmetric(OWLObjectProperty property) throws OWLReasonerException {
        return property.isAsymmetric(ontologies);
    }


    public Set<Set<OWLDataProperty>> getSuperProperties(OWLDataProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLDataProperty>> getSubProperties(OWLDataProperty property) throws OWLReasonerException {
        Set<Set<OWLDataProperty>> props = new HashSet<Set<OWLDataProperty>>();
        if (property.getIRI().equals(OWLRDFVocabulary.OWL_TOP_DATA_PROPERTY.getIRI())) {
            for (OWLDataProperty rootProp : dataPropertyRoots) {
                props.add(Collections.singleton(rootProp));
            }
        }
        for (OWLDataPropertyExpression sub : property.getSubProperties(ontologies)) {
            if (!sub.isAnonymous()) {
                props.add(Collections.singleton(sub.asOWLDataProperty()));
            }
        }
        return props;
    }


    public Set<Set<OWLDataProperty>> getAncestorProperties(OWLDataProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLDataProperty>> getDescendantProperties(OWLDataProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLDataProperty> getEquivalentProperties(OWLDataProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<Set<OWLClassExpression>> getDomains(OWLDataProperty property) throws OWLReasonerException {
        return Collections.emptySet();
    }


    public Set<OWLDataRange> getRanges(OWLDataProperty property) throws OWLReasonerException {
        return property.getRanges(ontologies);
    }


    public boolean isFunctional(OWLDataProperty property) throws OWLReasonerException {
        return false;
    }
}
