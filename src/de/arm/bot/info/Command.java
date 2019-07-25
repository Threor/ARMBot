package de.arm.bot.info;

/**
 * An enumeration of commands that can be send towards the game.
 * All available commands are defined in the game protocol.
 */
public enum Command {

    /**
     * The command that will be send if the player wants to move
     */
    GO,
    /**
     * The command that is used to pickup an item (i.e. forms or sheets)
     */
    TAKE,
    /**
     * The command used for kicking forms
     */
    KICK,
    /**
     * An unused command for retrieving information about the current position
     */
    POSITION,
    /**
     * The command that is used to put down a sheet
     */
    PUT,
    /**
     * The last command that will be sent if the bot thinks he can finish
     */
    FINISH;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
