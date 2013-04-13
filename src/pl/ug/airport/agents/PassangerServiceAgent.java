package pl.ug.airport.agents;


import pl.ug.airport.behaviours.PassangerServiceBehaviour;
import jade.core.Agent;

/*
 * Obs³uga pasa¿era
 */
public class PassangerServiceAgent extends Agent {
	
	
	
	private PassangerServiceBehaviour mainBhv;
	
	public PassangerServiceAgent() {
		// TODO Auto-generated constructor stub
	}
	
	protected void setup() {
		addBehaviour(mainBhv);
	}
}
