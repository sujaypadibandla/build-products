package com.gymplus.core;

import com.gymplus.core.primitives.ServiceFormat;
import net.minidev.json.JSONValue;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.gymplus.core.Settings.PROXY_HOST;
import static com.gymplus.core.Settings.PROXY_PORT;
import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpUtils {

    private static final Logger LOGGER = LogManager.getLogger(HttpUtils.class);

    public static Gmap forward(String method, String url, String body, Gmap bodyParams, Gmap queryParams, Gmap headers, ResponseProcessor responseHandler) throws Exception {
        return forward(method, url, body, bodyParams, queryParams, headers, responseHandler, true);
    }

    public static Gmap forward(String method, String url, String body, Gmap bodyParams, Gmap queryParams, Gmap headers, ResponseProcessor responseHandler, boolean useProxy) throws Exception {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        if (!StrUtils.isAnyEmpty(PROXY_HOST, PROXY_PORT) && useProxy) {
            HttpHost proxy = new HttpHost(PROXY_HOST, Integer.parseInt(PROXY_PORT), "http");
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            httpClientBuilder.setRoutePlanner(routePlanner);
        }

        try (CloseableHttpClient httpclient = httpClientBuilder.build()) {

            List<NameValuePair> bodyParamsNvp = new ArrayList<>();
            if (bodyParams != null) {
                bodyParams.forEach((key, value) -> bodyParamsNvp.add(new BasicNameValuePair(key, (String) value)));
            }

            URIBuilder uriBuilder = new URIBuilder(url);
            if (queryParams != null) { queryParams.forEach((key, value) -> uriBuilder.setParameter(key, value == null ? "" : value.toString())); }

            HttpRequestBase httpRequest = _createHttpRequest(method, uriBuilder.build(), body, bodyParamsNvp);

            if (headers != null) {
                headers.forEach((key, value) -> httpRequest.setHeader(new BasicHeader(key, (String) value)));
            }

            String urlToLog = StrUtils.hideSensitiveInformation((uriBuilder.build()).toString());
            long id = Math.round(Math.random() * 100_000);
            ThreadContext.put("requestId", String.valueOf(id));
            ThreadContext.put("direction", "FW");
            if (headers != null && !StrUtils.isEmpty(headers.gets("client-request-id"))) {
                ThreadContext.put("requestId", headers.gets("client-request-id"));
            }
            LOGGER.info("{}({}) BODY={}", urlToLog, method, JSONValue.compress(StrUtils.hideSensitiveInformation(body)));
            return httpclient.execute(httpRequest, response -> {
                int status = response.getStatusLine().getStatusCode();
                boolean isError = status > 299;
                String responseBody = null;
                Gmap responseHeaders = new Gmap();
                if (response.getEntity() != null) { responseBody = EntityUtils.toString(response.getEntity());
                    for (Header header : response.getAllHeaders()) {
                        responseHeaders.put(header.getName(), header.getValue());
                    }
                }
                ThreadContext.put("direction", "RE");
                if (isError) {
                    LOGGER.error("{}", JSONValue.compress(StrUtils.hideSensitiveInformation(responseBody)));
                } else {
                    if (response.getHeaders("request-id").length > 0) {
                        ThreadContext.put("ri", response.getHeaders("request-id")[0].getValue());
                    }
                    LOGGER.info("{}", JSONValue.compress(StrUtils.hideSensitiveInformation(responseBody)));
                }
                ThreadContext.remove("requestId");
                ThreadContext.remove("direction");
                ThreadContext.remove("ri");

                Gmap result = new Gmap(responseHandler.process(responseBody, status));
                return result.append("status", status, "responseHeaders", responseHeaders);
            });
        }
    }

    private static HttpRequestBase _createHttpRequest(String method, URI url, String body, List<NameValuePair> bodyParams) throws Exception {
        switch (method) {
            case "GET":
                return new HttpGet(url);
            case "PATCH": {
                HttpPatch httpPatch = new HttpPatch(url);
                if (body != null) {
                    httpPatch.setEntity(new StringEntity(body));
                } else if (bodyParams != null && !bodyParams.isEmpty()) {
                    httpPatch.setEntity(new UrlEncodedFormEntity(bodyParams, UTF_8));
                }
                return httpPatch;
            }
            case "POST": {
                HttpPost httpPost = new HttpPost(url);
                if (body != null) {
                    httpPost.setEntity(new StringEntity(body));
                } else if (bodyParams != null && !bodyParams.isEmpty()) {
                    httpPost.setEntity(new UrlEncodedFormEntity(bodyParams, UTF_8));
                }
                return httpPost;
            }
            case "PUT": {
                HttpPut httpPut = new HttpPut(url);
                if (body != null) {
                    httpPut.setEntity(new StringEntity(body));
                } else if (bodyParams != null && !bodyParams.isEmpty()) {
                    httpPut.setEntity(new UrlEncodedFormEntity(bodyParams, UTF_8));
                }
                return httpPut;
            }
            case "DELETE":
                return new HttpDelete(url);
        }
        throw new Exception("Unsupported Http method");
    }

    public static class ServiceResponse {
        public boolean isSuccess = false;
        public Object result = null;
        public Exception exception = null;
        public ServiceFormat format = null;
        public Gmap headers = null;

        public static ServiceResponse onFailure(Exception exception, Gmap headers) {
            ServiceResponse response = new ServiceResponse();
            response.isSuccess = false;
            response.exception = exception;
            response.headers = headers;
            return response;
        }

        public static ServiceResponse onSuccess(Object object, ServiceFormat format, Gmap headers) {
            ServiceResponse response = new ServiceResponse();
            response.isSuccess = true;
            response.result = object;
            response.format = format;
            response.headers = headers;
            return response;
        }
    }
}
