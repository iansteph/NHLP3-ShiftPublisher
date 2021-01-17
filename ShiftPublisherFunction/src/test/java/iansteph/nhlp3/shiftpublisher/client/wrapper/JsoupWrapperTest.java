package iansteph.nhlp3.shiftpublisher.client.wrapper;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpConnection.class)
public class JsoupWrapperTest {

    public static final String WWW_NHL_COM = "http://www.nhl.com";

    private final Connection mockConnection = mock(Connection.class);
    private final Document mockDocument = mock(Document.class);
    private final JsoupWrapper jsoupWrapper = new JsoupWrapper();

    @Before
    public void setup() throws IOException {

        PowerMockito.mockStatic(HttpConnection.class);
        when(HttpConnection.connect(anyString())).thenReturn(mockConnection);
        when(mockDocument.html()).thenReturn(WWW_NHL_COM);
        when(mockConnection.get()).thenReturn(mockDocument);
    }

    @Test
    public void test_parseHtmlFromUrl_successfully_parses_html_from_url() {

        final Optional<Document> actualDocument = jsoupWrapper.parseHtmlFromUrl(WWW_NHL_COM);

        assertThat(actualDocument, is(notNullValue()));
        assertThat(actualDocument, is(not(Optional.empty())));
    }

    @Test
    public void test_parseHtmlFromUrl_returns_empty_optional_when_endpoint_returns_http_status_code_404() throws IOException {

        when(mockConnection.get()).thenThrow(new HttpStatusException("Internal Server Error", 404, WWW_NHL_COM));

        final Optional<Document> actualDocument = jsoupWrapper.parseHtmlFromUrl(WWW_NHL_COM);

        assertThat(actualDocument, is(notNullValue()));
        assertThat(actualDocument, is(Optional.empty()));
    }

    @Test
    public void test_parseHtmlFromUrl_returns_empty_optional_when_endpoint_returns_http_status_code_403() throws IOException {

        when(mockConnection.get()).thenThrow(new HttpStatusException("Access Denied", 403, WWW_NHL_COM));

        final Optional<Document> actualDocument = jsoupWrapper.parseHtmlFromUrl(WWW_NHL_COM);

        assertThat(actualDocument, is(notNullValue()));
        assertThat(actualDocument, is(Optional.empty()));
    }

    @Test(expected = RuntimeException.class)
    public void test_parseHtmlFromUrl_throws_exception_when_endpoint_returns_http_status_exception_without_code_404() throws IOException {

        when(mockConnection.get()).thenThrow(new HttpStatusException("Internal Server Error", 500, WWW_NHL_COM));

        jsoupWrapper.parseHtmlFromUrl(WWW_NHL_COM);
    }

    @Test(expected = RuntimeException.class)
    public void test_parseHtmlFromUrl_throws_exception_on_error() throws IOException {

        when(mockConnection.get()).thenThrow(new IOException());

        jsoupWrapper.parseHtmlFromUrl(WWW_NHL_COM);
    }

    @Test
    public void test_getRawHtmlFromUrl_successfully_gets_raw_html_from_url() {

        final Optional<String> rawHtml = jsoupWrapper.getRawHtmlFromUrl(WWW_NHL_COM);

        assertThat(rawHtml, is(notNullValue()));
    }

    @Test
    public void test_getRawHtmlFromUrl_returns_empty_optional_when_endpoint_returns_http_status_code_404() throws IOException {

        when(mockConnection.get()).thenThrow(new HttpStatusException("Internal Server Error", 404, WWW_NHL_COM));

        final Optional<String> rawHtml = jsoupWrapper.getRawHtmlFromUrl(WWW_NHL_COM);

        assertThat(rawHtml, is(notNullValue()));
        assertThat(rawHtml, is(Optional.empty()));
    }

    @Test(expected = RuntimeException.class)
    public void test_getRawHtmlFromUrl_throws_exception_when_endpoint_returns_http_status_exception_without_code_404() throws IOException {

        when(mockConnection.get()).thenThrow(new HttpStatusException("Internal Server Error", 500, WWW_NHL_COM));

        jsoupWrapper.parseHtmlFromUrl(WWW_NHL_COM);
    }

    @Test(expected = RuntimeException.class)
    public void test_getRawHtmlFromUrl_throws_exception_on_error() throws IOException {

        when(mockConnection.get()).thenThrow(new IOException());

        jsoupWrapper.getRawHtmlFromUrl(WWW_NHL_COM);
    }
}
