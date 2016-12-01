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

    private double totalToneLength() {
        double length = 0;
        for (AWEUnit unit : getUnits()) {
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

    public List<AWEUnit> chopOfOverflow() {
        double length = 0;
        int i = 0;
        List<AWEUnit> fittingUnits = new ArrayList<AWEUnit>();
        List<AWEUnit> overflow = new ArrayList<AWEUnit>();
        for (AWEUnit unit : getUnits()) {
            boolean fits = length + unit.getToneLength() <= 1 + 0.00001;
            if (fits) {
                fittingUnits.add(unit);
            } else {
                double remainingSpace = 1 - length;
                if (remainingSpace > 0) {
                    AWEUnit[] parts = unit.split(remainingSpace);
                    fittingUnits.add(parts[0]);
                    overflow.add(parts[1]);
                    length = 1;
                } else {
                    overflow.add(unit);
                }
            }
            length += unit.getToneLength();
        }
        this.units = fittingUnits;
        return overflow;
    }

}
