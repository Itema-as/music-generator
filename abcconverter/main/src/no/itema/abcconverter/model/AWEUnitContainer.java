package no.itema.abcconverter.model;

import no.itema.abcconverter.util.AwesomeException;

import java.util.List;

/**
 * Created by lars on 03.12.16.
 */
public interface AWEUnitContainer {

    void addUnit(AWETimedUnit unit) throws AwesomeException;

    List<AWETimedUnit> getUnits();
}
