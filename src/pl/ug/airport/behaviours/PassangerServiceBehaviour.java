package pl.ug.airport.behaviours;

import pl.ug.airport.helpers.AirportLogger;
import pl.ug.airport.messages.AgentAddresses;
import pl.ug.airport.messages.StringMessages;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class PassangerServiceBehaviour extends CyclicBehaviour {

	private Agent agent;
	private String TAG = "PassangerServiceAgent: ";
	
	public PassangerServiceBehaviour(Agent _agent) {
		this.agent = _agent;

	}
	

	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			try {
				StringMessages messageStr = StringMessages.parseString(msg.getContent());
				messageHandler(messageStr);
			} catch(IllegalArgumentException ex){}
		}
		else {
			block();
		} 
	}
	
	private void messageHandler(StringMessages messageStr) {
		switch(messageStr) {
		case RESERVATION:
			AirportLogger.log(TAG + " Saving Reservation");
			send(AgentAddresses.getPassangerAgentAddress(1), StringMessages.RESPONSE_OK);
			break;
			
		case PASSANGERS_LEFT:
			AirportLogger.log(TAG + " Passangers data clean up");
			break;
			
		case INFORM_ABOUT_CHANGES:
			AirportLogger.log(TAG + " Changes recived");
			break;
			
		case LEAVING_AT:
			AirportLogger.log(TAG + " Saving flight data");
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
	
	//sending functions 
	private void informChanges() {
		send(AgentAddresses.getPassangerAgentAddress(1), StringMessages.INFORM_ABOUT_CHANGES);
		
	}
	
	private void informFlight() {
		send(AgentAddresses.getPassangerAgentAddress(1), StringMessages.INFORM_ABOUT_FLIGHT);
	}

}
