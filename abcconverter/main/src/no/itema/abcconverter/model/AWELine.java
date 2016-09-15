package no.itema.abcconverter.model;

import no.itema.abcconverter.util.AwesomeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jih on 14/09/16.
 */
public class AWELine {
    List<AWEBar> bars;

    public AWELine() {
        this.bars = new ArrayList<AWEBar>();
    }

    public AWELine(List<AWEBar> bars) {
        this.bars = bars;
    }

    public List<AWEBar> getBars() {
        return bars;
    }

    public AWEBar getBar(int index) throws AwesomeException {
        if(index<bars.size()) {
            return bars.get(index);
        }
        throw new AwesomeException("No bar at this index");
    }

    public String getLineString() {
        String res = "";
        for(AWEBar b: bars) {
            res += b.getBarString();
        }
        return res;
    }

    public void addBar(AWEBar bar) {
        this.bars.add(bar);
    }

}
