package model;

import java.util.Map;

public class RecurrenceResult {
    private final TreeNode root;
    private final Map<Integer, Double> levelCosts;
    private final String complexity;

    public RecurrenceResult(TreeNode root, Map<Integer, Double> levelCosts, String complexity) {
        this.root = root;
        this.levelCosts = new java.util.HashMap<>(levelCosts);
        this.complexity = complexity;
    }

    public TreeNode getRoot() { return root; }
    public Map<Integer, Double> getLevelCosts() { return java.util.Collections.unmodifiableMap(levelCosts); }
    public String getComplexity() { return complexity; }
}
