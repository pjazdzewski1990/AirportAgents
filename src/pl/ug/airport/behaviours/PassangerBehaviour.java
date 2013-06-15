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
		if (msg != null) {
			try{
				askForFlight();
//				StringMessages messageStr = StringMessages.parseString(msg.getContent());
//				messageHandler(messageStr);
			} catch(IllegalArgumentException ex){}	
		}
		else {
			block();
		} 
	}
	
	private void messageHandler(StringMessages messageStr) {
		switch(messageStr) {
		case INFORM_ABOUT_CHANGES:
			AirportLogger.log(TAG + " Updating flight information");
			//send(AgentAddresses.getPassangerAgentAddress(1), StringMessages.RESPONSE_OK);
			break;
			
		case INFORM_ABOUT_FLIGHT:
			AirportLogger.log(TAG + " Reminded about flight");
			break;
			
		default:
			AirportLogger.log(TAG + "Unknown message received " + messageStr);
			break;
		}
	}
	
	private void send(String address, StringMessages msgContent) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID(address, AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setContent(msgContent.toString());
		agent.send(msg);
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}


	public void askForFlight() {
		// WUT?
		//ontologyManager.getQueryManager().createCustomQueryClass(countryQueryOntology, "countryQuery", countryClass);
		
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		msg.addReceiver(new AID(AgentAddresses.getTimetableAddress(), AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology("http://www.semanticweb.org/michal/ontologies/2013/4/lotnisko");
		msg.setContent("Lot_do=Paris");
		agent.send(msg);
	}

}
