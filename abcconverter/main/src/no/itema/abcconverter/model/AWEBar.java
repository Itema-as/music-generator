package no.itema.abcconverter.model;

import no.itema.abcconverter.util.AwesomeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jih on 14/09/16.
 */
public class AWEBar {
    List<AWEUnit> units;

    public AWEBar() {
        this.units = new ArrayList<AWEUnit>();
    }

    public AWEBar(List<AWEUnit> units) {
        this.units = units;
    }

    public List<AWEUnit> getUnits() {
        return units;
    }

    public AWEUnit getUnit(int index) throws AwesomeException {
        if(index<units.size()) {
            return units.get(index);
        }
        throw new AwesomeException("No unit at this index");
    }

    public String getBarString() {
        String res = "";
        for(AWEUnit u: units) {
            res += u.getUnitString();
        }
        res += "|";
        return res;
    }

    public void addUnit(AWEUnit unit) {
        this.units.add(unit);
    }

}
