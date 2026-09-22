package model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    private final double tamanho;
    private final double custo;
    private final List<TreeNode> filhos;

    public TreeNode(double tamanho, double custo) {
        this.tamanho = tamanho;
        this.custo = custo;
        this.filhos = new ArrayList<>();
    }

    public void adicionarFilho(TreeNode filho) {
        this.filhos.add(filho);
    }

    public double getTamanho() {
        return tamanho;
    }

    public double getCusto() {
        return custo;
    }

    public List<TreeNode> getFilhos() {
        return java.util.Collections.unmodifiableList(filhos);
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"tamanho\":").append(tamanho).append(",");
        sb.append("\"custo\":").append(custo).append(",");
        sb.append("\"filhos\":[");
        for (int i = 0; i < filhos.size(); i++) {
            sb.append(filhos.get(i).toJson());
            if (i < filhos.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
}
