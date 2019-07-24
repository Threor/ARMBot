package de.arm.bot.info;

public class Action {

    private final Command command;

    private final String argument;

    private Action(Command command, String argument) {
        this.command = command;
        this.argument = argument;
    }

    public Action(Command command, Direction direction) {
        this(command,direction.toString());
    }

    public Action(Command command) {
        this(command,"");
    }

    @Override
    public String toString() {
        return String.format("%s %s", command, argument);
    }

}
