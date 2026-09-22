package gui;

import model.TreeNode;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.util.List;

public class TreePanel extends JPanel {
    private TreeNode root;
    private static final int NODE_RADIUS = 20;
    private static final int VERTICAL_GAP = 80;

    public void setRoot(TreeNode root) {
        this.root = root;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawTree(g2d, root, getWidth() / 2, 50, getWidth() / 4);
    }

    private void drawTree(Graphics2D g2d, TreeNode node, int x, int y, int horizontalGap) {
        String text = String.format("c(%.1f)", node.getSize());
        g2d.setColor(Color.BLACK);
        
        List<TreeNode> children = node.getChildren();
        int childCount = children.size();
        
        if (childCount > 0) {
            int startX = x - (horizontalGap * (childCount - 1)) / 2;
            for (int i = 0; i < childCount; i++) {
                int childX = startX + (i * horizontalGap);
                int childY = y + VERTICAL_GAP;
                g2d.drawLine(x, y + NODE_RADIUS, childX, childY - NODE_RADIUS);
                drawTree(g2d, children.get(i), childX, childY, horizontalGap / 2);
            }
        }
        
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x - fm.stringWidth(text) / 2;
        int textY = y + fm.getAscent() / 2 - 2;
        g2d.drawString(text, textX, textY);
    }
}
