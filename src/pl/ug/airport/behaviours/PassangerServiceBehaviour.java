package pl.ug.airport.behaviours;

import java.util.Set;

import pl.ug.airport.helpers.AirportLogger;
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
	
	public PassangerServiceBehaviour(Agent _agent) {
		this.agent = _agent;

	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			try {
				//StringMessages messageStr = StringMessages.parseString(msg.getContent());
				messageHandler(msg);
				
				
			} catch(IllegalArgumentException ex){}
			catch(NullPointerException ex){}
		}
		else {
			block();
		} 
	}
	
	private void messageHandler(ACLMessage msg) {
		switch(HelperMethods.getConvTag(msg.getConversationId())) {
		case RESERVATION:
			//indywidual do rezerwacji jest pod getIndividualByName(msg.getContent())
			//jak zapisujemy rezerwacje??
			
			//wysy³amy potwierdzenie rezerwacji

			ACLMessage reply = msg.createReply();
			reply.setContent("1"); //tutaj jakieœ id rezerwacji dla konkretnego pasa¿era po zapisaniu...
			reply.setConversationId(HelperMethods.switchTag(StringMessages.RESERVATION_DONE, msg.getConversationId()));
			
			agent.send(reply);			
			
			//AirportLogger.log(TAG + " Saving Reservation");
			//send(AgentAddresses.getPassangerAgentAddress(1), StringMessages.RESPONSE_OK);
			break;
			
		case PASSANGERS_LEFT:
			AirportLogger.log(TAG + " Passangers data clean up");
			break;
			
		case INFORM_ABOUT_CHANGES:
			AirportLogger.log(TAG + " Changes recived");
			send(AgentAddresses.getPassangerAgentAddress(0), StringMessages.INFORM_ABOUT_CHANGES);
			break;
			
		case LEAVING_AT:
			AirportLogger.log(TAG + " Saving flight data");
			send(AgentAddresses.getPassangerAgentAddress(0), StringMessages.INFORM_ABOUT_FLIGHT);
			break;
			
		default:
		//	AirportLogger.log(TAG + "Unknown message received " + messageStr);
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

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}
