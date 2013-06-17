package pl.ug.airport.agents;

import pl.ug.airport.behaviours.FlightControlBehaviour;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/*
 * Przyloty/Odloty
 */
public class FlightControlAgent extends Agent {
	
	@Override
	protected void setup() {
		Behaviour control = new FlightControlBehaviour(this);
		this.addBehaviour(control);
	}
}
