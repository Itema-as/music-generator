package no.itema.abcconverter.model;

import no.itema.abcconverter.util.AwesomeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jih on 14/09/16.
 */
public class AWETimeSlot {
    List<AWEUnit> units;

    public AWETimeSlot() {
        this.units = new ArrayList<AWEUnit>();
    }

    public AWETimeSlot(List<AWEUnit> units) {
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

    public String getTimeSlotString() {
        String res = units.size() > 1 ? "[": "";
        for(AWEUnit u: units) {
            res += u.getUnitString();
        }
        res += units.size() > 1 ? "]": "";;
        return res;
    }

    public void addUnit(AWEUnit unit) {
        this.units.add(unit);
    }

}
