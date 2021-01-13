package iansteph.nhlp3.shiftpublisher.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.shiftpublisher.model.event.ShiftEvent;
import iansteph.nhlp3.shiftpublisher.model.roster.player.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnsProxy {

    private final ObjectMapper objectMapper;
    private final SnsClient snsClient;

    private static final Logger LOGGER = LogManager.getLogger(SnsProxy.class);
    private static final String TOPIC_ARN = "arn:aws:sns:us-east-1:627812672245:NHLP3-Shift-Events";


    public SnsProxy(final ObjectMapper objectMapper, final SnsClient snsClient) {

        this.objectMapper = objectMapper;
        this.snsClient = snsClient;
    }

    public void publishShiftEvents(final List<ShiftEvent> shiftEventsToPublish) {

        shiftEventsToPublish.forEach(
                shiftEvent -> {

                        final String serializedShift = convertShiftEventToString(shiftEvent);
                        final Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
                        final MessageAttributeValue teamIdMessageAttributeValue = MessageAttributeValue.builder()
                                .dataType("Number")
                                .stringValue(String.valueOf(shiftEvent.getPlayerTeamId()))
                                .build();
                        final Integer playerId = shiftEvent.getPlayerId();
                        if (playerId != null) {

                            final MessageAttributeValue playerIdMessageAttributeValue = MessageAttributeValue.builder()
                                    .dataType("Number")
                                    .stringValue(String.valueOf(playerId))
                                    .build();
                            messageAttributes.put("playerId", playerIdMessageAttributeValue);
                        }
                        final Position position = shiftEvent.getPlayerPosition();
                        if (position != null) {

                            final String positionType = position.getType();
                            if (positionType != null) {

                                final MessageAttributeValue positionTypeMessageAttributeValue = MessageAttributeValue.builder()
                                        .dataType("String")
                                        .stringValue(position.getType())
                                        .build();
                                messageAttributes.put("positionType", positionTypeMessageAttributeValue);
                            }
                            final String positionCode = position.getCode();
                            if (positionCode != null) {

                                final MessageAttributeValue positionCodeMessageAttributeValue = MessageAttributeValue.builder()
                                        .dataType("String")
                                        .stringValue(position.getCode())
                                        .build();
                                messageAttributes.put("positionCode", positionCodeMessageAttributeValue);
                            }
                        }
                        messageAttributes.put("teamId", teamIdMessageAttributeValue);
                        final PublishRequest publishRequest = PublishRequest.builder()
                                .topicArn(TOPIC_ARN)
                                .message(serializedShift)
                                .messageAttributes(messageAttributes)
                                .build();
                        snsClient.publish(publishRequest);
                    }
                );
    }

    private String convertShiftEventToString(final ShiftEvent shiftEvent) {

        try {

            final String serializedShiftEvent = objectMapper.writeValueAsString(shiftEvent);
            return serializedShiftEvent;
        }
        catch (final JsonProcessingException e) {

            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }
}
