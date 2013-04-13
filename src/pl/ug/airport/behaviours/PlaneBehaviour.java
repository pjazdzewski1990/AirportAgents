package pl.ug.airport.behaviours;

import pl.ug.airport.helpers.AirportLogger;
import pl.ug.airport.helpers.PlaneStatus;
import pl.ug.airport.messages.AgentAddresses;
import pl.ug.airport.messages.StringMessages;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class PlaneBehaviour extends CyclicBehaviour {

	private Agent agent;

	private String TAG = "PlaneAgent: ";

	//it should be moved into the plane agent class
	private boolean flightReady = true;
	private boolean crewReady = true;
	private boolean scheduled = false;
	
	private PlaneStatus planeStatus = PlaneStatus.AT_AIRPORT;

	public PlaneBehaviour(Agent _agent) {
		agent = _agent;
	}

	@Override
	public void action() {
		if(scheduled && flightReady && crewReady){
			takeoff();
			return;
		}
		receiveMessages();
		sendMessages();
	}

	public void receiveMessages() {
		ACLMessage rec = agent.receive();
		if (rec != null) {
			AirportLogger.log(TAG + "Received: " + rec.getContent());
			StringMessages message;
			try {
				message = StringMessages.parseString(rec.getContent());
				handleAirportMessage(message);
			} catch (Exception e) {

			}
		}
	}

	private void handleAirportMessage(StringMessages message) {
		if (planeStatus != PlaneStatus.AT_AIRPORT) {
			switch (message) {
			case LEAVING_AT:
				AirportLogger.log(TAG + "Was scheduled for departure");
				if (!flightReady) {
					AirportLogger.log(TAG + "Was scheduled for departure - needs service");
					this.send(AgentAddresses.getTechServiceAgentAddress(),
							StringMessages.REQUEST_INSPECTION);
				}
				if (!crewReady) {
					AirportLogger
							.log(TAG + "Was scheduled for departure - crew not on board");
					this.send(AgentAddresses.getStaffAgentAddress(),
							StringMessages.REQUEST_INSPECTION);
				}
				
				scheduled = true;
				
				break;
			case PLANE_READY:
				AirportLogger.log(TAG + "Was inspected");
				flightReady = true;
				break;
			case BOARD_PLANE:
				AirportLogger.log(TAG + "Crew entered the plane");
				crewReady = true;
				break;
			default:
				AirportLogger.log(TAG + "Unknown message received " + message);
			}
		}
	}

	private void sendMessages() {
		switch (planeStatus) {
		case AT_AIRPORT: 
			AirportLogger.log(TAG + "Passangers have left the plane");
			this.send(AgentAddresses.getPassangerServiceAgentAddress(),
				StringMessages.PASSANGERS_LEFT);
			this.send(AgentAddresses.getFlightAgentAddress(),
					StringMessages.PASSANGERS_LEFT);
			break;
		case AT_FLIGHT:
			AirportLogger.log(TAG + "Ready to land");
			this.send(AgentAddresses.getFlightAgentAddress(),
				StringMessages.CLOSE_TO_AIRPORT);
			prepareToLand();
			break;
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
		planeStatus = PlaneStatus.AT_FLIGHT;
	}

	private void land() {
		planeStatus = PlaneStatus.AT_AIRPORT;
	}

	private void prepareToLand() {
		planeStatus = PlaneStatus.LANDING;
	}
	
}
