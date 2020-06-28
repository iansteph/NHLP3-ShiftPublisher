package iansteph.nhlp3.shiftpublisher.client.wrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static java.lang.String.format;

public class JsoupWrapper {

    private static final Logger LOGGER = LogManager.getLogger(JsoupWrapper.class);

    public Document parseHtmlFromUrl(final String url) {

        Document parsedHtml;
        try {

            final Connection httpConnection = HttpConnection.connect(url);
            parsedHtml = httpConnection.get();
        }
        catch (final IOException e) {

            LOGGER.info(format("Encountered exception when parsing HTML from URL for URL %s", url), e);
            throw new RuntimeException(e);
        }
        return parsedHtml;
    }
}
