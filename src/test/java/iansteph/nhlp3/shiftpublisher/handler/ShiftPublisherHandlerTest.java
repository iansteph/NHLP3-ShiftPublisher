package iansteph.nhlp3.shiftpublisher.handler;

import iansteph.nhlp3.shiftpublisher.client.NhlToiClient;
import iansteph.nhlp3.shiftpublisher.client.wrapper.JsoupWrapper;
import iansteph.nhlp3.shiftpublisher.model.Team;
import iansteph.nhlp3.shiftpublisher.model.request.ShiftPublisherRequest;
import iansteph.nhlp3.shiftpublisher.proxy.NhlToiProxy;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ShiftPublisherHandlerTest {

    private final JsoupWrapper jsoupWrapper = new JsoupWrapper();
    private final NhlToiClient nhlToiClient = new NhlToiClient(jsoupWrapper);
    private final NhlToiProxy nhlToiProxy = new NhlToiProxy(nhlToiClient);

    @Test
    public void test_temporary_integration_test_to_verify_TOI_report_is_parsed() {

        final ShiftPublisherRequest shiftPublisherRequest = new ShiftPublisherRequest();
        shiftPublisherRequest.setGameId(2019021079);
        final Team team = Team.HOME;

        final Document parsedToiReport = nhlToiProxy.getToiReportForGame(shiftPublisherRequest.getGameId(), team);


        final Elements elements = parsedToiReport.getElementsByClass("pageBreakAfter");
        final Element topLevelTable = elements.get(0).children().get(0);
        final Node topLevelTableBody = topLevelTable.childNodes().get(1);
        final Node topLevelTableBodyTeamData = topLevelTableBody.childNodes().get(6);
        final Node topLevelTableBodyTeamDataTable = topLevelTableBodyTeamData.childNodes().get(1);
        final Node topLevelTableBodyTeamDataTableData = topLevelTableBodyTeamDataTable.childNodes().get(1);
        final Node toiReportTableBody = topLevelTableBodyTeamDataTableData.childNodes().get(1);

        final Map<String, List<Element>> rawTimeOnIceData = new HashMap<>();
        final Stack<String> currentGroupingQueue = new Stack<>();
        currentGroupingQueue.push(null);

        toiReportTableBody.childNodes().stream()
                .filter(node -> node instanceof Element)
                .map(node -> (Element) node)
                .forEach(element -> {

                    final boolean one = element.childrenSize() == 1;
                    final Element firstChild = element.children().get(0);
                    final boolean firstChildElementIsPlayerHeader = firstChild.hasClass("playerHeading + border");
                    final Node firstChildsNode = firstChild.childNode(0);
                    final boolean firstChildsNodeIsTextNode = firstChildsNode instanceof TextNode;
                    final boolean firstChildsTextNodeIsNotBlank = !((TextNode) firstChildsNode).isBlank();
                    final String currentGrouping = currentGroupingQueue.peek();
                    if (one &&
                        firstChildElementIsPlayerHeader &&
                        firstChildsNodeIsTextNode &&
                        firstChildsTextNodeIsNotBlank) {

                        final String playerHeadingRawText = ((TextNode) firstChildsNode).text();
                        currentGroupingQueue.push(playerHeadingRawText);
                        rawTimeOnIceData.put(playerHeadingRawText, new ArrayList<>());
                    }
                    else if (currentGrouping != null) {

                        rawTimeOnIceData.get(currentGrouping).add(element);
                    }
                });

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
