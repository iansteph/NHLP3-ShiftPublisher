package iansteph.nhlp3.shiftpublisher.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.shiftpublisher.client.NhlDataClient;
import iansteph.nhlp3.shiftpublisher.client.NhlTimeOnIceClient;
import iansteph.nhlp3.shiftpublisher.client.wrapper.JsoupWrapper;
import iansteph.nhlp3.shiftpublisher.model.Team;
import iansteph.nhlp3.shiftpublisher.model.event.ShiftEvent;
import iansteph.nhlp3.shiftpublisher.model.request.ShiftPublisherRequest;
import iansteph.nhlp3.shiftpublisher.model.roster.Player;
import iansteph.nhlp3.shiftpublisher.model.roster.player.Position;
import iansteph.nhlp3.shiftpublisher.model.toi.PlayerTimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.TimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.player.Shift;
import iansteph.nhlp3.shiftpublisher.parse.TimeOnIceReportParser;
import iansteph.nhlp3.shiftpublisher.proxy.DynamoDbProxy;
import iansteph.nhlp3.shiftpublisher.proxy.NhlDataProxy;
import iansteph.nhlp3.shiftpublisher.proxy.NhlTimeOnIceProxy;
import iansteph.nhlp3.shiftpublisher.proxy.SnsProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.utils.AttributeMap;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class ShiftPublisherHandler implements RequestHandler<ShiftPublisherRequest, Object> {

    private final DynamoDbProxy dynamoDbProxy;
    private final NhlTimeOnIceProxy nhlTimeOnIceProxy;
    private final SnsProxy snsProxy;
    private final TimeOnIceReportParser timeOnIceReportParser;

    private static final Logger LOGGER = LogManager.getLogger(ShiftPublisherHandler.class);

    public ShiftPublisherHandler() {

        final AwsCredentialsProvider defaultAwsCredentialsProvider = DefaultCredentialsProvider.builder().build();
        final SdkHttpClient httpClient = ApacheHttpClient.builder().buildWithDefaults(AttributeMap.empty());
        final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .credentialsProvider(defaultAwsCredentialsProvider)
                .endpointOverride(URI.create("https://dynamodb.us-east-1.amazonaws.com/"))
                .httpClient(httpClient)
                .region(Region.US_EAST_1)
                .build();
        this.dynamoDbProxy = new DynamoDbProxy(dynamoDbClient);
        final JsoupWrapper jsoupWrapper = new JsoupWrapper();
        final S3Client s3Client = S3Client.builder()
                .credentialsProvider(defaultAwsCredentialsProvider)
                .endpointOverride(URI.create("https://s3.us-east-1.amazonaws.com/"))
                .httpClient(httpClient)
                .region(Region.US_EAST_1)
                .build();
        final NhlTimeOnIceClient nhlTimeOnIceClient = new NhlTimeOnIceClient(jsoupWrapper, s3Client);
        this.nhlTimeOnIceProxy = new NhlTimeOnIceProxy(nhlTimeOnIceClient);
        final ObjectMapper objectMapper = new ObjectMapper();
        final SnsClient snsClient = SnsClient.builder()
                .credentialsProvider(defaultAwsCredentialsProvider)
                .endpointOverride(URI.create("https://sns.us-east-1.amazonaws.com/"))
                .httpClient(httpClient)
                .region(Region.US_EAST_1)
                .build();
        this.snsProxy = new SnsProxy(objectMapper, snsClient);

        final RestTemplate restTemplate = new RestTemplate();
        final NhlDataClient nhlDataClient = new NhlDataClient(restTemplate);
        final NhlDataProxy nhlDataProxy = new NhlDataProxy(nhlDataClient);
        this.timeOnIceReportParser = new TimeOnIceReportParser(nhlDataProxy);
    }

    public ShiftPublisherHandler(
            final DynamoDbProxy dynamoDbProxy,
            final NhlTimeOnIceProxy nhlTimeOnIceProxy,
            final SnsProxy snsProxy,
            final TimeOnIceReportParser timeOnIceReportParser
    ) {
        this.dynamoDbProxy = dynamoDbProxy;
        this.nhlTimeOnIceProxy = nhlTimeOnIceProxy;
        this.snsProxy = snsProxy;
        this.timeOnIceReportParser = timeOnIceReportParser;
    }

    public Object handleRequest(final ShiftPublisherRequest shiftPublisherRequest, final Context context) {

        final int gameId = shiftPublisherRequest.getGameId();
        final Map<String, Map<String, Integer>> shiftPublishingRecord = dynamoDbProxy.getShiftPublishingRecordForGameId(gameId);
        final Optional<TimeOnIceReport> visitorTimeOnIceReport = retrieveTimeOnIceReport(gameId, Team.VISITOR);
        final List<ShiftEvent> visitorShiftsToPublish = retrieveShiftsToPublishForTeam(Team.VISITOR, visitorTimeOnIceReport, shiftPublishingRecord);
        final Optional<TimeOnIceReport> homeTimeOnIceReport = retrieveTimeOnIceReport(gameId, Team.HOME);
        final List<ShiftEvent> homeShiftsToPublish = retrieveShiftsToPublishForTeam(Team.HOME, homeTimeOnIceReport, shiftPublishingRecord);
        Arrays.asList(visitorShiftsToPublish, homeShiftsToPublish).forEach(snsProxy::publishShiftEvents);
        dynamoDbProxy.putShiftPublishingRecord(gameId, visitorTimeOnIceReport, homeTimeOnIceReport);
        return HttpStatusCode.OK;
    }

    private Map<String, List<ShiftEvent>> retrieveShiftsToPublishFromTimeOnIceReport(
            final TimeOnIceReport timeOnIceReport,
            final Map<String, Integer> shiftPublishingRecord
    ) {
        final Map<String, List<ShiftEvent>> shiftsToPublishForPlayers =
        timeOnIceReport.getPlayerTimeOnIceReports().stream()
                .collect(Collectors.toMap(
                        this::buildPlayerTimeOnIceReportKey,
                        playerTimeOnIceReport -> {

                            final String playerShiftPublishingRecordKey = buildPlayerTimeOnIceReportKey(playerTimeOnIceReport);
                            if (shiftPublishingRecord.containsKey(playerShiftPublishingRecordKey)) {

                                final List<Shift> playerShifts = playerTimeOnIceReport.getShifts();
                                final int lastAvailableShiftNumber = playerShifts.get(playerShifts.size() - 1).getShiftNumber();
                                final int lastPublishedShiftNumber = shiftPublishingRecord.get(playerShiftPublishingRecordKey);
                                if (lastPublishedShiftNumber < lastAvailableShiftNumber) {

                                    final List<Shift> shifts = playerShifts.subList(lastPublishedShiftNumber, playerShifts.size());
                                    final List<ShiftEvent> shiftEvents = shifts.stream()
                                            .map(shift -> buildShiftEvent(playerTimeOnIceReport, shift))
                                            .collect(Collectors.toList());
                                    return shiftEvents;
                                }
                                else {

                                    // If the value stored in the database is greater than or equal to the TimeOnIceReport -- Do nothing
                                    return Collections.emptyList();
                                }
                            }
                            else {

                                // If the player has not had any shifts published all of their shifts are new and should be published
                                final List<Shift> shifts = playerTimeOnIceReport.getShifts();
                                final List<ShiftEvent> shiftEvents = shifts.stream()
                                        .map(shift -> buildShiftEvent(playerTimeOnIceReport, shift))
                                        .collect(Collectors.toList());
                                return shiftEvents;
                            }
                        }
                ));
        return shiftsToPublishForPlayers;
    }

    private String buildPlayerTimeOnIceReportKey(final PlayerTimeOnIceReport playerTimeOnIceReport) {

        return format("%s %s %d", playerTimeOnIceReport.getFirstName(), playerTimeOnIceReport.getLastName(), playerTimeOnIceReport.getNumber());
    }

    private List<ShiftEvent> retrieveShiftsToPublishForTeam(
            final Team team,
            final Optional<TimeOnIceReport> optionalTimeOnIceReport,
            final Map<String, Map<String, Integer>> shiftPublishingRecord
    ) {

        if (!optionalTimeOnIceReport.isPresent()) {

            // If there was no shift data available there are no shifts to publish
            return Collections.emptyList();
        }
        final TimeOnIceReport teamTimeOnIceReport = optionalTimeOnIceReport.get();
        final String teamKey = team.getLabel().equals("V") ? "visitor" : "home";
        final Map<String, Integer> teamShiftPublishingRecord = shiftPublishingRecord.get(teamKey);
        final Map<String, List<ShiftEvent>> shiftsToPublish = retrieveShiftsToPublishFromTimeOnIceReport(teamTimeOnIceReport, teamShiftPublishingRecord);
        final List<ShiftEvent> shiftEventsToPublish = shiftsToPublish.entrySet().stream()
                .flatMap(stringListEntry -> stringListEntry.getValue().stream())
                .collect(Collectors.toList());
        return shiftEventsToPublish;
    }

    private Optional<TimeOnIceReport> retrieveTimeOnIceReport(final int gameId, final Team team) {

        final Optional<Document> rawTeamTimeOnIceReport = nhlTimeOnIceProxy.getToiReportForGame(gameId, team);
        if (rawTeamTimeOnIceReport.isPresent()) {

            final Optional<TimeOnIceReport> teamTimeOnIceReport = timeOnIceReportParser.parse(rawTeamTimeOnIceReport.get());
            return teamTimeOnIceReport;
        }
        else {

            return Optional.empty();
        }
    }

    private ShiftEvent buildShiftEvent(final PlayerTimeOnIceReport playerTimeOnIceReport, final Shift shift) {

        final ShiftEvent shiftEvent = new ShiftEvent();
        shiftEvent.setPlayerTeamName(playerTimeOnIceReport.getTeamName());
        shiftEvent.setPlayerFirstName(playerTimeOnIceReport.getFirstName());
        shiftEvent.setPlayerLastName(playerTimeOnIceReport.getLastName());
        shiftEvent.setPlayerJerseyNumber(playerTimeOnIceReport.getNumber());
        shiftEvent.setPlayerTeamId(playerTimeOnIceReport.getTeamId());
        shiftEvent.setShift(shift);

        final Player player = playerTimeOnIceReport.getPlayer();
        if (player != null) {

            final int playerId = player.getPerson().getId();
            shiftEvent.setPlayerId(playerId);
            final Position playerPosition = player.getPosition();
            shiftEvent.setPlayerPosition(playerPosition);
        }
        return shiftEvent;
    }
}
