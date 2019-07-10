package de.arm.bot;

import de.arm.bot.dca.DCARunnable;
import de.arm.bot.io.Input;
import de.arm.bot.io.Output;
import de.arm.bot.ki.KI;

public class Main {

	public Main() {
		Input input=new Input();
		KI ki=input.readInitInfo().generateKI();
		Thread dcaThread=new Thread(new DCARunnable(ki.getMaze()));
		dcaThread.start();
		while(true) {
			Output.sendAction(ki.generateNextTurn(input.readTurnInfo()));
		}
	}
	
	public static void main(String[] args) {
		new Main();
	}

}
