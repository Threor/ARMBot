package de.arm.bot;

import de.arm.bot.io.Input;
import de.arm.bot.io.Output;
import de.arm.bot.ki.KI;

public class Main {

    private Main() {
        Input input = new Input();
        KI ki = input.readInitInfo().generateKI();
        while (true) {
            Output.logDebug("TURN");
            Output.sendAction(ki.generateNextTurn(input.readTurnInfo()));
        }
    }

    public static void main(String[] args) {
        new Main();
    }

}
