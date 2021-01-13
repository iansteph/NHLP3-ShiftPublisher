package iansteph.nhlp3.shiftpublisher.model.roster.player;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {

    private Integer id;
    private String fullName;

    public Integer getId() {

        return id;
    }

    public void setId(final Integer id) {

        this.id = id;
    }

    public String getFullName() {

        return fullName;
    }

    public void setFullName(final String fullName) {

        this.fullName = fullName;
    }

    @Override
    public String toString() {

        return "Person{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                Objects.equals(fullName, person.fullName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, fullName);
    }
}
