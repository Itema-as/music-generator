package no.itema.abcconverter;

import no.itema.abcconverter.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jih on 14/09/16.
 */
public class ABCToAWEParser {

    public class Symbol {
        public static final char BAR = '|';
        public static final char LINE_END = '!';
        public static final char PAUSE = 'x';
        public static final char SHARP = '^';
        public static final char FLAT = '_';
        public static final char COPY = '-';
        public static final char OCT_UP = '\'';
        public static final char OCT_DOWN = ',';
    }



    public static AWEFile getAWEFormat(ABCFile abcFile) {
        AWEFile awe = new AWEFile();
        for(String abcLine: abcFile.getLines()) {
            awe.addLine(parseLine(abcLine));
        }
        return awe;
    }

    private static AWELine parseLine(String abcLine) {

        abcLine += Symbol.LINE_END;
        char[] symbols = abcLine.trim().toCharArray();
        AWEUnit unit = null;
        AWELine line = new AWELine();
        AWEBar bar = new AWEBar();

        for(char sym: symbols) {
            if(endOfLastUnit(unit, sym)) {
                if(unit != null) {
                    if(!endLine(sym) && !"".equals(unit.getTone())) {
                        bar.addUnit(unit);
                    }
                    if (bar(sym)) {
                        line.addBar(bar);
                        bar = new AWEBar();
                    }
                }
                unit = new AWEUnit();
            }

            if(toneHeight(sym)) {
                unit.setTone(String.valueOf(sym));
            }
            if(sharp(sym) || flat(sym)) {
                unit.setTransp(unit.getTransp() + String.valueOf(sym));
            }
            if(toneLength(sym)) {
                // Create a new unit
                bar.addUnit(unit);
                for(int i=0; i<getNumOfCopies(sym); i++) {
                    unit = new AWEUnit();
                    unit.setTone(String.valueOf(Symbol.COPY));
                    bar.addUnit(unit);
                }
                unit = new AWEUnit();
            }
            if(octaveUp(sym) || octaveDown(sym)) {
                unit.setOctave(String.valueOf(sym));
            }
        }

        return line;
    }

    private static boolean endOfLastUnit(AWEUnit unit, char sym) {
        System.out.println("Symbol " + sym + ": " + (unit == null) + ":");
        if(unit != null) {
            System.out.println(
                    (unit.getTone() != "" && sharp(sym)) + ", " +
                    (unit.getTone() != "" && flat(sym)) + ", " +
                    (unit.getTone() != "" && toneHeight(sym)) + ", " +
                    (unit.getTone() == "x") + ", " +
                    (endLine(sym)));
        }

        return unit == null || // This is the first unit
                (unit.getTone() != "" && (sharp(sym)) || flat(sym)) || // Tone exists, but there is a new first symbol(sharp/flat)
                (unit.getTone() != "" && toneHeight(sym)) || // Tone exists, and there is a new tone
                (unit.getTone() == String.valueOf(Symbol.PAUSE)) ||
                bar(sym) || // End of bar
                endLine(sym); // End of line
    }
    private static boolean bar(char sym) {
        return sym == Symbol.BAR;
    }
    private static boolean endLine(char sym) {
        return sym == Symbol.LINE_END;
    }
    private static int getNumOfCopies(char sym) {
        int len = Integer.parseInt(String.valueOf(sym));
        return (len/2)-1;
    }
    private static boolean toneHeight(char c) {
        return Character.isLetter(c);
    }
    private static boolean toneLength(char c) {
        return Character.isDigit(c);
    }
    private static boolean sharp(char c) {
        return c == Symbol.SHARP;
    }
    private static boolean flat(char c) {
        return c == Symbol.FLAT;
    }
    private static boolean octaveUp(char c) {
        return c == Symbol.OCT_UP;
    }
    private static boolean octaveDown(char c) {
        return c == Symbol.OCT_DOWN;
    }

}
