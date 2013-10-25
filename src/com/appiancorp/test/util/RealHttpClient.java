package com.appiancorp.test.util;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Marcel
 * Date: 10/24/13
 * Time: 9:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class RealHttpClient {
    private final URI uri;
    private final org.apache.http.client.HttpClient client;
    public RealHttpClient(URI uri) throws IOException {
        this.uri = uri;
        this.client = HttpClients.createDefault();
    }

    public RealHttpClient(URI uri, String username, String password) throws IOException {
        this.uri = uri;

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
                                     new UsernamePasswordCredentials(username, password));
        this.client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
    }

    public RealHttpResponse getRequest(String relativeHref) throws IOException {
        HttpGet request = new HttpGet(this.uri.resolve(relativeHref));
        HttpResponse response = client.execute(request);
        return new RealHttpResponse(response);
    }

    public RealHttpResponse getRequest(HttpRequestConfig config) throws IOException {
        HttpGet request = new HttpGet(this.uri.resolve(config.href()));
        if(config.contentType() != null) { request.addHeader(HttpHeaders.ACCEPT, config.contentType()); }
        HttpResponse response = client.execute(request);

        return new RealHttpResponse(response);
    }

    public RealHttpResponse postRequest(String href, String payload) throws IOException {
        HttpPost request = new HttpPost(this.uri.resolve(href));
        request.setEntity(new StringEntity(payload));
        HttpResponse response = client.execute(request);

        return new RealHttpResponse(response);
    }

    public RealHttpResponse postRequest(HttpRequestConfig config, String payload) throws IOException {
        HttpPost request = new HttpPost(this.uri.resolve(config.href()));
        request.setEntity(new StringEntity(payload));
        if(config.contentType() != null) { request.addHeader(HttpHeaders.ACCEPT, config.contentType()); }
        HttpResponse response = client.execute(request);

        return new RealHttpResponse(response);
    }
}
