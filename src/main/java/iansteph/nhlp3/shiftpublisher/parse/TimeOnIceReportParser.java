package iansteph.nhlp3.shiftpublisher.parse;

import iansteph.nhlp3.shiftpublisher.model.toi.PlayerTimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.TimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.player.Shift;
import iansteph.nhlp3.shiftpublisher.model.toi.player.Summary;
import iansteph.nhlp3.shiftpublisher.model.toi.player.summary.ShiftAggregation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class TimeOnIceReportParser {

    private static final String PLAYER_SHIFT_REPORT_DATA_KEY = "playerShiftReportData";
    private static final String PLAYER_TIME_ON_ICE_SUMMARY_DATA_KEY = "playerTimeOnIceSummaryData";
    private static final String SHIFT_EVENT_GOAL_KEY = "G";
    private static final String SHIFT_EVENT_PENALTY_KEY = "P";
    private static final String TOTAL_SHIFT_AGGREGATION = "TOT";

    private static final Logger LOGGER = LogManager.getLogger(TimeOnIceReportParser.class);

    public TimeOnIceReport parse(final Document rawTimeOnIceReport) {

        final Element mainDataTable = retrieveMainDataTable(rawTimeOnIceReport);

        final Map<String, Map<String, List<Element>>> rawTimeOnIceData = new HashMap<>();
        final Stack<String> currentGroupingQueue = new Stack<>();
        currentGroupingQueue.push(null);

        mainDataTable.children()
                .forEach(element -> {

                    final boolean doesElementHaveChildren = element.childrenSize() == 1;
                    final Element childElement = element.child(0);
                    final boolean isPlayerHeaderChildElement = childElement.hasClass("playerHeading + border");
                    final TextNode grandchildElement = (TextNode) childElement.childNode(0);
                    final String grandchildTextNodeValue = grandchildElement.text();
                    final String currentGrouping = currentGroupingQueue.peek();
                    if (doesElementHaveChildren &&
                            isPlayerHeaderChildElement &&
                            !grandchildTextNodeValue.trim().isEmpty()) {

                        currentGroupingQueue.push(grandchildTextNodeValue);
                        final Map<String, List<Element>> separatedTimeOnIceReportData = new HashMap<>();
                        separatedTimeOnIceReportData.put(PLAYER_SHIFT_REPORT_DATA_KEY, new ArrayList<>());
                        separatedTimeOnIceReportData.put(PLAYER_TIME_ON_ICE_SUMMARY_DATA_KEY, new ArrayList<>());
                        rawTimeOnIceData.put(grandchildTextNodeValue, separatedTimeOnIceReportData);
                    }
                    else if (currentGrouping != null) {

                        final Map<String, List<Element>> currentGroupingData = rawTimeOnIceData.get(currentGrouping);
                        if (isElementPlayerShiftReportData(element)) {

                            currentGroupingData.get(PLAYER_SHIFT_REPORT_DATA_KEY).add(element);
                        }
                        else if (isElementPlayerTimeOnIceSummaryData(element)) {

                            currentGroupingData.get(PLAYER_TIME_ON_ICE_SUMMARY_DATA_KEY).add(element);
                        }
                    }
                });

        final List<PlayerTimeOnIceReport> playerTimeOnIceReports = rawTimeOnIceData.entrySet().stream()
                .map(entry -> {

                    final String rawPlayerIdentifier = entry.getKey();
                    final Map<String, List<Element>> rawTimeOnIceReportData = entry.getValue();
                    final List<Element> rawShiftReportData = rawTimeOnIceReportData.get(PLAYER_SHIFT_REPORT_DATA_KEY);
                    final List<Element> rawTimeOnIceSummaryData = rawTimeOnIceReportData.get(PLAYER_TIME_ON_ICE_SUMMARY_DATA_KEY);
                    final String teamName = parseTeamName(rawTimeOnIceReport);
                    final PlayerTimeOnIceReport playerTimeOnIceReport = parsePlayerIdentifiers(teamName, rawPlayerIdentifier);
                    final List<Shift> shift = parsePlayerShiftReport(rawShiftReportData);
                    playerTimeOnIceReport.setShifts(shift);
                    final Summary summary = parsePlayerTimeOnIceSummary(rawTimeOnIceSummaryData);
                    playerTimeOnIceReport.setSummary(summary);
                    return playerTimeOnIceReport;
                })
                .collect(Collectors.toList());
        final TimeOnIceReport timeOnIceReport = parseGameContext(rawTimeOnIceReport);
        timeOnIceReport.setPlayerTimeOnIceReports(playerTimeOnIceReports);

        return timeOnIceReport;
    }

    private Element retrieveMainDataTable(final Document document) {

        final Element mainDataTable = document
                .body()    // Get the HTML body
                .child(2)  // Get the main section of the page
                .child(0)  // Narrow down into top-most table
                .child(0)  // Narrow down into table body
                .child(3)  // Skip to the beginning of shift data rows
                .child(0)  // Remove surrounding <td> element containing all shift data
                .child(0)  // Narrow down into surrounding <table> element containing all shift data
                .child(0); // Narrow down into table body element containing all shift data
        return mainDataTable;
    }

    private boolean isElementPlayerShiftReportData(final Element element) {

        final boolean hasOddColorClass = element.hasClass("oddColor");
        final boolean hasEvenColorClass = element.hasClass("evenColor");
        final boolean isPlayerShiftReportDataElement = hasOddColorClass || hasEvenColorClass;
        return isPlayerShiftReportDataElement;
    }

    private boolean isElementPlayerTimeOnIceSummaryData(final Element element) {

        final boolean doesElementHaveChild = element.childrenSize() == 1;
        if (doesElementHaveChild) {

            final boolean doesChildElementHaveChild = element.child(0).childrenSize() == 1;
            if (doesChildElementHaveChild) {

                final boolean doesElementContainTableChildElement = element.child(0).child(0).tagName().equals("table");
                return doesElementContainTableChildElement;
            }
            else {

                return false;
            }
        }
        else {

            return false;
        }
    }

    private TimeOnIceReport parseGameContext(final Document document) {

        final List<Element> gameContextData = document.getElementsByAttribute("xmlns:ext").first().child(0).child(0).children();
        TimeOnIceReport baseTimeOnIceReport = new TimeOnIceReport();

        // Visitor and home game context
        final Element rawVisitorTeamContext = gameContextData.get(0);
        final TimeOnIceReport timeOnIceReportWithVisitorContext = parseTeamContext(baseTimeOnIceReport, rawVisitorTeamContext);
        final Element rawHomeTeamContext = gameContextData.get(2);
        final TimeOnIceReport timeOnIceReportWithVisitorAndHomeContext = parseTeamContext(timeOnIceReportWithVisitorContext, rawHomeTeamContext);

        // Shared game context
        final List<String> rawSharedGameContext = gameContextData.get(1).child(0).child(0).children().stream()
                .map(element -> element.child(0))
                .filter(element -> !element.childNodes().isEmpty())
                .map(element -> ((TextNode) element.childNode(0)).text().trim())
                .filter(string -> !string.isEmpty())
                .collect(Collectors.toList());

        // Playoff game TOI Reports have an additional line in this section for the playoff round name
        int indexOffsetIfPlayoffGame = rawSharedGameContext.size() == 7 ? 1 : 0;

        // Date
        final String rawDate = rawSharedGameContext.get(1 + indexOffsetIfPlayoffGame);
        timeOnIceReportWithVisitorAndHomeContext.setDate(rawDate);

        // Venue context
        final String[] rawVenueContextComponents = rawSharedGameContext.get(2 + indexOffsetIfPlayoffGame).split("at");

        // Attendance
        final String[] attendanceComponents = rawVenueContextComponents[0].split(" ");
        try {

            final Integer attendance = NumberFormat.getNumberInstance(Locale.ENGLISH).parse(attendanceComponents[1].trim()).intValue();
            timeOnIceReportWithVisitorAndHomeContext.setAttendance(attendance);
        }
        catch (final Exception e) {

            LOGGER.error(e);
            throw new RuntimeException(e);
        }

        // Venue name
        final String venueName = rawVenueContextComponents[1].trim();
        timeOnIceReportWithVisitorAndHomeContext.setVenueName(venueName);

        // Game time
        final String[] gameTimeComponents = rawSharedGameContext.get(3 + indexOffsetIfPlayoffGame).split(";");

        // Start time
        final String startTime = gameTimeComponents[0].trim().substring(6);
        timeOnIceReportWithVisitorAndHomeContext.setStartTime(startTime);

        // End time
        final String endTime = gameTimeComponents[1].trim().substring(4);
        timeOnIceReportWithVisitorAndHomeContext.setEndTime(endTime);

        // NHL game number
        final String nhlGameNumber = rawSharedGameContext.get(4 + indexOffsetIfPlayoffGame).trim().substring(5);
        timeOnIceReportWithVisitorAndHomeContext.setNhlGameNumber(nhlGameNumber);

        // Game state
        final String gameState = rawSharedGameContext.get(5 + indexOffsetIfPlayoffGame);
        timeOnIceReportWithVisitorAndHomeContext.setGameState(gameState);
        return timeOnIceReportWithVisitorAndHomeContext;
    }

    private TimeOnIceReport parseTeamContext(final TimeOnIceReport timeOnIceReport, final Element rawTeamContext) {

        final List<Element> teamContextData = rawTeamContext.child(0).child(0).children();
        final Integer teamScore = Integer.parseInt(((TextNode) teamContextData.get(1).child(0).child(0).child(0).child(0).child(1).childNode(0)).text().trim());
        final Element teamGameDescription = teamContextData.get(2).child(0);
        final String teamName = ((TextNode) teamGameDescription.childNode(0)).text().trim();
        final String[] teamGameIdentifierComponents = ((TextNode) teamGameDescription.childNode(2)).text().trim().split(" ");
        final Integer teamOverallGameNumber = Integer.parseInt(teamGameIdentifierComponents[1]);
        final Integer teamSpecificGameNumber = Integer.parseInt(teamGameIdentifierComponents[4]);

        if (teamGameIdentifierComponents[2].equals("Away")) {

            timeOnIceReport.setVisitorTeamScore(teamScore);
            timeOnIceReport.setVisitorTeamName(teamName);
            timeOnIceReport.setVisitorTeamGameNumber(teamOverallGameNumber);
            timeOnIceReport.setVisitorTeamAwayGameNumber(teamSpecificGameNumber);
        }
        else {

            timeOnIceReport.setHomeTeamScore(teamScore);
            timeOnIceReport.setHomeTeamName(teamName);
            timeOnIceReport.setHomeTeamGameNumber(teamOverallGameNumber);
            timeOnIceReport.setHomeTeamHomeGameNumber(teamSpecificGameNumber);
        }
        return timeOnIceReport;
    }

    private String parseTeamName(final Document document) {

        final String teamName = ((TextNode) document.getElementsByClass("teamHeading + border").first().childNode(0)).text().trim();

        return teamName;
    }

    private PlayerTimeOnIceReport parsePlayerIdentifiers(final String teamName, final String rawPlayerIdentifier) {

        final PlayerTimeOnIceReport playerTimeOnIceReport = new PlayerTimeOnIceReport();
        playerTimeOnIceReport.setTeamName(teamName);
        final String[] identifiers = rawPlayerIdentifier.split(" ");
        final Integer jerseyNumber = Integer.parseInt(identifiers[0].trim());
        playerTimeOnIceReport.setNumber(jerseyNumber);
        final String trimmedLastName = identifiers[1].replace(",", "").trim();
        playerTimeOnIceReport.setLastName(trimmedLastName);
        final String trimmedFirstName = identifiers[2].trim();
        playerTimeOnIceReport.setFirstName(trimmedFirstName);
        return playerTimeOnIceReport;
    }

    private List<Shift> parsePlayerShiftReport(final List<Element> playerShiftReportData) {

        final List<Shift> shifts = new ArrayList<>();
        playerShiftReportData
                .forEach(element -> {

                    final List<String> columns = element.children().stream()
                            .map(childElement -> ((TextNode) childElement.childNode(0)).text().trim())
                            .collect(Collectors.toList());
                    final Shift shift = new Shift();

                    // Shift number
                    final String rawShiftNumber = columns.get(0);
                    final Integer shiftNumber = Integer.valueOf(rawShiftNumber);
                    shift.setShiftNumber(shiftNumber);

                    // Period
                    final String period = columns.get(1);
                    shift.setPeriod(period);

                    // Start of shift column
                    final String[] rawStartOfShift = columns.get(2).split("/");

                    // Shift start elapsed time
                    final Duration shiftStartElapsedTime = durationFromTextNode(rawStartOfShift[0]);
                    shift.setShiftStartElapsedTime(shiftStartElapsedTime);

                    // Shift start game clock time
                    final Duration shiftStartGameClockTime = durationFromTextNode(rawStartOfShift[1]);
                    shift.setShiftStartGameClockTime(shiftStartGameClockTime);

                    // End of shift column
                    final String[] rawEndOfShift = columns.get(3).split("/");

                    // Shift start elapsed time
                    final Duration endOfShiftElapsedTime = durationFromTextNode(rawEndOfShift[0]);
                    shift.setShiftEndElapsedTime(endOfShiftElapsedTime);

                    // Shift start game clock time
                    final Duration endOfShiftGameClockTime = durationFromTextNode(rawEndOfShift[1]);
                    shift.setShiftEndGameClockTime(endOfShiftGameClockTime);

                    // Duration
                    final String rawShiftDuration = columns.get(4);
                    final Duration shiftDuration = durationFromTextNode(rawShiftDuration);
                    shift.setShiftDuration(shiftDuration);

                    // Event
                    final String rawEvent = columns.get(5);

                    // Has goal during shift
                    final boolean hasGoalDuringShift = rawEvent.contains(SHIFT_EVENT_GOAL_KEY);
                    shift.setHasGoalDuringShift(hasGoalDuringShift);

                    // Has penalty during shift
                    final boolean hasPenaltyDuringShift = rawEvent.contains(SHIFT_EVENT_PENALTY_KEY);
                    shift.setHasPenaltyDuringShift(hasPenaltyDuringShift);

                    shifts.add(shift);
                });
        return shifts;
    }

    private Summary parsePlayerTimeOnIceSummary(final List<Element> playerTimeOnIceSummaryData) {

        final List<Element> rawPlayerSummaryData = playerTimeOnIceSummaryData.get(0).child(0).child(0).child(0).children(); // Select the inner table body
        final List<Element> playerSummaryData = rawPlayerSummaryData.subList(1, rawPlayerSummaryData.size()); // Ignore the first header row
        final Summary summary = new Summary();
        List<ShiftAggregation> shiftAggregations = new ArrayList<>();

        playerSummaryData
                .forEach(element -> {

                    final List<String> columns = element.children().stream()
                            .map(childElement -> ((TextNode) childElement.childNode(0)).text().trim())
                            .collect(Collectors.toList());

                    // Sometimes there can be an empty row in the summary section... 🤷‍♀️
                    // - Example: http://www.nhl.com/scores/htmlreports/20192020/TV020273.HTM @ 52 BEMSTROM, EMIL between the "3" and "TOT" rows
                    if (!columns.isEmpty()) {
                        
                        final ShiftAggregation shiftAggregation = new ShiftAggregation();

                        // Aggregation name
                        final String aggregationName = columns.get(0);
                        shiftAggregation.setAggregationName(aggregationName);

                        // Shifts For
                        final String rawShiftsFor = columns.get(1);
                        shiftAggregation.setShiftsFor(Integer.parseInt(rawShiftsFor));

                        // Average Shift Length
                        final String rawAverageShiftLength = columns.get(2);
                        final Duration averageShiftLength = durationFromTextNode(rawAverageShiftLength);
                        shiftAggregation.setAverageShiftLength(averageShiftLength);

                        // TOI
                        final String rawTimeOnIce = columns.get(3);
                        final Duration timeOnIce = durationFromTextNode(rawTimeOnIce);
                        shiftAggregation.setTimeOnIce(timeOnIce);

                        // Even strength TOI
                        final String rawEvenStrengthTimeOnIce = columns.get(4);
                        final Duration evenStrengthTimeOnIce = durationFromTextNode(rawEvenStrengthTimeOnIce);
                        shiftAggregation.setEvenStrengthTimeOnIce(evenStrengthTimeOnIce);

                        // Power play TOI
                        final String rawPowerPlayTimeOnIce = columns.get(5);
                        final Duration powerPlaceTimeOnIce = durationFromTextNode(rawPowerPlayTimeOnIce);
                        shiftAggregation.setPowerPlayTimeOnIce(powerPlaceTimeOnIce);

                        // Shorthanded TOI
                        final String rawShortHandedTimeOnIce = columns.get(6);
                        final Duration shortHandedTimeOnice = durationFromTextNode(rawShortHandedTimeOnIce);
                        shiftAggregation.setShortHandedTimeOnIce(shortHandedTimeOnice);

                        if (aggregationName.equals(TOTAL_SHIFT_AGGREGATION)) {

                            summary.setTotals(shiftAggregation);
                        } else {

                            shiftAggregations.add(shiftAggregation);
                        }
                    }
                });

        summary.setShiftAggregations(shiftAggregations);
        return summary;
    }

    private Duration durationFromTextNode(final String rawDuration) {

        final String[] durationComponents = rawDuration.split(":");
        final Duration duration = Duration
                .ofMinutes(Long.parseLong(durationComponents[0].trim()))
                .plusSeconds(Long.parseLong(durationComponents[1].trim()));
        return duration;
    }
}
