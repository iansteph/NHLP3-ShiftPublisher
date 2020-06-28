package iansteph.nhlp3.shiftpublisher.client;

import iansteph.nhlp3.shiftpublisher.client.wrapper.JsoupWrapper;
import org.jsoup.nodes.Document;

import static java.lang.String.format;

public class NhlToiClient {

    private final JsoupWrapper jsoupWrapper;


    public NhlToiClient(final JsoupWrapper jsoupWrapper) {

        this.jsoupWrapper = jsoupWrapper;
    }

    public Document getTeamToiReportForGame(final String season, final String teamAbbreviation, final String game) {

        final String resolvedUrl = format("http://www.nhl.com/scores/htmlreports/%s/T%s%s.HTM", season, teamAbbreviation, game);
        final Document toiReport = jsoupWrapper.parseHtmlFromUrl(resolvedUrl);
        return toiReport;
    }
}
