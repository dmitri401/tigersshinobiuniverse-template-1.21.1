package com.dmitri401.tigersshinobiuniverse.jutsu;

/**
 * Defines the eight possible three-input combinations made from two hand signs.
 *
 * Slot order:
 * 1 = 1-1-1
 * 2 = 1-1-2
 * 3 = 1-2-1
 * 4 = 1-2-2
 * 5 = 2-1-1
 * 6 = 2-1-2
 * 7 = 2-2-1
 * 8 = 2-2-2
 */
public final class JutsuSlots {

    public static final int SLOT_COUNT = 8;
    public static final int SEQUENCE_LENGTH = 3;

    private JutsuSlots() {
    }

    public static int getSlotForSequence(
            int first,
            int second,
            int third
    ) {
        validateSign(first);
        validateSign(second);
        validateSign(third);

        int binaryValue =
                ((first - 1) << 2)
                        | ((second - 1) << 1)
                        | (third - 1);

        return binaryValue + 1;
    }

    public static String getSequenceText(int slot) {
        if (slot < 1 || slot > SLOT_COUNT) {
            throw new IllegalArgumentException(
                    "Jutsu slot must be between 1 and 8"
            );
        }

        int value = slot - 1;

        int first = ((value >> 2) & 1) + 1;
        int second = ((value >> 1) & 1) + 1;
        int third = (value & 1) + 1;

        return first + " - " + second + " - " + third;
    }

    /**
     * Placeholder assignment hook. Later this can read the player's saved
     * jutsu loadout instead of returning an empty slot.
     */
    public static String getAssignedJutsuName(int slot) {
        return "Empty";
    }

    private static void validateSign(int sign) {
        if (sign != 1 && sign != 2) {
            throw new IllegalArgumentException(
                    "Hand sign must be 1 or 2"
            );
        }
    }
}
