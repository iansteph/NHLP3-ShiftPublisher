package iansteph.nhlp3.shiftpublisher.parse;

import iansteph.nhlp3.shiftpublisher.handler.NhlTeamIdMapping;
import iansteph.nhlp3.shiftpublisher.model.roster.Player;
import iansteph.nhlp3.shiftpublisher.model.roster.Roster;
import iansteph.nhlp3.shiftpublisher.model.toi.PlayerTimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.TimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.player.Shift;
import iansteph.nhlp3.shiftpublisher.model.toi.player.Summary;
import iansteph.nhlp3.shiftpublisher.model.toi.player.summary.ShiftAggregation;
import iansteph.nhlp3.shiftpublisher.proxy.NhlDataProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class TimeOnIceReportParser {

    private final NhlDataProxy nhlDataProxy;

    private static final String PLAYER_SHIFT_REPORT_DATA_KEY = "playerShiftReportData";
    private static final String PLAYER_TIME_ON_ICE_SUMMARY_DATA_KEY = "playerTimeOnIceSummaryData";
    private static final String SHIFT_EVENT_GOAL_KEY = "G";
    private static final String SHIFT_EVENT_PENALTY_KEY = "P";
    private static final String TOTAL_SHIFT_AGGREGATION = "TOT";

    private static final Logger LOGGER = LogManager.getLogger(TimeOnIceReportParser.class);

    public TimeOnIceReportParser(final NhlDataProxy nhlDataProxy) {

        this.nhlDataProxy = nhlDataProxy;
    }

    public Optional<TimeOnIceReport> parse(final Document rawTimeOnIceReport) {

        final Optional<Element> optionalMainDataTable = retrieveMainDataTable(rawTimeOnIceReport);
        if (!optionalMainDataTable.isPresent()) {

            // If there is no shift data in the main data table yet, skip parsing
            return Optional.empty();
        }
        final Element mainDataTable = optionalMainDataTable.get();
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

        final String teamName = parseTeamName(rawTimeOnIceReport);
        final int teamId = NhlTeamIdMapping.TEAM_NAME_TO_TEAM_ID_MAP.get(teamName);
        final Roster roster = nhlDataProxy.getRosterForTeamId(teamId);
        final Map<String, Player> playerDataMap = roster.getRoster().stream()
                .collect(Collectors.toMap(
                        player -> (player.getJerseyNumber() + " " + player.getPerson().getFullName()).toUpperCase(),
                        player -> player
                ));
        final List<PlayerTimeOnIceReport> playerTimeOnIceReports = rawTimeOnIceData.entrySet().stream()
                .map(entry -> {

                    final String rawPlayerIdentifier = entry.getKey();
                    final Map<String, List<Element>> rawTimeOnIceReportData = entry.getValue();
                    final List<Element> rawShiftReportData = rawTimeOnIceReportData.get(PLAYER_SHIFT_REPORT_DATA_KEY);
                    final List<Element> rawTimeOnIceSummaryData = rawTimeOnIceReportData.get(PLAYER_TIME_ON_ICE_SUMMARY_DATA_KEY);
                    final PlayerTimeOnIceReport playerTimeOnIceReport = parsePlayerIdentifiers(teamName, rawPlayerIdentifier);
                    final List<Shift> shift = parsePlayerShiftReport(rawShiftReportData);
                    playerTimeOnIceReport.setShifts(shift);
                    final Summary summary = parsePlayerTimeOnIceSummary(rawTimeOnIceSummaryData);
                    playerTimeOnIceReport.setSummary(summary);
                    playerTimeOnIceReport.setTeamId(teamId);
                    final String playerKey = playerTimeOnIceReport.getNumber() + " " + playerTimeOnIceReport.getFirstName() + " " + playerTimeOnIceReport.getLastName();
                    final Player player = playerDataMap.get(playerKey);
                    playerTimeOnIceReport.setPlayer(player);
                    return playerTimeOnIceReport;
                })
                .collect(Collectors.toList());
        final TimeOnIceReport timeOnIceReport = parseGameContext(rawTimeOnIceReport);
        timeOnIceReport.setPlayerTimeOnIceReports(playerTimeOnIceReports);

        return Optional.of(timeOnIceReport);
    }

    private Optional<Element> retrieveMainDataTable(final Document document) {

        final Element mainDataTable = document
                .body()    // Get the HTML body
                .child(2)  // Get the main section of the page
                .child(0)  // Narrow down into top-most table
                .child(0)  // Narrow down into table body
                .child(3)  // Skip to the beginning of shift data rows
                .child(0)  // Remove surrounding <td> element containing all shift data
                .child(0);  // Narrow down into surrounding <table> element containing all shift data

        // When the game starts in the first minutes there is no shift data in the table
        if (mainDataTable.childrenSize() > 0) {

            final Element mainDataTableElement =  mainDataTable.child(0); // Narrow down into table body element containing all shift data
            return Optional.of(mainDataTableElement);
        }
        else {

            // TODO - Write test case for this
            return Optional.empty();
        }
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

        /*
         * For the COVID impacted NHL seasons there appears to be a change to the venue and attendance line on the TOI report. For venues
         * that do not allow fans (or for some other reason) there is no "Attendance X at <VENUE_NAME>" and it is just "<VENUE_NAME>"
         */
        final String rawVenueAndAttendance = rawSharedGameContext.get(2 + indexOffsetIfPlayoffGame);

        // Another case where attendance has no data but is rendered just so slightly differently
        if (rawVenueAndAttendance.contains("Attendance at")) {

            // Attendance
            timeOnIceReportWithVisitorAndHomeContext.setAttendance(null);

            // Venue name
            final String[] rawVenueContextComponents = rawVenueAndAttendance.split("at");
            final String venueName = rawVenueContextComponents[1].trim();
            timeOnIceReportWithVisitorAndHomeContext.setVenueName(venueName);
        }
        else if (rawVenueAndAttendance.contains(" at ")) {

            final String[] rawVenueContextComponents = rawVenueAndAttendance.split("at");

            // Attendance
            final String[] attendanceComponents = rawVenueContextComponents[0].split(" ");
            final String attendance = attendanceComponents[1].trim();
            timeOnIceReportWithVisitorAndHomeContext.setAttendance(attendance);

            // Venue name
            final String venueName = rawVenueContextComponents[1].trim();
            timeOnIceReportWithVisitorAndHomeContext.setVenueName(venueName);
        }
        else {

            // There is no "at" nor attendance information. It is just the venue name
            timeOnIceReportWithVisitorAndHomeContext.setAttendance(null);
            timeOnIceReportWithVisitorAndHomeContext.setVenueName(rawVenueAndAttendance);
        }

        // Game time
        final String[] gameTimeComponents = rawSharedGameContext.get(3 + indexOffsetIfPlayoffGame).split(";");

        // Start time
        final String startTime = gameTimeComponents[0].trim().substring(6);
        timeOnIceReportWithVisitorAndHomeContext.setStartTime(startTime);

        // End time
        if (gameTimeComponents.length > 1) {

            // When game is in progress the end time is not present
            final String endTime = gameTimeComponents[1].trim().substring(4);
            timeOnIceReportWithVisitorAndHomeContext.setEndTime(endTime);
        }

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
        final int teamId = NhlTeamIdMapping.TEAM_NAME_TO_TEAM_ID_MAP.get(teamName);

        if (teamGameIdentifierComponents[2].equals("Away")) {

            timeOnIceReport.setVisitorTeamScore(teamScore);
            timeOnIceReport.setVisitorTeamName(teamName);
            timeOnIceReport.setVisitorTeamId(teamId);
            timeOnIceReport.setVisitorTeamGameNumber(teamOverallGameNumber);
            timeOnIceReport.setVisitorTeamAwayGameNumber(teamSpecificGameNumber);
        }
        else {

            timeOnIceReport.setHomeTeamScore(teamScore);
            timeOnIceReport.setHomeTeamName(teamName);
            timeOnIceReport.setHomeTeamId(teamId);
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
        final int teamId = NhlTeamIdMapping.TEAM_NAME_TO_TEAM_ID_MAP.get(teamName);
        playerTimeOnIceReport.setTeamId(teamId);
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
                    if (period.isEmpty() && shifts.isEmpty()) {

                        // If the period is missing and there's no previous shift then it is the beginning of the game
                        shift.setPeriod("1");
                    }
                    else if (period.isEmpty()) {

                        // If the period is missing then use the last shift's period for this current shift's period
                        shift.setPeriod(shifts.get(shifts.size() - 1).getPeriod());
                    }
                    else {

                        shift.setPeriod(period);
                    }

                    // Start of shift column
                    final String[] rawStartOfShift = columns.get(2).split("/");

                    if (rawStartOfShift.length == 2) {

                        // Shift start elapsed time
                        final Duration shiftStartElapsedTime = durationFromTextNode(rawStartOfShift[0]);
                        shift.setShiftStartElapsedTimeInSeconds(convertDurationToSeconds(shiftStartElapsedTime));

                        // Shift start game clock time
                        final Duration shiftStartGameClockTime = durationFromTextNode(rawStartOfShift[1]);
                        shift.setShiftStartGameClockTimeInSeconds(convertDurationToSeconds(shiftStartGameClockTime));
                    }
                    else {

                        shift.setShiftStartElapsedTimeInSeconds(0);
                        shift.setShiftStartGameClockTimeInSeconds(0);
                    }

                    // End of shift column
                    final String[] rawEndOfShift = columns.get(3).split("/");

                    if (rawEndOfShift.length == 2) {

                        // Shift start elapsed time
                        final Duration endOfShiftElapsedTime = durationFromTextNode(rawEndOfShift[0]);
                        shift.setShiftEndElapsedTimeInSeconds(convertDurationToSeconds(endOfShiftElapsedTime));

                        // Shift start game clock time
                        final Duration endOfShiftGameClockTime = durationFromTextNode(rawEndOfShift[1]);
                        shift.setShiftEndGameClockTimeInSeconds(convertDurationToSeconds(endOfShiftGameClockTime));
                    }
                    else {

                        shift.setShiftEndElapsedTimeInSeconds(0);
                        shift.setShiftEndGameClockTimeInSeconds(0);
                    }

                    // Duration
                    final String rawShiftDuration = columns.get(4);
                    final Duration shiftDuration = durationFromTextNode(rawShiftDuration);
                    shift.setShiftDurationInSeconds(convertDurationToSeconds(shiftDuration));

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
                        if (!rawAverageShiftLength.trim().isEmpty()) { // Sometimes there can be a blank cell data (observed for avg column for goalies)

                            final Duration averageShiftLength = durationFromTextNode(rawAverageShiftLength);
                            shiftAggregation.setAverageShiftLengthInSeconds(convertDurationToSeconds(averageShiftLength));
                        }

                        // TOI
                        final String rawTimeOnIce = columns.get(3);
                        final Duration timeOnIce = durationFromTextNode(rawTimeOnIce);
                        shiftAggregation.setTimeOnIceInSeconds(convertDurationToSeconds(timeOnIce));

                        // Even strength TOI
                        final String rawEvenStrengthTimeOnIce = columns.get(4);
                        final Duration evenStrengthTimeOnIce = durationFromTextNode(rawEvenStrengthTimeOnIce);
                        shiftAggregation.setEvenStrengthTimeOnIceInSeconds(convertDurationToSeconds(evenStrengthTimeOnIce));

                        // Power play TOI
                        final String rawPowerPlayTimeOnIce = columns.get(5);
                        final Duration powerPlaceTimeOnIce = durationFromTextNode(rawPowerPlayTimeOnIce);
                        shiftAggregation.setPowerPlayTimeOnIceInSeconds(convertDurationToSeconds(powerPlaceTimeOnIce));

                        // Shorthanded TOI
                        final String rawShortHandedTimeOnIce = columns.get(6);
                        final Duration shortHandedTimeOnice = durationFromTextNode(rawShortHandedTimeOnIce);
                        shiftAggregation.setShortHandedTimeOnIceInSeconds(convertDurationToSeconds(shortHandedTimeOnice));

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

    private Integer convertDurationToSeconds(final Duration duration) {

        return Math.toIntExact(duration.getSeconds());
    }
}
