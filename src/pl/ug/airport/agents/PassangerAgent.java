package pl.ug.airport.agents;

import pl.ug.airport.behaviours.PassangerBehaviour;
import jade.core.Agent;

public class PassangerAgent extends Agent{
	
	
	
	private PassangerBehaviour mainBhv;
	
	public PassangerAgent() {

	}
	
	protected void setup() {
		
		addBehaviour(mainBhv);
	}

}
