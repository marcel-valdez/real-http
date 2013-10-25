package com.appiancorp.test.util;

import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: Marcel
 * Date: 10/24/13
 * Time: 9:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class RealHttpResponse {

    private final HttpResponse inner;
    private String content = null;

    public RealHttpResponse(HttpResponse response) {
        this.inner = response;
    }

    public void waitForReponse() throws IOException, TimeoutException, InterruptedException {
        this.content();
    }

    public String content() throws IOException, InterruptedException, TimeoutException {
        if (this.content == null) {
            InputStreamReader reader = new InputStreamReader(this.inner.getEntity().getContent());
            long max = 2000;
            while(!reader.ready() && max >= 0) {
                synchronized (this) {
                    this.wait(10);
                }

                max -= 10;
                //if(max <= 0) { throw new TimeoutException("Response was not ready for 2 seconds"); }
            }

            CharBuffer buffer = CharBuffer.allocate(10000);
            BufferedReader buffReader = new BufferedReader(reader);
            buffReader.read(buffer);
            content = new String(buffer.array()).trim();

            this.inner.getEntity().getContent().close();
        }

        return this.content;
    }

    public int getStatusCode() {
        return this.inner.getStatusLine().getStatusCode();
    }

    public boolean isEmpty() throws IOException, TimeoutException, InterruptedException {
        return this.content().length() == 0;
    }
}
