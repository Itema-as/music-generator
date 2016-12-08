package no.itema.abcconverter;

import no.itema.abcconverter.model.*;
import no.itema.abcconverter.util.AwesomeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

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
        public static final char FRACTIONAL_TONE_LENGTH_START = '/';

    }

    public static AWEFile getAWEFile(ABCFile abcFile) throws AwesomeException {

        AWEFile awe = parse(abcFile);

        convertToUnifiedTimeSlots(awe);

        return awe;
    }

    private static AWEFile parse(ABCFile abcFile) throws AwesomeException {
        AWEFile awe = new AWEFile();
        for (String abcLine : abcFile.getLines()) {
            //TODO: skip lines that aren't music.
            //-detect instrument indications: %%MIDI channel 10
            //-put music into seperate buckets per instrument track
            //-finally check that all instrument tracks have same number of units, throw otherwise
            awe.addLine(parseLine(abcLine));
        }
        return awe;
    }

    private static void convertToUnifiedTimeSlots(AWEFile awe) {

        for (AWELine line : awe.getLines()) {
            for (AWEBar bar : line.getBars()) {
                List<AWETimeSlot> timeSlots = bar.getTimeSlots();
                ListIterator<AWETimeSlot> iterator = timeSlots.listIterator();
                AWETimeSlot prevTimeSlot = null;
                while (iterator.hasNext()) {
                    AWETimeSlot timeSlot = iterator.next();

                    if (prevTimeSlot != null && !prevTimeSlot.isFilled()) {
                        List<AWETimedUnit> units = timeSlot.chopOfFromBeginning(prevTimeSlot.remainingSpace());
                        for (AWETimedUnit unit : units) {
                            prevTimeSlot.addUnit(unit);
                        }
                        if (timeSlot.totalToneLength() == 0) {
                            iterator.remove();
                            continue;
                        }
                    }


                    //split timeslot into multiple, if it is overflowing
                    while (timeSlot.overflows()) {
                        List<AWETimedUnit> overflow = timeSlot.overflows()
                                ? timeSlot.chopOfOverflow()
                                : null;

                        if (overflow != null && overflow.size() > 0) {
                            timeSlot = new AWETimeSlot(overflow);
                            iterator.add(timeSlot); //add after the current, but before what will be returned by iterator.next()
                        } else {
                            break;
                        }
                    }
                    prevTimeSlot = timeSlot;
                }
            }
        }
    }

    private static AWELine parseLine(String abcLine) throws AwesomeException {

        abcLine += Symbol.LINE_END;
        char[] symbols = abcLine.trim().toCharArray();
        AWEUnit unit = null;
        AWELine line = new AWELine();
        AWEBar bar = new AWEBar();
        AWETimeSlot timeSlot = new AWETimeSlot();
        AWEUnitContainer container = timeSlot;

        boolean insideChord = false;
        for (int i = 0; i < symbols.length; i++) {
            char sym = symbols[i];
            if(chordStart(sym)) {
                AWEChord chord = new AWEChord();
                container = chord;
                timeSlot.addUnit(chord);
                insideChord = true;
            }
            if(chordEnd(sym)) {
                insideChord = false;
            }
            if(endOfLastUnit(unit, sym)) {
                if(unit != null) {
                    if(!endLine(sym) && !"".equals(unit.getTone())) {
                        container.addUnit(unit);
                        if(!insideChord) {
                            bar.addTimeSlot(timeSlot);
                            timeSlot = new AWETimeSlot();
                            container = timeSlot;
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
                    container = timeSlot;
                }
            }

            if(fractionalToneLengthStart(sym)) {
                unit.setToneLengthIsFractional(true);
            }
            if(toneHeight(sym)) {
                unit.setTone(String.valueOf(sym));
            }
            if(sharp(sym) || flat(sym)) {
                unit.setTransp(unit.getTransp() + String.valueOf(sym));
            }

            if(toneLength(sym)) {
                // Create a new unit
                int length = Integer.parseInt(String.valueOf(sym));
                if (unit.getToneLengthIsFractional()) {
                    unit.setToneLengthDenominator(length);
                } else {
                    unit.setToneLengthNumerator(length);
                }
                boolean unitIsDone = (i+1 == symbols.length) || !fractionalToneLengthStart(symbols[i+1]);
                if (unitIsDone) {
                    container.addUnit(unit);
                    unit = new AWEUnit();
                    bar.addTimeSlot(timeSlot);
                    timeSlot = new AWETimeSlot();
                    container = timeSlot;
                }
            }

            if(octaveUp(sym) || octaveDown(sym)) {
                unit.addOctave(String.valueOf(sym));
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
                chordEnd(sym)   ||                                      // End of a chord
                bar(sym) ||                                             // End of bar
                endLine(sym);                                           // End of line
    }
    private static boolean fractionalToneLengthStart(char c) { return c == Symbol.FRACTIONAL_TONE_LENGTH_START; }
    private static boolean bar(char c) {
        return c == Symbol.BAR;
    }
    private static boolean endLine(char c) {
        return c == Symbol.LINE_END;
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
    private static boolean octaveDown(char c) { return c == Symbol.OCT_DOWN; }

    private static boolean chordStart(char c) {
        return c == Symbol.CHORD_START;
    }
    private static boolean chordEnd(char c) {
        return c == Symbol.CHORD_END;
    }

}
