package iansteph.nhlp3.shiftpublisher.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.shiftpublisher.model.event.ShiftEvent;
import iansteph.nhlp3.shiftpublisher.model.roster.player.Position;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SnsProxyTest {

    private final ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
    private final SnsClient mockSnsClient = mock(SnsClient.class);
    private final SnsProxy snsProxy = new SnsProxy(mockObjectMapper, mockSnsClient);

    @Test
    public void test_publishShiftEvents_successfully_publishes_list_of_shift_events() throws JsonProcessingException {

        when(mockObjectMapper.writeValueAsString(any(ShiftEvent.class))).thenReturn("SomeShiftEvent");
        when(mockSnsClient.publish(any(PublishRequest.class))).thenReturn(PublishResponse.builder().build());
        final ShiftEvent shiftEvent1 = new ShiftEvent();
        shiftEvent1.setPlayerTeamId(1);
        shiftEvent1.setPlayerId(1);
        final Position position1 = new Position();
        position1.setType("Forward");
        position1.setCode("C");
        shiftEvent1.setPlayerPosition(position1);
        final ShiftEvent shiftEvent2 = new ShiftEvent();
        shiftEvent2.setPlayerTeamId(2);
        shiftEvent2.setPlayerId(2);
        final Position position2 = new Position();
        position2.setType("Forward");
        position2.setCode("RW");
        shiftEvent2.setPlayerPosition(position2);
        final ShiftEvent shiftEvent3 = new ShiftEvent();
        shiftEvent3.setPlayerTeamId(3);
        shiftEvent3.setPlayerId(3);
        final Position position3 = new Position();
        position3.setType("Defenseman");
        position3.setCode("D");
        shiftEvent3.setPlayerPosition(position3);
        final List<ShiftEvent> shiftEvents = Arrays.asList(shiftEvent1, shiftEvent2, shiftEvent3);

        snsProxy.publishShiftEvents(shiftEvents);

        verify(mockObjectMapper, times(3)).writeValueAsString(any(ShiftEvent.class));
        final ArgumentCaptor<PublishRequest> publishRequestArgumentCaptor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(mockSnsClient, times(3)).publish(publishRequestArgumentCaptor.capture());
        final List<PublishRequest> publishRequests = publishRequestArgumentCaptor.getAllValues();
        final PublishRequest publishRequest1 = publishRequests.get(0);
        assertThat(publishRequest1.messageAttributes().size(), is(4));
        assertThat(publishRequest1.messageAttributes().get("teamId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("1").build()));
        assertThat(publishRequest1.messageAttributes().get("playerId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("1").build()));
        assertThat(publishRequest1.messageAttributes().get("positionType"), is(MessageAttributeValue.builder().dataType("String").stringValue("Forward").build()));
        assertThat(publishRequest1.messageAttributes().get("positionCode"), is(MessageAttributeValue.builder().dataType("String").stringValue("C").build()));
        final PublishRequest publishRequest2 = publishRequests.get(1);
        assertThat(publishRequest2.messageAttributes().size(), is(4));
        assertThat(publishRequest2.messageAttributes().get("teamId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("2").build()));
        assertThat(publishRequest2.messageAttributes().get("playerId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("2").build()));
        assertThat(publishRequest2.messageAttributes().get("positionType"), is(MessageAttributeValue.builder().dataType("String").stringValue("Forward").build()));
        assertThat(publishRequest2.messageAttributes().get("positionCode"), is(MessageAttributeValue.builder().dataType("String").stringValue("RW").build()));
        final PublishRequest publishRequest3 = publishRequests.get(2);
        assertThat(publishRequest3.messageAttributes().size(), is(4));
        assertThat(publishRequest3.messageAttributes().get("teamId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("3").build()));
        assertThat(publishRequest3.messageAttributes().get("playerId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("3").build()));
        assertThat(publishRequest3.messageAttributes().get("positionType"), is(MessageAttributeValue.builder().dataType("String").stringValue("Defenseman").build()));
        assertThat(publishRequest3.messageAttributes().get("positionCode"), is(MessageAttributeValue.builder().dataType("String").stringValue("D").build()));
    }

    @Test
    public void test_publishShiftEvents_does_not_add_player_id_sns_message_attribute_if_player_id_is_null() throws JsonProcessingException {

        when(mockObjectMapper.writeValueAsString(any(ShiftEvent.class))).thenReturn("SomeShiftEvent");
        when(mockSnsClient.publish(any(PublishRequest.class))).thenReturn(PublishResponse.builder().build());
        final ShiftEvent shiftEvent = new ShiftEvent();
        shiftEvent.setPlayerTeamId(1);
        shiftEvent.setPlayerId(null);
        final Position position = new Position();
        position.setType("Forward");
        position.setCode("C");
        shiftEvent.setPlayerPosition(position);
        final List<ShiftEvent> shiftEvents = Collections.singletonList(shiftEvent);

        snsProxy.publishShiftEvents(shiftEvents);

        verify(mockObjectMapper, times(1)).writeValueAsString(any(ShiftEvent.class));
        final ArgumentCaptor<PublishRequest> publishRequestArgumentCaptor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(mockSnsClient, times(1)).publish(publishRequestArgumentCaptor.capture());
        final List<PublishRequest> publishRequests = publishRequestArgumentCaptor.getAllValues();
        final PublishRequest publishRequest = publishRequests.get(0);
        assertThat(publishRequest.messageAttributes().size(), is(3));
        assertThat(publishRequest.messageAttributes().get("teamId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("1").build()));
        assertFalse(publishRequest.messageAttributes().containsKey("playerId"));
        assertThat(publishRequest.messageAttributes().get("positionType"), is(MessageAttributeValue.builder().dataType("String").stringValue("Forward").build()));
        assertThat(publishRequest.messageAttributes().get("positionCode"), is(MessageAttributeValue.builder().dataType("String").stringValue("C").build()));
    }

    @Test
    public void test_publishShiftEvents_does_not_add_any_position_sns_message_attributes_if_position_is_null() throws JsonProcessingException {

        when(mockObjectMapper.writeValueAsString(any(ShiftEvent.class))).thenReturn("SomeShiftEvent");
        when(mockSnsClient.publish(any(PublishRequest.class))).thenReturn(PublishResponse.builder().build());
        final ShiftEvent shiftEvent = new ShiftEvent();
        shiftEvent.setPlayerTeamId(1);
        shiftEvent.setPlayerId(1);
        shiftEvent.setPlayerPosition(null);
        final List<ShiftEvent> shiftEvents = Collections.singletonList(shiftEvent);

        snsProxy.publishShiftEvents(shiftEvents);

        verify(mockObjectMapper, times(1)).writeValueAsString(any(ShiftEvent.class));
        final ArgumentCaptor<PublishRequest> publishRequestArgumentCaptor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(mockSnsClient, times(1)).publish(publishRequestArgumentCaptor.capture());
        final List<PublishRequest> publishRequests = publishRequestArgumentCaptor.getAllValues();
        final PublishRequest publishRequest = publishRequests.get(0);
        assertThat(publishRequest.messageAttributes().size(), is(2));
        assertThat(publishRequest.messageAttributes().get("teamId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("1").build()));
        assertThat(publishRequest.messageAttributes().get("playerId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("1").build()));
        assertFalse(publishRequest.messageAttributes().containsKey("positionType"));
        assertFalse(publishRequest.messageAttributes().containsKey("positionCode"));
    }

    @Test
    public void test_publishShiftEvents_does_not_add_position_type_sns_message_attribute_if_player_type_is_null() throws JsonProcessingException {

        when(mockObjectMapper.writeValueAsString(any(ShiftEvent.class))).thenReturn("SomeShiftEvent");
        when(mockSnsClient.publish(any(PublishRequest.class))).thenReturn(PublishResponse.builder().build());
        final ShiftEvent shiftEvent = new ShiftEvent();
        shiftEvent.setPlayerTeamId(1);
        shiftEvent.setPlayerId(1);
        final Position position = new Position();
        position.setCode("C");
        shiftEvent.setPlayerPosition(position);
        final List<ShiftEvent> shiftEvents = Collections.singletonList(shiftEvent);

        snsProxy.publishShiftEvents(shiftEvents);

        verify(mockObjectMapper, times(1)).writeValueAsString(any(ShiftEvent.class));
        final ArgumentCaptor<PublishRequest> publishRequestArgumentCaptor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(mockSnsClient, times(1)).publish(publishRequestArgumentCaptor.capture());
        final List<PublishRequest> publishRequests = publishRequestArgumentCaptor.getAllValues();
        final PublishRequest publishRequest = publishRequests.get(0);
        assertThat(publishRequest.messageAttributes().size(), is(3));
        assertThat(publishRequest.messageAttributes().get("teamId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("1").build()));
        assertThat(publishRequest.messageAttributes().get("playerId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("1").build()));
        assertFalse(publishRequest.messageAttributes().containsKey("positionType"));
        assertThat(publishRequest.messageAttributes().get("positionCode"), is(MessageAttributeValue.builder().dataType("String").stringValue("C").build()));
    }

    @Test
    public void test_publishShiftEvents_does_not_add_position_code_sns_message_attribute_if_player_code_is_null() throws JsonProcessingException {

        when(mockObjectMapper.writeValueAsString(any(ShiftEvent.class))).thenReturn("SomeShiftEvent");
        when(mockSnsClient.publish(any(PublishRequest.class))).thenReturn(PublishResponse.builder().build());
        final ShiftEvent shiftEvent = new ShiftEvent();
        shiftEvent.setPlayerTeamId(1);
        shiftEvent.setPlayerId(1);
        final Position position = new Position();
        position.setType("Forward");
        shiftEvent.setPlayerPosition(position);
        final List<ShiftEvent> shiftEvents = Collections.singletonList(shiftEvent);

        snsProxy.publishShiftEvents(shiftEvents);

        verify(mockObjectMapper, times(1)).writeValueAsString(any(ShiftEvent.class));
        final ArgumentCaptor<PublishRequest> publishRequestArgumentCaptor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(mockSnsClient, times(1)).publish(publishRequestArgumentCaptor.capture());
        final List<PublishRequest> publishRequests = publishRequestArgumentCaptor.getAllValues();
        final PublishRequest publishRequest = publishRequests.get(0);
        assertThat(publishRequest.messageAttributes().size(), is(3));
        assertThat(publishRequest.messageAttributes().get("teamId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("1").build()));
        assertThat(publishRequest.messageAttributes().get("playerId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("1").build()));
        assertThat(publishRequest.messageAttributes().get("positionType"), is(MessageAttributeValue.builder().dataType("String").stringValue("Forward").build()));
        assertFalse(publishRequest.messageAttributes().containsKey("positionCode"));
    }

    @Test(expected = RuntimeException.class)
    public void test_publishShiftEvents_throws_exception_when_serialization_fails() throws JsonProcessingException {

        when(mockObjectMapper.writeValueAsString(any(ShiftEvent.class))).thenThrow(new IOException());
        final List<ShiftEvent> shiftEvents = Collections.singletonList(new ShiftEvent());

        snsProxy.publishShiftEvents(shiftEvents);

        verify(mockObjectMapper, times(0)).writeValueAsString(any(ShiftEvent.class));
        verify(mockSnsClient, never()).publish(any(PublishRequest.class));
    }
}
