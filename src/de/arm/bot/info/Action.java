package de.arm.bot.info;

/**
 * A class representing an Action that can be sent to the game.
 * An action consists of a command that is defined by the given protocols and an (optional) additional argument.
 *
 * @author Team ARM
 * @see de.arm.bot.info.Command
 */
public class Action {

    /**
     * The comment that should be send to the game.
     * All available commands are defined by the protocol
     */
    private final Command command;

    /**
     * An argument that can be sent additionally after the command
     */
    private final String argument;

    /**
     * The constructor of this class that initializes all fields. Only used internally
     *
     * @param command  The command of this action
     * @param argument The argument of this action
     */
    private Action(Command command, String argument) {
        this.command = command;
        this.argument = argument;
    }

    /**
     * The constructor of this class that uses directions as additional argument
     *
     * @param command   The command of this action
     * @param direction The direction towards the command should head
     */
    public Action(Command command, Direction direction) {
        this(command, direction.toString());
    }

    /**
     * The constructor of this class that is used if an action consists of only one command without arguments
     *
     * @param command The command of this action
     */
    public Action(Command command) {
        this(command, "");
    }

    @Override
    public String toString() {
        return String.format("%s %s", command, argument);
    }

}
