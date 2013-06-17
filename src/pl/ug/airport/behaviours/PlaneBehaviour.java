package pl.ug.airport.behaviours;

import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import pl.ug.airport.agents.PlaneAgent;
import pl.ug.airport.helpers.AirportLogger;
import pl.ug.airport.helpers.Constants;
import pl.ug.airport.helpers.HelperMethods;
import pl.ug.airport.helpers.PlaneStatus;
import pl.ug.airport.messages.AgentAddresses;
import pl.ug.airport.messages.StringMessages;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PlaneBehaviour extends AirportBaseBehaviour {

	private PlaneAgent agent;

	private String TAG = "PlaneAgent: ";

	private Boolean test = false;
	
	//TODO: hard coded
	private String seflUri = "http://www.semanticweb.org/michal/ontologies/2013/4/lotnisko#Orzel";
	
	public PlaneBehaviour(PlaneAgent _agent) {
		agent = _agent;
	}
	
	public PlaneBehaviour(PlaneAgent _agent, String planeUri) {
		agent = _agent;
		this.seflUri = planeUri;
		requestConfigData();
	}

	private void requestConfigData() {
		OWLNamedIndividual indv = getIndividualByUri(this.seflUri);
		
	}

	@Override
	public void action() {
		if(!test) {
//			requestLandingPermission();
			//test=true;
			//System.out.println("Panie pilocie, dziura w samolocie");
			//failureUnicast("0","0","0");
		
		}
		
		ACLMessage msg = agent.receive();		
		if(msg != null){
			handleAirportMessage(msg);
		}else{
			pause(1000);
			sendMessages();
			//NON blocking
			//block();
		}
	}


	private void handleAirportMessage(ACLMessage msg) {
		if (agent.getPlaneStatus() == PlaneStatus.AT_AIRPORT) {
			
			switch(HelperMethods.getConvTag(msg.getConversationId())) {
				
			case PERMISSION_TO_LAND:
				System.out.println("Landing! "+ msg.getContent());
				
				break;
				
			case LEAVING_AT:
				AirportLogger.log(TAG + "Was scheduled for departure");
				
				pause(1000);
				
				String[] content = msg.getContent().split(";");
				String flightUri = content[0];
				String planeUri = content[1];
				requestTakeoffPermission(flightUri, planeUri);
				
				break;
				
			case REQUEST_TAKEOFF:
				AirportLogger.log(TAG + "Received response for TAKEOFF");
				if(msg.getPerformative() == ACLMessage.AGREE){
					AirportLogger.log(TAG + "OK to leave. Starting engines");
					agent.setPlaneStatus(PlaneStatus.AT_FLIGHT);
				}else{
					AirportLogger.log(TAG + "Refused. Waiting then asking one more time");

					pause(1000);
					
					String[] refusedFlight = msg.getContent().split(";");
					String refusedFlightUri = refusedFlight[0];
					String refusedPlaneUri = refusedFlight[1];
					requestTakeoffPermission(refusedFlightUri, refusedPlaneUri);
				}
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

	private void failureUnicast(String flightUri, String planeUri, String failureUri) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(new AID(AgentAddresses.getFlightAgentAddress(),
				AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology(Constants.ontoURL);
		msg.setContent(flightUri + ";" + planeUri + ";" + failureUri);
		msg.setConversationId(HelperMethods.generateMSGTag(StringMessages.FAILURE_INFO));

		agent.send(msg);
		
	}

	
	private void requestTakeoffPermission(String flightUri, String planeUri) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(new AID(AgentAddresses.getFlightAgentAddress(),
				AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology(Constants.ontoURL);
		msg.setContent(flightUri + ";" + planeUri);
		msg.setConversationId(HelperMethods.generateMSGTag(StringMessages.REQUEST_TAKEOFF));

		agent.send(msg);
	}

	private void sendMessages() {
		switch (agent.getPlaneStatus()) {
		case AT_AIRPORT: 
//			if(agent.isPassangersOnBoard()){
//				AirportLogger.log(TAG + "Passangers have left the plane");
//				agent.setPassangersOnBoard(false);
//				this.send(AgentAddresses.getPassangerServiceAgentAddress(),
//						StringMessages.PASSANGERS_LEFT);
//				this.send(AgentAddresses.getFlightAgentAddress(),
//						StringMessages.PASSANGERS_LEFT);
//			}
			break;
		case AT_FLIGHT:
			AirportLogger.log(TAG + "Up in the sky");
			pause(1000);
			prepareToLand();
			//break;
		case LANDING: 
			pause(1000);
			AirportLogger.log(TAG + "Is going to land");
			land();
			requestServiceCheck();
			break;
		}
	}

	private void requestServiceCheck() {
		send(AgentAddresses.getTechServiceAgentAddress(), seflUri);
	}

	private void send(String address, StringMessages msgContent) {
		send(address, msgContent.toString());
	}
	
	private void send(String address, String msgContent) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID(address, AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setContent(msgContent);
		msg.setConversationId(HelperMethods.generateMSGTag(StringMessages.REQUEST_INSPECTION));
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
