package iansteph.nhlp3.shiftpublisher.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.shiftpublisher.handler.NhlTeamIdMapping;
import iansteph.nhlp3.shiftpublisher.model.roster.Player;
import iansteph.nhlp3.shiftpublisher.model.roster.Roster;
import iansteph.nhlp3.shiftpublisher.model.toi.PlayerTimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.TimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.player.Shift;
import iansteph.nhlp3.shiftpublisher.model.toi.player.Summary;
import iansteph.nhlp3.shiftpublisher.model.toi.player.summary.ShiftAggregation;
import iansteph.nhlp3.shiftpublisher.proxy.NhlDataProxy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimeOnIceReportParserTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String IN_PROGRESS_GAME_ROSTER_TEST_RESOURCE = "src/test/resources/rosters/in-progress-game.json";
    private static final String IN_PROGRESS_GAME_TOI_REPORT_TEST_RESOURCE = "src/test/resources/timeonicereports/in-progress-game.HTM";
    private static final String NO_SHIFT_DATA_ROSTER_TEST_RESOURCE = "src/test/resources/rosters/no-shift-data.json";
    private static final String NO_SHIFT_DATA_TOI_REPORT_TEST_RESOURCE = "src/test/resources/timeonicereports/no-shift-data.HTM";
    private static final String PLAYOFF_HOME_GAME_ROSTER_TEST_RESOURCE = "src/test/resources/rosters/playoff-game.json";
    private static final String PLAYOFF_HOME_GAME_TOI_REPORT_TEST_RESOURCE = "src/test/resources/timeonicereports/playoff-game.HTM";
    private static final String REGULAR_SEASON_VISITOR_GAME_ROSTER_TEST_RESOURCE = "src/test/resources/rosters/regular-season-game.json";
    private static final String REGULAR_SEASON_VISITOR_GAME_TOI_REPORT_TEST_RESOURCE = "src/test/resources/timeonicereports/regular-season-game.HTM";

    private final NhlDataProxy mockNhlDataProxy = mock(NhlDataProxy.class);
    private final TimeOnIceReportParser timeOnIceReportParser = new TimeOnIceReportParser(mockNhlDataProxy);

    @Test
    public void test_parse_successfully_parses_time_on_ice_report_for_a_given_team_and_regular_season_gameId() throws IOException {

        final Roster roster = parseTestResourceIntoRoster(REGULAR_SEASON_VISITOR_GAME_ROSTER_TEST_RESOURCE);
        when(mockNhlDataProxy.getRosterForTeamId(anyInt())).thenReturn(roster);
        final Document document = loadTestResourceAsDocumentFromFile(REGULAR_SEASON_VISITOR_GAME_TOI_REPORT_TEST_RESOURCE);

        final Optional<TimeOnIceReport> optionalTimeOnIceReport = timeOnIceReportParser.parse(document);

        assertThat(optionalTimeOnIceReport, is(not(Optional.empty())));
        final TimeOnIceReport timeOnIceReport = optionalTimeOnIceReport.get();
        assertThat(timeOnIceReport.getDate(), is("Tuesday, November 12, 2019"));
        assertThat(timeOnIceReport.getAttendance(), is("20,758"));
        assertThat(timeOnIceReport.getVenueName(), is("Centre Bell"));
        assertThat(timeOnIceReport.getStartTime(), is("7:08 EST"));
        assertThat(timeOnIceReport.getEndTime(), is("9:52 EST"));
        assertThat(timeOnIceReport.getNhlGameNumber(), is("0273"));
        assertThat(timeOnIceReport.getGameState(), is("Final"));
        assertThat(timeOnIceReport.getVisitorTeamScore(), is(2));
        assertThat(timeOnIceReport.getVisitorTeamName(), is("COLUMBUS BLUE JACKETS"));
        assertThat(timeOnIceReport.getVisitorTeamId(), is(29));
        assertThat(timeOnIceReport.getVisitorTeamGameNumber(), is(18));
        assertThat(timeOnIceReport.getVisitorTeamAwayGameNumber(), is(9));
        assertThat(timeOnIceReport.getHomeTeamScore(), is(3));
        assertThat(timeOnIceReport.getHomeTeamName(), is("MONTREAL CANADIENS"));
        assertThat(timeOnIceReport.getHomeTeamId(), is(8));
        assertThat(timeOnIceReport.getHomeTeamGameNumber(), is(18));
        assertThat(timeOnIceReport.getHomeTeamHomeGameNumber(), is(9));
        verifyPlayerTimeOnIceReports(timeOnIceReport.getPlayerTimeOnIceReports());
    }

    @Test
    public void test_parse_successfully_parses_time_on_ice_report_for_a_given_team_and_playoff_gameId() throws IOException {

        final Roster roster = parseTestResourceIntoRoster(PLAYOFF_HOME_GAME_ROSTER_TEST_RESOURCE);
        when(mockNhlDataProxy.getRosterForTeamId(anyInt())).thenReturn(roster);
        final Document document = loadTestResourceAsDocumentFromFile(PLAYOFF_HOME_GAME_TOI_REPORT_TEST_RESOURCE);

        final Optional<TimeOnIceReport> optionalTimeOnIceReport = timeOnIceReportParser.parse(document);

        assertThat(optionalTimeOnIceReport, is(not(Optional.empty())));
        final TimeOnIceReport timeOnIceReport = optionalTimeOnIceReport.get();
        assertThat(timeOnIceReport.getDate(), is("Monday, May 6, 2019"));
        assertThat(timeOnIceReport.getAttendance(), is("18,098"));
        assertThat(timeOnIceReport.getVenueName(), is("Pepsi Center"));
        assertThat(timeOnIceReport.getStartTime(), is("8:09 MDT"));
        assertThat(timeOnIceReport.getEndTime(), is("11:04 MDT"));
        assertThat(timeOnIceReport.getNhlGameNumber(), is("0246"));
        assertThat(timeOnIceReport.getGameState(), is("Final"));
        assertThat(timeOnIceReport.getVisitorTeamScore(), is(3));
        assertThat(timeOnIceReport.getVisitorTeamName(), is("SAN JOSE SHARKS"));
        assertThat(timeOnIceReport.getVisitorTeamId(), is(28));
        assertThat(timeOnIceReport.getVisitorTeamGameNumber(), is(13));
        assertThat(timeOnIceReport.getVisitorTeamAwayGameNumber(), is(6));
        assertThat(timeOnIceReport.getHomeTeamScore(), is(4));
        assertThat(timeOnIceReport.getHomeTeamName(), is("COLORADO AVALANCHE"));
        assertThat(timeOnIceReport.getHomeTeamId(), is(21));
        assertThat(timeOnIceReport.getHomeTeamGameNumber(), is(11));
        assertThat(timeOnIceReport.getHomeTeamHomeGameNumber(), is(5));
        verifyPlayerTimeOnIceReports(timeOnIceReport.getPlayerTimeOnIceReports());
    }

    @Test
    public void test_parse_successfully_parses_time_on_ice_report_for_a_given_team_and_with_empty_average_shift_length_cell_in_aggregation_section() throws IOException {

        final Roster roster = parseTestResourceIntoRoster(IN_PROGRESS_GAME_ROSTER_TEST_RESOURCE);
        when(mockNhlDataProxy.getRosterForTeamId(anyInt())).thenReturn(roster);
        final Document document = loadTestResourceAsDocumentFromFile(IN_PROGRESS_GAME_TOI_REPORT_TEST_RESOURCE);

        final Optional<TimeOnIceReport> optionalTimeOnIceReport = timeOnIceReportParser.parse(document);

        assertThat(optionalTimeOnIceReport, is(not(Optional.empty())));
        final TimeOnIceReport timeOnIceReport = optionalTimeOnIceReport.get();
        assertThat(timeOnIceReport.getDate(), is("Tuesday, July 28, 2020"));
        assertThat(timeOnIceReport.getAttendance(), is("n/a"));
        assertThat(timeOnIceReport.getVenueName(), is("Scotiabank Arena"));
        assertThat(timeOnIceReport.getStartTime(), is("8:12 EDT"));
        assertThat(timeOnIceReport.getEndTime(), is(nullValue()));
        assertThat(timeOnIceReport.getNhlGameNumber(), is("1002"));
        assertThat(timeOnIceReport.getGameState(), is("Period 3 (07:07 Remaining)"));
        assertThat(timeOnIceReport.getVisitorTeamScore(), is(4));
        assertThat(timeOnIceReport.getVisitorTeamName(), is("TORONTO MAPLE LEAFS"));
        assertThat(timeOnIceReport.getVisitorTeamId(), is(10));
        assertThat(timeOnIceReport.getVisitorTeamGameNumber(), is(1));
        assertThat(timeOnIceReport.getVisitorTeamAwayGameNumber(), is(1));
        assertThat(timeOnIceReport.getHomeTeamScore(), is(2));
        assertThat(timeOnIceReport.getHomeTeamName(), is("MONTREAL CANADIENS"));
        assertThat(timeOnIceReport.getHomeTeamId(), is(8));
        assertThat(timeOnIceReport.getHomeTeamGameNumber(), is(1));
        assertThat(timeOnIceReport.getHomeTeamHomeGameNumber(), is(1));
        verifyPlayerTimeOnIceReports(timeOnIceReport.getPlayerTimeOnIceReports());
    }

    @Test
    public void test_parse_successfully_parses_time_on_ice_report_for_a_given_team_with_no_shift_data() throws IOException {

        final Roster roster = parseTestResourceIntoRoster(NO_SHIFT_DATA_ROSTER_TEST_RESOURCE);
        when(mockNhlDataProxy.getRosterForTeamId(anyInt())).thenReturn(roster);
        final Document document = loadTestResourceAsDocumentFromFile(NO_SHIFT_DATA_TOI_REPORT_TEST_RESOURCE);

        final Optional<TimeOnIceReport> optionalTimeOnIceReport = timeOnIceReportParser.parse(document);

        assertThat(optionalTimeOnIceReport, is(Optional.empty()));
    }

    private Document loadTestResourceAsDocumentFromFile(final String fileName) throws IOException {

        final File testResource = new File(fileName);
        final Document testDocument = Jsoup.parse(testResource, "UTF-8");
        return testDocument;
    }

    private Roster parseTestResourceIntoRoster(final String filename) throws IOException {

        final File testResource = new File(filename);
        final Roster roster = OBJECT_MAPPER.readValue(testResource, Roster.class);
        return roster;
    }

    private void verifyPlayerTimeOnIceReports(final List<PlayerTimeOnIceReport> playerTimeOnIceReports) {

        playerTimeOnIceReports.forEach(timeOnIceReport -> {

            assertThat(timeOnIceReport.getTeamName(), is(notNullValue()));
            final int teamId = NhlTeamIdMapping.TEAM_NAME_TO_TEAM_ID_MAP.get(timeOnIceReport.getTeamName());
            assertThat(timeOnIceReport.getTeamId(), is(teamId));
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
            shiftGroups.entrySet().forEach(entry -> {

                final String key = entry.getKey();
                final List<Shift> shifts = entry.getValue();
                final ShiftAggregation match = summary.getShiftAggregations().stream()
                        .filter(shiftAggregation -> shiftAggregation.getAggregationName().equals(key))
                        .collect(Collectors.toList()).get(0);
                assertThat(match.getAggregationName(), is(key));
                assertThat(match.getShiftsFor(), is(shifts.size()));
                assertThat(match.getTimeOnIceInSeconds(), is(notNullValue()));
                assertThat(match.getAverageShiftLengthInSeconds(), is(notNullValue()));
            });
            verifyPlayer(timeOnIceReport.getPlayer());
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

        // Sometimes there can be a blank cell data (observed for avg column for goalies)
        if (shiftAggregation.getAverageShiftLengthInSeconds() != null) {

            assertTrue(shiftAggregation.getAverageShiftLengthInSeconds() >= 0);
        }
        assertTrue(shiftAggregation.getTimeOnIceInSeconds() <= totals.getTimeOnIceInSeconds());
        assertTrue(shiftAggregation.getEvenStrengthTimeOnIceInSeconds() <= totals.getEvenStrengthTimeOnIceInSeconds());
        assertTrue(shiftAggregation.getPowerPlayTimeOnIceInSeconds() <= totals.getPowerPlayTimeOnIceInSeconds());
        assertTrue(shiftAggregation.getShortHandedTimeOnIceInSeconds() <= totals.getShortHandedTimeOnIceInSeconds());
        final int sum = shiftAggregation.getEvenStrengthTimeOnIceInSeconds() +
                shiftAggregation.getPowerPlayTimeOnIceInSeconds() +
                shiftAggregation.getShortHandedTimeOnIceInSeconds();
        assertThat(shiftAggregation.getTimeOnIceInSeconds(), is(sum));
    }

    private void verifyPlayer(final Player player) {

        assertThat(player.getJerseyNumber(), is(notNullValue()));
        assertThat(player.getPerson(), is(notNullValue()));
        assertThat(player.getPosition(), is(notNullValue()));
    }
}
