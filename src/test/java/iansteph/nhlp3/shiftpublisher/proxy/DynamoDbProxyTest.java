package iansteph.nhlp3.shiftpublisher.proxy;

import iansteph.nhlp3.shiftpublisher.model.toi.PlayerTimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.TimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.player.Shift;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DynamoDbProxyTest {

    private static final String TABLE_NAME = "NHLP3-Aggregate";
    private static final String TABLE_PARTITION_KEY_ATTRIBUTE_NAME = "PK";
    private static final String TABLE_SORT_KEY_ATTRIBUTE_NAME = "SK";
    private static final String TABLE_SHIFT_PUBLISHING_RECORD_ATTRIBUTE_NAME = "shiftPublishingRecord";

    private final DynamoDbClient mockDynamoDbClient = mock(DynamoDbClient.class);
    private final DynamoDbProxy dynamoDbProxy = new DynamoDbProxy(mockDynamoDbClient);

    @Test
    public void test_getShiftPublishingRecordForGameId_successfully_retrieves_processing_record_from_DynamoDB() {

        final Map<String, AttributeValue> visitorPlayerMap = new HashMap<>();
        visitorPlayerMap.put("TIMO MEIER 28", AttributeValue.builder().n("1").build());
        visitorPlayerMap.put("ERIK KARLSSON 65", AttributeValue.builder().n("10").build());
        final Map<String, AttributeValue> homePlayerMap = new HashMap<>();
        homePlayerMap.put("AUSTON MATTHEWS 34", AttributeValue.builder().n("4").build());
        final Map<String, AttributeValue> teamMap = new HashMap<>();
        teamMap.put("visitor", AttributeValue.builder().m(visitorPlayerMap).build());
        teamMap.put("home", AttributeValue.builder().m(homePlayerMap).build());
        final Map<String, AttributeValue> item = new HashMap<>();
        item.put("shiftPublishingRecord", AttributeValue.builder().m(teamMap).build());
        final GetItemResponse getItemResponse = GetItemResponse.builder().item(item).build();
        when(mockDynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(getItemResponse);
        final int gameId = 2019030273;

        final Map<String, Map<String, Integer>> result = dynamoDbProxy.getShiftPublishingRecordForGameId(gameId);

        verify(mockDynamoDbClient, times(1)).getItem(any(GetItemRequest.class));
        assertThat(result.size(), is(2));
        final Map<String, Integer> visitor = result.get("visitor");
        assertThat(visitor.size(), is(2));
        assertThat(visitor.get("TIMO MEIER 28"), is(1));
        assertThat(visitor.get("ERIK KARLSSON 65"), is(10));
        final Map<String, Integer> home = result.get("home");
        assertThat(home.size(), is(1));
        assertThat(home.get("AUSTON MATTHEWS 34"), is(4));
    }

    @Test(expected = RuntimeException.class)
    public void test_getShiftPublishingRecordGameId_throws_exception_when_DynamoDB_throws_an_exception() {

        when(mockDynamoDbClient.getItem(any(GetItemRequest.class))).thenThrow(ResourceNotFoundException.builder().build());
        final int gameId = 2019030273;

        dynamoDbProxy.getShiftPublishingRecordForGameId(gameId);
    }

    @Test
    public void test_putShiftPublishingRecord_successfully_stores_shift_publishing_record_in_DynamoDB() {

        final TimeOnIceReport visitorTimeOnIceReport = new TimeOnIceReport();
        final List<PlayerTimeOnIceReport> visitorPlayerTimeOnIceReports = new ArrayList<>();
        final PlayerTimeOnIceReport visitorPlayerTimeOnIceReport = new PlayerTimeOnIceReport();
        visitorPlayerTimeOnIceReport.setFirstName("ERIK");
        visitorPlayerTimeOnIceReport.setLastName("KARLSSON");
        visitorPlayerTimeOnIceReport.setNumber(65);
        final List<Shift> visitorPlayershifts = new ArrayList<>();
        visitorPlayershifts.add(new Shift());
        visitorPlayerTimeOnIceReport.setShifts(visitorPlayershifts);
        visitorPlayerTimeOnIceReports.add(visitorPlayerTimeOnIceReport);
        visitorTimeOnIceReport.setPlayerTimeOnIceReports(visitorPlayerTimeOnIceReports);
        final TimeOnIceReport homeTimeOnIceReport = new TimeOnIceReport();
        final List<PlayerTimeOnIceReport> homePlayerTimeOnIceReports = new ArrayList<>();
        final PlayerTimeOnIceReport homePlayerTimeOnIceReport = new PlayerTimeOnIceReport();
        homePlayerTimeOnIceReport.setFirstName("TIMO");
        homePlayerTimeOnIceReport.setLastName("MEIER");
        homePlayerTimeOnIceReport.setNumber(28);
        final List<Shift> homePlayerShifts = new ArrayList<>();
        homePlayerShifts.add(new Shift());
        homePlayerShifts.add(new Shift());
        homePlayerTimeOnIceReport.setShifts(homePlayerShifts);
        homePlayerTimeOnIceReports.add(homePlayerTimeOnIceReport);
        homeTimeOnIceReport.setPlayerTimeOnIceReports(homePlayerTimeOnIceReports);
        when(mockDynamoDbClient.putItem(any(PutItemRequest.class))).thenReturn(PutItemResponse.builder().build());
        final int gameId = 2019030273;
        final ArgumentCaptor<PutItemRequest> putItemRequestArgumentCaptor = ArgumentCaptor.forClass(PutItemRequest.class);

        dynamoDbProxy.putShiftPublishingRecord(gameId, visitorTimeOnIceReport, homeTimeOnIceReport);
        verify(mockDynamoDbClient, times(1)).putItem(putItemRequestArgumentCaptor.capture());
        final PutItemRequest putItemRequest = putItemRequestArgumentCaptor.getValue();
        assertThat(putItemRequest.tableName(), is(TABLE_NAME));
        final Map<String, AttributeValue> item = putItemRequest.item();
        assertThat(item.size(), is(3));
        final String key = format("SHIFTPUBLISHING#%d", gameId);
        assertThat(item.get(TABLE_PARTITION_KEY_ATTRIBUTE_NAME).s(), is(key));
        assertThat(item.get(TABLE_SORT_KEY_ATTRIBUTE_NAME).s(), is(key));
        final AttributeValue shiftPublishingRecordAttribute = item.get(TABLE_SHIFT_PUBLISHING_RECORD_ATTRIBUTE_NAME);
        final Map<String, AttributeValue> teams = shiftPublishingRecordAttribute.m();
        assertThat(teams.size(), is(2));
        final Map<String, AttributeValue> visitor = teams.get("visitor").m();
        assertThat(visitor.size(), is(1));
        final String visitorPlayer = visitor.get("ERIK KARLSSON 65").n();
        assertThat(visitorPlayer, is("1"));
        final Map<String, AttributeValue> home = teams.get("home").m();
        assertThat(home.size(), is(1));
        final String homePlayer = home.get("TIMO MEIER 28").n();
        assertThat(homePlayer, is("2"));
    }

    @Test(expected = RuntimeException.class)
    public void test_putShiftPublishingRecord_throws_exception_when_DynamoDB_throws_an_exception() {

        when(mockDynamoDbClient.putItem(any(PutItemRequest.class))).thenThrow(ResourceNotFoundException.builder().build());
        final int gameId = 2019030273;

        dynamoDbProxy.putShiftPublishingRecord(gameId, new TimeOnIceReport(), new TimeOnIceReport());
    }
}
