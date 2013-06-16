package pl.ug.airport.helpers;

import java.math.BigInteger;
import java.security.SecureRandom;

import pl.ug.airport.messages.StringMessages;

public class HelperMethods {
	public static String generateConvId() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
	
	public static String generateMSGTag(StringMessages tag) {
		
		return tag+";"+generateConvId();
	}
	
	public static String getConvId(String msg) {
		
		return msg.split(";")[1];
	}
	
	public static StringMessages getConvTag(String msg) {
		
		return StringMessages.parseString(msg.split(";")[0]);
	}
}
