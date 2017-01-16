package no.itema.abcconverter;

import no.itema.abcconverter.model.*;
import no.itema.abcconverter.util.AwesomeException;

import static no.itema.abcconverter.Symbol.*;
/**
 * Created by Lars on 2017-01-16.
 */
public class AWEToABCParser extends ABCToAWEParser {

    public static AWEFile getABCFile(ABCFile abcFile, AWEFile awefile) throws AwesomeException {

        AWEFile awe = parse(abcFile, awefile);

        convertToAbcTimeSlots(awe);

        awe.getAWELine(0, 0).getAbcString();
        return awe;
    }

    private static void convertToAbcTimeSlots(AWEFile awe) {
        //TODO
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
                throw new AwesomeException("Don't know how to handle ABC ties yet");
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
}
