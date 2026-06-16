package com.github.svetanis.agentic.commons.http;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.inject.Provider;

/**
 * Jakarta {@link Provider} that builds pre-configured {@link HttpClient} instances.
 *
 * <p>Every client uses a cached daemon-thread pool (named {@code judge-http}) and a 5-second
 * connect timeout, suitable for the short-lived HTTP calls judges and runners make.
 */
public class HttpClientProvider implements Provider<HttpClient> {

    /**
     * Creates and returns a new {@link HttpClient} configured with a daemon-thread executor and a
     * 5-second connect timeout.
     *
     * @return a ready-to-use {@link HttpClient}
     */
    @Override
    public HttpClient get() {
        return HttpClient.newBuilder() //
                .executor(executor()) //
                .connectTimeout(Duration.ofSeconds(5)) //
                .build(); //
    }

    private ExecutorService executor() {
        return Executors.newCachedThreadPool(
                r -> {
                    Thread t = new Thread(r, "judge-http");
                    t.setDaemon(true);
                    return t;
                });
    }
}
