package pl.ug.airport.agents;

import pl.ug.airport.behaviours.PlaneBehaviour;
import pl.ug.airport.behaviours.TechServiceBehaviour;
import pl.ug.airport.helpers.PlaneStatus;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/*
 * Samolot
 */
public class PlaneAgent extends Agent {
	
	private boolean passangersOnBoard = true;
	private boolean flightReady = false;
	private boolean crewReady = false;
	private boolean scheduled = false;
	
	private PlaneStatus planeStatus = PlaneStatus.AT_AIRPORT;
	
	@Override
	protected void setup() {
		Behaviour plane = new PlaneBehaviour(this);
		this.addBehaviour(plane);
	}

	public boolean isPassangersOnBoard() {
		return passangersOnBoard;
	}

	public void setPassangersOnBoard(boolean passangersOnBoard) {
		this.passangersOnBoard = passangersOnBoard;
	}
	
	public boolean isFlightReady() {
		return flightReady;
	}

	public void setFlightReady(boolean flightReady) {
		this.flightReady = flightReady;
	}

	public boolean isCrewReady() {
		return crewReady;
	}

	public void setCrewReady(boolean crewReady) {
		this.crewReady = crewReady;
	}

	public boolean isScheduled() {
		return scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

	public PlaneStatus getPlaneStatus() {
		return planeStatus;
	}

	public void setPlaneStatus(PlaneStatus planeStatus) {
		this.planeStatus = planeStatus;
	}
}
