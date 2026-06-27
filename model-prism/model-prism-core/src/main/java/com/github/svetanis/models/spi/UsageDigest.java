package com.github.svetanis.models.spi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.adk.events.Event;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.genai.types.GenerateContentResponseUsageMetadata;

public final class UsageDigest {

	private long promptTokens;
	private long candidatesTokens;
	private long totalTokens;
	private int llmCallCount;
	private final List<String> finishReasons = new ArrayList<>();
	private final Set<String> modelVersions = new LinkedHashSet<>();

	/* Records the LLM-response fields carried by {@code event}, if any. */
	public void record(Event event) {
		var usage = event.usageMetadata();
		if (usage.isEmpty()) {
			return;
		}
		llmCallCount++;
		GenerateContentResponseUsageMetadata m = usage.get();
		m.promptTokenCount().ifPresent(n -> promptTokens += n);
		m.candidatesTokenCount().ifPresent(n -> candidatesTokens += n);
		m.totalTokenCount().ifPresent(n -> totalTokens += n);
		event.finishReason().ifPresent(fr -> finishReasons.add(fr.toString()));
		event.modelVersion().ifPresent(modelVersions::add);
	}

	public long promptTokens() {
		return promptTokens;
	}

	public long candidatesTokens() {
		return candidatesTokens;
	}

	public long totalTokens() {
		return totalTokens;
	}

	public int llmCallCount() {
		return llmCallCount;
	}

	public ImmutableList<String> finishReasons() {
		return ImmutableList.copyOf(finishReasons);
	}

	public ImmutableSet<String> modelVersions() {
		return ImmutableSet.copyOf(modelVersions);
	}

	@Override
	public String toString() {
		return format();
	}

	private String format() {
		StringBuilder sb = new StringBuilder();
		sb.append("Usage digest:\n");
		sb.append(String.format(" LLM calls : %d%n", llmCallCount));
		sb.append(String.format(" prompt tokens : %d%n", promptTokens));
		sb.append(String.format(" output tokens : %d%n", candidatesTokens));
		sb.append(String.format(" total tokens : %d%n", totalTokens));
		sb.append(String.format(" finish reasons : %s%n", finishReasons.isEmpty() ? "(none)" : finishReasons));
		sb.append(String.format(" model versions : %s%n", modelVersions.isEmpty() ? "(none)" : modelVersions));
		return sb.toString();
	}
}
