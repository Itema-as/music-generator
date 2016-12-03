package no.itema.abcconverter.model;

import no.itema.abcconverter.ABCToAWEParser;
import no.itema.abcconverter.util.AwesomeException;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lars on 03.12.16.
 */
public class AWEChord implements AWETimedUnit, AWEUnitContainer {
    private final ArrayList<AWEUnit> units;

    public AWEChord() {
        this.units = new ArrayList<AWEUnit>();
    }

    public AWEChord(ArrayList<AWEUnit> units) {
        this.units = units;
    }

    public void addUnit(AWETimedUnit unit) throws AwesomeException {
        if (units.size() > 0 && units.get(0).getToneLength() != unit.getToneLength()) {
            throw new AwesomeException("Can't handle chord with units of different tone length");
        }
        if (!(unit instanceof AWEUnit)) {
            throw new AwesomeException("Anything added to AWEChord must be of type AWEUnit");
        }
        units.add((AWEUnit)unit);
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
    public AWETimedUnit[] split(double time) {
        // Take a unit and split it into two.
        // The first unit gets the toneLength of the parameter, the second gets the remaining tone length.
        double toneLength = getToneLength();
        if (toneLength <= time) {
            throw new IllegalArgumentException("Split time must be greater than tone length");
        }
        Stream<AWEUnit[]> parts = units.stream().map(u -> (AWEUnit[])u.split(time));
        ArrayList<AWEUnit> first = parts.map(p -> p[0]).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<AWEUnit> second = parts.map(p -> p[1]).collect(Collectors.toCollection(ArrayList::new));

        return new AWEChord[] { new AWEChord(first), new AWEChord(second) };
    }
}