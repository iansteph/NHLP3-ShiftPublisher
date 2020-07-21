package iansteph.nhlp3.shiftpublisher.client.wrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static java.lang.String.format;

public class JsoupWrapper {

    private static final Logger LOGGER = LogManager.getLogger(JsoupWrapper.class);

    public Document parseHtmlFromUrl(final String url) {

        try {

            final Document parsedHtml = HttpConnection.connect(url).get();
            return parsedHtml;
        }
        catch (final IOException e) {

            LOGGER.info(format("Encountered exception when parsing HTML from URL for URL %s", url), e);
            throw new RuntimeException(e);
        }
    }

    public String getRawHtmlFromUrl(final String url) {

        try {

            final String rawHtml = HttpConnection.connect(url).get().html();
            return rawHtml;

        } catch (IOException e) {

            LOGGER.info(format("Encountered exception when retrieving raw HTML from URL for URL %s", url), e);
            throw new RuntimeException(e);
        }
    }
}
