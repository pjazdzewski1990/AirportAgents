package pl.ug.airport.messages;

import org.omg.CORBA.DynAnyPackage.Invalid;

public enum StringMessages {
	
	RESPONSE_OK,
	RESPONSE_REPEAT,
	//flight control agent
	LEAVING_AT,
	FLIGHTPLAN_WAS_CHANGED,
	PERMISSION_TO_LAND,
	//passanger agent
	FLIGHT_INFO,
	RESERVATION,
	FLIGHT_TABLE_REQUEST,
	//timetable
	FLIGHT_TABLE_DATA,
	//passanger service agent
	RESERVATION_DONE,
	INFORM_ABOUT_FLIGHT,
	INFORM_ABOUT_CHANGES,
	//plane agent
	REQUEST_CREW,
	CLOSE_TO_AIRPORT,
	REQUEST_LANDING, 
	REQUEST_TAKEOFF,
	PASSANGERS_LEFT,
	REQUEST_INSPECTION,
	TAKE_OFF,
	FAILURE_INFO,
	//staff assignment agent
	BOARD_PLANE,
	//tech service agent
	PLANE_READY;
	
	public static StringMessages parseString(String str){
		switch(str){
			case "RESPONSE_OK": return RESPONSE_OK; 
			case "RESPONSE_REPEAT": return RESPONSE_REPEAT;
			
			case "LEAVING_AT": return LEAVING_AT;
			case "FLIGHTPLAN_WAS_CHANGED": return FLIGHTPLAN_WAS_CHANGED;
			case "PERMISSION_TO_LAND": return PERMISSION_TO_LAND;
					
			case "RESERVATION": return RESERVATION;
			case "FLIGHT_INFO": return FLIGHT_INFO;
			case "FLIGHT_TABLE_REQUEST": return FLIGHT_TABLE_REQUEST;
			
			case "FLIGHT_TABLE_DATA": return FLIGHT_TABLE_DATA;
			
			case "INFORM_ABOUT_FLIGHT": return INFORM_ABOUT_FLIGHT;
			case "INFORM_ABOUT_CHANGES": return INFORM_ABOUT_CHANGES ;
			case "RESERVATION_DONE": return RESERVATION_DONE ;
			
			case "REQUEST_CREW": return REQUEST_CREW;
			case "CLOSE_TO_AIRPORT": return CLOSE_TO_AIRPORT;
			case "REQUEST_LANDING": return REQUEST_LANDING;
			case "REQUEST_TAKEOFF": return REQUEST_TAKEOFF;
			case "PASSANGERS_LEFT": return PASSANGERS_LEFT;
			case "REQUEST_INSPECTION": return REQUEST_INSPECTION;
			case "TAKE_OFF": return TAKE_OFF;
			case "FAILURE_INFO": return FAILURE_INFO;
			
			
			case "BOARD_PLANE": return BOARD_PLANE;
			case "PLANE_READY": return PLANE_READY;
			default: throw new IllegalArgumentException("String " + str + " is an illegal enum");
		}
	}
}
