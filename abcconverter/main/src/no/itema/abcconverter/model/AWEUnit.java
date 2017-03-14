package no.itema.abcconverter.model;

import no.itema.abcconverter.Symbol;

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
    private boolean isContinuation;
    private boolean tie;
    private boolean slurStart;
    private boolean slurEnd;

    public AWEUnit() {
        symbols = new ArrayList<String>();
        tone = "";
        transp = "";
        natural = "";
        octaves = new ArrayList<String>();
        toneLengthNumerator = 1;
        toneLengthDenominator = 1;
        isContinuation = false;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public String getUnitString() {
        return String.join("", symbols) + transp + tone + String.join("", octaves) + toneLengthString();
    }

    public String getAbcString() {
        return String.join("", symbols) + transp + tone + String.join("", octaves) + toneLengthString() + (tie ? Symbol.TIE : "");
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
                ? ("" + Symbol.FRACTIONAL_TONE_LENGTH_START + Math.round(1/toneLength))
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
        b.tone = String.valueOf(Symbol.CONTINUATION);
        double remainingLength = toneLength - time;
        b.toneLengthNumerator = remainingLength;
        return new AWEUnit[] { a, b };
    }


    public void setIsContinuation(boolean isContinuation) {
        this.isContinuation = isContinuation;
    }

    public boolean isContinuation() {
        return this.isContinuation;
    }

    @Override
    public boolean isTie() {
        return tie;
    }

    public double getToneLengthDenominator() {
        return toneLengthDenominator;
    }

    public double getToneLengthNumerator() {
        return toneLengthNumerator;
    }

    public void setTie(boolean tie) {
        this.tie = tie;
    }

    public void setSlurStart(boolean slurStart) {
        this.slurStart = slurStart;
    }

    public void setSlurEnd(boolean slurEnd) {
        this.slurEnd = slurEnd;
    }

    public boolean isSlurStart() {
        return slurStart;
    }

    public boolean isSlurEnd() {
        return slurEnd;
    }

    public void copyValuesFrom(AWEUnit aweUnit) {
        symbols = aweUnit.symbols;
        tone = aweUnit.tone;
        transp = aweUnit.transp;
        natural = aweUnit.natural;
        octaves = aweUnit.octaves;
        isContinuation = false;
    }

}
