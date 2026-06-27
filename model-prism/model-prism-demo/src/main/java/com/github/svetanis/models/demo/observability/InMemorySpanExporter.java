package com.github.svetanis.models.demo.observability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;

/**
 * Demo-scoped {@link SpanExporter} that retains exported spans in memory so the
 * demo can print a summary at the end of a run.
 * 
 * <p>
 * Not for production use - it grows without bound and is not thread-safe beyond
 * that the OTel SDK already guarantees for the single span-processor callback
 * path.
 */

public final class InMemorySpanExporter implements SpanExporter {

	private final List<SpanData> spans = Collections.synchronizedList(new ArrayList<>());

	@Override
	public CompletableResultCode export(Collection<SpanData> batch) {
		spans.addAll(batch);
		return CompletableResultCode.ofSuccess();
	}

	@Override
	public CompletableResultCode flush() {
		return CompletableResultCode.ofSuccess();
	}

	@Override
	public CompletableResultCode shutdown() {
		return CompletableResultCode.ofSuccess();
	}

	List<SpanData> captured() {
		synchronized (spans) {
			return List.copyOf(spans);
		}
	}
}
