package pl.ug.airport;

import java.io.File;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class Main {
	
	public static void main(String[] args) {
		
		OWLOntology ontology;
		OWLReasoner reasoner;
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		System.out.println("load attempt");
		try {
			ontology = manager.loadOntologyFromOntologyDocument(new File("src/pl/ug/airport/protege/lotnisko.owl"));
			reasoner = new Reasoner(ontology);
			
			if ( reasoner.isConsistent() ) {
			    System.err.println( "The aligned ontologies are consistent" );
			} else {
			    System.err.println( "The aligned ontologies are inconsistent" );
			}
			OWLClass kontrola = manager.getOWLDataFactory().getOWLClass(IRI.create("Kontrola_lotow"));
			
			System.out.println("loaded onto");
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Starting airport (semantic) simulation");
	}

}
