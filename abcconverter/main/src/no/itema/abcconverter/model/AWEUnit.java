package no.itema.abcconverter.model;

import no.itema.abcconverter.ABCToAWEParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jih on 14/09/16.
 */
public class AWEUnit implements AWETimedUnit {

    List<String> symbols;
    String tone;
    String transp;
    List<String> octaves;
    private String natural;
    private boolean toneLengthIsFractional;
    private double toneLengthNumerator;
    private double toneLengthDenominator;

    public AWEUnit() {
        symbols = new ArrayList<String>();
        tone = "";
        transp = "";
        natural = "";
        octaves = new ArrayList<String>();
        toneLengthNumerator = 1;
        toneLengthDenominator = 1;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public String getUnitString() {
        return String.join("", symbols) + transp + tone + String.join("", octaves) + toneLengthString();
    }

    public void addSymbol(String symbol) {
        symbols.add(symbol);
    }

    public List<String> getOctaves() {
        return octaves;
    }

    public void addOctave(String octave) { this.octaves.add(octave);  }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getTransp() {
        return transp;
    }

    public void setTransp(String transp) {
        this.transp = transp;
    }

    //toneLengthIsFractional is just a flag used during parsing, indicating if we've seen a /
    public void setToneLengthIsFractional(boolean toneLengthIsFractional) { this.toneLengthIsFractional = toneLengthIsFractional; }

    public boolean getToneLengthIsFractional() { return this.toneLengthIsFractional; }

    private String toneLengthString() {
        double toneLength = getToneLength();
        return toneLength < (1 - 0.00001)
                ? ("" + ABCToAWEParser.Symbol.FRACTIONAL_TONE_LENGTH_START + Math.round(1/toneLength))
                : toneLength > (1 + 0.00001)
                    ? "" + Math.round(toneLength)
                    : "";
    }

    public void setToneLengthDenominator(double toneLengthDenominator) {
        if (toneLengthDenominator <= 0) throw new IllegalArgumentException("Tone length must be positive");

        this.toneLengthDenominator = toneLengthDenominator;
    }

    public void setToneLengthNumerator(double toneLengthNumerator) {
        if (toneLengthNumerator <= 0) throw new IllegalArgumentException("Tone length must be positive");

        this.toneLengthNumerator = toneLengthNumerator;
    }

    public double getToneLength() {
        return toneLengthNumerator/toneLengthDenominator;
    }

    public AWETimedUnit[] split(double time) {
        // Take a unit and split it into two.
        // The first unit gets the toneLength of the parameter, the second gets the remaining tone length.
        double toneLength = getToneLength();
        if (toneLength <= time) {
            throw new IllegalArgumentException("Split time must be greater than tone length");
        }
        AWEUnit a = new AWEUnit();
        a.symbols = symbols;
        a.tone = tone;
        a.transp = transp;
        a.octaves = octaves;
        a.toneLengthNumerator = time;
        AWEUnit b = new AWEUnit();
        b.tone = String.valueOf(ABCToAWEParser.Symbol.COPY);
        double remainingLength = toneLength - time;
        b.toneLengthNumerator = remainingLength;
        return new AWEUnit[] { a, b };
    }


}
