package pl.ug.airport.behaviours;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import pl.ug.airport.helpers.AirportLogger;
import pl.ug.airport.helpers.Constants;
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

	private int test = 0; // do wywalenia

	private String[] resData = { "null", "null" };

	private String reservationURI; 
	
	public PassangerBehaviour(Agent _agent) {
		this.agent = _agent;
	}

	@Override
	public void action() {
		ACLMessage msg = myAgent.receive();

		// printStatus(msg);
		if (test == 0) {
			// System.out.println("Zapytanie o liste dostêpnych lotów");
			askForFlight();
			test=1;
		}
		if (msg != null) {
			messageHandler(msg);
		} else {
			block();
		}
	}

	private void presentFlightData(String[] availableFlightsUris) {
		int number = 0;
		for (String uri : availableFlightsUris) {
			OWLNamedIndividual individual = getIndividualByUri(uri);
			System.out.println(++number + ": " + individual);
		}
	}

	private void messageHandler(ACLMessage msg) {
		switch (HelperMethods.getConvTag(msg.getConversationId())) {
		case FLIGHT_TABLE_DATA:

			reserveFlight(msg.getContent().split(";")[0], msg.getConversationId());

			// testowo zacznijmy kolejn¹ rezerwacje dla tego samego agenta
			// podczas trwania innej rezerwacji
			if (test == 1) {
				askForFlight();
				test = 2;
			}

			break;

		case RESERVATION_DONE:
			handleReservation(msg);
			break;

		case INFORM_ABOUT_CHANGES:
			AirportLogger.log(TAG + " Updating flight information");
			// send(AgentAddresses.getPassangerAgentAddress(1),
			// StringMessages.RESPONSE_OK);
			break;

		case LEAVING_AT:
		case INFORM_ABOUT_FLIGHT:
			AirportLogger.log(TAG + " Reminded about flight");
			break;

		default:
			AirportLogger.log(TAG + "Unknown message received " + msg);
			break;
		}
	}
	
	private void handleReservation(ACLMessage msg) {
		reservationURI = msg.getContent();
		AirportLogger.log(TAG + " Reserved seat");
	}

	public void askForFlight() {
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		msg.addReceiver(new AID(AgentAddresses.getTimetableAddress(),
				AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology(Constants.ontoURL);
		msg.setContent("Lot_do=Paris");
		msg.setConversationId(HelperMethods.generateMSGTag(StringMessages.FLIGHT_TABLE_REQUEST));

		agent.send(msg);
	}

	public void reserveFlight(String reservation, String convId) {
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		msg.addReceiver(new AID(AgentAddresses
				.getPassangerServiceAgentAddress(), AID.ISLOCALNAME));
		msg.setConversationId(HelperMethods.switchTag(
				StringMessages.RESERVATION, convId));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology(Constants.ontoURL);
		msg.setContent(reservation);
		agent.send(msg);
	}
}
