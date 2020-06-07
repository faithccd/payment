package com.faithccd.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	protected static Logger log = LoggerFactory.getLogger(Utils.class.getName());
	
	public static String maskCardNumber(String cardNumber, String mask) {

	    int index = 0;
	    StringBuilder maskedNumber = new StringBuilder();
	    for (int i = 0; i < mask.length(); i++) {
	        char c = mask.charAt(i);
	        if (c == '#') {
	            maskedNumber.append(cardNumber.charAt(index));
	            index++;
	        } else if (c == '*') {
	            maskedNumber.append(c);
	            index++;
	        } else {
	            maskedNumber.append(c);
	        }
	    }

	    return maskedNumber.toString();
	}

}
