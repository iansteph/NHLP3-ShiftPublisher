package iansteph.nhlp3.shiftpublisher.model.request;

import java.util.Objects;

public class ShiftPublisherRequest {

    private int gameId;

    public ShiftPublisherRequest() {}

    public ShiftPublisherRequest(final int gameId) {

        this.gameId = gameId;
    }

    public void setGameId(final int gameId) {

        this.gameId = gameId;
    }

    public int getGameId() {

        return gameId;
    }

    @Override
    public String toString() {

        return "ShiftPublisherRequest{" +
                "gameId=" + gameId +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShiftPublisherRequest that = (ShiftPublisherRequest) o;
        return gameId == that.gameId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(gameId);
    }
}
