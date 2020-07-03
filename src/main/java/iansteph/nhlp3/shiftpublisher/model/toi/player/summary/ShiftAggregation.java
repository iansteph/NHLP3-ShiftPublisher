package iansteph.nhlp3.shiftpublisher.model.toi.player.summary;

import java.time.Duration;
import java.util.Objects;

public class ShiftAggregation {

    private String aggregationName;
    private Integer shiftsFor;
    private Duration averageShiftLength;
    private Duration timeOnIce;
    private Duration evenStrengthTimeOnIce;
    private Duration powerPlayTimeOnIce;
    private Duration shortHandedTimeOnIce;

    public String getAggregationName() {

        return aggregationName;
    }

    public void setAggregationName(final String aggregationName) {

        this.aggregationName = aggregationName;
    }

    public Integer getShiftsFor() {

        return shiftsFor;
    }

    public void setShiftsFor(final Integer shiftsFor) {

        this.shiftsFor = shiftsFor;
    }

    public Duration getAverageShiftLength() {

        return averageShiftLength;
    }

    public void setAverageShiftLength(final Duration averageShiftLength) {

        this.averageShiftLength = averageShiftLength;
    }

    public Duration getTimeOnIce() {

        return timeOnIce;
    }

    public void setTimeOnIce(final Duration timeOnIce) {

        this.timeOnIce = timeOnIce;
    }

    public Duration getEvenStrengthTimeOnIce() {

        return evenStrengthTimeOnIce;
    }

    public void setEvenStrengthTimeOnIce(final Duration evenStrengthTimeOnIce) {

        this.evenStrengthTimeOnIce = evenStrengthTimeOnIce;
    }

    public Duration getPowerPlayTimeOnIce() {

        return powerPlayTimeOnIce;
    }

    public void setPowerPlayTimeOnIce(final Duration powerPlayTimeOnIce) {

        this.powerPlayTimeOnIce = powerPlayTimeOnIce;
    }

    public Duration getShortHandedTimeOnIce() {

        return shortHandedTimeOnIce;
    }

    public void setShortHandedTimeOnIce(final Duration shortHandedTimeOnIce) {

        this.shortHandedTimeOnIce = shortHandedTimeOnIce;
    }

    @Override
    public String toString() {

        return "ShiftAggregation{" +
                "aggregationName='" + aggregationName + '\'' +
                ", shiftsFor=" + shiftsFor +
                ", averageShiftLength=" + averageShiftLength +
                ", timeOnIce=" + timeOnIce +
                ", evenStrengthTimeOnIce=" + evenStrengthTimeOnIce +
                ", powerPlayTimeOnIce=" + powerPlayTimeOnIce +
                ", shortHandedTimeOnIce=" + shortHandedTimeOnIce +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftAggregation that = (ShiftAggregation) o;
        return Objects.equals(aggregationName, that.aggregationName) &&
                Objects.equals(shiftsFor, that.shiftsFor) &&
                Objects.equals(averageShiftLength, that.averageShiftLength) &&
                Objects.equals(timeOnIce, that.timeOnIce) &&
                Objects.equals(evenStrengthTimeOnIce, that.evenStrengthTimeOnIce) &&
                Objects.equals(powerPlayTimeOnIce, that.powerPlayTimeOnIce) &&
                Objects.equals(shortHandedTimeOnIce, that.shortHandedTimeOnIce);
    }

    @Override
    public int hashCode() {

        return Objects.hash(aggregationName, shiftsFor, averageShiftLength, timeOnIce, evenStrengthTimeOnIce, powerPlayTimeOnIce, shortHandedTimeOnIce);
    }
}
