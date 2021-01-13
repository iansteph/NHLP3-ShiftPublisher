package iansteph.nhlp3.shiftpublisher.model.roster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Roster {

    private List<Player> roster;

    public List<Player> getRoster() {

        return roster;
    }

    public void setRoster(final List<Player> roster) {

        this.roster = roster;
    }

    @Override
    public String toString() {

        return "Roster{" +
                "roster=" + roster +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Roster roster1 = (Roster) o;
        return Objects.equals(roster, roster1.roster);
    }

    @Override
    public int hashCode() {

        return Objects.hash(roster);
    }
}
