package de.arm.bot;

import de.arm.bot.io.Input;
import de.arm.bot.io.Output;
import de.arm.bot.ki.KI;

/**
 * The Main class of the bot
 */
public class Main {

    /**
     * The Main routine of this bot
     */
    private Main() {
        Input input = new Input();
        KI ki = input.readInitInfo().generateKI();
        while (true) {
            Output.sendAction(ki.generateNextTurn(input.readTurnInfo()));
        }
    }

    /**
     * The entry point of this application
     *
     * @param args The cmd args
     */
    public static void main(String[] args) {
        new Main();
    }

}
