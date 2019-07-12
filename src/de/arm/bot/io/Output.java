package de.arm.bot.io;

import de.arm.bot.info.Action;

public class Output {
	
	private static long cur;
	
	public static void sendAction(Action action) {
		System.out.println(action);
		System.err.println(action);
		System.err.println((System.nanoTime()-cur)/1_000_000+" ms");
	}
	
	public static void logDebug(String message) {
		System.err.println(message);
	}
	
	static void setStart() {
		cur=System.nanoTime();
	}
}
