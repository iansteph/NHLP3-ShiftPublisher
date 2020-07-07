package iansteph.nhlp3.shiftpublisher.client;

import iansteph.nhlp3.shiftpublisher.client.wrapper.JsoupWrapper;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NhlTimeOnIceClientTest {

    private final static String SEASON = "20192020";
    private final static String TEAM_ABBREVIATION = "V";
    private final static String GAME = "021079";

    private final JsoupWrapper mockJsoupWrapper = mock(JsoupWrapper.class);
    private final NhlTimeOnIceClient nhlTimeOnIceClient = new NhlTimeOnIceClient(mockJsoupWrapper);

    @Test
    public void test_getTeamToiReportForGame_successfully_retrieves_toi_report_for_game() {

        when(mockJsoupWrapper.parseHtmlFromUrl(anyString())).thenReturn(new Document("SomeBaseUri"));

        final Document actual = nhlTimeOnIceClient.getTeamToiReportForGame(SEASON, TEAM_ABBREVIATION, GAME);

        assertThat(actual, is(notNullValue()));
    }

    @Test(expected = RuntimeException.class)
    public void test_getTeamToiReportForGame_throws_exception_when_retrieving_toi_report_from_url() {

        when(mockJsoupWrapper.parseHtmlFromUrl(anyString())).thenThrow(new RuntimeException());

        nhlTimeOnIceClient.getTeamToiReportForGame(SEASON, TEAM_ABBREVIATION, GAME);
    }
}
