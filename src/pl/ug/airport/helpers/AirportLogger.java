package pl.ug.airport.helpers;

import java.util.Date;

public class AirportLogger {
	public static void log(String str){
		System.out.println("" + new Date() + " " + str);
	}
}
