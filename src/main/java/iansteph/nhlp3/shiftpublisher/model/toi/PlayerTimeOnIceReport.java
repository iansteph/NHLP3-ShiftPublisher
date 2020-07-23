package iansteph.nhlp3.shiftpublisher.model.toi;

import iansteph.nhlp3.shiftpublisher.model.toi.player.Shift;
import iansteph.nhlp3.shiftpublisher.model.toi.player.Summary;

import java.util.List;
import java.util.Objects;

public class PlayerTimeOnIceReport {

    private Integer number;
    private String lastName;
    private String firstName;
    private String teamName;
    private Integer teamId;
    private List<Shift> shifts;
    private Summary summary;

    public Integer getNumber() {

        return number;
    }

    public void setNumber(final Integer number) {

        this.number = number;
    }

    public String getLastName() {

        return lastName;
    }

    public void setLastName(final String lastName) {

        this.lastName = lastName;
    }

    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(final String firstName) {

        this.firstName = firstName;
    }

    public String getTeamName() {

        return teamName;
    }

    public void setTeamName(final String teamName) {

        this.teamName = teamName;
    }

    public Integer getTeamId() {

        return teamId;
    }

    public void setTeamId(final Integer teamId) {

        this.teamId = teamId;
    }

    public List<Shift> getShifts() {

        return shifts;
    }

    public void setShifts(final List<Shift> shifts) {

        this.shifts = shifts;
    }

    public Summary getSummary() {

        return summary;
    }

    public void setSummary(final Summary summary) {

        this.summary = summary;
    }

    @Override
    public String toString() {

        return "PlayerTimeOnIceReport{" +
                "number=" + number +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", teamName='" + teamName + '\'' +
                ", teamId='" + teamId + '\'' +
                ", shifts=" + shifts +
                ", summary=" + summary +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerTimeOnIceReport that = (PlayerTimeOnIceReport) o;
        return Objects.equals(number, that.number) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(teamName, that.teamName) &&
                Objects.equals(teamId, that.teamId) &&
                Objects.equals(shifts, that.shifts) &&
                Objects.equals(summary, that.summary);
    }

    @Override
    public int hashCode() {

        return Objects.hash(number, lastName, firstName, teamName, teamId, shifts, summary);
    }
}
