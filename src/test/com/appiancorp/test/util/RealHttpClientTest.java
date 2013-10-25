package test.com.appiancorp.test.util;

import com.appiancorp.test.util.RealHttpClient;
import com.appiancorp.test.util.RealHttpResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.CharBuffer;

/**
 * RealHttpClient Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Oct 24, 2013</pre>
 */
public class RealHttpClientTest {

    private MinimalHttpHandler httpHandler;
    private HttpServer server;

    @Before
    public void before() throws Exception {
        this.httpHandler = new MinimalHttpHandler();
        server = HttpServer.create(new InetSocketAddress(7777), 7777);
        server.createContext("/test", this.httpHandler);
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    @After
    public void after() throws Exception {
        server.stop(0);
    }

    @Test
    public void testCanRequestAUrl() throws Exception {
        // Arrange
        URI uri = new URI("http://localhost:7777");
        RealHttpClient target = new RealHttpClient(uri);
        String expected = "SUCCESS";
        this.httpHandler.setResponse(expected);

        // Act
        RealHttpResponse actual =  target.getRequest("/test");
        actual.waitForReponse();

        // Assert
        Assert.assertNotNull(actual);
        Assert.assertEquals(200, actual.getStatusCode());
        Assert.assertEquals(expected, actual.content());
    }

    @Test
    public void testCanPostAUrl() throws Exception {
        // Arrange
        URI uri = new URI("http://localhost:7777");
        RealHttpClient target = new RealHttpClient(uri);
        String expected = "SUCCESS";
        this.httpHandler.setResponse(expected);

        // Act
        RealHttpResponse actual = target.postRequest("/test", "TEST");
        actual.waitForReponse();

        // Assert
        Assert.assertNotNull(actual);
        Assert.assertEquals(200, actual.getStatusCode());
        Assert.assertEquals(expected, actual.content());
    }

    @Test
    public void testCanMaintainSession() throws Exception {
        // Arrange
        URI uri = new URI("http://localhost:7777");
        RealHttpClient target = new RealHttpClient(uri);
        String payload = "TEST";
        target.postRequest("/test", payload).waitForReponse();
        int expected = this.httpHandler.lastRemoteAddress().getPort();

        // Act
        target.postRequest("/test", payload).waitForReponse();
        int actual = this.httpHandler.lastRemoteAddress().getPort();

        // Assert
        Assert.assertEquals(expected, actual);
    }

    class MinimalHttpHandler implements HttpHandler {
        private String content;
        private String response;
        private InetSocketAddress remoteAddress;

        public void setResponse(String response) {
            this.response = response;
        }

        public String content() {
            return this.content;
        }

        public InetSocketAddress lastRemoteAddress() {
            return this.remoteAddress;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            read(is);
            String payload = response != null ? response : content;
            t.sendResponseHeaders(200, payload.length());
            OutputStream os = t.getResponseBody();
            os.write(payload.getBytes());
            this.remoteAddress = t.getRemoteAddress();
            os.flush();
            os.close();
        }


        private void read(InputStream is) {
            InputStreamReader reader = new InputStreamReader(is);
            CharBuffer buffer = CharBuffer.allocate(10000);
            try {
                reader.read(buffer);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }

            content = new String(buffer.array()).trim();
        }
    }
} 
