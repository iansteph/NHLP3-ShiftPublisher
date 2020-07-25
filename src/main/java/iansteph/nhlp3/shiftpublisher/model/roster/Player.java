package iansteph.nhlp3.shiftpublisher.model.roster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.shiftpublisher.model.roster.player.Person;
import iansteph.nhlp3.shiftpublisher.model.roster.player.Position;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    private Person person;
    private String jerseyNumber;
    private Position position;

    public Person getPerson() {

        return person;
    }

    public void setPerson(final Person person) {

        this.person = person;
    }

    public String getJerseyNumber() {

        return jerseyNumber;
    }

    public void setJerseyNumber(final String jerseyNumber) {

        this.jerseyNumber = jerseyNumber;
    }

    public Position getPosition() {

        return position;
    }

    public void setPosition(final Position position) {

        this.position = position;
    }

    @Override
    public String toString() {

        return "Player{" +
                "person=" + person +
                ", jerseyNumber='" + jerseyNumber + '\'' +
                ", position=" + position +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(person, player.person) &&
                Objects.equals(jerseyNumber, player.jerseyNumber) &&
                Objects.equals(position, player.position);
    }

    @Override
    public int hashCode() {

        return Objects.hash(person, jerseyNumber, position);
    }
}
