package com.tcsl.zodiac.cilent.http;

import lombok.Builder;
import lombok.Data;

import java.net.URI;
import java.util.Map;

/**
 * @className: HttpResponse
 * @description:
 * @author: yang pan
 */
@Data
@Builder
public class HttpResponse<T> {
    private  int statusCode;
    private  T entity;
    private  Map<String, String> headers;
    private  URI location;


    public HttpResponse(int statusCode, T entity, Map<String, String> headers, URI location) {
        this.statusCode = statusCode;
        this.entity = entity;
        this.headers = headers;
        this.location = location;
    }

    public HttpResponse(int statusCode, T entity) {
        this.statusCode = statusCode;
        this.entity = entity;
        this.headers = null;
        this.location = null;
    }
}
