package no.itema.abcconverter;

import no.itema.abcconverter.model.*;
import no.itema.abcconverter.util.AwesomeException;

import static no.itema.abcconverter.Symbol.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by jih on 14/09/16.
 */
public class ABCToAWEParser {

    public static AWEFile getAWEFileWithDefaultChannel(ABCFile abcFile) throws AwesomeException {
        AWEFile awe = new AWEFile();
        awe.addChannel(-1);
        return getAWEFile(abcFile, awe);
    }

    public static AWEFile getAWEFile(ABCFile abcFile) throws AwesomeException {
        return getAWEFile(abcFile, new AWEFile());
    }

    public static AWEFile getAWEFile(ABCFile abcFile, AWEFile awefile) throws AwesomeException {
        return getAWEFile(abcFile, awefile, false);
    }

    public static AWEFile getAWEFile(ABCFile abcFile, AWEFile awefile, boolean padChannels) throws AwesomeException {

        AWEFile awe = parse(abcFile, awefile);

        handleTies(awe);

        convertToUnifiedTimeSlots(awe);

        if (padChannels) {
            padWithPausesAtEnd(awe);
        }

        return awe;
    }

    public static AWEFile getAWEFile(ABCFile abcFile, boolean padChannels) throws AwesomeException {
        return getAWEFile(abcFile, new AWEFile(), padChannels);
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

    private static AWEFile parse(ABCFile abcFile, AWEFile awe) throws AwesomeException {
        for (String abcLine : abcFile.getLines()) {
            if (abcLine.startsWith("%%MIDI channel")) {
                int instrument = Integer.parseInt(abcLine.replaceAll("[^0-9]", ""));
                awe.addChannel(instrument);
            }

            //skip lines that aren't music.
            if (abcLine.startsWith("%")) {
                continue;
            }
            if (abcLine.contains(":")) {
                continue;
            }

            awe.addLine(parseLine(abcLine));
        }
        return awe;
    }

    private static void handleTies(AWEFile awe) {

        for (AWEChannel channel : awe.getChannels()) {
            for (AWELine line : channel.getLines()) {
                boolean makeContinuation = false;
                for (AWEBar bar : line.getBars()) {
                    if (makeContinuation) {
                        AWEUnit unit = ((AWEUnit)bar.getUnits().get(0));
                        unit.copyValuesFrom(new AWEUnit());
                        unit.setTone(String.valueOf(Symbol.CONTINUATION));

                        makeContinuation = false;
                    }

                    ArrayList<AWETimedUnit> units = bar.getUnits();
                    if (units.get(units.size()-1).isTie()) {
                        makeContinuation = true;
                    }
                }
            }
        }
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
                            List<AWETimedUnit> units = timeSlot.chopOffFromBeginning(prevTimeSlot.remainingSpace());
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
                    if (endLine(sym)) {
                        //wrap up loose ends
                        if (!"".equals(unit.getTone()) && !container.getUnits().contains(unit)) {
                            container.addUnit(unit);
                            if (timeSlot.totalToneLength() > 0 && !bar.getTimeSlots().contains(timeSlot)) {
                                bar.addTimeSlot(timeSlot);
                                if (!line.getBars().contains(bar)) {
                                    line.addBar(bar);
                                }
                            }
                        }
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
                unit.setTie(true);
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


}