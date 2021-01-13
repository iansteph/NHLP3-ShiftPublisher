package iansteph.nhlp3.shiftpublisher.model.toi.player.summary;

import java.util.Objects;

public class ShiftAggregation {

    private String aggregationName;
    private int shiftsFor;
    private Integer averageShiftLengthInSeconds;
    private int timeOnIceInSeconds;
    private int evenStrengthTimeOnIceInSeconds;
    private int powerPlayTimeOnIceInSeconds;
    private int shortHandedTimeOnIceInSeconds;

    public String getAggregationName() {

        return aggregationName;
    }

    public void setAggregationName(final String aggregationName) {

        this.aggregationName = aggregationName;
    }

    public int getShiftsFor() {

        return shiftsFor;
    }

    public void setShiftsFor(final int shiftsFor) {

        this.shiftsFor = shiftsFor;
    }

    public Integer getAverageShiftLengthInSeconds() {

        return averageShiftLengthInSeconds;
    }

    public void setAverageShiftLengthInSeconds(final Integer averageShiftLengthInSeconds) {

        this.averageShiftLengthInSeconds = averageShiftLengthInSeconds;
    }

    public int getTimeOnIceInSeconds() {

        return timeOnIceInSeconds;
    }

    public void setTimeOnIceInSeconds(final int timeOnIceInSeconds) {

        this.timeOnIceInSeconds = timeOnIceInSeconds;
    }

    public int getEvenStrengthTimeOnIceInSeconds() {

        return evenStrengthTimeOnIceInSeconds;
    }

    public void setEvenStrengthTimeOnIceInSeconds(final int evenStrengthTimeOnIceInSeconds) {

        this.evenStrengthTimeOnIceInSeconds = evenStrengthTimeOnIceInSeconds;
    }

    public int getPowerPlayTimeOnIceInSeconds() {

        return powerPlayTimeOnIceInSeconds;
    }

    public void setPowerPlayTimeOnIceInSeconds(final int powerPlayTimeOnIceInSeconds) {

        this.powerPlayTimeOnIceInSeconds = powerPlayTimeOnIceInSeconds;
    }

    public int getShortHandedTimeOnIceInSeconds() {

        return shortHandedTimeOnIceInSeconds;
    }

    public void setShortHandedTimeOnIceInSeconds(final int shortHandedTimeOnIceInSeconds) {

        this.shortHandedTimeOnIceInSeconds = shortHandedTimeOnIceInSeconds;
    }

    @Override
    public String toString() {

        return "ShiftAggregation{" +
                "aggregationName='" + aggregationName + '\'' +
                ", shiftsFor=" + shiftsFor +
                ", averageShiftLengthInSeconds=" + averageShiftLengthInSeconds +
                ", timeOnIceInSeconds=" + timeOnIceInSeconds +
                ", evenStrengthTimeOnIceInSeconds=" + evenStrengthTimeOnIceInSeconds +
                ", powerPlayTimeOnIceInSeconds=" + powerPlayTimeOnIceInSeconds +
                ", shortHandedTimeOnIceInSeconds=" + shortHandedTimeOnIceInSeconds +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftAggregation that = (ShiftAggregation) o;
        return Objects.equals(aggregationName, that.aggregationName) &&
                Objects.equals(shiftsFor, that.shiftsFor) &&
                Objects.equals(averageShiftLengthInSeconds, that.averageShiftLengthInSeconds) &&
                Objects.equals(timeOnIceInSeconds, that.timeOnIceInSeconds) &&
                Objects.equals(evenStrengthTimeOnIceInSeconds, that.evenStrengthTimeOnIceInSeconds) &&
                Objects.equals(powerPlayTimeOnIceInSeconds, that.powerPlayTimeOnIceInSeconds) &&
                Objects.equals(shortHandedTimeOnIceInSeconds, that.shortHandedTimeOnIceInSeconds);
    }

    @Override
    public int hashCode() {

        return Objects.hash(aggregationName, shiftsFor, averageShiftLengthInSeconds, timeOnIceInSeconds, evenStrengthTimeOnIceInSeconds,
                powerPlayTimeOnIceInSeconds, shortHandedTimeOnIceInSeconds);
    }
}
