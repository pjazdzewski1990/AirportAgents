package pl.ug.airport.behaviours;

import pl.ug.airport.helpers.AirportLogger;
import pl.ug.airport.messages.AgentAddresses;
import pl.ug.airport.messages.StringMessages;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class TechServiceBehaviour extends CyclicBehaviour {

	private Agent agent;

	private String TAG = "TechServiceAgent: ";
	
	public TechServiceBehaviour(Agent _agent) {
		agent = _agent;
	}
	
	@Override
	public void action() {
		ACLMessage rec = agent.receive();
		if(rec != null){
			try{
				StringMessages message = StringMessages.parseString(rec.getContent());
				handleAirportMessage(message);
			} catch(IllegalArgumentException ex){}
		}else{
			block();
		}
	}
	
	private void handleAirportMessage(StringMessages message) {
		switch(message){
			case REQUEST_INSPECTION: 
				AirportLogger.log(TAG + "Performed inspection");
				this.send(AgentAddresses.getFlightAgentAddress(), StringMessages.PLANE_READY);
				this.send(AgentAddresses.getPlaneAgentAddress(0), StringMessages.PLANE_READY);
			break;
			default:
				AirportLogger.log(TAG + "Unknown message received " + message);
		}
	}

	private void send(String address, StringMessages msgContent) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID(address, AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setContent(msgContent.toString());
		agent.send(msg);
	}

}
