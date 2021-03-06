package pl.ug.airport.behaviours;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import pl.ug.airport.agents.FlightControlAgent;
import pl.ug.airport.helpers.AirportLogger;
import pl.ug.airport.helpers.Constants;
import pl.ug.airport.helpers.HelperMethods;
import pl.ug.airport.helpers.PlaneStatus;
import pl.ug.airport.messages.AgentAddresses;
import pl.ug.airport.messages.StringMessages;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class FlightControlBehaviour extends AirportBaseBehaviour {

	private FlightControlAgent agent;

	private String TAG = "FlightControlAgent: ";
	
	private Set<String> flights;
	
	private int maxPlanesAtAirport = 1;
	private Set<String> planesAtAirport;
	
	public FlightControlBehaviour(FlightControlAgent _agent){
		agent = _agent;
		
		OWLClass flight = manager.getOWLDataFactory().getOWLClass(IRI.create(Constants.ontoLot));
		flights = individualsToUriStrings(reasoner.getInstances(flight, true).getFlattened());
		
		//TODO: hard coded 
		planesAtAirport = new HashSet<>();
		planesAtAirport.add("http://www.semanticweb.org/michal/ontologies/2013/4/lotnisko#Orzel");
	}
	
	@Override
	public void action() {
		ACLMessage msg = agent.receive();
		if (msg != null) {
			handleAirportMessage(msg);
		} else {
			pause(1000);
			sendMessages();
//			block();
		}
	}
	
	private void handleAirportMessage(ACLMessage msg) {
		ACLMessage reply;
		
		switch(HelperMethods.getConvTag(msg.getConversationId())) {
		case REPORT_PRESENCE:
			addPlaneToReady(msg.getContent().split(";")[1]);
		break;
		case FAILURE_INFO:
			reply = msg.createReply();
			reply.setContent(msg.getContent());
			reply.setConversationId(HelperMethods.switchTag(StringMessages.PERMISSION_TO_LAND, msg.getConversationId()));
			
			//blokada pasa
			//broadcast do wszystkich samolotow o utrudnieniach
			
			agent.send(reply);
			String[] failureData = msg.getContent().split(";");
			getAirportSupport(failureData[0], failureData[1], failureData[2]);
			break;
		case CLOSE_TO_AIRPORT:
			AirportLogger.log(TAG + "Plane is closing in. Prepare airfield");
			break;
		case REQUEST_LANDING:
			AirportLogger.log(TAG + "Plane is requesting landing permission.");
			//TODO: 
			//agent.setAvailablePlanes( agent.getAvailablePlanes() + 1 );
			
			reply = msg.createReply();
			reply.setContent(msg.getContent());
			reply.setConversationId(HelperMethods.switchTag(StringMessages.PERMISSION_TO_LAND, msg.getConversationId()));
			
			agent.send(reply);
			break;	
		case PASSANGERS_LEFT:
			AirportLogger.log(TAG + "Plane is empty");
			break;
		case REQUEST_TAKEOFF:
			AirportLogger.log(TAG + "Plane requested take off permission");
			
			if(new Random().nextBoolean()){
				AirportLogger.log(TAG + "Plane left the airport. Bye");
				
				reply = msg.createReply();
				reply.setPerformative(ACLMessage.AGREE);
			} else {
				AirportLogger.log(TAG + "Plane was stopped from leaving.");
				
				reply = msg.createReply();
				reply.setPerformative(ACLMessage.REFUSE);
			}
			reply.setContent(msg.getContent());
			agent.send(reply);
			break;
		case PLANE_READY:
			addPlaneToReady(msg.getContent());
			break;
		default:
			//AirportLogger.log(TAG + "Unknown message received " + message);
		}
	}
	
	private void addPlaneToReady(String plane) {
		AirportLogger.log(TAG + "Plane " + plane.substring(plane.lastIndexOf("#") + 1) + " is ready for scheduling");
		planesAtAirport.add(plane);
	}

	private void getAirportSupport(String flightURI, String planeURI, String failureURI) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(new AID(AgentAddresses.getTechServiceAgentAddress(),
				AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology(Constants.ontoURL);
		msg.setContent(flightURI + ";" + planeURI);
		msg.setConversationId(HelperMethods.generateMSGTag(StringMessages.FAILURE_INFO));

		agent.send(msg);
		
	}

	private void sendMessages() {
		Random random = new Random();
		switch (random.nextInt(5)) {
			case 1://odlot
				if(flights.size()>0 && planesAtAirport.size()>0){
					int flightsOffset = random.nextInt() % flights.size(); 
					int planesOffset = random.nextInt() % planesAtAirport.size();
					
					Iterator<String> flightsIterator = flights.iterator();
					for(int i=0; i<flightsOffset; i++)
						flightsIterator.next();
					Iterator<String> planesIterator = planesAtAirport.iterator();
					for(int i=0; i<planesOffset; i++)
						planesIterator.next();
					
					startDepartureProcedure(flightsIterator.next(), planesIterator.next());
				}
				break;
			default:
//				break;
		}
	}
	
	private void startDepartureProcedure(String flightUri, String planeUri) {
		planesAtAirport.remove(planeUri);
		
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(new AID(AgentAddresses.getPassangerServiceAgentAddress(),
				AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setOntology(Constants.ontoURL);
		msg.setContent(flightUri + ";" + planeUri);
		msg.setConversationId(HelperMethods.generateMSGTag(StringMessages.LEAVING_AT));

		agent.send(msg);
	}

	private void send(String address, StringMessages msgContent) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID(address, AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setContent(msgContent.toString());
		agent.send(msg);
	}
}
