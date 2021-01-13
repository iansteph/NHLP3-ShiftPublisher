package iansteph.nhlp3.shiftpublisher.model.toi.player;

import java.util.Objects;

public class Shift {

    private int shiftNumber;
    private String period;
    private int shiftStartElapsedTimeInSeconds;
    private int shiftStartGameClockTimeInSeconds;
    private int shiftEndElapsedTimeInSeconds;
    private int shiftEndGameClockTimeInSeconds;
    private int shiftDurationInSeconds;
    private Boolean hasPenaltyDuringShift;
    private Boolean hasGoalDuringShift;

    public int getShiftNumber() {

        return shiftNumber;
    }

    public void setShiftNumber(final int shiftNumber) {

        this.shiftNumber = shiftNumber;
    }

    public String getPeriod() {

        return period;
    }

    public void setPeriod(final String period) {

        this.period = period;
    }

    public int getShiftStartElapsedTimeInSeconds() {

        return shiftStartElapsedTimeInSeconds;
    }

    public void setShiftStartElapsedTimeInSeconds(final int shiftStartElapsedTimeInSeconds) {

        this.shiftStartElapsedTimeInSeconds = shiftStartElapsedTimeInSeconds;
    }

    public int getShiftStartGameClockTimeInSeconds() {

        return shiftStartGameClockTimeInSeconds;
    }

    public void setShiftStartGameClockTimeInSeconds(final int shiftStartGameClockTimeInSeconds) {

        this.shiftStartGameClockTimeInSeconds = shiftStartGameClockTimeInSeconds;
    }

    public int getShiftEndElapsedTimeInSeconds() {

        return shiftEndElapsedTimeInSeconds;
    }

    public void setShiftEndElapsedTimeInSeconds(final int shiftEndElapsedTimeInSeconds) {

        this.shiftEndElapsedTimeInSeconds = shiftEndElapsedTimeInSeconds;
    }

    public int getShiftEndGameClockTimeInSeconds() {

        return shiftEndGameClockTimeInSeconds;
    }

    public void setShiftEndGameClockTimeInSeconds(final int shiftEndGameClockTimeInSeconds) {

        this.shiftEndGameClockTimeInSeconds = shiftEndGameClockTimeInSeconds;
    }

    public int getShiftDurationInSeconds() {

        return shiftDurationInSeconds;
    }

    public void setShiftDurationInSeconds(final int shiftDurationInSeconds) {

        this.shiftDurationInSeconds = shiftDurationInSeconds;
    }

    public Boolean getHasPenaltyDuringShift() {

        return hasPenaltyDuringShift;
    }

    public void setHasPenaltyDuringShift(final Boolean hasPenaltyDuringShift) {

        this.hasPenaltyDuringShift = hasPenaltyDuringShift;
    }

    public Boolean getHasGoalDuringShift() {

        return hasGoalDuringShift;
    }

    public void setHasGoalDuringShift(final Boolean hasGoalDuringShift) {

        this.hasGoalDuringShift = hasGoalDuringShift;
    }

    @Override
    public String toString() {

        return "Shift{" +
                "shiftNumber=" + shiftNumber +
                ", period='" + period + '\'' +
                ", shiftStartElapsedTimeInSeconds=" + shiftStartElapsedTimeInSeconds +
                ", shiftStartGameClockTimeInSeconds=" + shiftStartGameClockTimeInSeconds +
                ", shiftEndElapsedTimeInSeconds=" + shiftEndElapsedTimeInSeconds +
                ", shiftEndGameClockTimeInSeconds=" + shiftEndGameClockTimeInSeconds +
                ", shiftDurationInSeconds=" + shiftDurationInSeconds +
                ", hasPenaltyDuringShift=" + hasPenaltyDuringShift +
                ", hasGoalDuringShift=" + hasGoalDuringShift +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return Objects.equals(shiftNumber, shift.shiftNumber) &&
                Objects.equals(period, shift.period) &&
                Objects.equals(shiftStartElapsedTimeInSeconds, shift.shiftStartElapsedTimeInSeconds) &&
                Objects.equals(shiftStartGameClockTimeInSeconds, shift.shiftStartGameClockTimeInSeconds) &&
                Objects.equals(shiftEndElapsedTimeInSeconds, shift.shiftEndElapsedTimeInSeconds) &&
                Objects.equals(shiftEndGameClockTimeInSeconds, shift.shiftEndGameClockTimeInSeconds) &&
                Objects.equals(shiftDurationInSeconds, shift.shiftDurationInSeconds) &&
                Objects.equals(hasPenaltyDuringShift, shift.hasPenaltyDuringShift) &&
                Objects.equals(hasGoalDuringShift, shift.hasGoalDuringShift);
    }

    @Override
    public int hashCode() {

        return Objects.hash(shiftNumber, period, shiftStartElapsedTimeInSeconds, shiftStartGameClockTimeInSeconds, shiftEndElapsedTimeInSeconds,
                shiftEndGameClockTimeInSeconds, shiftDurationInSeconds, hasPenaltyDuringShift, hasGoalDuringShift);
    }
}
