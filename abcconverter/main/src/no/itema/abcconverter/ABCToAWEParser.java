package no.itema.abcconverter;

import no.itema.abcconverter.model.*;
import no.itema.abcconverter.util.AwesomeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

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
        public static final char SLUR_START = '(';
        public static final char SLUR_END = ')';
        public static final char NATURAL = '=';
        public static final char FRACTIONAL_TONE_LENGTH_START = '/';
        public static final char TIE = '-';
    }

    public static AWEFile getAWEFileWithDefaultChannel(ABCFile abcFile) throws AwesomeException {
        AWEFile awe = new AWEFile();
        awe.addChannel();
        return getAWEFile(abcFile, awe, false);
    }

    public static AWEFile getAWEFile(ABCFile abcFile, boolean padChannels) throws AwesomeException {
        return getAWEFile(abcFile, new AWEFile(), padChannels);
    }

    public static AWEFile getAWEFile(ABCFile abcFile, AWEFile awefile, boolean padChannels) throws AwesomeException {

        AWEFile awe = parse(abcFile, awefile);

        convertToUnifiedTimeSlots(awe);

        dropEmptyChannels(awe);

        if (padChannels) {
            padWithPausesAtEnd(awe);
        }

        return awe;
    }

    private static void padWithPausesAtEnd(AWEFile awe) {
        int maxBars = 0;
        for (AWEChannel c : awe.getChannels()) {
            int numBars = c.getBars().size();
            maxBars = numBars > maxBars ? numBars : maxBars;
        }
        final int max = maxBars;
        awe.getChannels().forEach(c -> {
            List<AWEBar> bars = c.getBars();
            if (bars.size() == 0) return;
            bars.get(bars.size()-1).padWithPausesAtEnd(8);
            int numBars = bars.size();
            if (numBars < max) {
                AWELine line = c.getLines().get(c.getLines().size()-1);
                for (int i = 0; i < (max-numBars); i ++) {
                    AWEBar bar = new AWEBar();
                    bar.padWithPausesAtEnd(8);
                    line.addBar(bar);
                }
            }
        });
    }

    private static void dropEmptyChannels(AWEFile awe) {
        awe.setChannels(awe.getChannels().stream().filter(c -> c.getLines().size() > 0).collect(Collectors.toList()));
    }

    private static AWEFile parse(ABCFile abcFile, AWEFile awe) throws AwesomeException {
        String lineConcat = "";
        for (String abcLine : abcFile.getLines()) {
            if (abcLine.startsWith("V:")) {
                if (!"".equals(lineConcat)) {
                    awe.addLine(parseLine(lineConcat));
                    lineConcat = "";
                }
                awe.addChannel();
                AWEChannel channel = awe.getChannels().get(awe.getChannels().size()-1);
                channel.setInstrument(1); //default to piano, if any instrument is set in the file, this will be overwritten
            }
            if (abcLine.startsWith("%%MIDI program")) {
                //note: its possible for a channel to change instruments throughout the file. we ignore that for now, and use whatever instrument type is set last
                int instrument = Integer.parseInt(abcLine.replaceAll("[^0-9]", ""));
                if (awe.getChannels().size() > 0 && instrument > 0) {
                    AWEChannel channel = awe.getChannels().get(awe.getChannels().size()-1);
                    channel.setInstrument(instrument + 1);//+1, because ABC stores them 0-indexed, we have them stored 1-indexed
                }
            }

            if (abcLine.startsWith("L:") && !abcLine.contains("1/8")) {
                throw new AwesomeException("Currently only supporting L:1/8. Found: " + abcLine);
            }
            //skip lines that aren't music.
            if (abcLine.startsWith("%")) {
                continue;
            }
            if (abcLine.contains(":")) {
                continue;
            }

            lineConcat += abcLine;
            lineConcat += " ";
        }
        if (!"".equals(lineConcat)) {
            awe.addLine(parseLine(lineConcat));
        }
        return awe;
    }

    private static void convertToUnifiedTimeSlots(AWEFile awe) {
        for (AWEChannel channel : awe.getChannels()) {
            for (AWELine line : channel.getLines()) {
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
                //if(endOfLastUnit(unit, sym) && unit != null) {
                //    container.addUnit(unit);
                //}
                insideChord = true;
            }
            if(chordEnd(sym)) {
                insideChord = false;
            }
            if(endOfLastUnit(unit, sym)) {
                if(unit != null && !endLine(sym) && !"".equals(unit.getTone())) {
                    container.addUnit(unit);
                }
                if (chordStart(sym)) {
                    AWEChord chord = new AWEChord();
                    container = chord;
                    timeSlot.addUnit(chord);
                } else if (!insideChord && timeSlot.getUnits().size() > 0) {
                    bar.addTimeSlot(timeSlot);
                    timeSlot = new AWETimeSlot();
                    container = timeSlot;
                }
                if (bar(sym)) {
                    line.addBar(bar);
                    bar = new AWEBar();
                }
                if (unit != null && endLine(sym)) {
                    //wrap up loose ends
                    if (!"".equals(unit.getTone()) && !container.getUnits().contains(unit)) {
                        container.addUnit(unit);
                    }
                    if (timeSlot.totalToneLength() > 0 && !bar.getTimeSlots().contains(timeSlot)) {
                        bar.addTimeSlot(timeSlot);
                    }
                    if (bar.getTimeSlots().size() > 0 && !line.getBars().contains(bar) && bar.getTotalToneLength() > 0) {
                        line.addBar(bar);
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
            if(natural(sym)) {
                unit.addSymbol(String.valueOf(sym));
            }
            if(tie(sym)) {
                //we use dash for something else, represent ties with another symbol
                //TODO: this isn't quite perfect, we should represent the tie as one long with continuations (-) instead of two tied notes, but it will do for now.
                unit.addSymbol("¤");
            }
            if(slurStart(sym) || slurEnd(sym)) {
                throw new AwesomeException("Slurs are not handled at this point");
            }


            if(toneLength(sym)) {
                // Create a new unit
                int length = Integer.parseInt(String.valueOf(sym));
                if (unit.getToneLengthIsFractional()) {
                    unit.setToneLengthDenominator(length);
                } else {
                    unit.setToneLengthNumerator(length);
                }
                boolean unitIsDone = (i+1 == symbols.length) || (!fractionalToneLengthStart(symbols[i+1]) && !tie(symbols[i+1]));
                if (unitIsDone) {
                    container.addUnit(unit);
                    unit = new AWEUnit();
                    if (!insideChord) {
                        bar.addTimeSlot(timeSlot);
                        timeSlot = new AWETimeSlot();
                        container = timeSlot;
                    }
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
    private static boolean natural(char c) { return c == Symbol.NATURAL; }
    private static boolean tie(char c) { return c == Symbol.TIE; }

    private static boolean chordStart(char c) {
        return c == Symbol.CHORD_START;
    }
    private static boolean chordEnd(char c) {
        return c == Symbol.CHORD_END;
    }
    private static boolean slurStart(char c) {
        return c == Symbol.SLUR_START;
    }
    private static boolean slurEnd(char c) {
        return c == Symbol.SLUR_END;
    }

}
