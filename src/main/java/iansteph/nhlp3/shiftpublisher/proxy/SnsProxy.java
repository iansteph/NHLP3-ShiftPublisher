package iansteph.nhlp3.shiftpublisher.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.shiftpublisher.model.event.ShiftEvent;
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

            return objectMapper.writeValueAsString(shiftEvent);
        }
        catch (final JsonProcessingException e) {

            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }
}
