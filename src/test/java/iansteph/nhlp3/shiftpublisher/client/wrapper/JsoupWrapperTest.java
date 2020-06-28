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

    private final Connection mockConnection = mock(Connection.class);
    private final JsoupWrapper jsoupWrapper = new JsoupWrapper();

    @Before
    public void setup() {

        PowerMockito.mockStatic(HttpConnection.class);
        when(HttpConnection.connect(anyString())).thenReturn(mockConnection);
    }

    @Test
    public void test_parseHtmlFromUrl_successfully_parses_html_from_url() throws IOException {

        when(mockConnection.get()).thenReturn(new Document("SomeBaseUri"));
        final String url = "http://www.nhl.com";

        final Document actualDocument = jsoupWrapper.parseHtmlFromUrl(url);

        assertThat(actualDocument, is(notNullValue()));
    }

    @Test(expected = RuntimeException.class)
    public void test_parseHtmlFromUrl_throws_exception_on_error() throws IOException {

        when(mockConnection.get()).thenThrow(new IOException());
        final String url = "http://www.nhl.com";

        jsoupWrapper.parseHtmlFromUrl(url);
    }
}
