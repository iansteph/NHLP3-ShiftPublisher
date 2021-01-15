package iansteph.nhlp3.shiftpublisher.client;

import iansteph.nhlp3.shiftpublisher.client.wrapper.JsoupWrapper;
import org.jsoup.nodes.Document;
import org.junit.Test;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NhlTimeOnIceClientTest {

    private final static String SEASON = "20192020";
    private final static String TEAM_ABBREVIATION = "V";
    private final static String GAME = "021079";

    private final JsoupWrapper mockJsoupWrapper = mock(JsoupWrapper.class);
    private final S3Client mockS3Client = mock(S3Client.class);
    private final NhlTimeOnIceClient nhlTimeOnIceClient = new NhlTimeOnIceClient(mockJsoupWrapper, mockS3Client);

    @Test
    public void test_getTeamToiReportForGame_successfully_retrieves_toi_report_for_game() {

        when(mockJsoupWrapper.getRawHtmlFromUrl(anyString())).thenReturn(Optional.of("SomeString"));
        when(mockJsoupWrapper.parseHtmlFromUrl(anyString())).thenReturn(Optional.of(new Document("SomeBaseUri")));

        final Optional<Document> actual = nhlTimeOnIceClient.getTeamToiReportForGame(SEASON, TEAM_ABBREVIATION, GAME);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(not(Optional.empty())));
        verify(mockS3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));

    }

    @Test
    public void test_getTeamToiReportForGame_returns_empty_optional_when_retrieved_toi_report_is_empty_optional() {

        when(mockJsoupWrapper.getRawHtmlFromUrl(anyString())).thenReturn(Optional.empty());
        when(mockJsoupWrapper.parseHtmlFromUrl(anyString())).thenReturn(Optional.empty());

        final Optional<Document> actual = nhlTimeOnIceClient.getTeamToiReportForGame(SEASON, TEAM_ABBREVIATION, GAME);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(Optional.empty()));
        verify(mockS3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test(expected = RuntimeException.class)
    public void test_getTeamToiReportForGame_throws_exception_when_retrieving_toi_report_from_url() {

        when(mockJsoupWrapper.getRawHtmlFromUrl(anyString())).thenThrow(new RuntimeException());
        when(mockJsoupWrapper.parseHtmlFromUrl(anyString())).thenThrow(new RuntimeException());

        try {

            nhlTimeOnIceClient.getTeamToiReportForGame(SEASON, TEAM_ABBREVIATION, GAME);
        }
        catch (final RuntimeException e) {

            verify(mockS3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
            throw e;
        }
    }
}
