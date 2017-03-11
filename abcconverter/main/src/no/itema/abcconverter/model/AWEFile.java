package no.itema.abcconverter.model;

import no.itema.abcconverter.util.AwesomeException;
import no.itema.abcconverter.util.InstrumentCategories;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jih on 14/09/16.
 */
public class AWEFile {

    private List<AWEChannel> channels;

    public AWEFile() {
        channels = new ArrayList<AWEChannel>();
    }

    public AWEFile(List<AWEChannel> channels) {
        this.channels = channels;
    }


    public AWELine getAWELine(int channelIndex, int index) throws AwesomeException {
        return channels.get(channelIndex).getLines().get(index);
    }

    public String getAbcString() {
        String res = "";
        for (AWEChannel c : channels) {
            for (AWELine b : c.getLines()) {
                res += b.getAbcString();
            }
        }
        return res;
    }

    public String getFileString() {
        String res = "";
        for (AWEChannel c : channels) {
            for (AWELine b : c.getLines()) {
                res += b.getLineString();
            }
        }
        return res;
    }

    public int getNumLines() {
        int numLines = 0;
        for (AWEChannel c : channels) {
            for (AWELine line : c.getLines()) {
                numLines++;
            }
        }
        return numLines;
    }

    public void addLine(AWELine line) throws AwesomeException {
        if (this.channels.size() == 0) {
            this.channels.add(new AWEChannel(-1));
            //throw new AwesomeException("A channel must be added before lines can be added");
        }
        this.channels.get(this.channels.size()-1).addLine(line);
    }

    public void ensureIsValid() throws AwesomeException {
        int numLines = 0;
        int prevNumTimeSlots = -1;
        for (AWEChannel c : channels) {
            int numTimeSlots = 0;
            String s = c.toAweString();
            for (AWELine line : c.getLines()) {
                numLines++;
                for (AWEBar bar : line.getBars()) {
                    numTimeSlots += bar.getTimeSlots().size();
                    if (bar.getTimeSlots().size() != 8 && bar != line.getBar(0)) {
                        throw new AwesomeException("All bars except first must have 8 timeslots");
                    }
                }
            }
            if (prevNumTimeSlots != -1 && prevNumTimeSlots != numTimeSlots) {
                throw new AwesomeException("Number of timeslots differed between channels");
            }
            prevNumTimeSlots = numTimeSlots;
        }
        if (numLines == 0) {
            throw new AwesomeException("No lines in the file");
        }
    }

    public void addChannel() {
        channels.add(new AWEChannel());
    }

    public List<AWEChannel> getChannels() { return channels; }

    public void setChannels(List<AWEChannel> channels) { this.channels = channels; }
}
