package iansteph.nhlp3.shiftpublisher.model.toi.player;

import iansteph.nhlp3.shiftpublisher.model.toi.player.summary.ShiftAggregation;

import java.util.List;
import java.util.Objects;

public class Summary {

    private List<ShiftAggregation> shiftAggregations;
    private ShiftAggregation totals;

    public List<ShiftAggregation> getShiftAggregations() {

        return shiftAggregations;
    }

    public void setShiftAggregations(final List<ShiftAggregation> shiftAggregations) {

        this.shiftAggregations = shiftAggregations;
    }

    public ShiftAggregation getTotals() {

        return totals;
    }

    public void setTotals(final ShiftAggregation totals) {

        this.totals = totals;
    }

    @Override
    public String toString() {

        return "Summary{" +
                "shiftAggregations=" + shiftAggregations +
                ", totals=" + totals +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Summary summary = (Summary) o;
        return Objects.equals(shiftAggregations, summary.shiftAggregations) &&
                Objects.equals(totals, summary.totals);
    }

    @Override
    public int hashCode() {

        return Objects.hash(shiftAggregations, totals);
    }
}
