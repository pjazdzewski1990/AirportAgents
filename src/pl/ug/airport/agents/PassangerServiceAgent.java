package pl.ug.airport.agents;


import pl.ug.airport.behaviours.PassangerServiceBehaviour;
import jade.core.Agent;

/*
 * Obs�uga pasa�era
 */
public class PassangerServiceAgent extends Agent {
	
	protected void setup() {
		addBehaviour(new PassangerServiceBehaviour(this));
	}
}
