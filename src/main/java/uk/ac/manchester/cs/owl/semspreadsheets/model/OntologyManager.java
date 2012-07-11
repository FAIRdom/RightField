/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.bioportal.BioPortalRepository;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.ErrorHandler;

/**
 * Encapsulates everything to do with Ontologies, and wraps the {@link OWLOntologyManager} and {@link OntologyTermValidationManager}
 * 
 * @author Stuart Owen
 *
 */
public class OntologyManager {

	private static final Logger logger = Logger.getLogger(OntologyManager.class);
	
	private final OWLOntologyManager owlManager;
	private OWLReasoner reasoner;
	public Map<OWLOntologyID,OWLReasoner> ontologyReasoners = new HashMap<OWLOntologyID, OWLReasoner>();
	private OWLOntologyLoaderConfiguration ontologyLoaderConfiguration = new OWLOntologyLoaderConfiguration();
	private BidirectionalShortFormProviderAdapter shortFormProvider;
	private OntologyTermValidationManager ontologyTermValidationManager;
	private Set<OntologyManagerListener> ontologyManagerListeners = new HashSet<OntologyManagerListener>();
	private OWLPropertyHandler propertyHandler;	

	private Set<OWLOntology> loadedOntologies = new HashSet<OWLOntology>();

	public OntologyManager(WorkbookManager workbookManager) {
		this.owlManager = OWLManager.createOWLOntologyManager();;	
		ontologyLoaderConfiguration = ontologyLoaderConfiguration.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);		
		shortFormProvider = new BidirectionalShortFormProviderAdapter(new SimpleShortFormProvider());
		ontologyTermValidationManager = new OntologyTermValidationManager(workbookManager);
		propertyHandler = new OWLPropertyHandler(owlManager);
	}
	
	public void addListener(OntologyTermValidationListener listener) {
		getOntologyTermValidationManager().addListener(listener);
	}
	
	public void addListener(OntologyManagerListener listener) {
		ontologyManagerListeners.add(listener);
	}
	
	public void clearOntologyTermValidation(Range range) {
    	getOntologyTermValidationManager().clearValidation(range);
    }
	
	public void clearOntologyTermValidations() {
    	getOntologyTermValidationManager().clearValidations();
    }
	
	private void fireOntologiesChanged() {
		for (OntologyManagerListener listener : getCopyOfListeners()) {
			listener.ontologiesChanged();
		}
	}

	private void fireOntologySelected(OWLOntology ontology) {
		for (OntologyManagerListener listener : getCopyOfListeners()) {
			listener.ontologySelected(ontology);
		}
	}
	
	public Collection<OntologyTermValidation> getContainingOntologyTermValidations(Range range) {
        return getOntologyTermValidationManager().getContainingValidations(range);
    }   
	
	private List<OntologyManagerListener> getCopyOfListeners() {
        return new ArrayList<OntologyManagerListener>(ontologyManagerListeners);
    }
	
	public OWLDataFactory getDataFactory() {
        return getOWLOntologyManager().getOWLDataFactory();
    }
	
	public Collection<OWLEntity> getEntitiesForShortForm(String shortForm) {
        Set<OWLEntity> result = new HashSet<OWLEntity>();
        for(String s : shortFormProvider.getShortForms()) {
            if(s.toLowerCase().contains(shortForm.toLowerCase())) {
                result.addAll(shortFormProvider.getEntities(s));
            }
        }
        return result;
    }
	
	public Collection<OntologyTermValidation> getIntersectingOntologyTermValidations(Range range) {
        return getOntologyTermValidationManager().getIntersectingValidations(range);
    }
	
	/**
	 * 
	 * @return a Set containing only the ontologies that have been specifically opened, and doesn't include the imported ontologies
	 */
	public Set<OWLOntology> getLoadedOntologies() {        
		return new HashSet<OWLOntology>(loadedOntologies);		
	}
	 
	/**
	 * 
	 * @return a Set of all ontologies that have been opened, included the imported ontologies
	 */
	public Set<OWLOntology> getAllOntologies() {
		return getOWLOntologyManager().getOntologies(); 
	}
    
    public Collection<IRI> getOntologyIRIs() {
    	return getOntologyTermValidationManager().getOntologyIRIs();
    }
    
    /**
     * Determines whether the ontology is used in the spreadsheet (i.e. stored as an OntologyTermValidation). Either directly, or through one of its imports.
     * @param ontology
     * @return whether the ontology is used.
     */
    public boolean isOntologyInUse(OWLOntology ontology) {
    	Set<IRI> ontologyIRIs = new HashSet<IRI>();
    	
    	ontologyIRIs.add(ontology.getOntologyID().getOntologyIRI());
    	for (OWLOntology o : getOWLOntologyManager().getImports(ontology)) {
    		ontologyIRIs.add(o.getOntologyID().getOntologyIRI());
    	}
    	
    	boolean used = false;
    	for (IRI iri : getOntologyIRIs()) {
    		if (ontologyIRIs.contains(iri)) {
    			used=true;
    			break;
    		}
    	}
    	    	
		return used;			
    }
    
    public OntologyTermValidationManager getOntologyTermValidationManager() {
        return ontologyTermValidationManager;
    }
    
    public Collection<OntologyTermValidation> getOntologyTermValidations() {
        return getOntologyTermValidationManager().getValidations();
    }
            
    
    public OWLOntologyManager getOWLOntologyManager() {
		return owlManager;
	}

	public String getRendering(OWLObject object) {
        if (object instanceof OWLEntity) {
            return shortFormProvider.getShortForm((OWLEntity) object);
        }
        else {
            return object.toString();
        }
    }
	
	/**
     * Returns a StructuralReasoner that works over all loaded ontologies
     * @see #getStructuralReasoner(OWLOntology) for reasoners over individual ontologies
     * @return
     */
    public OWLReasoner getStructuralReasoner() {
        if (reasoner == null) {
            updateStructuralReasoner();
        }        
        return reasoner;
    }
	
	/**
     * returns (and setsup if necessary) a StucturalReasoner that works over the given ontology only
     * @see #getStructuralReasoner()
     * @param ontology
     * @return
     */
    public OWLReasoner getStructuralReasoner(OWLOntology ontology) {
    	OWLReasoner reasoner = null;
    	synchronized (ontologyReasoners) {
    		reasoner = ontologyReasoners.get(ontology.getOntologyID());
    		if (reasoner==null) {
    			reasoner = updateStructuralReasoner(ontology);
    		}
		}
    	return reasoner;
    }       
    
    public void loadEmbeddedTermOntologies() {
        
        OWLOntologyIRIMapper mapper = new OntologyTermValdiationManagerMapper(ontologyTermValidationManager);
        owlManager.addIRIMapper(mapper);                
        for(IRI iri : getOntologyTermValidationManager().getOntologyIRIs()) {        	
            if(!owlManager.contains(iri)) {
                try {
                	loadOntology(iri);
				} catch (OWLOntologyCreationException e) {
					logger.error("Error loading ontology for embedded term.",e);
				}
            }
        }        
        owlManager.removeIRIMapper(mapper);        
        setLabelRendering(true);
        fireOntologiesChanged();
    }
    
    public OWLOntology loadOntology(URI physicalURI) throws OWLOntologyCreationException {
    	return loadOntology(IRI.create(physicalURI));
    }
    
    public OWLOntology loadOntology(IRI physicalIRI) throws OWLOntologyCreationException {
    	
    	OWLOntologyID newID = null;
    	IRI logIRI = null;
    	
        logger.info("Loading: " + physicalIRI);
        //See if an ontology with such ID had been loaded. If yes, unload it
        unloadOntology(physicalIRI);
                
        OWLOntologyDocumentSource source = new IRIDocumentSource(BioPortalRepository.handleBioPortalAPIKey(physicalIRI));
                
        OWLOntology ontology = processOntologyDocumentSourceWithTimeout(source, 30);        
    	
    	logIRI = ontology.getOntologyID().getOntologyIRI();
    	//Create a new ID and use the physical IRI as a version ID        
        newID = new OWLOntologyID(logIRI,physicalIRI);        
        getOWLOntologyManager().applyChange(new SetOntologyID(ontology, newID));              
        
        
        updateStructuralReasoner();
        updateStructuralReasoner(ontology);
        
        loadedOntologies.add(ontology);
        
        setLabelRendering(true);
        fireOntologiesChanged();        
        
        return ontology;
    }
    
    /**
     * Uses a Future to allow the opening of the ontology source to timeout (since the owlAPI doesn't always seem to timeout for invalid URI's such
     * @param source - the OWLOntologyDocumentSource containing the IRI of the ontology to be opened
     * @param timeout - time in SECONDS
     * @return the OWLOntology
     * @throws OWLOntologyCreationException
     */
    private OWLOntology processOntologyDocumentSourceWithTimeout(final OWLOntologyDocumentSource source,int timeout) throws OWLOntologyCreationException {
    	
		Callable<OWLOntology> callable = new Callable<OWLOntology>() {
			@Override
			public OWLOntology call() throws Exception {
				return owlManager.loadOntologyFromOntologyDocument(source,
						ontologyLoaderConfiguration);
			}
		};

		OWLOntology ontology;

		ExecutorService executor = Executors.newCachedThreadPool();
		Future<OWLOntology> future = executor.submit(callable);
		try {
			ontology = future.get(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("There was an error trying to open the ontology "
					+ source.getDocumentIRI().toString(), e);
			throw new OWLOntologyCreationException(
					"There was an error trying to open the ontology from "
							+ source.getDocumentIRI().toString() + " - "
							+ e.getMessage());
		} catch (ExecutionException e) {
			if (e.getCause() instanceof OWLOntologyCreationException) {
				throw (OWLOntologyCreationException) e.getCause();
			} else {
				throw new OWLOntologyCreationException(
						"There was an error trying to open the ontology from "
								+ source.getDocumentIRI().toString() + " - "
								+ e.getMessage());
			}
		} catch (TimeoutException e) {
			throw new OWLOntologyCreationException(
					"There was a timeout whilst fetching the ontology from "
							+ source.getDocumentIRI().toString());
		}

		return ontology;  	
    }
    
    public void remoteOntologyTermValidations(Range range) {
    	getOntologyTermValidationManager().removeValidations(range);
    }
    
    public void remoteOntologyTermValidations(Sheet sheet) {
    	getOntologyTermValidationManager().removeValidations(sheet);
    }

    public void removeListener(OntologyTermValidationListener listener) {
		getOntologyTermValidationManager().removeListener(listener);
	}

    public void removeListener(OntologyManagerListener listener) {
		ontologyManagerListeners.remove(listener);
	}
    
    public void removeOntology(OWLOntology ontology) {
    	getOWLOntologyManager().removeOntology(ontology);
    	loadedOntologies.remove(ontology);
    	fireOntologiesChanged();
    }
    
    public void setLabelRendering(boolean b) {
        if(b) {
            IRI iri = OWLRDFVocabulary.RDFS_LABEL.getIRI();
            OWLAnnotationProperty prop = owlManager.getOWLDataFactory().getOWLAnnotationProperty(iri);
            List<OWLAnnotationProperty> props = new ArrayList<OWLAnnotationProperty>();
            props.add(prop);
            ShortFormProvider provider = new AnnotationValueShortFormProvider(props, new HashMap<OWLAnnotationProperty, List<String>>(), owlManager);
            shortFormProvider.dispose();
            shortFormProvider = new BidirectionalShortFormProviderAdapter(owlManager.getOntologies(), provider);
            final Set<OWLEntity> entities = new HashSet<OWLEntity>();
            for(OWLOntology ont : owlManager.getOntologies()) {
                entities.addAll(ont.getSignature());
            }
            shortFormProvider.rebuild(new OWLEntitySetProvider<OWLEntity>() {
                public Set<OWLEntity> getEntities() {
                    return entities;
                }
            });
        }
        else {
            ShortFormProvider provider = new SimpleShortFormProvider();
            shortFormProvider = new BidirectionalShortFormProviderAdapter(owlManager.getOntologies(), provider);
        }
    }

    public void setOntologyTermValidation(Range rangeToApply,
			ValidationType type, IRI entityIRI, OWLPropertyItem property) {
		getOntologyTermValidationManager().setValidation(rangeToApply, type, entityIRI, property);		
	}

    public void unloadOntology(IRI physicalIRI) {    	
    	OWLOntology loaded = null;
    	
    	for (OWLOntology ontology : getAllOntologies()) {    		
    		if (physicalIRI.equals(ontology.getOntologyID().getVersionIRI())) {    			
    			loaded = ontology;
    			break;
    		}
    	}    	
    	if (loaded != null) getOWLOntologyManager().removeOntology(loaded);
    }
    
    private OWLReasoner updateStructuralReasoner() {    	
        try {        	
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();            
            OWLOntology root = man.createOntology(IRI.create("owlapi:reasoner"), getAllOntologies());
            reasoner = new StructuralReasoner(root, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
            reasoner.precomputeInferences();
        }
        catch (OWLOntologyCreationException e) {
            ErrorHandler.getErrorHandler().handleError(e);
        }
        return reasoner;
    }

	private OWLReasoner updateStructuralReasoner(OWLOntology ontology) {
		OWLReasoner reasoner;
		reasoner = new StructuralReasoner(ontology, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
		reasoner.precomputeInferences();
		ontologyReasoners.put(ontology.getOntologyID(), reasoner);
		return reasoner;
	}
	
	public void ontologySelected(OWLOntology ontology) {		
		fireOntologySelected(ontology);
	}

	//Property related stuff
	
	public Set<OWLPropertyItem> getAllOWLProperties(OWLOntology ontology,
			ValidationType type) {
		return propertyHandler.getAllOWLProperties(ontology, type);
	}

	public Set<OWLPropertyItem> getAllOWLProperties(OWLOntology ontology) {
		return propertyHandler.getAllOWLProperties(ontology);
	}

	public Set<OWLPropertyItem> getOWLDataProperties(OWLOntology ontology) {
		return propertyHandler.getOWLDataProperties(ontology);
	}

	public Set<OWLPropertyItem> getOWLObjectProperties(OWLOntology ontology) {
		return propertyHandler.getOWLObjectProperties(ontology);
	}

	public Set<OWLPropertyItem> getAllOWLProperties() {
		return propertyHandler.getAllOWLProperties();
	}

	public Set<OWLPropertyItem> getOWLDataProperties() {
		return propertyHandler.getOWLDataProperties();
	}

	public Set<OWLPropertyItem> getOWLObjectProperties() {
		return propertyHandler.getOWLObjectProperties();
	}
}
