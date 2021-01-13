package iansteph.nhlp3.shiftpublisher.proxy;

import iansteph.nhlp3.shiftpublisher.client.NhlDataClient;
import iansteph.nhlp3.shiftpublisher.model.roster.Roster;
import org.junit.Test;
import org.springframework.web.client.RestClientException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NhlDataProxyTest {

    private final NhlDataClient mockNhlDataClient = mock(NhlDataClient.class);
    private final NhlDataProxy nhlDataProxy = new NhlDataProxy(mockNhlDataClient);

    @Test
    public void test_getRosterForTeamId_successfully_retrieves_roster() {

        when(mockNhlDataClient.getRosterForTeamId(anyInt())).thenReturn(new Roster());

        final Roster response = nhlDataProxy.getRosterForTeamId(28);

        assertThat(response, is(notNullValue()));
        verify(mockNhlDataClient, times(1)).getRosterForTeamId(anyInt());
    }

    @Test(expected = RestClientException.class)
    public void test_getRosterForTeamId_throws_exception_when_nhlDataClient_has_error() {

        when(mockNhlDataClient.getRosterForTeamId(anyInt())).thenThrow(new RestClientException(""));

        nhlDataProxy.getRosterForTeamId(28);

        verify(mockNhlDataClient, times(1)).getRosterForTeamId(anyInt());
    }
}
