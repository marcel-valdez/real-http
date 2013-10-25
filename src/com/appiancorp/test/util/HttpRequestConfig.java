package com.appiancorp.test.util;

/**
 * Created with IntelliJ IDEA.
 * User: Marcel
 * Date: 10/24/13
 * Time: 11:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequestConfig {
    private String contentType;
    private String relativeHref;

    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setHref(String relativeHref) { this.relativeHref = relativeHref; }

    public String contentType() { return this.contentType; }
    public String href() { return this.relativeHref; }
}
