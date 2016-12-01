package no.itema.abcconverter.model;

import no.itema.abcconverter.ABCToAWEParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jih on 14/09/16.
 */
public class AWEUnit {

    List<String> symbols;
    String tone;
    String transp;
    String octave;
    double toneLength;



    public AWEUnit() {
        this.symbols = new ArrayList<String>();
        tone = "";
        transp = "";
        octave = "";
        toneLength = 1;
    }

    public AWEUnit(List<String> symbols) {
        this.symbols = symbols;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public String getUnitString() {
        return transp + tone + octave;
    }

    public void addSymbol(String symbol) {
        symbols.add(symbol);
    }

    public String getOctave() {
        return octave;
    }

    public void setOctave(String octave) {
        this.octave = octave;
    }

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

    public double getToneLength() { return this.toneLength; }

    public void setToneLength(double toneLength) {
        if (toneLength <= 0) throw new IllegalArgumentException("Tone length must be positive");
        this.toneLength = toneLength;
    }

    public AWEUnit[] split(double time) {
        // Take a unit and split it into two.
        // The first unit gets the toneLength of the parameter, the second gets the remaining tone length.
        if (toneLength <= time) {
            throw new IllegalArgumentException("Split time must be greater than tone length");
        }
        AWEUnit a = new AWEUnit();
        a.symbols = symbols;
        a.tone = tone;
        a.transp = transp;
        a.octave = octave;
        a.toneLength = time;
        AWEUnit b = new AWEUnit();
        b.tone = String.valueOf(ABCToAWEParser.Symbol.COPY);
        b.toneLength = toneLength - time;
        return new AWEUnit[] { a, b };
    }

}
