package iansteph.nhlp3.shiftpublisher.model.event;

import iansteph.nhlp3.shiftpublisher.model.toi.player.Shift;

import java.util.Objects;

public class ShiftEvent {

    private String playerTeamName;
    private Integer playerTeamId;
    private String playerFirstName;
    private String playerLastName;
    private int playerJerseyNumber;
    private Shift shift;

    public String getPlayerTeamName() {

        return playerTeamName;
    }

    public void setPlayerTeamName(final String playerTeamName) {

        this.playerTeamName = playerTeamName;
    }

    public Integer getPlayerTeamId() {

        return playerTeamId;
    }

    public void setPlayerTeamId(final Integer playerTeamId) {

        this.playerTeamId = playerTeamId;
    }

    public String getPlayerFirstName() {

        return playerFirstName;
    }

    public void setPlayerFirstName(final String playerFirstName) {

        this.playerFirstName = playerFirstName;
    }

    public String getPlayerLastName() {

        return playerLastName;
    }

    public void setPlayerLastName(final String playerLastName) {

        this.playerLastName = playerLastName;
    }

    public int getPlayerJerseyNumber() {

        return playerJerseyNumber;
    }

    public void setPlayerJerseyNumber(final int playerJerseyNumber) {

        this.playerJerseyNumber = playerJerseyNumber;
    }

    public Shift getShift() {

        return shift;
    }

    public void setShift(final Shift shift) {

        this.shift = shift;
    }

    @Override
    public String toString() {

        return "ShiftEvent{" +
                "playerTeamName='" + playerTeamName + '\'' +
                ", playerTeamId='" + playerTeamId + '\'' +
                ", playerFirstName='" + playerFirstName + '\'' +
                ", playerLastName='" + playerLastName + '\'' +
                ", playerJerseyNumber=" + playerJerseyNumber +
                ", shift=" + shift +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftEvent that = (ShiftEvent) o;
        return playerJerseyNumber == that.playerJerseyNumber &&
                Objects.equals(playerTeamName, that.playerTeamName) &&
                Objects.equals(playerTeamId, that.playerTeamId) &&
                Objects.equals(playerFirstName, that.playerFirstName) &&
                Objects.equals(playerLastName, that.playerLastName) &&
                Objects.equals(shift, that.shift);
    }

    @Override
    public int hashCode() {

        return Objects.hash(playerTeamName, playerTeamId, playerFirstName, playerLastName, playerJerseyNumber, shift);
    }
}
