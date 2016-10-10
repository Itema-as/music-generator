package no.itema.abcconverter.model;

import no.itema.abcconverter.util.AwesomeException;

import java.util.ArrayList;
import java.util.List;

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

    public void addTimeSlot(AWETimeSlot timeSlot) {
        this.timeSlots.add(timeSlot);
    }

}
