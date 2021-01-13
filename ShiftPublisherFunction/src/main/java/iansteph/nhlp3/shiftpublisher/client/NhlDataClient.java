package iansteph.nhlp3.shiftpublisher.client;

import iansteph.nhlp3.shiftpublisher.model.roster.Roster;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static java.lang.String.format;

public class NhlDataClient {

    private final RestTemplate restTemplate;

    private final static String BASE_NHL_URL = "http://statsapi.web.nhl.com/api/v1/";

    public NhlDataClient(final RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    public Roster getRosterForTeamId(final int teamId) {

        final String resolvedUrl = format("%s/teams/%d/roster", BASE_NHL_URL, teamId);
        final URI uri = UriComponentsBuilder.fromHttpUrl(resolvedUrl)
                .build()
                .toUri();
        final Roster roster = restTemplate.getForObject(uri, Roster.class);
        return roster;
    }
}
