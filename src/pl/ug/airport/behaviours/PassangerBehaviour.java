package pl.ug.airport.behaviours;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import pl.ug.airport.helpers.AirportLogger;
import pl.ug.airport.helpers.HelperMethods;
import pl.ug.airport.messages.AgentAddresses;
import pl.ug.airport.messages.StringMessages;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class PassangerBehaviour extends AirportBaseBehaviour {

	private Agent agent;
	private String TAG = "PassangerAgent: ";
	
	public PassangerBehaviour(Agent _agent) {
		this.agent = _agent;
	}
	
	@Override
	public void action() {
		ACLMessage msg = myAgent.receive();
		
		askForFlight();
		if (msg != null) {
			try{
				//StringMessages messageStr = StringMessages.parseString(msg.getContent());
				
				messageHandler(msg);


				
				
				
				
				
			} catch(IllegalArgumentException ex){}	
		}
		else {
			block();
		} 
	}
	
	private void presentFlightData(String[] availableFlightsUris) {
		System.out.println("Possible flights: ");
		int number = 0; 
		for(String uri : availableFlightsUris){
			OWLNamedIndividual individual = getIndividualByUri(uri);
			System.out.println(++number + ": " + individual);
		}
	}

	private void messageHandler(ACLMessage msg) {
		switch(HelperMethods.getConvTag(msg.getConversationId())) {
		case FLIGHT_INFO:
			presentFlightData(msg.getContent().split(";"));
			
			break;
		case INFORM_ABOUT_CHANGES:
			AirportLogger.log(TAG + " Updating flight information");
			//send(AgentAddresses.getPassangerAgentAddress(1), StringMessages.RESPONSE_OK);
			break;
			
		case INFORM_ABOUT_FLIGHT:
			AirportLogger.log(TAG + " Reminded about flight");
			break;
			
		default:
			AirportLogger.log(TAG + "Unknown message received " + msg);
			break;
		}
	}
	
	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}


	public void askForFlight() {
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		msg.addReceiver(new AID(AgentAddresses.getTimetableAddress(), AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology("http://www.semanticweb.org/michal/ontologies/2013/4/lotnisko");
		msg.setContent("Lot_do=Paris");
		msg.setConversationId(HelperMethods.generateMSGTag(StringMessages.FLIGHT_INFO));
		agent.send(msg);
	}
	
	public void reserveFlight(OWLNamedIndividual reservation) {
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		msg.addReceiver(new AID(AgentAddresses.getTimetableAddress(), AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology("http://www.semanticweb.org/michal/ontologies/2013/4/lotnisko");
		msg.setContent(reservation.toString());
		agent.send(msg);
	}

}
