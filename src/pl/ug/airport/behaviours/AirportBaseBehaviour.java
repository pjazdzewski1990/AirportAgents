package pl.ug.airport.behaviours;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.model.Individual;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import pl.ug.airport.helpers.Constants;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;

public abstract class AirportBaseBehaviour extends CyclicBehaviour {

	protected OWLOntology ontology;
	protected OWLReasoner reasoner;
	protected OWLOntologyManager manager;
	protected OWLDataFactory factory;
	
	protected String baseURI = Constants.ontoURL;
	
	public AirportBaseBehaviour() {
		manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
		
		try {
			ontology = manager.loadOntologyFromOntologyDocument(new File("src/pl/ug/airport/protege/lotnisko.owl"));
			
			OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
	        OWLReasonerConfiguration config = new SimpleConfiguration();
			reasoner = reasonerFactory.createReasoner(ontology, config);
			
			if ( reasoner.isConsistent() ) {
			    System.err.println( "The aligned ontologies are consistent" );
			} else {
			    System.err.println( "The aligned ontologies are inconsistent" );
			    throw new IllegalStateException("The aligned ontologies are inconsistent");
			}
		} catch (OWLOntologyCreationException e) {
			System.out.println("Error at agent creation");
			throw new RuntimeException(e);
		}
	}
	
	protected OWLClass getClassByName(String name) {
		return factory.getOWLClass(IRI.create(Constants.ontoURL + "#" + name));
	}
	
	protected OWLNamedIndividual getIndividualByUri(String uri){
		return factory.getOWLNamedIndividual(IRI.create(uri));
	}
	
	protected OWLNamedIndividual getIndividualByName(String name){
		return getIndividualByUri(baseURI + "#" + name);
	}
	
	protected OWLDataProperty getDataPropertyByName(String name){
		return factory.getOWLDataProperty(IRI.create(Constants.ontoURL + "#" + name));
	}
	
	protected Set<OWLNamedIndividual> filterIndividualsByDataPropertValue(Set<OWLNamedIndividual> individuals, OWLDataProperty property, String value) { 
		Set<OWLNamedIndividual> filtered = new HashSet<OWLNamedIndividual>();
		for (OWLNamedIndividual ind: individuals) {
			Set<OWLLiteral> valuesNodeSet = reasoner.getDataPropertyValues(ind, property);
			for (OWLLiteral literal : valuesNodeSet) {
				if (literal.getLiteral().toString().equals(value)) {
					filtered.add(ind);
					break;
				}
			}
		}
		return filtered;
	}
	
	protected Set<String> individualsToUriStrings(Set<OWLNamedIndividual> individuals) {
		Set<String> uris = new HashSet<>();
		for(OWLNamedIndividual i : individuals) {
			uris.add(i.toStringID());
		}
		return uris;
	}
}
