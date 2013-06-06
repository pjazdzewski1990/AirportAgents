package pl.ug.airport.agents;


import pl.ug.airport.behaviours.PassangerServiceBehaviour;
import jade.core.Agent;

/*
 * Obs³uga pasa¿era
 */
public class PassangerServiceAgent extends AirportBaseAgent {
	
	protected void setup() {
		addBehaviour(new PassangerServiceBehaviour(this));
	}
}
