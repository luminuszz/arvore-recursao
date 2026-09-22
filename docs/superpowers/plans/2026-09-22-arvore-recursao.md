# Arvore de Recursao Implementation Plan

> **For agentic workers:** 
> - REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans to implement. 
> - REQUIRED SUB-SKILL: Use superpowers:dispatching-parallel-agents to execute Task 2 and Task 3 in PARALLEL since they are independent.
> - REQUIRED SUB-SKILL: Use ponytail:ponytail-review after EACH task to ensure zero comments and minimal code.
**Goal:** Desenvolver um sistema genérico com interface gráfica em Java (Swing) para resolver e visualizar Árvores de Recursão baseadas em parâmetros dinâmicos, exibindo custos por nível e a complexidade assintótica final.

**Architecture:** O sistema utilizará uma arquitetura MVC simples. O `Model` conterá a representação do nó da árvore e a lógica de construção e cálculo de custos/complexidade. A `View` usará `Java Swing` para exibir formulários de entrada e um painel de desenho customizado para renderizar a árvore hierarquicamente. O `Controller` mediará as entradas do usuário e a atualização da tela.

**Tech Stack:** Java SE, Java Swing (para GUI). Não haverá dependências externas para facilitar a execução acadêmica, usando ferramentas nativas.

**Spec:** Documento PDF do projeto (`projeto-doc.pdf`).

## Global Constraints

- Proibido Comentários: O código-fonte do sistema NÃO pode conter nenhum tipo de comentário.
- Clean Code: O código deve ser escrito de forma limpa e autoexplicativa.
- Generacidade: Sem valores hardcoded; os parâmetros da recorrência devem ser recebidos dinamicamente.
- Nomes de variáveis, métodos e classes devem refletir perfeitamente seu propósito, compensando a ausência de comentários.

---

### Task 1: Estrutura Base de Dados (Model)

**Files:**
- Create: `src/model/TreeNode.java`

**Interfaces:**
- Consumes: N/A
- Produces: `TreeNode` class with children list, node value (size `n`), cost value, and getters/setters.

- [ ] **Step 1: Write the minimal implementation**

```java
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

    public double getSize() { return size; }
    public double getCost() { return cost; }
    public List<TreeNode> getChildren() { return children; }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/model/TreeNode.java
git commit -m "feat: create TreeNode data structure"
```

### Task 2: Construtor da Árvore e Calculadora de Custos

Para mantermos a genericidade sem a complexidade de um parser de expressões completo em Java puro, vamos modelar $f(n) = c \cdot n^k$. Os inputs serão $a$ (ramificações), $b$ (fator de divisão), $c$ e $k$ (para $f(n)$), e o $n$ inicial.

**Files:**
- Create: `src/model/TreeBuilder.java`
- Create: `src/model/RecurrenceResult.java`

**Interfaces:**
- Consumes: `TreeNode`
- Produces: `TreeBuilder.buildTree(int n, int a, int b, double c, double k)`, `RecurrenceResult` class.

- [ ] **Step 1: Implement `RecurrenceResult`**

```java
package model;

import java.util.Map;

public class RecurrenceResult {
    private final TreeNode root;
    private final Map<Integer, Double> levelCosts;
    private final String complexity;

    public RecurrenceResult(TreeNode root, Map<Integer, Double> levelCosts, String complexity) {
        this.root = root;
        this.levelCosts = levelCosts;
        this.complexity = complexity;
    }

    public TreeNode getRoot() { return root; }
    public Map<Integer, Double> getLevelCosts() { return levelCosts; }
    public String getComplexity() { return complexity; }
}
```

- [ ] **Step 2: Implement `TreeBuilder` with basic structure**

```java
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
```

- [ ] **Step 3: Commit**

```bash
git add src/model/TreeBuilder.java src/model/RecurrenceResult.java
git commit -m "feat: implement tree builder and complexity calculator"
```

### Task 3: Painel de Desenho da Árvore (Visualização)

**Files:**
- Create: `src/gui/TreePanel.java`

**Interfaces:**
- Consumes: `TreeNode`
- Produces: `TreePanel` UI component extending `JPanel`.

- [ ] **Step 1: Implement `TreePanel` drawing logic**

```java
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
```

- [ ] **Step 2: Commit**

```bash
git add src/gui/TreePanel.java
git commit -m "feat: create custom panel for rendering the tree"
```

### Task 4: Interface Principal (Formulário e Janela)

**Files:**
- Create: `src/gui/MainWindow.java`

**Interfaces:**
- Consumes: `TreeBuilder`, `TreePanel`
- Produces: `MainWindow` UI component extending `JFrame`.

- [ ] **Step 1: Implement `MainWindow`**

```java
package gui;

import model.TreeBuilder;
import model.RecurrenceResult;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Map;

public class MainWindow extends JFrame {
    private JTextField fieldN, fieldA, fieldB, fieldC, fieldK;
    private TreePanel treePanel;
    private JTextArea resultArea;

    public MainWindow() {
        setTitle("Visualizador de Arvore de Recursao");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 6, 5, 5));
        
        fieldA = new JTextField("2");
        fieldB = new JTextField("2");
        fieldC = new JTextField("1");
        fieldK = new JTextField("1");
        fieldN = new JTextField("16");
        
        inputPanel.add(new JLabel("a:"));
        inputPanel.add(fieldA);
        inputPanel.add(new JLabel("b:"));
        inputPanel.add(fieldB);
        inputPanel.add(new JLabel("c (de c*n^k):"));
        inputPanel.add(fieldC);
        inputPanel.add(new JLabel("k (de c*n^k):"));
        inputPanel.add(fieldK);
        inputPanel.add(new JLabel("N inicial:"));
        inputPanel.add(fieldN);
        
        JButton btnSolve = new JButton("Calcular e Desenhar");
        btnSolve.addActionListener(e -> solveRecurrence());
        inputPanel.add(btnSolve);
        
        treePanel = new TreePanel();
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(treePanel), new JScrollPane(resultArea));
        splitPane.setResizeWeight(0.7);
        
        add(inputPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void solveRecurrence() {
        try {
            int a = Integer.parseInt(fieldA.getText());
            double b = Double.parseDouble(fieldB.getText());
            double c = Double.parseDouble(fieldC.getText());
            double k = Double.parseDouble(fieldK.getText());
            double n = Double.parseDouble(fieldN.getText());
            
            RecurrenceResult result = TreeBuilder.buildTree(n, a, b, c, k);
            treePanel.setRoot(result.getRoot());
            
            StringBuilder sb = new StringBuilder();
            sb.append("T(n) = ").append(a).append("T(n/").append(b).append(") + ").append(c).append("*n^").append(k).append("\n\n");
            
            sb.append("Custos por Nivel:\n");
            for (Map.Entry<Integer, Double> entry : result.getLevelCosts().entrySet()) {
                sb.append("Nivel ").append(entry.getKey()).append(": ").append(String.format("%.2f", entry.getValue())).append("\n");
            }
            
            sb.append("\nComplexidade Assintotica:\n");
            sb.append(result.getComplexity());
            
            resultArea.setText(sb.toString());
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores numericos validos.");
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/gui/MainWindow.java
git commit -m "feat: create main window UI with inputs and bindings"
```

### Task 5: Ponto de Entrada da Aplicação

**Files:**
- Create: `src/Main.java`

**Interfaces:**
- Consumes: `MainWindow`
- Produces: Executable application.

- [ ] **Step 1: Implement `Main`**

```java
import gui.MainWindow;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/Main.java
git commit -m "feat: add application entry point"
```
