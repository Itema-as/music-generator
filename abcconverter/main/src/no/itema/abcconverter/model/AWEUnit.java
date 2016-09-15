package no.itema.abcconverter.model;

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



    public AWEUnit() {
        this.symbols = new ArrayList<String>();
        tone = "";
        transp = "";
        octave = "";
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
}
