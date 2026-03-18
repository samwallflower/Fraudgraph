package com.fraudgraph.dtos;

import lombok.Data;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class GraphDataResponse {

    private Map<String, GraphNode> nodes = new LinkedHashMap<>();
    private List<GraphEdge> edges = new ArrayList<>();

    public void addNode(String id, String type, String label, String riskLevel) {
        nodes.putIfAbsent(id, new GraphNode(id, type, label, riskLevel));
        if (riskLevel != null && nodes.containsKey(id)) {
            nodes.get(id).setRiskLevel(riskLevel);
        }
    }

    public void addEdge(String from, String to, String type) {
        edges.add(new GraphEdge(from, to, type));
    }

    @Data
    public static class GraphNode {
        private String id;
        private String type;
        private String label;
        private String riskLevel;

        public GraphNode(String id, String type, String label, String riskLevel) {
            this.id = id;
            this.type = type;
            this.label = label;
            this.riskLevel = riskLevel;
        }
    }

    @Data
    public static class GraphEdge {
        private String from;
        private String to;
        private String type;

        public GraphEdge(String from, String to, String type) {
            this.from = from;
            this.to = to;
            this.type = type;
        }
    }
}
