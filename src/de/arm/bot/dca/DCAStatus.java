package de.arm.bot.dca;

public enum DCAStatus {

    C0,
    C1,
    C2,
    C3,
    C4;

    public static DCAStatus get(int of) {
        switch (of) {
            case 0:
                return C0;
            case 1:
                return C1;
            case 2:
                return C2;
            case 3:
                return C3;
            case 4:
                return C4;
            default:
                return null;
        }
    }
}
