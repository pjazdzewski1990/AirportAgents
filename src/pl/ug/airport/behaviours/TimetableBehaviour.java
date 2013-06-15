package pl.ug.airport.behaviours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import pl.ug.airport.helpers.AirportLogger;

public class TimetableBehaviour extends AirportBaseBehaviour {

	private Agent agent;
	private String TAG = "TimetableAgent: ";
	
	public TimetableBehaviour(Agent _agent) {
		this.agent = _agent;
	}
	
	@Override
	public void action() {
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			try {
				OWLClass flight = manager.getOWLDataFactory().getOWLClass(IRI.create("http://www.semanticweb.org/michal/ontologies/2013/4/lotnisko#Lot"));
				Set<OWLNamedIndividual> flightIndividuals = reasoner.getInstances(flight, true).getFlattened();
				
				String request = msg.getContent();
				System.out.println(request);
				Map<OWLDataProperty, String> expected = parseRequest(request);
				
				Set<OWLNamedIndividual> correctFlights = flightIndividuals;

				Iterator<Entry<OWLDataProperty, String>> it = expected.entrySet().iterator();
			    while (it.hasNext()) {
			        Entry<OWLDataProperty, String> pair = it.next();
			        System.out.println(pair.getKey() + " = " + pair.getValue());
			        
			        OWLDataProperty property = pair.getKey();
			        String value = pair.getValue();
			        
			        correctFlights = filterIndividualsByDataPropertValue(correctFlights, property, value);
			    }
				
				System.out.println("Do odeslania " + correctFlights);
			} catch(IllegalArgumentException ex) {
				AirportLogger.log(TAG + " error " + ex);
			}	
		}
		else {
			block();
		} 
	}

	private Map<OWLDataProperty, String> parseRequest(String request) {
		Map<OWLDataProperty, String> expected = new HashMap<>();
		for(String req: request.split("&")){
			String[] pair = req.split("=");
			
			OWLDataProperty key = getDataPropertyByName(pair[0]);
			String value = pair[1];
			expected.put(key, value);
		}
		return expected;
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
