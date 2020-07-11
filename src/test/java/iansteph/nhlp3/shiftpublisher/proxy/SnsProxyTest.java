package iansteph.nhlp3.shiftpublisher.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.shiftpublisher.model.event.ShiftEvent;
import org.junit.Test;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        final List<ShiftEvent> shiftEvents = Arrays.asList(new ShiftEvent(), new ShiftEvent(), new ShiftEvent());

        snsProxy.publishShiftEvents(shiftEvents);

        verify(mockObjectMapper, times(3)).writeValueAsString(any(ShiftEvent.class));
        verify(mockSnsClient, times(3)).publish(any(PublishRequest.class));
    }

    @Test(expected = RuntimeException.class)
    public void test_publishShiftEvents_throws_exception_when_serialization_fails() throws JsonProcessingException {

        when(mockObjectMapper.writeValueAsString(any(ShiftEvent.class))).thenThrow(new IOException());
        final List<ShiftEvent> shiftEvents = Collections.singletonList(new ShiftEvent());

        snsProxy.publishShiftEvents(shiftEvents);

        verify(mockObjectMapper, times(1)).writeValueAsString(any(ShiftEvent.class));
    }
}
