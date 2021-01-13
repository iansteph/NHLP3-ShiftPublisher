package iansteph.nhlp3.shiftpublisher.client;

import iansteph.nhlp3.shiftpublisher.model.roster.Roster;
import org.junit.Test;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NhlDataClientTest {

    private final RestTemplate mockRestTemplate = mock(RestTemplate.class);
    private final NhlDataClient nhlDataClient = new NhlDataClient(mockRestTemplate);

    private final static String BASE_NHL_URL = "http://statsapi.web.nhl.com/api/v1/";

    @Test
    public void test_getRosterForTeamId_successfully_retrieves_roster_from_NHL_API() {

        final int teamId = 28;
        final String resolvedUrl = format("%s/teams/%d/roster", BASE_NHL_URL, teamId);
        final URI uri = UriComponentsBuilder.fromHttpUrl(resolvedUrl)
                .build()
                .toUri();
        when(mockRestTemplate.getForObject(eq(uri), eq(Roster.class))).thenReturn(new Roster());

        final Roster response = nhlDataClient.getRosterForTeamId(teamId);

        assertThat(response, is(notNullValue()));
        verify(mockRestTemplate, times(1)).getForObject(eq(uri), eq(Roster.class));
    }

    @Test(expected = RestClientException.class)
    public void test_getRosterForTeamId_throws_exception_when_error_occurs_calling_NHL_API() {

        final int teamId = 28;
        final String resolvedUrl = format("%s/teams/%d/roster", BASE_NHL_URL, teamId);
        final URI uri = UriComponentsBuilder.fromHttpUrl(resolvedUrl)
                .build()
                .toUri();
        when(mockRestTemplate.getForObject(eq(uri), eq(Roster.class))).thenThrow(new RestClientException(""));

        nhlDataClient.getRosterForTeamId(teamId);

        verify(mockRestTemplate, times(1)).getForObject(eq(uri), eq(Roster.class));
    }
}
