package iansteph.nhlp3.shiftpublisher.proxy;

import iansteph.nhlp3.shiftpublisher.client.NhlToiClient;
import iansteph.nhlp3.shiftpublisher.client.wrapper.JsoupWrapper;
import iansteph.nhlp3.shiftpublisher.model.Team;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NhlTimeOnIceProxyTest {

    private static final int GAME_ID = 2019021079;
    private static final Team TEAM = Team.VISITOR;

    private final NhlToiClient mockNhlToiClient = mock(NhlToiClient.class);
    private final NhlTimeOnIceProxy nhlTimeOnIceProxy = new NhlTimeOnIceProxy(mockNhlToiClient);

    @Test
    public void test_constructor_successfully_builds_NhlToiProxy_when_non_null_NhlToiClient_provided() {

        final NhlToiClient nhlToiClient = new NhlToiClient(new JsoupWrapper());

        try {

            final NhlTimeOnIceProxy nhlTimeOnIceProxy = new NhlTimeOnIceProxy(nhlToiClient);
        }
        catch (final Exception e) {

            fail("Constructing NhlToiProxy with non-null NhlToiClient should not throw an exception.");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_throws_exception_when_null_NhlToiClient_provided() {

        new NhlTimeOnIceProxy(null);
    }

    @Test
    public void test_getToiReportForGame_successfully_retrieves_toi_report_for_game() {

        final String season = "20192020";
        final String teamAbbreviation = "V";
        final String game = "021079";
        when(mockNhlToiClient.getTeamToiReportForGame(eq(season), eq(teamAbbreviation), eq(game))).thenReturn(new Document("SomeBaseUri"));

        final Document result = nhlTimeOnIceProxy.getToiReportForGame(GAME_ID, TEAM);

        assertThat(result, is(notNullValue()));
        verify(mockNhlToiClient, times(1)).getTeamToiReportForGame(eq(season), eq(teamAbbreviation), eq(game));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getToiReportForGame_throws_IllegalArgumentException_when_gameId_is_negative() {

        nhlTimeOnIceProxy.getToiReportForGame(-2019021079, TEAM);

        verify(mockNhlToiClient, never()).getTeamToiReportForGame(anyString(), anyString(), anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getToiReportForGame_throws_IllegalArgumentException_when_gameId_is_not_ten_characters_long() {

        final int gameId = 2019;

        nhlTimeOnIceProxy.getToiReportForGame(gameId, TEAM);

        verify(mockNhlToiClient, never()).getTeamToiReportForGame(anyString(), anyString(), anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getToiReportForGame_throws_IllegalArgumentException_when_gameId_is_for_an_invalid_season() {

        final int gameId = 1900021079;

        nhlTimeOnIceProxy.getToiReportForGame(gameId, TEAM);

        verify(mockNhlToiClient, never()).getTeamToiReportForGame(anyString(), anyString(), anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getToiReportForGame_throws_IllegalArgumentException_when_team_is_null() {

        nhlTimeOnIceProxy.getToiReportForGame(GAME_ID, null);

        verify(mockNhlToiClient, never()).getTeamToiReportForGame(anyString(), anyString(), anyString());
    }
}
