package pl.ug.airport.agents;

import pl.ug.airport.behaviours.PlaneBehaviour;
import pl.ug.airport.behaviours.TechServiceBehaviour;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/*
 * Samolot
 */
public class PlaneAgent extends Agent {
	@Override
	protected void setup() {
		Behaviour plane = new PlaneBehaviour(this);
		this.addBehaviour(plane);
	}
}
