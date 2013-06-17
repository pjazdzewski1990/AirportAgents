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

	public PassangerBehaviour(Agent _agent) {
		this.agent = _agent;
	}

	@Override
	public void action() {
		ACLMessage msg = myAgent.receive();

		// printStatus(msg);
		if (test == 0) {
			// System.out.println("Zapytanie o liste dostêpnych lotów");
			// askForFlight();
			// test=1;
		}
		if (msg != null) {
			try {
				// StringMessages messageStr =
				// StringMessages.parseString(msg.getContent());

				messageHandler(msg);

			} catch (IllegalArgumentException ex) {
			}
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

			// presentFlightData(msg.getContent().split(";"));
			// tutaj trzeba siê zastanowiæ nad wybieraniem lotów, narazie
			// bierzemy pierwszy z brzegu i rezerwujemy
			// System.out.println("Dostêpne loty:");
			// presentFlightData(msg.getContent().split(";"));

			// System.out.println("Rezerwacja lotu: " +
			// msg.getContent().split(";")[0]);
			reserveFlight(getIndividualByName(msg.getContent().split(";")[0]),
					msg.getConversationId());

			// testowo zacznijmy kolejn¹ rezerwacje dla tego samego agenta
			// podczas trwania innej rezerwacji
			if (test == 1) {
				askForFlight();
				test = 2;
			}

			break;

		case RESERVATION_DONE:
			System.out.println("Rezerwowanie zakoñczone powodzeniem");
			break;

		case INFORM_ABOUT_CHANGES:
			AirportLogger.log(TAG + " Updating flight information");
			// send(AgentAddresses.getPassangerAgentAddress(1),
			// StringMessages.RESPONSE_OK);
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
		msg.addReceiver(new AID(AgentAddresses.getTimetableAddress(),
				AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology(Constants.ontoURL);
		msg.setContent("Lot_do=Paris");
		msg.setConversationId(HelperMethods
				.generateMSGTag(StringMessages.FLIGHT_TABLE_REQUEST));
		printStatus(msg);

		agent.send(msg);
	}

	public void reserveFlight(OWLNamedIndividual reservation, String convId) {
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		msg.addReceiver(new AID(AgentAddresses
				.getPassangerServiceAgentAddress(), AID.ISLOCALNAME));
		msg.setConversationId(HelperMethods.switchTag(
				StringMessages.RESERVATION, convId));
		printStatus(msg);
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology(Constants.ontoURL);
		msg.setContent(reservation.toString());
		agent.send(msg);
	}

	// do usuniecia
	private void printStatus(ACLMessage msg) {
		try {
			if (resData[0] == "null") {
				resData[0] = msg.getConversationId();
			} else {
				if (HelperMethods.getConvId(resData[0]).equals(
						HelperMethods.getConvId(msg.getConversationId()))) {
					resData[0] = msg.getConversationId();
				} else {
					resData[1] = msg.getConversationId();
				}
			}
			System.out.println("-------------------");
			System.out.println("Status komunikatu 0: " + resData[0]);
			System.out.println("Status komunikatu 1: " + resData[1]);
			System.out.println("-------------------");

		} catch (NullPointerException ex) {

		}
	}
}
