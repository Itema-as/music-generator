package no.itema.abcconverter.model;

import no.itema.abcconverter.util.AwesomeException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jih on 14/09/16.
 */
public class AWEBar {
    List<AWETimeSlot> timeSlots;

    public AWEBar() {
        this.timeSlots = new ArrayList<AWETimeSlot>();
    }

    public AWEBar(List<AWETimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public List<AWETimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public AWETimeSlot getTimeSlot(int index) throws AwesomeException {
        if(index<timeSlots.size()) {
            return timeSlots.get(index);
        }
        throw new AwesomeException("No timeSlot at this index");
    }

    public String getBarString() {
        String res = "";
        for(AWETimeSlot u: timeSlots) {
            res += u.getTimeSlotString();
            res += " ";
        }
        res += "| ";
        return res;
    }

    public String getAbcString() {
        String res = "";
        for(AWETimeSlot u: timeSlots) {
            res += u.getAbcString();
        }
        res += " | ";
        return res;
    }


    public double getTotalToneLength() {
        double duration = 0;
        for (AWETimeSlot t : timeSlots) {
            duration += t.totalToneLength();
        }
        return duration;
    }

    public void addTimeSlot(AWETimeSlot timeSlot) {
        this.timeSlots.add(timeSlot);
    }

    public ArrayList<AWETimedUnit> getUnits() {
        ArrayList<AWETimedUnit> units = new ArrayList<>();
        for (AWETimeSlot ts : timeSlots) {
            units.addAll(ts.getUnits());
        }
        return units;
    }

    public void padWithPausesAtEnd(double wantedDuration) {
        double duration = getTotalToneLength();
        double remains = wantedDuration - duration;
        if (remains > 0) {
            int wholeRemaining = (int)remains;
            double fractionalRemaining = remains - wholeRemaining;
            if (fractionalRemaining > 0.0001) {
                AWEUnit unit = new AWEUnit();
                unit.setTone("x");
                unit.setToneLengthDenominator(Math.round(1/fractionalRemaining));
                timeSlots.get(timeSlots.size()-1).addUnit(unit);
            }
            for (int i = 0; i < wholeRemaining; i++) {
                AWEUnit unit = new AWEUnit();
                unit.setTone("x");
                AWETimeSlot timeSlot = new AWETimeSlot();
                timeSlot.addUnit(unit);
                timeSlots.add(timeSlot);
            }
        }
    }
}
