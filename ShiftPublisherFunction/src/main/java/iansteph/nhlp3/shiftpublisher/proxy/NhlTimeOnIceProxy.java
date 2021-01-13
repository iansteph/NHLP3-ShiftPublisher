package iansteph.nhlp3.shiftpublisher.proxy;

import iansteph.nhlp3.shiftpublisher.client.NhlTimeOnIceClient;
import iansteph.nhlp3.shiftpublisher.model.Team;
import org.jsoup.nodes.Document;

import static java.lang.String.format;

public class NhlTimeOnIceProxy {

    private final NhlTimeOnIceClient nhlTimeOnIceClient;

    public NhlTimeOnIceProxy(final NhlTimeOnIceClient nhlTimeOnIceClient) {

        if (nhlTimeOnIceClient == null) {

            throw new IllegalArgumentException("When constructing NhlToiProxy the NhlToiClient parameter must be non-null");
        }
        this.nhlTimeOnIceClient = nhlTimeOnIceClient;
    }

    public Document getToiReportForGame(final int gameId, final Team team) {

        validateGameId(gameId);
        validateTeam(team);
        final String season = getSeasonFromGameId(gameId);
        final String teamAbbreviation = team.getLabel();
        final String game = getGameFromGameId(gameId);
        final Document toiReport = nhlTimeOnIceClient.getTeamToiReportForGame(season, teamAbbreviation, game);
        return toiReport;
    }

    // Example gameId 2019021079
    private void validateGameId(final int gameId) {

        final String gameIdToValidate = String.valueOf(gameId);
        if (gameId < 0) {

            throw new IllegalArgumentException(format("GameId must be a valid non-negative GameId. Offending GameId: %d", gameId));
        }
        else if (gameIdToValidate.length() < 10) {

            throw new IllegalArgumentException(format("GameId must be 10 characters long. Offending GameId: %d", gameId));
        }
        else if (Integer.parseInt(gameIdToValidate.substring(0, 4)) <= 1917) { // NHL was founded in 1917

            throw new IllegalArgumentException(format("GameId must be for a valid season. Offending GameId: %d", gameId));
        }
    }

    private void validateTeam(final Team team) {

        if (team == null) {

            throw new IllegalArgumentException(format("Team must be non-null. Offending Team: %s", team));
        }
    }

    // Example gameId 2019021079
    private String getSeasonFromGameId(final int gameId) {

        final String gameIdAsString = String.valueOf(gameId);
        final int seasonStartYear = Integer.parseInt(gameIdAsString.substring(0,4));
        final int seasonEndYear = seasonStartYear + 1;
        final String season = format("%d%d", seasonStartYear, seasonEndYear);
        return season;
    }

    // Example gameId 2019021079
    private String getGameFromGameId(final int gameId) {

        final String gameIdAsString = String.valueOf(gameId);
        final String game = gameIdAsString.substring(4);
        return game;
    }
}
