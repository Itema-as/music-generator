package no.itema.abcconverter;

import no.itema.abcconverter.model.AWEUnit;

/**
 * Created by Lars on 2017-01-16.
 */

public class Symbol {
    public static final char BAR = '|';
    public static final char LINE_END = '!';
    public static final char PAUSE = 'x';
    public static final char SHARP = '^';
    public static final char FLAT = '_';
    public static final char CONTINUATION = '☃';
    public static final char OCT_UP = '\'';
    public static final char OCT_DOWN = ',';
    public static final char CHORD_START = '[';
    public static final char CHORD_END = ']';
    public static final char NATURAL = '=';
    public static final char FRACTIONAL_TONE_LENGTH_START = '/';
    public static final char TIE = '-';
    public static final char SLUR_START = '(';
    public static final char SLUR_END = ')';

    public static boolean endOfLastUnit(AWEUnit unit, char sym) {
        //System.out.println("Symbol " + sym + ": " + (unit == null) + ":");
        if(unit != null) {
            /*
            System.out.println(
                    (unit.getTone() != "" && sharp(sym)) + ", " +
                    (unit.getTone() != "" && flat(sym)) + ", " +
                    (unit.getTone() != "" && toneHeight(sym)) + ", " +
                    (unit.getTone() == "x") + ", " +
                    (endLine(sym)));
            */
        }

        return unit == null ||                                          // This is the first unit
                unit.isContinuation() ||
                continuation(sym) ||
                (unit.getTone() != "" && (sharp(sym)) || flat(sym)) ||  // Tone exists, but there is a new first symbol(sharp/flat)
                (unit.getTone() != "" && toneHeight(sym)) ||            // Tone exists, and there is a new tone
                (unit.getTone() == String.valueOf(Symbol.PAUSE)) ||     // Tone exists, and is a pause
                chordStart(sym) ||                                      // Start of a new chord
                chordEnd(sym)   ||                                      // End of a chord
                bar(sym) ||                                             // End of bar
                endLine(sym);                                           // End of line
    }


    public static boolean fractionalToneLengthStart(char c) { return c == Symbol.FRACTIONAL_TONE_LENGTH_START; }
    public static boolean bar(char c) {
        return c == Symbol.BAR;
    }
    public static boolean endLine(char c) {
        return c == Symbol.LINE_END;
    }
    public static boolean continuation(char c) { return c == Symbol.CONTINUATION; }
    public static boolean toneHeight(char c) {
        return Character.isLetter(c);
    }
    public static boolean toneLength(char c) {
        return Character.isDigit(c);
    }
    public static boolean sharp(char c) {
        return c == Symbol.SHARP;
    }
    public static boolean flat(char c) {
        return c == Symbol.FLAT;
    }
    public static boolean octaveUp(char c) {
        return c == Symbol.OCT_UP;
    }
    public static boolean octaveDown(char c) { return c == Symbol.OCT_DOWN; }
    public static boolean natural(char c) { return c == Symbol.NATURAL; }
    public static boolean tie(char c) { return c == Symbol.TIE; }
    public static boolean slurStart(char c) { return c == Symbol.SLUR_START; }
    public static boolean slurEnd(char c) { return c == Symbol.SLUR_END; }

    public static boolean chordStart(char c) {
        return c == Symbol.CHORD_START;
    }
    public static boolean chordEnd(char c) {
        return c == Symbol.CHORD_END;
    }
}
