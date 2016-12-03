package no.itema.abcconverter.model;

/**
 * Created by lars on 03.12.16.
 */
public interface AWETimedUnit {
    double getToneLength();

    String getUnitString();

    AWETimedUnit[] split(double remainingSpace);
}
