package pl.ug.airport.agents;

import pl.ug.airport.behaviours.PassangerBehaviour;
import pl.ug.airport.behaviours.TimetableBehaviour;
import jade.core.Agent;

public class TimetableAgent extends Agent {

	@Override
	protected void setup() {
		addBehaviour(new TimetableBehaviour(this));
	}
	
}
