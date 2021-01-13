package iansteph.nhlp3.shiftpublisher.handler;

import iansteph.nhlp3.shiftpublisher.model.Team;
import iansteph.nhlp3.shiftpublisher.model.event.ShiftEvent;
import iansteph.nhlp3.shiftpublisher.model.request.ShiftPublisherRequest;
import iansteph.nhlp3.shiftpublisher.model.roster.Player;
import iansteph.nhlp3.shiftpublisher.model.roster.player.Person;
import iansteph.nhlp3.shiftpublisher.model.roster.player.Position;
import iansteph.nhlp3.shiftpublisher.model.toi.PlayerTimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.TimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.player.Shift;
import iansteph.nhlp3.shiftpublisher.parse.TimeOnIceReportParser;
import iansteph.nhlp3.shiftpublisher.proxy.DynamoDbProxy;
import iansteph.nhlp3.shiftpublisher.proxy.NhlTimeOnIceProxy;
import iansteph.nhlp3.shiftpublisher.proxy.SnsProxy;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShiftPublisherHandlerTest {

    private final DynamoDbProxy mockDynamoDbProxy = mock(DynamoDbProxy.class);
    private final NhlTimeOnIceProxy mockNhlTimeOnIceProxy = mock(NhlTimeOnIceProxy.class);
    private final SnsProxy mockSnsProxy = mock(SnsProxy.class);
    private final TimeOnIceReportParser mockTimeOnIceReportParser = mock(TimeOnIceReportParser.class);

    private final ShiftPublisherHandler shiftPublisherHandler = new ShiftPublisherHandler(mockDynamoDbProxy, mockNhlTimeOnIceProxy, mockSnsProxy, mockTimeOnIceReportParser);

    @Test
    public void test_handleRequest_publishes_shift_events_when_there_are_new_shifts_in_the_time_on_ice_report() {

        final int gameId = 2019030273;

        // Mock DynamoDB
        final Map<String, Map<String, Integer>> shiftPublishingRecord = new HashMap<>();
        final Map<String, Integer> visitorShiftPublishingRecord = new HashMap<>();
        visitorShiftPublishingRecord.put("ERIK KARLSSON 65", 1);
        shiftPublishingRecord.put("visitor", visitorShiftPublishingRecord);
        final Map<String, Integer> homeShiftPublishingRecord = new HashMap<>();
        homeShiftPublishingRecord.put("AUSTON MATTHEWS 34", 1);
        shiftPublishingRecord.put("home", homeShiftPublishingRecord);
        when(mockDynamoDbProxy.getShiftPublishingRecordForGameId(anyInt())).thenReturn(shiftPublishingRecord);

        // Mock HTML Retrieval
        when(mockNhlTimeOnIceProxy.getToiReportForGame(anyInt(), any(Team.class))).thenReturn(new Document("SomeBaseUri"));

        // Mock HTML Parsing
        final TimeOnIceReport visitorTimeOnIceReport = new TimeOnIceReport();
        final PlayerTimeOnIceReport erikKarlsson65 = new PlayerTimeOnIceReport();
        erikKarlsson65.setFirstName("ERIK");
        erikKarlsson65.setLastName("KARLSSON");
        erikKarlsson65.setNumber(65);
        erikKarlsson65.setTeamName("San Jose Sharks");
        erikKarlsson65.setTeamId(28);
        final Player erikKarlssonPlayer = new Player();
        final Person erikKarlssonPerson = new Person();
        erikKarlssonPerson.setId(65);
        erikKarlssonPlayer.setPosition(new Position());
        erikKarlssonPlayer.setPerson(erikKarlssonPerson);
        erikKarlsson65.setPlayer(erikKarlssonPlayer);
        final Shift erikKarlssonShift1 = new Shift();
        erikKarlssonShift1.setShiftNumber(1);
        final Shift erikKarlssonShift2 = new Shift();
        erikKarlssonShift2.setShiftNumber(2);
        erikKarlsson65.setShifts(Arrays.asList(erikKarlssonShift1, erikKarlssonShift2));
        visitorTimeOnIceReport.setPlayerTimeOnIceReports(Collections.singletonList(erikKarlsson65));
        final TimeOnIceReport homeTimeOnIceReport = new TimeOnIceReport();
        final PlayerTimeOnIceReport austonMatthews34 = new PlayerTimeOnIceReport();
        austonMatthews34.setFirstName("AUSTON");
        austonMatthews34.setLastName("MATTHEWS");
        austonMatthews34.setNumber(34);
        austonMatthews34.setTeamName("Toronto Maple Leafs");
        austonMatthews34.setTeamId(10);
        final Player austonMatthewsPlayer = new Player();
        final Person austonMatthewsPerson = new Person();
        austonMatthewsPerson.setId(34);
        austonMatthewsPlayer.setPosition(new Position());
        austonMatthewsPlayer.setPerson(austonMatthewsPerson);
        austonMatthews34.setPlayer(austonMatthewsPlayer);
        final Shift austonMatthewsShift1 = new Shift();
        austonMatthewsShift1.setShiftNumber(1);
        final Shift austonMatthewsShift2 = new Shift();
        austonMatthewsShift2.setShiftNumber(2);
        austonMatthews34.setShifts(Arrays.asList(austonMatthewsShift1, austonMatthewsShift2));
        homeTimeOnIceReport.setPlayerTimeOnIceReports(Collections.singletonList(austonMatthews34));
        when(mockTimeOnIceReportParser.parse(any(Document.class))).thenReturn(Optional.of(visitorTimeOnIceReport)).thenReturn(Optional.of(homeTimeOnIceReport));

        // Mock ShiftPublisherRequest
        final ShiftPublisherRequest shiftPublisherRequest = new ShiftPublisherRequest();
        shiftPublisherRequest.setGameId(gameId);

        // Expected ShiftEvents
        final ShiftEvent erikkarlsson = new ShiftEvent();
        erikkarlsson.setPlayerFirstName("ERIK");
        erikkarlsson.setPlayerLastName("KARLSSON");
        erikkarlsson.setPlayerJerseyNumber(65);
        erikkarlsson.setPlayerTeamName("San Jose Sharks");
        erikkarlsson.setPlayerTeamId(28);
        erikkarlsson.setShift(erikKarlssonShift2);
        erikkarlsson.setPlayerPosition(new Position());
        erikkarlsson.setPlayerId(65);
        final ShiftEvent austonMatthews = new ShiftEvent();
        austonMatthews.setPlayerFirstName("AUSTON");
        austonMatthews.setPlayerLastName("MATTHEWS");
        austonMatthews.setPlayerJerseyNumber(34);
        austonMatthews.setPlayerTeamName("Toronto Maple Leafs");
        austonMatthews.setPlayerTeamId(10);
        austonMatthews.setShift(austonMatthewsShift2);
        austonMatthews.setPlayerId(34);
        austonMatthews.setPlayerPosition(new Position());

        shiftPublisherHandler.handleRequest(shiftPublisherRequest, null);

        verify(mockDynamoDbProxy, times(1)).getShiftPublishingRecordForGameId(anyInt());
        verify(mockDynamoDbProxy, times(1)).putShiftPublishingRecord(anyInt(), any(), any());
        verify(mockNhlTimeOnIceProxy, times(1)).getToiReportForGame(eq(gameId), eq(Team.VISITOR));
        verify(mockNhlTimeOnIceProxy, times(1)).getToiReportForGame(eq(gameId), eq(Team.HOME));
        verify(mockTimeOnIceReportParser, times(2)).parse(any(Document.class));
        verify(mockSnsProxy, times(1)).publishShiftEvents(eq(Collections.singletonList(erikkarlsson)));
        verify(mockSnsProxy, times(1)).publishShiftEvents(eq(Collections.singletonList(austonMatthews)));
    }

    @Test
    public void test_handleRequest_publishes_zero_shift_events_when_there_are_not_new_shifts_in_the_time_on_ice_report() {

        final int gameId = 2019030273;

        // Mock DynamoDB
        final Map<String, Map<String, Integer>> shiftPublishingRecord = new HashMap<>();
        final Map<String, Integer> visitorShiftPublishingRecord = new HashMap<>();
        visitorShiftPublishingRecord.put("ERIK KARLSSON 65", 1);
        shiftPublishingRecord.put("visitor", visitorShiftPublishingRecord);
        final Map<String, Integer> homeShiftPublishingRecord = new HashMap<>();
        homeShiftPublishingRecord.put("AUSTON MATTHEWS 34", 1);
        shiftPublishingRecord.put("home", homeShiftPublishingRecord);
        when(mockDynamoDbProxy.getShiftPublishingRecordForGameId(anyInt())).thenReturn(shiftPublishingRecord);

        // Mock HTML Retrieval
        when(mockNhlTimeOnIceProxy.getToiReportForGame(anyInt(), any(Team.class))).thenReturn(new Document("SomeBaseUri"));

        // Mock HTML Parsing
        final TimeOnIceReport visitorTimeOnIceReport = new TimeOnIceReport();
        final PlayerTimeOnIceReport erikKarlsson65 = new PlayerTimeOnIceReport();
        erikKarlsson65.setFirstName("ERIK");
        erikKarlsson65.setLastName("KARLSSON");
        erikKarlsson65.setNumber(65);
        erikKarlsson65.setTeamName("San Jose Sharks");
        final Shift erikKarlssonShift = new Shift();
        erikKarlssonShift.setShiftNumber(1);
        erikKarlsson65.setShifts(Collections.singletonList(erikKarlssonShift));
        visitorTimeOnIceReport.setPlayerTimeOnIceReports(Collections.singletonList(erikKarlsson65));
        final TimeOnIceReport homeTimeOnIceReport = new TimeOnIceReport();
        final PlayerTimeOnIceReport austonMatthews34 = new PlayerTimeOnIceReport();
        austonMatthews34.setFirstName("AUSTON");
        austonMatthews34.setLastName("MATTHEWS");
        austonMatthews34.setNumber(34);
        austonMatthews34.setTeamName("Toronto Maple Leafs");
        final Shift austonMatthewsShift = new Shift();
        austonMatthewsShift.setShiftNumber(1);
        austonMatthews34.setShifts(Collections.singletonList(austonMatthewsShift));
        homeTimeOnIceReport.setPlayerTimeOnIceReports(Collections.singletonList(austonMatthews34));
        when(mockTimeOnIceReportParser.parse(any(Document.class))).thenReturn(Optional.of(visitorTimeOnIceReport)).thenReturn(Optional.of(homeTimeOnIceReport));

        // Mock ShiftPublisherRequest
        final ShiftPublisherRequest shiftPublisherRequest = new ShiftPublisherRequest();
        shiftPublisherRequest.setGameId(gameId);

        shiftPublisherHandler.handleRequest(shiftPublisherRequest, null);

        verify(mockDynamoDbProxy, times(1)).getShiftPublishingRecordForGameId(anyInt());
        verify(mockDynamoDbProxy, times(1)).putShiftPublishingRecord(anyInt(), any(), any());
        verify(mockNhlTimeOnIceProxy, times(1)).getToiReportForGame(eq(gameId), eq(Team.VISITOR));
        verify(mockNhlTimeOnIceProxy, times(1)).getToiReportForGame(eq(gameId), eq(Team.HOME));
        verify(mockTimeOnIceReportParser, times(2)).parse(any(Document.class));
        final ArgumentCaptor<List<ShiftEvent>> shiftEventArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockSnsProxy, times(2)).publishShiftEvents(shiftEventArgumentCaptor.capture());
        final List<ShiftEvent> actualShiftEvents = shiftEventArgumentCaptor.getValue();
        assertThat(actualShiftEvents.size(), is(0));
    }

    @Test
    public void test_handleRequest_publishes_zero_shift_events_when_the_time_on_ice_report_has_no_shift_data() {

        final int gameId = 2019030273;

        // Mock DynamoDB
        final Map<String, Map<String, Integer>> shiftPublishingRecord = new HashMap<>();
        final Map<String, Integer> visitorShiftPublishingRecord = new HashMap<>();
        shiftPublishingRecord.put("visitor", visitorShiftPublishingRecord);
        final Map<String, Integer> homeShiftPublishingRecord = new HashMap<>();
        shiftPublishingRecord.put("home", homeShiftPublishingRecord);
        when(mockDynamoDbProxy.getShiftPublishingRecordForGameId(anyInt())).thenReturn(shiftPublishingRecord);

        // Mock HTML Retrieval
        when(mockNhlTimeOnIceProxy.getToiReportForGame(anyInt(), any(Team.class))).thenReturn(new Document("SomeBaseUri"));

        // Mock HTML Parsing
        when(mockTimeOnIceReportParser.parse(any(Document.class))).thenReturn(Optional.empty()).thenReturn(Optional.empty());

        // Mock ShiftPublisherRequest
        final ShiftPublisherRequest shiftPublisherRequest = new ShiftPublisherRequest();
        shiftPublisherRequest.setGameId(gameId);

        shiftPublisherHandler.handleRequest(shiftPublisherRequest, null);

        verify(mockDynamoDbProxy, times(1)).getShiftPublishingRecordForGameId(anyInt());
        verify(mockDynamoDbProxy, times(1)).putShiftPublishingRecord(anyInt(), any(), any());
        verify(mockNhlTimeOnIceProxy, times(1)).getToiReportForGame(eq(gameId), eq(Team.VISITOR));
        verify(mockNhlTimeOnIceProxy, times(1)).getToiReportForGame(eq(gameId), eq(Team.HOME));
        verify(mockTimeOnIceReportParser, times(2)).parse(any(Document.class));
        final ArgumentCaptor<List<ShiftEvent>> shiftEventArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockSnsProxy, times(2)).publishShiftEvents(shiftEventArgumentCaptor.capture());
        final List<ShiftEvent> actualShiftEvents = shiftEventArgumentCaptor.getValue();
        assertThat(actualShiftEvents.size(), is(0));
    }

    @Test
    public void test_handleRequest_publishes_shift_events_when_there_is_a_new_player_with_shift_data_in_the_time_on_ice_report() {

        final int gameId = 2019030273;

        // Mock DynamoDB
        final Map<String, Map<String, Integer>> shiftPublishingRecord = new HashMap<>();
        shiftPublishingRecord.put("visitor", Collections.emptyMap());
        shiftPublishingRecord.put("home", Collections.emptyMap());
        when(mockDynamoDbProxy.getShiftPublishingRecordForGameId(anyInt())).thenReturn(shiftPublishingRecord);

        // Mock HTML Retrieval
        when(mockNhlTimeOnIceProxy.getToiReportForGame(anyInt(), any(Team.class))).thenReturn(new Document("SomeBaseUri"));

        // Mock HTML Parsing
        final TimeOnIceReport visitorTimeOnIceReport = new TimeOnIceReport();
        final PlayerTimeOnIceReport erikKarlsson65 = new PlayerTimeOnIceReport();
        erikKarlsson65.setFirstName("ERIK");
        erikKarlsson65.setLastName("KARLSSON");
        erikKarlsson65.setNumber(65);
        erikKarlsson65.setTeamName("San Jose Sharks");
        erikKarlsson65.setTeamId(28);
        final Shift erikKarlssonShift1 = new Shift();
        erikKarlssonShift1.setShiftNumber(1);
        final Shift erikKarlssonShift2 = new Shift();
        erikKarlssonShift2.setShiftNumber(2);
        erikKarlsson65.setShifts(Arrays.asList(erikKarlssonShift1, erikKarlssonShift2));
        visitorTimeOnIceReport.setPlayerTimeOnIceReports(Collections.singletonList(erikKarlsson65));
        final TimeOnIceReport homeTimeOnIceReport = new TimeOnIceReport();
        final PlayerTimeOnIceReport austonMatthews34 = new PlayerTimeOnIceReport();
        austonMatthews34.setFirstName("AUSTON");
        austonMatthews34.setLastName("MATTHEWS");
        austonMatthews34.setNumber(34);
        austonMatthews34.setTeamName("Toronto Maple Leafs");
        austonMatthews34.setTeamId(10);
        final Shift austonMatthewsShift1 = new Shift();
        austonMatthewsShift1.setShiftNumber(1);
        final Shift austonMatthewsShift2 = new Shift();
        austonMatthewsShift2.setShiftNumber(2);
        austonMatthews34.setShifts(Arrays.asList(austonMatthewsShift1, austonMatthewsShift2));
        homeTimeOnIceReport.setPlayerTimeOnIceReports(Collections.singletonList(austonMatthews34));
        when(mockTimeOnIceReportParser.parse(any(Document.class))).thenReturn(Optional.of(visitorTimeOnIceReport)).thenReturn(Optional.of(homeTimeOnIceReport));

        // Mock ShiftPublisherRequest
        final ShiftPublisherRequest shiftPublisherRequest = new ShiftPublisherRequest();
        shiftPublisherRequest.setGameId(gameId);

        // Expected ShiftEvents
        final String erik = "ERIK";
        final String karlsson = "KARLSSON";
        final int sixtyFive = 65;
        final String sanJoseSharks = "San Jose Sharks";
        final ShiftEvent erikKarlssonShiftEvent1 = new ShiftEvent();
        erikKarlssonShiftEvent1.setPlayerFirstName(erik);
        erikKarlssonShiftEvent1.setPlayerLastName(karlsson);
        erikKarlssonShiftEvent1.setPlayerJerseyNumber(sixtyFive);
        erikKarlssonShiftEvent1.setPlayerTeamName(sanJoseSharks);
        erikKarlssonShiftEvent1.setPlayerTeamId(28);
        erikKarlssonShiftEvent1.setShift(erikKarlssonShift1);
        final ShiftEvent erikKarlssonShiftEvent2 = new ShiftEvent();
        erikKarlssonShiftEvent2.setPlayerFirstName(erik);
        erikKarlssonShiftEvent2.setPlayerLastName(karlsson);
        erikKarlssonShiftEvent2.setPlayerJerseyNumber(sixtyFive);
        erikKarlssonShiftEvent2.setPlayerTeamName(sanJoseSharks);
        erikKarlssonShiftEvent2.setPlayerTeamId(28);
        erikKarlssonShiftEvent2.setShift(erikKarlssonShift2);
        final String auston = "AUSTON";
        final String matthews = "MATTHEWS";
        final int thirtyFour = 34;
        final String torontoMapleLeafs = "Toronto Maple Leafs";
        final ShiftEvent austonMatthewsShiftEvent1 = new ShiftEvent();
        austonMatthewsShiftEvent1.setPlayerFirstName(auston);
        austonMatthewsShiftEvent1.setPlayerLastName(matthews);
        austonMatthewsShiftEvent1.setPlayerJerseyNumber(thirtyFour);
        austonMatthewsShiftEvent1.setPlayerTeamName(torontoMapleLeafs);
        austonMatthewsShiftEvent1.setPlayerTeamId(10);
        austonMatthewsShiftEvent1.setShift(austonMatthewsShift1);
        final ShiftEvent austonMatthewsShiftEvent2 = new ShiftEvent();
        austonMatthewsShiftEvent2.setPlayerFirstName(auston);
        austonMatthewsShiftEvent2.setPlayerLastName(matthews);
        austonMatthewsShiftEvent2.setPlayerJerseyNumber(thirtyFour);
        austonMatthewsShiftEvent2.setPlayerTeamName(torontoMapleLeafs);
        austonMatthewsShiftEvent2.setPlayerTeamId(10);
        austonMatthewsShiftEvent2.setShift(austonMatthewsShift2);

        shiftPublisherHandler.handleRequest(shiftPublisherRequest, null);

        verify(mockDynamoDbProxy, times(1)).getShiftPublishingRecordForGameId(anyInt());
        verify(mockDynamoDbProxy, times(1)).putShiftPublishingRecord(anyInt(), any(), any());
        verify(mockNhlTimeOnIceProxy, times(1)).getToiReportForGame(eq(gameId), eq(Team.VISITOR));
        verify(mockNhlTimeOnIceProxy, times(1)).getToiReportForGame(eq(gameId), eq(Team.HOME));
        verify(mockTimeOnIceReportParser, times(2)).parse(any(Document.class));
        verify(mockSnsProxy, times(1)).publishShiftEvents(eq(Arrays.asList(erikKarlssonShiftEvent1, erikKarlssonShiftEvent2)));
        verify(mockSnsProxy, times(1)).publishShiftEvents(eq(Arrays.asList(austonMatthewsShiftEvent1, austonMatthewsShiftEvent2)));
    }
}
