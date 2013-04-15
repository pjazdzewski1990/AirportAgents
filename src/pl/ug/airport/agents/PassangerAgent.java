package pl.ug.airport.agents;

import pl.ug.airport.behaviours.PassangerBehaviour;
import jade.core.Agent;

public class PassangerAgent extends Agent{
	
	@Override
	protected void setup() {
		addBehaviour(new PassangerBehaviour(this));
	}

}
