package no.itema.abcconverter.model;

import no.itema.abcconverter.ABCToAWEParser;
import no.itema.abcconverter.util.AwesomeException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lars on 03.12.16.
 */
public class AWEChord implements AWETimedUnit, AWEUnitContainer {
    private final ArrayList<AWETimedUnit> units;

    public AWEChord() {
        this.units = new ArrayList<AWETimedUnit>();
    }

    public AWEChord(ArrayList<AWETimedUnit> units) {
        this.units = units;
    }

    public void addUnit(AWETimedUnit unit) throws AwesomeException {
        if (units.size() > 0 && units.get(0).getToneLength() != unit.getToneLength()) {
            throw new AwesomeException("Can't handle chord with units of different tone length");
        }
        if (!(unit instanceof AWEUnit)) {
            throw new AwesomeException("Anything added to AWEChord must be of type AWEUnit");
        }
        units.add(unit);
    }

    @Override
    public double getToneLength() {
        return units.size() > 0 ? units.get(0).getToneLength() : 0;
    }

    @Override
    public String getUnitString() {
        return "[" + units.stream().map(u -> u.getUnitString()).collect(Collectors.joining(""))  + "]";
    }

    @Override
    public String getAbcString() {
        return "[" + units.stream().map(u -> u.getAbcString()).collect(Collectors.joining(""))  + "]";
    }

    public List<AWETimedUnit> getUnits() {
        return units;
    }


    @Override
    public AWETimedUnit[] split(double time) {
        // Take a unit and split it into two.
        // The first unit gets the toneLength of the parameter, the second gets the remaining tone length.
        double toneLength = getToneLength();
        if (toneLength <= time) {
            throw new IllegalArgumentException("Split time must be greater than tone length");
        }
        ArrayList<AWEUnit[]> parts = units.stream().map(u -> (AWEUnit[])u.split(time)).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<AWETimedUnit> first = parts.stream().map(p -> p[0]).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<AWETimedUnit> second = parts.stream().map(p -> p[1]).collect(Collectors.toCollection(ArrayList::new));

        return new AWEChord[] { new AWEChord(first), new AWEChord(second) };
    }

    @Override
    public boolean isContinuation() {
        return false;
    }

    @Override
    public boolean isTie() {
        return false;
    }
}
