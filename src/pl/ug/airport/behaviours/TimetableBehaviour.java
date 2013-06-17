package pl.ug.airport.behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import pl.ug.airport.helpers.AirportLogger;
import pl.ug.airport.helpers.Constants;
import pl.ug.airport.helpers.HelperMethods;
import pl.ug.airport.messages.StringMessages;

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
				messageHandler(msg);
				
			} catch(IllegalArgumentException ex) {
				AirportLogger.log(TAG + " error " + ex);
			}	
		}
		else {
			block();
		} 
	}
	
	
	private void messageHandler(ACLMessage msg) {
		switch(HelperMethods.getConvTag(msg.getConversationId())) {
		case FLIGHT_TABLE_REQUEST:
			Set<String> uris = pickFlightTimetable(msg.getContent());
			ACLMessage reply = msg.createReply();
			
			reply.setConversationId(HelperMethods.switchTag(StringMessages.FLIGHT_TABLE_DATA, msg.getConversationId()));
			
			replyWithflightInfo(reply, uris);
			break;
		default:
			
			break;
		}
	}

	private Set<String> pickFlightTimetable(String request) {
		OWLClass flight = manager.getOWLDataFactory().getOWLClass(IRI.create(Constants.ontoLot));
		Set<OWLNamedIndividual> flightIndividuals = reasoner.getInstances(flight, true).getFlattened();
		
		Map<OWLDataProperty, String> expected = parseRequest(request);
		
		Set<OWLNamedIndividual> correctFlights = flightIndividuals;

		Iterator<Entry<OWLDataProperty, String>> it = expected.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<OWLDataProperty, String> pair = it.next();
	        
	        OWLDataProperty property = pair.getKey();
	        String value = pair.getValue();
	        
	        correctFlights = filterIndividualsByDataPropertValue(correctFlights, property, value);
	    }
		
		Set<String> uris = individualsToUriStrings(correctFlights);
		
		return uris;
		
	}
	
	private void replyWithflightInfo(ACLMessage msg, Set<String> uris) {
		StringBuilder sb = new StringBuilder();
		for(String uri : uris){
			sb.append(uri);
			sb.append(";");
		}
		sb.deleteCharAt(sb.length()-1);
		
		msg.setContent(sb.toString());
		agent.send(msg);
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

}
