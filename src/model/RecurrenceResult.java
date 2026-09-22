package model;

import java.util.Map;

public class RecurrenceResult {
    private final TreeNode raiz;
    private final Map<Integer, Double> custosPorNivel;
    private final String complexidade;

    public RecurrenceResult(TreeNode raiz, Map<Integer, Double> custosPorNivel, String complexidade) {
        this.raiz = raiz;
        this.custosPorNivel = new java.util.HashMap<>(custosPorNivel);
        this.complexidade = complexidade;
    }

    public TreeNode getRaiz() { return raiz; }
    public Map<Integer, Double> getCustosPorNivel() { return java.util.Collections.unmodifiableMap(custosPorNivel); }
    public String getComplexidade() { return complexidade; }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"complexidade\":\"").append(complexidade.replace("\"", "\\\"")).append("\",");
        sb.append("\"custosPorNivel\":{");
        int count = 0;
        for (Map.Entry<Integer, Double> entry : custosPorNivel.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
            if (++count < custosPorNivel.size()) {
                sb.append(",");
            }
        }
        sb.append("},");
        sb.append("\"raiz\":").append(raiz.toJson());
        sb.append("}");
        return sb.toString();
    }
}
