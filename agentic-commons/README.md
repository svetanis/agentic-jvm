# Agentic JVM Commons (`agentic-commons`)

A shared library providing core transport SPIs, HTTP client configurations, and OAuth2 security/authentication helper suppliers for the `agentic-jvm` ecosystem.

---

## Key Features

### 1. Unified HTTP SPI (`HttpService`)
A simple, reactive-friendly HTTP client interface that abstracts request dispatching.
* Supports standard synchronous **GET** and **POST** requests.
* Supports **SSE (Server-Sent Events)** streaming via `postStream(String requestBody)` returning a lazy `Stream<String>`.

### 2. Autodetecting Authentication (`AccessTokenSupplier`)
An abstraction for acquiring OAuth2 tokens to communicate securely with Google Cloud Vertex AI and other GCP-protected endpoints.
* **`EnvVarAccessTokenSupplier`**: Utilizes the `VERTEX_ACCESS_TOKEN` environment variable for quick local prototyping or overrides.
* **`AdcAccessTokenSupplier`**: Leverages standard Google Application Default Credentials (ADC) to request and automatically refresh access tokens in secure environments.
* **Auto-Discovery**: Calling `AccessTokenSupplier.autoDetect()` resolves the correct strategy automatically based on your runtime environment.

### 3. Pre-configured HttpClient (`HttpClientProvider`)
A `Provider<HttpClient>` that builds thread-safe HTTP client instances customized for Agentic tasks:
* Configured with a dedicated daemon thread executor (`judge-http`).
* Connect timeout pre-set to 5 seconds to ensure short-lived calls fail fast instead of hanging threads.

---

## Main APIs

### HttpService
Interface to perform operations over the wire:
```java
public interface HttpService {
    Response get(String url) throws Exception;
    Response post(String requestBody) throws Exception;
    Response post(byte[] requestBody) throws Exception;
    Stream<String> postStream(String requestBody) throws Exception;
}
```

### AccessTokenSupplier
Strategy resolver for secure token acquisition:
```java
// Resolve appropriate supplier based on env
AccessTokenSupplier supplier = AccessTokenSupplier.autoDetect();
String bearerToken = supplier.getAccessToken();
```

---

## Dependency Configuration

To use `agentic-commons` in another Maven module within the ecosystem, add it to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.svetanis</groupId>
    <artifactId>agentic-commons</artifactId>
    <version>${project.version}</version>
</dependency>
```
