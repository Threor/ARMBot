package de.arm.bot;

import de.arm.bot.io.Input;
import de.arm.bot.io.Output;
import de.arm.bot.ki.KI;

public class Main {

	private Input input;
	private KI ki;
	
	public Main() {
		this.input=new Input();
		this.ki=input.readInitInfo().generateKI();
		while(input.hasData()) {
			Output.sendAction(ki.generateNextTurn(input.readTurnInfo()));
		}
		
	}
	
	public static void main(String[] args) {
		new Main();
	}

}
