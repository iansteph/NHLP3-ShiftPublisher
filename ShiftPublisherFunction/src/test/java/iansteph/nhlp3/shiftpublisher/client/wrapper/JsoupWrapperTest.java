package iansteph.nhlp3.shiftpublisher.client.wrapper;

import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
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

        final Document actualDocument = jsoupWrapper.parseHtmlFromUrl(WWW_NHL_COM);

        assertThat(actualDocument, is(notNullValue()));
    }

    @Test(expected = RuntimeException.class)
    public void test_parseHtmlFromUrl_throws_exception_on_error() throws IOException {

        when(mockConnection.get()).thenThrow(new IOException());

        jsoupWrapper.parseHtmlFromUrl(WWW_NHL_COM);
    }

    @Test
    public void test_getRawHtmlFromUrl_successfully_gets_raw_html_from_url() {

        final String rawHtml = jsoupWrapper.getRawHtmlFromUrl(WWW_NHL_COM);

        assertThat(rawHtml, is(notNullValue()));
    }

    @Test(expected = RuntimeException.class)
    public void test_getRawHtmlFromUrl_throws_exception_on_error() throws IOException {

        when(mockConnection.get()).thenThrow(new IOException());

        jsoupWrapper.getRawHtmlFromUrl(WWW_NHL_COM);
    }
}
