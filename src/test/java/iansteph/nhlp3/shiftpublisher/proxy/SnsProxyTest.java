package iansteph.nhlp3.shiftpublisher.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.shiftpublisher.model.event.ShiftEvent;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
        final ShiftEvent shiftEvent2 = new ShiftEvent();
        shiftEvent2.setPlayerTeamId(2);
        final ShiftEvent shiftEvent3 = new ShiftEvent();
        shiftEvent3.setPlayerTeamId(3);
        final List<ShiftEvent> shiftEvents = Arrays.asList(shiftEvent1, shiftEvent2, shiftEvent3);

        snsProxy.publishShiftEvents(shiftEvents);

        verify(mockObjectMapper, times(3)).writeValueAsString(any(ShiftEvent.class));
        final ArgumentCaptor<PublishRequest> publishRequestArgumentCaptor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(mockSnsClient, times(3)).publish(publishRequestArgumentCaptor.capture());
        final List<PublishRequest> publishRequests = publishRequestArgumentCaptor.getAllValues();
        final PublishRequest publishRequest1 = publishRequests.get(0);
        assertThat(publishRequest1.messageAttributes().size(), is(1));
        assertThat(publishRequest1.messageAttributes().get("teamId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("1").build()));
        final PublishRequest publishRequest2 = publishRequests.get(1);
        assertThat(publishRequest2.messageAttributes().size(), is(1));
        assertThat(publishRequest2.messageAttributes().get("teamId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("2").build()));
        final PublishRequest publishRequest3 = publishRequests.get(2);
        assertThat(publishRequest3.messageAttributes().size(), is(1));
        assertThat(publishRequest3.messageAttributes().get("teamId"), is(MessageAttributeValue.builder().dataType("Number").stringValue("3").build()));
    }

    @Test(expected = RuntimeException.class)
    public void test_publishShiftEvents_throws_exception_when_serialization_fails() throws JsonProcessingException {

        when(mockObjectMapper.writeValueAsString(any(ShiftEvent.class))).thenThrow(new IOException());
        final List<ShiftEvent> shiftEvents = Collections.singletonList(new ShiftEvent());

        snsProxy.publishShiftEvents(shiftEvents);

        verify(mockObjectMapper, times(1)).writeValueAsString(any(ShiftEvent.class));
    }
}
