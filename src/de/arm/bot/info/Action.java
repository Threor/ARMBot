package de.arm.bot.info;

public class Action {

    private Command command;

    private String argument;

    public Action(Command command, String argument) {
        this.command = command;
        this.argument = argument;
    }

    public Action(Command command) {
        this.command = command;
        this.argument = "";
    }

    /**
     * @return command
     */
    public Command getCommand() {
        return command;
    }

    /**
     * @param command das zu setzende Objekt command
     */
    public void setCommand(Command command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return String.format("%s %s", command, argument);
    }

}
