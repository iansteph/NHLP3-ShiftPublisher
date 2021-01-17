package iansteph.nhlp3.shiftpublisher.client.wrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Optional;

import static java.lang.String.format;

public class JsoupWrapper {

    private static final Logger LOGGER = LogManager.getLogger(JsoupWrapper.class);

    public Optional<Document> parseHtmlFromUrl(final String url) {

        try {

            final Document parsedHtml = HttpConnection.connect(url).get();
            return Optional.of(parsedHtml);
        }
        catch (final HttpStatusException e) {

            int httpStatusCode = e.getStatusCode();
            if (httpStatusCode == 404) {

                LOGGER.info("Encountered HTTP Status Code 404 for the URL: interpreting as if game is too far into the future for the TOI report to exist yet");
                return Optional.empty();
            }
            else if (httpStatusCode == 403) {

                LOGGER.info("Encountered HTTP Status Code 403 for the URL: interpreting as an intermittent NHL server-side issue preventing retrieving the TOI report");
                return Optional.empty();
            }
            else {

                LOGGER.info(format("Encountered HTTP Status Code %d when parsing HTML from URL for URL %s", httpStatusCode, url), e);
                throw new RuntimeException(e);
            }
        }
        catch (final IOException e) {

            LOGGER.info(format("Encountered exception when parsing HTML from URL for URL %s", url), e);
            throw new RuntimeException(e);
        }
    }

    public Optional<String> getRawHtmlFromUrl(final String url) {

        final Optional<Document> htmlDocument = parseHtmlFromUrl(url);
        if (htmlDocument.isPresent()) {

            final String rawHtml = parseHtmlFromUrl(url).get().html();
            return Optional.of(rawHtml);
        }
        else {

            return Optional.empty();
        }
    }
}
