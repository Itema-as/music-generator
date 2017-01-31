package no.itema.abcconverter.model;

import no.itema.abcconverter.io.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lars on 08.12.16.
 */
public class AWEChannel {

    private List<AWELine> lines;
    private int instrument;

    public AWEChannel(int instrument) {
        lines = new ArrayList<AWELine>();
        this.instrument = instrument;
    }

    public void addLine(AWELine line) {
        this.lines.add(line);
    }

    public List<AWELine> getLines() {
        return lines;
    }

    public int getInstrument() { return instrument; }

    public void writeToFile(String filename) throws IOException {
        String res = "";
        for (AWELine b : getLines()) {
            res += b.getLineString();
        }
        FileManager.saveFileContents(filename, res);
    }

    public ArrayList<AWETimedUnit> getUnits() {
        ArrayList<AWETimedUnit> units = new ArrayList<>();
        for (AWELine aweLine : getLines()) {
            for (AWEBar aweBar : aweLine.getBars()) {
                units.addAll(aweBar.getUnits());
            }
        }

        return units;
    }
}
