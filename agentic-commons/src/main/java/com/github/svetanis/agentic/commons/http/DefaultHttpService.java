package com.github.svetanis.agentic.commons.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

/**
 * Default {@link HttpService} implementation backed by {@code java.net.http.HttpClient}.
 */
public final class DefaultHttpService implements HttpService {

    private static final HttpClient SHARED_HTTP_CLIENT = new HttpClientProvider().get();

    private final HttpClient http;
    private final String apiUrl;
    private final Supplier<Optional<String>> tokenSupplier;
    private final Optional<Duration> timeout;

    public DefaultHttpService(String apiUrl, Optional<String> staticToken) {
        this(SHARED_HTTP_CLIENT, apiUrl, () -> staticToken, Optional.empty());
    }

    public DefaultHttpService(HttpClient http, URI uri, String staticToken, Duration timeout) {
        this(http, uri.toString(), () -> Optional.of(staticToken), Optional.of(timeout));
    }

    public DefaultHttpService(HttpClient http, String apiUrl, Supplier<Optional<String>> tokenSupplier, Optional<Duration> timeout) {
        this.http = Preconditions.checkNotNull(http, "http");
        this.apiUrl = Preconditions.checkNotNull(apiUrl, "apiUrl");
        this.tokenSupplier = Preconditions.checkNotNull(tokenSupplier, "tokenSupplier");
        this.timeout = Preconditions.checkNotNull(timeout, "timeout");
    }

    @Override
    public Response get(String url) throws Exception {
        HttpRequest request = newRequestBuilder(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return new Response(response.statusCode(), response.body());
    }

    @Override
    public Response post(String requestBody) throws Exception {
        HttpRequest request = newRequestBuilder(apiUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return new Response(response.statusCode(), response.body());
    }

    @Override
    public Response post(byte[] requestBody) throws Exception {
        HttpRequest request = newRequestBuilder(apiUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return new Response(response.statusCode(), response.body());
    }

    @Override
    public Stream<String> postStream(String requestBody) throws Exception {
        HttpRequest request = newRequestBuilder(apiUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<Stream<String>> response = http.send(request, HttpResponse.BodyHandlers.ofLines());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String error = response.body().collect(Collectors.joining());
            throw new RuntimeException("HTTP [" + response.statusCode() + "] -> " + error);
        }
        return response.body();
    }

    private HttpRequest.Builder newRequestBuilder(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url));
        tokenSupplier.get().ifPresent(token -> builder.header("Authorization", "Bearer " + token));
        timeout.ifPresent(builder::timeout);
        return builder;
    }
}
