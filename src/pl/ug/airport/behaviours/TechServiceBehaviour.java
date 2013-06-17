package pl.ug.airport.behaviours;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

import pl.ug.airport.helpers.AirportLogger;
import pl.ug.airport.messages.AgentAddresses;
import pl.ug.airport.messages.StringMessages;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class TechServiceBehaviour extends AirportBaseBehaviour {

	private Agent agent;

	private String TAG = "TechServiceAgent: ";
	
	private OWLClass accidentClass = manager.getOWLDataFactory().getOWLClass(IRI.create("http://www.semanticweb.org/michal/ontologies/2013/4/lotnisko#Sytuacja_alarmowa"));
	
	public TechServiceBehaviour(Agent _agent) {
		agent = _agent;
	}
	
	@Override
	public void action() {
		ACLMessage msg = agent.receive();
		if(msg != null){
			handleAirportMessage(msg);
		}else{
			block();
		}
	}
	private void handleAirportMessage(ACLMessage message) {
		String[] content = message.getContent().split(";");
		if(content.length == 2){
			String planeUri = content[0];
			String accidentTypeUri = content[1];
			serviceAccident(planeUri, accidentTypeUri);
			
			sendServiceReport();
		}
		if(content.length == 1){
			String planeUri = content[0];
			routineCheck(planeUri);
			
			sendServiceReport();
		}
	}

	private void sendServiceReport() {
		this.send(AgentAddresses.getFlightAgentAddress(), StringMessages.PLANE_READY);
		this.send(AgentAddresses.getPlaneAgentAddress(0), StringMessages.PLANE_READY);
	}

	private void routineCheck(String planeUri) {
		AirportLogger.log(TAG + " Routine check of plane " + planeUri.substring(planeUri.lastIndexOf("#") + 1));
	}

	private void serviceAccident(String planeUri, String accidentTypeUri) {
		
	}

	private void send(String address, StringMessages msgContent) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID(address, AID.ISLOCALNAME));
		msg.setLanguage(AgentAddresses.getLang());
		msg.setContent(msgContent.toString());
		agent.send(msg);
	}

}
