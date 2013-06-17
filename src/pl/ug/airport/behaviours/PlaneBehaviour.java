package pl.ug.airport.behaviours;

import pl.ug.airport.agents.PlaneAgent;
import pl.ug.airport.helpers.AirportLogger;
import pl.ug.airport.helpers.Constants;
import pl.ug.airport.helpers.HelperMethods;
import pl.ug.airport.helpers.PlaneStatus;
import pl.ug.airport.messages.AgentAddresses;
import pl.ug.airport.messages.StringMessages;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class PlaneBehaviour extends CyclicBehaviour {

	private PlaneAgent agent;

	private String TAG = "PlaneAgent: ";

	private Boolean test = false;
	
	public PlaneBehaviour(PlaneAgent _agent) {
		agent = _agent;
	}

	@Override
	public void action() {
		if(agent.isScheduled() && agent.isFlightReady() && agent.isCrewReady()){
			agent.setPassangersOnBoard(true);
			takeoff();
			//block();
		}
		ACLMessage msg = agent.receive();
		if(!test) {
		requestLandingPermission();
		test=true;
		}
		if(msg != null){
			
			
			handleAirportMessage(msg);
			//receiveMessages(rec);
		}else{
			block();
			sendMessages();
		}
	}



	/*public void receiveMessages(ACLMessage rec) {
		try {
			StringMessages message = StringMessages.parseString(rec.getContent());
			handleAirportMessage(message);
		} catch(IllegalArgumentException ex){}
	}*/

	private void handleAirportMessage(ACLMessage msg) {
		if (agent.getPlaneStatus() == PlaneStatus.AT_AIRPORT) {
			
				switch(HelperMethods.getConvTag(msg.getConversationId())) {
				
			case PERMISSION_TO_LAND:
				
				System.out.println("Landing! "+ msg.getContent());
				
				break;
				
			case LEAVING_AT:
				AirportLogger.log(TAG + "Was scheduled for departure");
				
				if (!agent.isFlightReady()) {
					AirportLogger.log(TAG + "Was scheduled for departure - needs service");
					this.send(AgentAddresses.getTechServiceAgentAddress(),
							StringMessages.REQUEST_INSPECTION);
				}
				
				if (!agent.isCrewReady()) {
					AirportLogger.log(TAG + "Was scheduled for departure - crew not on board");
					this.send(AgentAddresses.getStaffAgentAddress(),
							StringMessages.REQUEST_CREW);
				}
				
				agent.setScheduled(true);
				break;
			case PLANE_READY:
				AirportLogger.log(TAG + "Was inspected");
				agent.setFlightReady(true);
				break;
			case BOARD_PLANE:
				AirportLogger.log(TAG + "Crew entered the plane");
				agent.setCrewReady(true);
				break;
			default:
				//AirportLogger.log(TAG + "Unknown message received " + message);
			}
		}
	}

	private void sendMessages() {
		switch (agent.getPlaneStatus()) {
		case AT_AIRPORT: 
			if(agent.isPassangersOnBoard()){
				AirportLogger.log(TAG + "Passangers have left the plane");
				agent.setPassangersOnBoard(false);
				this.send(AgentAddresses.getPassangerServiceAgentAddress(),
						StringMessages.PASSANGERS_LEFT);
				this.send(AgentAddresses.getFlightAgentAddress(),
						StringMessages.PASSANGERS_LEFT);
			}
			break;
		case AT_FLIGHT:
			AirportLogger.log(TAG + "Up in the sky");
			this.send(AgentAddresses.getFlightAgentAddress(),
				StringMessages.CLOSE_TO_AIRPORT);
			prepareToLand();
			//break;
		case LANDING: 
			AirportLogger.log(TAG + "Is going to land");
			land();
			this.send(AgentAddresses.getTechServiceAgentAddress(),
					StringMessages.REQUEST_INSPECTION);
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
	
	private void takeoff(){
		this.send(AgentAddresses.getFlightAgentAddress(),
				StringMessages.TAKE_OFF);
		agent.setPlaneStatus(PlaneStatus.AT_FLIGHT);
	}

	private void land() {
		agent.setPlaneStatus(PlaneStatus.AT_AIRPORT);
	}

	private void prepareToLand() {
		agent.setPlaneStatus(PlaneStatus.LANDING);
		
		
	}
	
	private void requestLandingPermission() {
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		msg.addReceiver(new AID(AgentAddresses.getFlightAgentAddress(), AID.ISLOCALNAME));
		msg.setConversationId(HelperMethods.generateMSGTag(StringMessages.REQUEST_LANDING));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology(Constants.ontoURL);
		msg.setContent(this.getFlightURI());
		agent.send(msg);
		
	}
	
	private String getFlightURI() {
		return "lot-test";
	}
	
	private void callCrew() {
		StringMessages rsv = StringMessages.REQUEST_CREW;
		send(AgentAddresses.getStaffAgentAddress(), rsv);
	}
	
}
