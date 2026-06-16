# Agentic JVM

An ecosystem of pluggable libraries, model providers, and testing utilities designed to simplify, scale, and evaluate AI agent applications on the JVM. This repository primarily extends and integrates with the [Google ADK for Java](https://github.com/google/adk-java).

---

## Repository Tagline / Description
> **An ecosystem of pluggable model providers, tool registries, and testing frameworks for building and evaluating robust AI agents on the JVM.**

---

## Active Modules

Currently, the aggregator [pom.xml](pom.xml) activates the following two core modules:

### 1. [`agentic-commons`](agentic-commons/README.md)
* **Artifact:** `agentic-commons`
* **README:** [agentic-commons/README.md](agentic-commons/README.md)
* **Purpose:** Provides reusable utilities for HTTP transport and security/authentication context when interfacing with GenAI endpoints.
* **Key Features:**
  * Unified `HttpService` and `HttpClientProvider` wrappers.
  * Google Cloud / Vertex AI token management via `AccessTokenSupplier` (supporting both Application Default Credentials (ADC) and environment-variable configurations).
  * Common serialization and codec helpers.

### 2. [`model-prism`](model-prism/README.md)
* **Artifact:** `model-prism`
* **README:** [model-prism/README.md](model-prism/README.md)
* **Submodules:** `model-prism-core`, `model-prism-groq`, `model-prism-ollama`, `model-prism-openrouter`, `model-prism-vertex-gemini`, `model-prism-vertex-openai`, `model-prism-web`
* **Purpose:** A pluggable LLM provider SPI and registry using Java `ServiceLoader`.
* **Key Features:**
  * **Zero Boilerplate Discovery:** Simply add a provider dependency to your classpath (e.g. Groq, Ollama, OpenRouter, Vertex AI) and use model ID strings like `"groq/llama-3.1-8b-instant"` or `"ollama/llama3"` transparently.
  * **Multi-Agent Optimization:** Seamlessly route tasks to different providers (e.g. Gemini for search/grounding, fast hosted Groq models for heavy generation, and local Ollama models for classification/formatting) to minimize API costs.
  * **JSON Schema Compliance:** Fixes strict schema typing constraints required by alternative OpenAI-compatible backends (e.g. lowercasing schema types) that would otherwise trigger HTTP 400 validation errors in standard ADK serializations.

---

## Building and running

### Prerequisites
* Java 25 or higher
* Maven 3.9+
* Compilation requires the `-parameters` flag (pre-configured in POMs) to preserve method argument names for tool schema generation.

### Build the Active Modules
Run the following Maven command from the repository root:
```bash
mvn clean install
```

---

## License

This project is licensed under the Apache License 2.0. See the `LICENSE` file for details.
