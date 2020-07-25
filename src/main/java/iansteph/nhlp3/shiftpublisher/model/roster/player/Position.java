package iansteph.nhlp3.shiftpublisher.model.roster.player;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Position {

    private String code;
    private String name;
    private String type;
    private String abbreviation;

    public String getCode() {

        return code;
    }

    public void setCode(final String code) {

        this.code = code;
    }

    public String getName() {

        return name;
    }

    public void setName(final String name) {

        this.name = name;
    }

    public String getType() {

        return type;
    }

    public void setType(final String type) {

        this.type = type;
    }

    public String getAbbreviation() {

        return abbreviation;
    }

    public void setAbbreviation(final String abbreviation) {

        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {

        return "Position{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(code, position.code) &&
                Objects.equals(name, position.name) &&
                Objects.equals(type, position.type) &&
                Objects.equals(abbreviation, position.abbreviation);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, name, type, abbreviation);
    }
}
