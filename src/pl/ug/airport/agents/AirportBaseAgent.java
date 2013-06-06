package pl.ug.airport.agents;

import java.io.File;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import jade.core.Agent;

public class AirportBaseAgent  extends Agent{

	OWLOntology ontology;
	OWLReasoner reasoner;
	OWLOntologyManager manager;
	
	public AirportBaseAgent() {
		manager = OWLManager.createOWLOntologyManager();
		
		try {
			ontology = manager.loadOntologyFromOntologyDocument(new File("src/pl/ug/airport/protege/lotnisko.owl"));
			reasoner = new Reasoner(ontology);
			
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
	
}
