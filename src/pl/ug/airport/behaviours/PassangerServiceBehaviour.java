package pl.ug.airport.behaviours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import pl.ug.airport.helpers.AirportLogger;
import pl.ug.airport.helpers.Constants;
import pl.ug.airport.helpers.HelperMethods;
import pl.ug.airport.messages.AgentAddresses;
import pl.ug.airport.messages.StringMessages;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class PassangerServiceBehaviour extends AirportBaseBehaviour {

	private Agent agent;
	private String TAG = "PassangerServiceAgent: ";
	
	private Map<String, List<AID>> flightSubscribers = new HashMap<>(); 
	
	public PassangerServiceBehaviour(Agent _agent) {
		this.agent = _agent;
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			messageHandler(msg);
		}
		else {
			pause(1000);
			block();
		} 
	}
	
	private void messageHandler(ACLMessage msg) {
		switch(HelperMethods.getConvTag(msg.getConversationId())) {
		case RESERVATION:
			String[] responseUris = msg.getContent().split(";"); 
			String reservationUri = responseUris[ new Random().nextInt() % responseUris.length ];
			subscribeFlight(reservationUri, msg.getSender());
			
			//wysy³amy potwierdzenie rezerwacji
			ACLMessage reply = msg.createReply();
			reply.setContent(reservationUri); 
			reply.setConversationId(HelperMethods.switchTag(StringMessages.RESERVATION_DONE, msg.getConversationId()));
			
			agent.send(reply); 
			
			break;
			
		case PASSANGERS_LEFT:
			AirportLogger.log(TAG + " Passangers clean up");
			break;
			
		case INFORM_ABOUT_CHANGES:
			AirportLogger.log(TAG + " Changes recived");
			send(AgentAddresses.getPassangerAgentAddress(0), StringMessages.INFORM_ABOUT_CHANGES);
			break;
			
		case LEAVING_AT:
			AirportLogger.log(TAG + " Saving flight data");
			
			String[] content = msg.getContent().split(";");
			String flightUri = content[0];
			String planeUri = content[1];
			
			informPassanger(flightUri, planeUri);
			informPlane(flightUri, planeUri);
			break;
			
		default:
		//	AirportLogger.log(TAG + "Unknown message received " + messageStr);
			break;
		}
		
	}
	
	private void subscribeFlight(String reservationUri, AID sender) {
		List<AID> subscribedPassangers = new ArrayList<>();
		if(flightSubscribers.containsKey(reservationUri)){
			subscribedPassangers = flightSubscribers.get(reservationUri);
		}
		if(!subscribedPassangers.contains(sender)){
			subscribedPassangers.add(sender);
		}
		flightSubscribers.put(reservationUri, subscribedPassangers);
	}

	private void informPlane(String flightUri, String planeUri) {
		ACLMessage msg = createMessage(flightUri, planeUri);
		//TODO: który samolot powinienem poinformowac
		msg.addReceiver(new AID(AgentAddresses.getPlaneAgentAddress(0),
				AID.ISLOCALNAME));
		agent.send(msg);
	}

	private void informPassanger(String flightUri, String planeUri) {
		ACLMessage msg = createMessage(flightUri, planeUri);
		//TODO: którego pasazera powinienem poinformowac
		for(AID subscriber : subscribedPassangers(flightUri)){
			msg.addReceiver(subscriber);
		}
		agent.send(msg);
	}

	private List<AID> subscribedPassangers(String flightUri) {
		if(flightSubscribers.containsKey(flightUri)){
			return flightSubscribers.get(flightUri);
		}
		return new ArrayList<AID>();
	}

	private ACLMessage createMessage(String flightUri, String planeUri) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology(Constants.ontoURL);
		msg.setContent(flightUri + ";" + planeUri);
		msg.setConversationId(HelperMethods.generateMSGTag(StringMessages.LEAVING_AT));
		return msg;
	}

	private void send(String address, StringMessages msgContent) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID(address, AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setContent(msgContent.toString());
		agent.send(msg);
	}
	
	//sending functions 
	private void informChanges() {
		send(AgentAddresses.getPassangerAgentAddress(1), StringMessages.INFORM_ABOUT_CHANGES);
		
	}
	
	private void informFlight() {
		send(AgentAddresses.getPassangerAgentAddress(1), StringMessages.INFORM_ABOUT_FLIGHT);
	}

}
