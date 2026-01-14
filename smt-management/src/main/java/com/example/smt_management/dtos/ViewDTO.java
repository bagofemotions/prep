package com.example.smt_management.dtos;

import java.util.List;

public class ViewDTO {
	// Project selector
	public record ProjectView(String key, String name) {}

	// Executive
	public record DimensionSummaryView(
	    String key,
	    String name,
	    int score,
	    String label
	) {}

	public record ExecutiveView(
	    String projectKey,
	    String projectName,
	    int overallScore,
	    String direction,
	    List<DimensionSummaryView> dimensions,
	    List<String> risks,
	    String summary
	) {}

	// Dimension drill-down
	public record MetricView(
	    String displayName,
	    String value
	) {}

	public record DimensionDetailView(
	    String projectKey,
	    String dimensionKey,
	    String dimensionName,
	    int score,
	    String label,
	    List<MetricView> metrics,
	    List<String> rules,
	    String explanation
	) {}


}
