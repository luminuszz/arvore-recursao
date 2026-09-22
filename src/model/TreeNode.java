package model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    private final double size;
    private final double cost;
    private final List<TreeNode> children;

    public TreeNode(double size, double cost) {
        this.size = size;
        this.cost = cost;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        this.children.add(child);
    }

    public double getSize() {
        return size;
    }

    public double getCost() {
        return cost;
    }

    public List<TreeNode> getChildren() {
        return children;
    }
}
