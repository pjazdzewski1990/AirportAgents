package pl.ug.airport.agents;

import pl.ug.airport.behaviours.FlightControlBehaviour;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/*
 * Przyloty/Odloty
 */
public class FlightControlAgent extends Agent {
	
	private int availablePlanes = 0;
	
	@Override
	protected void setup() {
		Behaviour control = new FlightControlBehaviour(this);
		this.addBehaviour(control);
	}

	public int getAvailablePlanes() {
		return availablePlanes;
	}

	public void setAvailablePlanes(int availablePlanes) {
		this.availablePlanes = availablePlanes;
	}
}
