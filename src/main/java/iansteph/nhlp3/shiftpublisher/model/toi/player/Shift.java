package iansteph.nhlp3.shiftpublisher.model.toi.player;

import java.time.Duration;
import java.util.Objects;

public class Shift {

    private Integer shiftNumber;
    private String period;
    private Duration shiftStartElapsedTime;
    private Duration shiftStartGameClockTime;
    private Duration shiftEndElapsedTime;
    private Duration shiftEndGameClockTime;
    private Duration shiftDuration;
    private Boolean hasPenaltyDuringShift;
    private Boolean hasGoalDuringShift;

    public Integer getShiftNumber() {

        return shiftNumber;
    }

    public void setShiftNumber(final Integer shiftNumber) {

        this.shiftNumber = shiftNumber;
    }

    public String getPeriod() {

        return period;
    }

    public void setPeriod(final String period) {

        this.period = period;
    }

    public Duration getShiftStartElapsedTime() {

        return shiftStartElapsedTime;
    }

    public void setShiftStartElapsedTime(final Duration shiftStartElapsedTime) {

        this.shiftStartElapsedTime = shiftStartElapsedTime;
    }

    public Duration getShiftStartGameClockTime() {

        return shiftStartGameClockTime;
    }

    public void setShiftStartGameClockTime(final Duration shiftStartGameClockTime) {

        this.shiftStartGameClockTime = shiftStartGameClockTime;
    }

    public Duration getShiftEndElapsedTime() {

        return shiftEndElapsedTime;
    }

    public void setShiftEndElapsedTime(final Duration shiftEndElapsedTime) {

        this.shiftEndElapsedTime = shiftEndElapsedTime;
    }

    public Duration getShiftEndGameClockTime() {

        return shiftEndGameClockTime;
    }

    public void setShiftEndGameClockTime(final Duration shiftEndGameClockTime) {

        this.shiftEndGameClockTime = shiftEndGameClockTime;
    }

    public Duration getShiftDuration() {

        return shiftDuration;
    }

    public void setShiftDuration(final Duration shiftDuration) {

        this.shiftDuration = shiftDuration;
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
                ", shiftStartElapsedTime=" + shiftStartElapsedTime +
                ", shiftStartGameClockTime=" + shiftStartGameClockTime +
                ", shiftEndElapsedTime=" + shiftEndElapsedTime +
                ", shiftEndGameClockTime=" + shiftEndGameClockTime +
                ", shiftDuration=" + shiftDuration +
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
                Objects.equals(shiftStartElapsedTime, shift.shiftStartElapsedTime) &&
                Objects.equals(shiftStartGameClockTime, shift.shiftStartGameClockTime) &&
                Objects.equals(shiftEndElapsedTime, shift.shiftEndElapsedTime) &&
                Objects.equals(shiftEndGameClockTime, shift.shiftEndGameClockTime) &&
                Objects.equals(shiftDuration, shift.shiftDuration) &&
                Objects.equals(hasPenaltyDuringShift, shift.hasPenaltyDuringShift) &&
                Objects.equals(hasGoalDuringShift, shift.hasGoalDuringShift);
    }

    @Override
    public int hashCode() {

        return Objects.hash(shiftNumber, period, shiftStartElapsedTime, shiftStartGameClockTime, shiftEndElapsedTime, shiftEndGameClockTime, shiftDuration, hasPenaltyDuringShift, hasGoalDuringShift);
    }
}
