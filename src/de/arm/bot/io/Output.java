package de.arm.bot.io;

import de.arm.bot.info.Action;

/**
 * A class used for the output communication with the game
 * @author Team ARM
 */
public class Output {

	/**
	 * Temporary variable used for determining the calculation time
	 */
	private static long cur ;

	/** Sends the given action to the game, logs it and logs the time took by the calculation
	 * @param action The action to send to the game
	 */
	public static void sendAction(Action action) {
		System.out.println(action);
		System.err.println(action);
		System.err.println((System.nanoTime()-cur)/1_000_000+" ms");
	}

	/** Logs debug messages for the game using the System.err stream
	 * @param message The message to send to the game
	 */
	public static void logDebug(String message) {
		System.err.println(message);
	}

	/**
	 * Sets the start for determining the calculation time
	 */
	static void setStart() {
		cur=System.nanoTime();
	}
}
