package pl.ug.airport.agents;

import pl.ug.airport.behaviours.FlightControlBehaviour;
import pl.ug.airport.behaviours.StaffAssignmentBehaviour;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/*
 * Przydzia³ za³ogi
 */
public class StaffAssignmentAgent extends Agent {
	StaffAssignmentBehaviour mainBhv;
	@Override
	protected void setup() {
		mainBhv = new StaffAssignmentBehaviour();
		this.addBehaviour(mainBhv);
	}
}
