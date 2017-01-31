package no.itema.abcconverter.model;

import no.itema.abcconverter.util.AwesomeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jih on 14/09/16.
 */
public class AWETimeSlot implements AWEUnitContainer {
    List<AWETimedUnit> units;

    public AWETimeSlot() {
        this.units = new ArrayList<AWETimedUnit>();
    }

    public AWETimeSlot(List<AWETimedUnit> units) {
        this.units = units;
    }

    public List<AWETimedUnit> getUnits() {
        return units;
    }

    public AWETimedUnit getUnit(int index) throws AwesomeException {
        if(index<units.size()) {
            return units.get(index);
        }
        throw new AwesomeException("No unit at this index");
    }

    public String getTimeSlotString() {
        String res = "";
        for(AWETimedUnit u: units) {
            res += u.getUnitString();
        }
        return res;
    }

    public String getAbcString() {
        String res = "";
        for(AWETimedUnit u: units) {
            res += u.getAbcString();
        }
        return res;
    }

    public void addUnit(AWETimedUnit unit) {
        this.units.add(unit);
    }

    public double totalToneLength() {
        double length = 0;
        for (AWETimedUnit unit : getUnits()) {
            length += unit.getToneLength();
        }
        return length;
    }

    public boolean isFilled() {
        return (totalToneLength() + 0.00001) >= 1;
    }

    public boolean overflows() {
        return totalToneLength() > 1 + 0.00001;
    }

    public double remainingSpace() { return 1 - totalToneLength(); }

    private ArrayList<ArrayList<AWETimedUnit>> splitAt(double time) {
        double length = 0;
        int i = 0;
        ArrayList<AWETimedUnit> before = new ArrayList<AWETimedUnit>();
        ArrayList<AWETimedUnit> after = new ArrayList<AWETimedUnit>();
        for (AWETimedUnit unit : getUnits()) {
            boolean fits = length + unit.getToneLength() <= time + 0.00001;
            if (fits) {
                before.add(unit);
            } else {
                double remainingSpace = time - length;
                if (remainingSpace > 0) {
                    AWETimedUnit[] parts = unit.split(remainingSpace);
                    before.add(parts[0]);
                    after.add(parts[1]);
                    length = time;
                } else {
                    after.add(unit);
                }
            }
            length += unit.getToneLength();
        }
        ArrayList<ArrayList<AWETimedUnit>> lists = new ArrayList<ArrayList<AWETimedUnit>>();
        lists.add(before);
        lists.add(after);
        return lists;
    }

    public List<AWETimedUnit> chopOffFromBeginning(double time) {
        ArrayList<ArrayList<AWETimedUnit>> parts = splitAt(time);
        ArrayList<AWETimedUnit> before = parts.get(0);
        ArrayList<AWETimedUnit> after = parts.get(1);
        this.units = after;
        return before;
    }

    public List<AWETimedUnit> chopOfOverflow() {
        ArrayList<ArrayList<AWETimedUnit>> parts = splitAt(1);
        ArrayList<AWETimedUnit> fittingUnits = parts.get(0);
        ArrayList<AWETimedUnit> overflow = parts.get(1);
        this.units = fittingUnits;
        return overflow;
    }
}
