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
            AWEBar lastbar = bars.get(bars.size()-1);
            lastbar.padWithPausesAtEnd(8);
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
        String lineConcat = "";
        for (String abcLine : abcFile.getLines()) {
            if (abcLine.startsWith("V:")) {
                if (!"".equals(lineConcat)) {
                    awe.addLine(parseLine(lineConcat));
                    lineConcat = "";
                }
                awe.addChannel();
            }
            if (abcLine.startsWith("%%MIDI channel")) {
                int channel = Integer.parseInt(abcLine.replaceAll("[^0-9]", ""));
                if (channel == 10) {
                    AWEChannel lastChannel = awe.getChannels().get(awe.getChannels().size()-1);
                    lastChannel.setIsDrums(true);
                } else {
                    throw new AwesomeException("Unexpected channel number: "+channel);
                }
            }
            if (abcLine.startsWith("%%MIDI program")) {
                int instrument = Integer.parseInt(abcLine.replaceAll("[^0-9]", ""));
                AWEChannel lastChannel = awe.getChannels().isEmpty() ? new AWEChannel() : awe.getChannels().get(awe.getChannels().size()-1);
                lastChannel.setInstrument(instrument);
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

    private static void handleTies(AWEFile awe) {

        for (AWEChannel channel : awe.getChannels()) {
            for (AWELine line : channel.getLines()) {
                boolean makeContinuation = false;
                for (AWEBar bar : line.getBars()) {
                    if (makeContinuation) {
                        AWETimedUnit u = bar.getUnits().get(0);
                        if (u instanceof AWEUnit) {
                            AWEUnit unit = ((AWEUnit)u);
                        unit.copyValuesFrom(new AWEUnit());
                        unit.setTone(String.valueOf(Symbol.CONTINUATION));
                        } else {
                            AWEChord chord = ((AWEChord)u);
                            for (AWETimedUnit tu : chord.getUnits()) {
                                AWEUnit unit = ((AWEUnit)tu);
                                unit.copyValuesFrom(new AWEUnit());
                                unit.setTone(String.valueOf(Symbol.CONTINUATION));
                            }
                        }

                        makeContinuation = false;
                    }

                    ArrayList<AWETimedUnit> units = bar.getUnits();
                    AWETimedUnit lastUnit = units.get(units.size()-1);
                    if (lastUnit instanceof AWEChord) {
                        AWEChord chord = ((AWEChord)lastUnit);
                        lastUnit = chord.getUnits().get(chord.getUnits().size()-1);
                    }
                    if (lastUnit.isTie()) {
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
                        if(!insideChord) {
                            bar.addTimeSlot(timeSlot);
                            timeSlot = new AWETimeSlot();
                            container = timeSlot;
                        }
                    }
                    if (timeSlot.totalToneLength() > 0 && !bar.getTimeSlots().contains(timeSlot)) {
                        bar.addTimeSlot(timeSlot);
                    }
                    if (bar.getTimeSlots().size() > 0 && !line.getBars().contains(bar) && bar.getTotalToneLength() > 0) {
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
            if(natural(sym)) {
                unit.addSymbol(String.valueOf(sym));
            }
            if(tie(sym)) {
                unit.setTie(true);
            }
            if (slurStart(sym)) {
                unit.setSlurStart(true);
            }
            if (slurEnd(sym)) {
                unit.setSlurEnd(true);
            }

            if(toneLength(sym) || unit.isSlurEnd() || unit.isSlurStart()) {
                // Create a new unit
                if (!unit.isSlurEnd() && !unit.isSlurStart()) {
                    int length = Integer.parseInt(String.valueOf(sym));
                    if (unit.getToneLengthIsFractional()) {
                        unit.setToneLengthDenominator(length);
                    } else {
                        unit.setToneLengthNumerator(length);
                    }
                }
                boolean unitIsDone = (i+1 == symbols.length) || (!fractionalToneLengthStart(symbols[i+1]) && !tie(symbols[i+1]));
                if (unitIsDone) {
                    if (unit.isSlurEnd()) {
                        unit.setTone(unit.getTone() + ")");
                    }
                    if (unit.isSlurStart()) {
                        unit.setTone("(" + unit.getTone());
                    }
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