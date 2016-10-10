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
        public static final char CHORD_START = '[';
        public static final char CHORD_END = ']';
    }

    public static AWEFile getAWEFile(ABCFile abcFile) {
        AWEFile awe = new AWEFile();
        List<String> lines = abcFile.getLines();
        if (lines.size() < 100000) { //skip crazy big files, they're just mistakes by midi2abc
            for (String abcLine : abcFile.getLines()) {
                awe.addLine(parseLine(abcLine));
            }
        }
        return awe;
    }

    private static AWELine parseLine(String abcLine) {

        abcLine += Symbol.LINE_END;
        char[] symbols = abcLine.trim().toCharArray();
        AWEUnit unit = null;
        AWELine line = new AWELine();
        AWEBar bar = new AWEBar();
        AWETimeSlot timeSlot = new AWETimeSlot();
        boolean insideChord = false;
        for(char sym: symbols) {
            if(chordStart(sym)) {
                insideChord = true;
            }
            if(chordEnd(sym)) {
                insideChord = false;
            }
            if(endOfLastUnit(unit, sym)) {
                if(unit != null) {
                    if(!endLine(sym) && !"".equals(unit.getTone())) {
                        timeSlot.addUnit(unit);
                        if(!insideChord) {
                            bar.addTimeSlot(timeSlot);
                            timeSlot = new AWETimeSlot();
                        }
                    }
                    if (bar(sym)) {
                        line.addBar(bar);
                        bar = new AWEBar();
                    }
                }

                unit = new AWEUnit();
                if(!insideChord) {
                    timeSlot = new AWETimeSlot();
                }
            }

            if(toneHeight(sym)) {
                unit.setTone(String.valueOf(sym));
            }
            if(sharp(sym) || flat(sym)) {
                unit.setTransp(unit.getTransp() + String.valueOf(sym));
            }
            if(toneLength(sym)) {
                // Create a new unit
                timeSlot.addUnit(unit);
                bar.addTimeSlot(timeSlot);
                for(int i=0; i<getNumOfCopies(sym)-1; i++) {
                    unit = new AWEUnit();
                    unit.setTone(String.valueOf(Symbol.COPY));
                    timeSlot = new AWETimeSlot();
                    timeSlot.addUnit(unit);
                    bar.addTimeSlot(timeSlot);
                }
                unit = new AWEUnit();
                timeSlot = new AWETimeSlot();
            }
            if(octaveUp(sym) || octaveDown(sym)) {
                unit.setOctave(String.valueOf(sym));
            }
        }

        return line;
    }

    private static boolean endOfLastUnit(AWEUnit unit, char sym) {
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
                (unit.getTone() != "" && (sharp(sym)) || flat(sym)) ||  // Tone exists, but there is a new first symbol(sharp/flat)
                (unit.getTone() != "" && toneHeight(sym)) ||            // Tone exists, and there is a new tone
                (unit.getTone() == String.valueOf(Symbol.PAUSE)) ||     // Tone exists, and is a pause
                chordStart(sym) ||                                      // Start of a new chord
                chordEnd(sym)   ||                                        // End of a chord
                bar(sym) ||                                             // End of bar
                endLine(sym);                                           // End of line
    }
    private static boolean bar(char c) {
        return c == Symbol.BAR;
    }
    private static boolean endLine(char c) {
        return c == Symbol.LINE_END;
    }
    private static int getNumOfCopies(char c) {
        return Integer.parseInt(String.valueOf(c));
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

    private static boolean chordStart(char c) {
        return c == Symbol.CHORD_START;
    }
    private static boolean chordEnd(char c) {
        return c == Symbol.CHORD_END;
    }

}
