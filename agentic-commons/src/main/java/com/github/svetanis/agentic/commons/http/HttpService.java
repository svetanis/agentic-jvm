package com.github.svetanis.agentic.commons.http;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Unified Transport SPI for HTTP calls used across Agentic JVM projects.
 */
public interface HttpService {

    /**
     * Represents an HTTP response with the status code and raw body.
     */
    record Response(int statusCode, String body) {
        
        /** Returns true if the status code is in the 2xx range. */
        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        /** 
         * Returns the body if the request was successful, otherwise throws a RuntimeException.
         * Used by callers that expect a strict success (e.g. model-prism).
         */
        public String bodyOrThrow() {
            if (!isSuccess()) {
                throw new RuntimeException("HTTP [" + statusCode + "] -> " + body);
            }
            return body;
        }
    }

    /** Sends a GET request to the explicit URL. */
    Response get(String url) throws Exception;

    /** Sends a POST request to the configured base URL. */
    Response post(String requestBody) throws Exception;

    /** Sends a POST request to the configured base URL. */
    Response post(byte[] requestBody) throws Exception;

    /** Sends a POST request and returns the SSE response as a lazy line stream. */
    Stream<String> postStream(String requestBody) throws Exception;
}
