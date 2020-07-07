package iansteph.nhlp3.shiftpublisher.client;

import iansteph.nhlp3.shiftpublisher.client.wrapper.JsoupWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import static java.lang.String.format;

public class NhlTimeOnIceClient {

    private static final Logger LOGGER = LogManager.getLogger(NhlTimeOnIceClient.class);

    private final JsoupWrapper jsoupWrapper;


    public NhlTimeOnIceClient(final JsoupWrapper jsoupWrapper) {

        this.jsoupWrapper = jsoupWrapper;
    }

    public Document getTeamToiReportForGame(final String season, final String teamAbbreviation, final String game) {

        final String resolvedUrl = format("http://www.nhl.com/scores/htmlreports/%s/T%s%s.HTM", season, teamAbbreviation, game);
        LOGGER.info(format("Retrieving TOI report for game %s in season %s for team %s via URL %s", game, season, teamAbbreviation, resolvedUrl));
        final Document toiReport = jsoupWrapper.parseHtmlFromUrl(resolvedUrl);
        LOGGER.info(format("Successfully retrieved TOI report for game %s in season %s for team %s", game, season, teamAbbreviation));
        return toiReport;

        // TODO - Create method to archive TOI report file in S3 which catches all failures and logs them silently
    }
}