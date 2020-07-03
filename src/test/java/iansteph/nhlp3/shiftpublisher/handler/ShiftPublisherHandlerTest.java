package iansteph.nhlp3.shiftpublisher.handler;

import iansteph.nhlp3.shiftpublisher.client.NhlToiClient;
import iansteph.nhlp3.shiftpublisher.client.wrapper.JsoupWrapper;
import iansteph.nhlp3.shiftpublisher.model.Team;
import iansteph.nhlp3.shiftpublisher.model.request.ShiftPublisherRequest;
import iansteph.nhlp3.shiftpublisher.model.toi.TimeOnIceReport;
import iansteph.nhlp3.shiftpublisher.parse.TimeOnIceReportParser;
import iansteph.nhlp3.shiftpublisher.proxy.NhlTimeOnIceProxy;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ShiftPublisherHandlerTest {

    private final JsoupWrapper jsoupWrapper = new JsoupWrapper();
    private final NhlToiClient nhlToiClient = new NhlToiClient(jsoupWrapper);
    private final NhlTimeOnIceProxy nhlTimeOnIceProxy = new NhlTimeOnIceProxy(nhlToiClient);

    @Test
    public void test_temporary_integration_test_to_verify_TOI_report_is_parsed() {

        final ShiftPublisherRequest shiftPublisherRequest = new ShiftPublisherRequest();
        shiftPublisherRequest.setGameId(2019021079);
        final Team team = Team.HOME;
        final TimeOnIceReportParser timeOnIceReportParser = new TimeOnIceReportParser();

        final Document parsedToiReport = nhlTimeOnIceProxy.getToiReportForGame(shiftPublisherRequest.getGameId(), team);
        final TimeOnIceReport timeOnIceReport = timeOnIceReportParser.parse(parsedToiReport);

        /*
         * TODO
         *  1. Go through all of the filtered list of shift, name, and spacer rows
         *  2. Split rows into groupings for each player
         *  3. Pass each grouping into a transformer to build objects
         *  4. TBD
         */

        assertThat(parsedToiReport, is(notNullValue()));
    }
}
