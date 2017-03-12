package no.itema.abcconverter;

import no.itema.abcconverter.model.*;
import no.itema.abcconverter.util.AwesomeException;

import java.util.ArrayList;
import java.util.List;

import static no.itema.abcconverter.Symbol.*;
/**
 * Created by jih on 16/01/2017.
 */
public class AWEToABCParser {
    public static ABCFile getABCFile(AWEFile aweFile) {
        ABCFile abcFile = new ABCFile("");

        return abcFile;
    }

    public static AWEFile getABCFile(List<String> lines) throws AwesomeException {

        AWEFile aweFile = parse(lines);

        //addTies(aweFile);
        convertToAbcTimeSlots(aweFile);

        return aweFile;
    }


    private static AWEFile parse(List<String> lines) throws AwesomeException {

        AWEFile aweFile = new AWEFile();
        //aweFile.addChannel(0); //hack
        aweFile.addChannel(); //hack
        for(String line : lines) {
            aweFile.addLine(parseLine(line));
        }

        return aweFile;
    }

    private static void convertToAbcTimeSlots(AWEFile awe) {
        for (AWEChannel channel : awe.getChannels()) {
            for (AWELine line : channel.getLines()) {
                AWEBar prevBar = null;
                for (AWEBar bar : line.getBars()) {
                    AWEUnit prevUnit = null;
                    for (AWETimedUnit timedUnit : bar.getUnits()) {
                        if (timedUnit instanceof AWEUnit) {
                            AWEUnit unit = (AWEUnit)timedUnit;
                            if (unit.isContinuation()) {
                                prevUnit = handleContinuations(prevBar, prevUnit, unit);
                            } else {
                                prevUnit = unit;
                            }
                        }
                    }
                    prevBar = bar;
                }
            }
        }
    }

    private static AWEUnit handleContinuations(AWEBar prevBar, AWEUnit prevUnit, AWEUnit unit) {
        if (prevUnit == null) { //this is a tie, turn continuation into a note
            ArrayList<AWETimedUnit> units = prevBar.getUnits();
            for (int i = units.size()-1; i >= 0; i--) {
                if (!units.get(i).isContinuation() && units.get(i) instanceof AWEUnit){
                    unit.copyValuesFrom(((AWEUnit)units.get(i)));
                    ((AWEUnit)units.get(i)).setTie(true); //make sure the previous non-continuation is a tie.
                    prevUnit = unit;
                    break;
                }
            }
        } else {
            prevUnit.setToneLengthNumerator(prevUnit.getToneLengthDenominator() + prevUnit.getToneLengthNumerator());
        }
        return prevUnit;
    }

    private static AWEFile parse(ABCFile abcFile, AWEFile awe) throws AwesomeException {
        for (String abcLine : abcFile.getLines()) {
            awe.addLine(parseLine(abcLine));
        }
        return awe;
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
                if(unit != null) {
                    if(!endLine(sym) && (!"".equals(unit.getTone()) || unit.isContinuation())) {
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

            if(chordStart(sym)) {
                AWEChord chord = new AWEChord();
                container = chord;
                timeSlot.addUnit(chord);
            }

            if(fractionalToneLengthStart(sym)) {
                unit.setToneLengthIsFractional(true);
            }
            if(toneHeight(sym)) {
                unit.setTone(String.valueOf(sym));
            }
            if(continuation(sym)) {
                unit.setIsContinuation(true);
            }
            if(sharp(sym) || flat(sym)) {
                unit.setTransp(unit.getTransp() + String.valueOf(sym));
            }
            if(natural(sym)) {
                unit.addSymbol(String.valueOf(sym));
            }
            if(tie(sym)) {
                throw new AwesomeException("Don't know how to handle ABC ties yet");
            }
            if (slur(sym)) {
                throw new AwesomeException("Don't know how to handle ABC slurs yet");
            }


            if(toneLength(sym)) {
                // Create a new unit
                int length = Integer.parseInt(String.valueOf(sym));
                if (unit.getToneLengthIsFractional()) {
                    unit.setToneLengthDenominator(length);
                } else {
                    unit.setToneLengthNumerator(length);
                }
                boolean unitIsDone = ((i+1 == symbols.length) || !fractionalToneLengthStart(symbols[i+1])) && !tie(symbols[i+1]);
                if (unitIsDone) {
                    container.addUnit(unit);
                    unit = new AWEUnit();
                    bar.addTimeSlot(timeSlot);
                    timeSlot = new AWETimeSlot();
                    if (!insideChord) {
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
