package iansteph.nhlp3.shiftpublisher.model;

public enum Team {

    HOME("H"),
    VISITOR("V");

    private final String label;

    Team(final String label) {

        this.label = label;
    }

    public String getLabel() {

        return label;
    }

    @Override
    public String toString() {

        return "Team{" +
                "label='" + label + '\'' +
                '}';
    }
}
