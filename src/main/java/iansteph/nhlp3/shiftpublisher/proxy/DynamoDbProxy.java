package iansteph.nhlp3.shiftpublisher.proxy;

import iansteph.nhlp3.shiftpublisher.model.toi.PlayerTimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.model.toi.TimeOnIceReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class DynamoDbProxy {

    private static final String TABLE_NAME = "NHLP3-Aggregate";
    private static final String TABLE_PARTITION_KEY_ATTRIBUTE_NAME = "PK";
    private static final String TABLE_SORT_KEY_ATTRIBUTE_NAME = "SK";
    private static final String TABLE_SHIFT_PUBLISHING_RECORD_ATTRIBUTE_NAME = "shiftPublishingRecord";
    private static final Logger LOGGER = LogManager.getLogger(DynamoDbProxy.class);

    private final DynamoDbClient dynamoDbClient;

    public DynamoDbProxy(final DynamoDbClient dynamoDbClient) {

        this.dynamoDbClient = dynamoDbClient;
    }

    public Map<String, Map<String, Integer>> getShiftPublishingRecordForGameId(final int gameId) {

        try {

            final Map<String, AttributeValue> primaryKey = new HashMap<>();
            final String key = format("SHIFTPUBLISHING#%d", gameId);
            primaryKey.put(TABLE_PARTITION_KEY_ATTRIBUTE_NAME, AttributeValue.builder().s(key).build());
            primaryKey.put(TABLE_SORT_KEY_ATTRIBUTE_NAME, AttributeValue.builder().s(key).build());
            final GetItemRequest getItemRequest = GetItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(primaryKey)
                    .build();
            LOGGER.info(format("Retrieving shift publishing record from DynamoDB for partitionKey: %s and sortKey: %s on table %s", key, key, TABLE_NAME));
            final GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
            LOGGER.info(format("Retrieved shift publishing record: %s", getItemResponse));
            final Map<String, Map<String, Integer>> transformedResponse = transformDynamoDbResponse(getItemResponse);
            return transformedResponse;
        }
        catch (final Exception e) {

            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    public void putShiftPublishingRecord(
            final int gameId,
            final Optional<TimeOnIceReport> optionalVisitorTimeOnIceReport,
            final Optional<TimeOnIceReport> optionalHomeTimeOnIceReport
    ) {
        try {

            final String key = format("SHIFTPUBLISHING#%d", gameId);
            final Map<String, AttributeValue> item = transformTimeOnIceReportsToDynamoDbAttribute(optionalVisitorTimeOnIceReport, optionalHomeTimeOnIceReport);
            item.put(TABLE_PARTITION_KEY_ATTRIBUTE_NAME, AttributeValue.builder().s(key).build());
            item.put(TABLE_SORT_KEY_ATTRIBUTE_NAME, AttributeValue.builder().s(key).build());
            final PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .item(item)
                    .build();
            LOGGER.info(format("Putting shift publishing record into DynamoDB for partitionKey: %s and sortKey: %s on table %s", key, key, TABLE_NAME));
            dynamoDbClient.putItem(putItemRequest);
        }
        catch (final Exception e) {

            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    private Map<String, Map<String, Integer>> transformDynamoDbResponse(final GetItemResponse getItemResponse) {

        final Map<String, AttributeValue> item = getItemResponse.item();
        final Map<String, Map<String, Integer>> transformedResponse = item.get(TABLE_SHIFT_PUBLISHING_RECORD_ATTRIBUTE_NAME).m().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        teamMap -> teamMap.getValue().m().entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        playerMap -> Integer.parseInt(playerMap.getValue().n())
                                ))
                ));
        return transformedResponse;
    }

    private Map<String, AttributeValue> transformTimeOnIceReportsToDynamoDbAttribute(
            final Optional<TimeOnIceReport> visitorTimeOnIceReport,
            final Optional<TimeOnIceReport> homeTimeOnIceReport
    ) {
        final Map<String, AttributeValue> visitorMap = buildPlayerShiftPublishingRecordMap(visitorTimeOnIceReport);
        final Map<String, AttributeValue> homeMap = buildPlayerShiftPublishingRecordMap(homeTimeOnIceReport);
        final Map<String, AttributeValue> teams = new HashMap<>();
        teams.put("visitor", AttributeValue.builder().m(visitorMap).build());
        teams.put("home", AttributeValue.builder().m(homeMap).build());
        final Map<String, AttributeValue> shiftPublishingRecordAttribute = new HashMap<>();
        shiftPublishingRecordAttribute.put(TABLE_SHIFT_PUBLISHING_RECORD_ATTRIBUTE_NAME, AttributeValue.builder().m(teams).build());
        return shiftPublishingRecordAttribute;
    }

    private Map<String, AttributeValue> buildPlayerShiftPublishingRecordMap(final Optional<TimeOnIceReport> optionalTimeOnIceReport) {

        final Optional<List<PlayerTimeOnIceReport>> optionalPlayerTimeOnIceReports = optionalTimeOnIceReport.map(TimeOnIceReport::getPlayerTimeOnIceReports);
        final List<PlayerTimeOnIceReport> playerTimeOnIceReports = optionalPlayerTimeOnIceReports.orElse(Collections.emptyList());
        final Map<String, AttributeValue> map = playerTimeOnIceReports.stream()
                .collect(Collectors.toMap(
                        playerTimeOnIceReport -> format("%s %s %d", playerTimeOnIceReport.getFirstName(), playerTimeOnIceReport.getLastName(), playerTimeOnIceReport.getNumber()),
                        playerTimeOnIceReport -> AttributeValue.builder().n(String.valueOf(playerTimeOnIceReport.getShifts().size())).build()
                ));
        return map;
    }
}
