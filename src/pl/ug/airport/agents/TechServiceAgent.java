package pl.ug.airport.agents;

import pl.ug.airport.behaviours.TechServiceBehaviour;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/*
 * Obs³uga techniczna
 */
public class TechServiceAgent extends Agent {
	
	@Override
	protected void setup() {
		Behaviour techService = new TechServiceBehaviour(this);
		this.addBehaviour(techService);
	}
}
