package iansteph.nhlp3.shiftpublisher.parse;

import iansteph.nhlp3.shiftpublisher.model.toi.PlayerTimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.TimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.player.Shift;
import iansteph.nhlp3.shiftpublisher.model.toi.player.Summary;
import iansteph.nhlp3.shiftpublisher.model.toi.player.summary.ShiftAggregation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TimeOnIceReportParserTest {

    private static final String REGULAR_SEASON_VISITOR_GAME_TOI_REPORT_TEST_RESOURCE = "src/test/resources/TV020273.HTM";
    private static final String PLAYOFF_HOME_GAME_TOI_REPORT_TEST_RESOURCE = "src/test/resources/TH030246.HTM";

    private final TimeOnIceReportParser timeOnIceReportParser = new TimeOnIceReportParser();


    @Test
    public void test_parse_successfully_parses_time_on_ice_report_for_a_given_team_and_regular_season_gameId() throws IOException {

        final Document document = loadTestResourceAsDocumentFromFile(REGULAR_SEASON_VISITOR_GAME_TOI_REPORT_TEST_RESOURCE);

        final TimeOnIceReport timeOnIceReport = timeOnIceReportParser.parse(document);

        assertThat(timeOnIceReport.getDate(), is("Tuesday, November 12, 2019"));
        assertThat(timeOnIceReport.getAttendance(), is(20758));
        assertThat(timeOnIceReport.getVenueName(), is("Centre Bell"));
        assertThat(timeOnIceReport.getStartTime(), is("7:08 EST"));
        assertThat(timeOnIceReport.getEndTime(), is("9:52 EST"));
        assertThat(timeOnIceReport.getNhlGameNumber(), is("0273"));
        assertThat(timeOnIceReport.getGameState(), is("Final"));
        assertThat(timeOnIceReport.getVisitorTeamScore(), is(2));
        assertThat(timeOnIceReport.getVisitorTeamName(), is("COLUMBUS BLUE JACKETS"));
        assertThat(timeOnIceReport.getVisitorTeamGameNumber(), is(18));
        assertThat(timeOnIceReport.getVisitorTeamAwayGameNumber(), is(9));
        assertThat(timeOnIceReport.getHomeTeamScore(), is(3));
        assertThat(timeOnIceReport.getHomeTeamName(), is("MONTREAL CANADIENS"));
        assertThat(timeOnIceReport.getHomeTeamGameNumber(), is(18));
        assertThat(timeOnIceReport.getHomeTeamHomeGameNumber(), is(9));
        verifyPlayerTimeOnIceReports(timeOnIceReport.getPlayerTimeOnIceReports());
    }

    @Test
    public void test_parse_successfully_parses_time_on_ice_report_for_a_given_team_and_playoff_gameId() throws IOException {

        final Document document = loadTestResourceAsDocumentFromFile(PLAYOFF_HOME_GAME_TOI_REPORT_TEST_RESOURCE);

        final TimeOnIceReport timeOnIceReport = timeOnIceReportParser.parse(document);

        assertThat(timeOnIceReport.getDate(), is("Monday, May 6, 2019"));
        assertThat(timeOnIceReport.getAttendance(), is(18098));
        assertThat(timeOnIceReport.getVenueName(), is("Pepsi Center"));
        assertThat(timeOnIceReport.getStartTime(), is("8:09 MDT"));
        assertThat(timeOnIceReport.getEndTime(), is("11:04 MDT"));
        assertThat(timeOnIceReport.getNhlGameNumber(), is("0246"));
        assertThat(timeOnIceReport.getGameState(), is("Final"));
        assertThat(timeOnIceReport.getVisitorTeamScore(), is(3));
        assertThat(timeOnIceReport.getVisitorTeamName(), is("SAN JOSE SHARKS"));
        assertThat(timeOnIceReport.getVisitorTeamGameNumber(), is(13));
        assertThat(timeOnIceReport.getVisitorTeamAwayGameNumber(), is(6));
        assertThat(timeOnIceReport.getHomeTeamScore(), is(4));
        assertThat(timeOnIceReport.getHomeTeamName(), is("COLORADO AVALANCHE"));
        assertThat(timeOnIceReport.getHomeTeamGameNumber(), is(11));
        assertThat(timeOnIceReport.getHomeTeamHomeGameNumber(), is(5));
        verifyPlayerTimeOnIceReports(timeOnIceReport.getPlayerTimeOnIceReports());
    }

    private Document loadTestResourceAsDocumentFromFile(final String fileName) throws IOException {

        final File testResource = new File(fileName);
        final Document testDocument = Jsoup.parse(testResource, "UTF-8");
        return testDocument;
    }

    private void verifyPlayerTimeOnIceReports(final List<PlayerTimeOnIceReport> playerTimeOnIceReports) {

        playerTimeOnIceReports.forEach(timeOnIceReport -> {

            assertThat(timeOnIceReport.getTeamName(), is(notNullValue()));
            assertThat(timeOnIceReport.getNumber(), is(notNullValue()));
            assertThat(timeOnIceReport.getFirstName(), is(notNullValue()));
            assertThat(timeOnIceReport.getLastName(), is(notNullValue()));
            final Summary summary = timeOnIceReport.getSummary();
            final ShiftAggregation totals = summary.getTotals();
            verifyTotals(totals);
            summary.getShiftAggregations().forEach(shiftAggregation -> verifyShiftAggregation(totals, shiftAggregation));
            assertFalse(timeOnIceReport.getShifts().isEmpty());
            timeOnIceReport.getShifts().forEach(this::verifyShift);
            final Map<String, List<Shift>> shiftGroups = new HashMap<>();
            timeOnIceReport.getShifts().forEach(shift -> {

                if (shiftGroups.containsKey(shift.getPeriod())) {

                    shiftGroups.get(shift.getPeriod()).add(shift);
                }
                else {

                    final List<Shift> shifts = new ArrayList<>();
                    shifts.add(shift);
                    shiftGroups.put(shift.getPeriod(), shifts);
                }
            });
            assertThat(shiftGroups.size(), is(summary.getShiftAggregations().size()));
            shiftGroups.entrySet().forEach(entry -> {

                final String key = entry.getKey();
                final List<Shift> shifts = entry.getValue();
                final ShiftAggregation match = summary.getShiftAggregations().stream()
                        .filter(shiftAggregation -> shiftAggregation.getAggregationName().equals(key))
                        .collect(Collectors.toList()).get(0);
                assertThat(match.getAggregationName(), is(key));
                assertThat(match.getShiftsFor(), is(shifts.size()));
                final int sum = shifts.stream()
                        .mapToInt(Shift::getShiftDurationInSeconds)
                        .sum();
                assertThat(match.getTimeOnIceInSeconds(), is(sum));
                final int average = sum / shifts.size();
                assertThat(match.getAverageShiftLengthInSeconds(), is(average));
            });
        });
    }

    private void verifyShift(final Shift shift) {

        assertTrue(shift.getShiftNumber() > 0);
        assertThat(shift.getPeriod(), is(notNullValue()));
        assertTrue(shift.getShiftStartElapsedTimeInSeconds() <= shift.getShiftEndElapsedTimeInSeconds());
        assertTrue(shift.getShiftStartGameClockTimeInSeconds() >= shift.getShiftEndGameClockTimeInSeconds());
        assertTrue(shift.getShiftDurationInSeconds() > 0);
        assertThat(shift.getHasGoalDuringShift(), is(notNullValue()));
        assertThat(shift.getHasPenaltyDuringShift(), is(notNullValue()));
    }

    private void verifyTotals(final ShiftAggregation totals) {

        assertThat(totals.getAggregationName(), is("TOT"));
        assertTrue(totals.getShiftsFor() >= 0);
        assertTrue(totals.getAverageShiftLengthInSeconds() >= 0);
        assertTrue(totals.getTimeOnIceInSeconds() >= 0);
        assertTrue(totals.getEvenStrengthTimeOnIceInSeconds() >= 0);
        assertTrue(totals.getPowerPlayTimeOnIceInSeconds() >= 0);
        assertTrue(totals.getShortHandedTimeOnIceInSeconds() >= 0);
    }

    private void verifyShiftAggregation(final ShiftAggregation totals, final ShiftAggregation shiftAggregation) {

        assertThat(shiftAggregation.getAggregationName(), is(notNullValue()));
        assertTrue(shiftAggregation.getShiftsFor() <= totals.getShiftsFor());
        assertTrue(shiftAggregation.getAverageShiftLengthInSeconds() >= 0);
        assertTrue(shiftAggregation.getTimeOnIceInSeconds() <= totals.getTimeOnIceInSeconds());
        assertTrue(shiftAggregation.getEvenStrengthTimeOnIceInSeconds() <= totals.getEvenStrengthTimeOnIceInSeconds());
        assertTrue(shiftAggregation.getPowerPlayTimeOnIceInSeconds() <= totals.getPowerPlayTimeOnIceInSeconds());
        assertTrue(shiftAggregation.getShortHandedTimeOnIceInSeconds() <= totals.getShortHandedTimeOnIceInSeconds());
        final int sum = shiftAggregation.getEvenStrengthTimeOnIceInSeconds() +
                shiftAggregation.getPowerPlayTimeOnIceInSeconds() +
                shiftAggregation.getShortHandedTimeOnIceInSeconds();
        assertThat(shiftAggregation.getTimeOnIceInSeconds(), is(sum));
    }
}
