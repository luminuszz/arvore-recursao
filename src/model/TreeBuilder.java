package model;

import java.util.HashMap;
import java.util.Map;

public class TreeBuilder {

    public static RecurrenceResult buildTree(double n, int a, double b, double c, double k) {
        Map<Integer, Double> levelCosts = new HashMap<>();
        TreeNode root = generateNodes(n, a, b, c, k, 0, levelCosts);
        String complexity = calculateComplexity(a, b, k);
        return new RecurrenceResult(root, levelCosts, complexity);
    }

    private static TreeNode generateNodes(double n, int a, double b, double c, double k, int level, Map<Integer, Double> levelCosts) {
        double cost = c * Math.pow(n, k);
        TreeNode node = new TreeNode(n, cost);
        
        levelCosts.put(level, levelCosts.getOrDefault(level, 0.0) + cost);
        
        if (n > 1) {
            double nextSize = n / b;
            for (int i = 0; i < a; i++) {
                node.addChild(generateNodes(nextSize, a, b, c, k, level + 1, levelCosts));
            }
        }
        return node;
    }
    
    private static String calculateComplexity(int a, double b, double k) {
        double logBofA = Math.log(a) / Math.log(b);
        double epsilon = 0.0001;
        
        if (Math.abs(logBofA - k) < epsilon) {
            return "O(n^" + String.format("%.2f", k) + " * log(n))";
        } else if (logBofA > k) {
            return "O(n^" + String.format("%.2f", logBofA) + ")";
        } else {
            return "O(n^" + String.format("%.2f", k) + ")";
        }
    }
}
