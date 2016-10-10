package no.itema.abcconverter.model;

import no.itema.abcconverter.util.AwesomeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jih on 14/09/16.
 */
public class AWEFile {

    private List<AWELine> lines;

    public AWEFile() {
        lines = new ArrayList<AWELine>();
    }

    public AWEFile(List<AWELine> lines) {
        this.lines = lines;
    }

    public AWELine getAWELine(int index) throws AwesomeException {
        if(index<lines.size()) {
            return lines.get(index);
        }
        throw new AwesomeException("No line at this index");
    }

    public String getFileString() {
        String res = "";
        for(AWELine b: lines) {
            res += b.getLineString();
        }
        return res;
    }

    public List<AWELine> getLines() {
        return lines;
    }

    public void addLine(AWELine line) {
        this.lines.add(line);
    }

    public boolean isValid() {
        for (AWELine line : lines) {
            for (AWEBar bar : line.getBars()) {
                if (bar.getTimeSlots().size() != 16) {
                    return false;
                }
            }
        }
        return lines.size() > 0;
    }
}
