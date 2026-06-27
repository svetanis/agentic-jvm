package com.github.svetanis.models.spi.routing;

import com.google.common.base.Preconditions;

public record ModelMetadata(String fullModelName, double costPer1kPromptTokens, double costPer1kCompletionsTokens,
		long typicalLatencyMs, double qualityScore) {

	public ModelMetadata {
		Preconditions.checkNotNull(fullModelName, "fullModelName");
		Preconditions.checkArgument(costPer1kPromptTokens > 0, "costPer1kPromptTokens must be non-negative");
		Preconditions.checkArgument(costPer1kCompletionsTokens > 0, "costPer1kCompletionsTokens must be non-negative");
		Preconditions.checkArgument(typicalLatencyMs > 0, "typicalLatencyMs must be non-negative");
		Preconditions.checkArgument(qualityScore >= 0 && qualityScore <= 1.0, "qualityScore must be in [0.0, 1.0]");
	}

	public double combinedCostPer1kTokens() {
		return costPer1kPromptTokens + costPer1kCompletionsTokens;
	}
}
