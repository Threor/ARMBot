package de.arm.bot.io;

import de.arm.bot.info.Action;

public class Output {
	
	public static void sendAction(Action action) {
		System.out.println(action);
	}
	
	public static void logDebug(String message) {
		System.err.println(message);
	}
}
