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
import java.util.Locale;
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
                sb.append("Nivel ").append(entry.getKey()).append(": ").append(String.format(Locale.US, "%.2f", entry.getValue())).append("\n");
            }
            
            sb.append("\nComplexidade Assintotica:\n");
            sb.append(result.getComplexity());
            
            resultArea.setText(sb.toString());
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato numerico invalido. Use valores numericos validos.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Entrada invalida: certifique-se de que a >= 1, b > 1 e n > 0.");
        }
    }
}
