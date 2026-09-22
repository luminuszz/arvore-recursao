package test;

import model.TreeNode;

public class TreeNodeTest {

    public static void main(String[] args) {
        testaCriacaoDeNo();
        testaAdicaoDeFilhos();
        testaToJson();
        System.out.println("TreeNodeTest: TODOS OS TESTES PASSARAM.");
    }

    private static void testaCriacaoDeNo() {
        TreeNode no = new TreeNode(16.0, 50.0);
        if (no.getTamanho() != 16.0) throw new RuntimeException("Tamanho do no incorreto");
        if (no.getCusto() != 50.0) throw new RuntimeException("Custo do no incorreto");
        if (no.getFilhos() == null || !no.getFilhos().isEmpty()) throw new RuntimeException("Lista de filhos deve iniciar vazia");
    }

    private static void testaAdicaoDeFilhos() {
        TreeNode raiz = new TreeNode(16.0, 50.0);
        TreeNode filho1 = new TreeNode(8.0, 25.0);
        TreeNode filho2 = new TreeNode(8.0, 25.0);
        
        raiz.adicionarFilho(filho1);
        raiz.adicionarFilho(filho2);
        
        if (raiz.getFilhos().size() != 2) throw new RuntimeException("Quantidade de filhos incorreta");
        if (raiz.getFilhos().get(0) != filho1) throw new RuntimeException("Filho 1 incorreto");
        if (raiz.getFilhos().get(1) != filho2) throw new RuntimeException("Filho 2 incorreto");
    }
    
    private static void testaToJson() {
        TreeNode no = new TreeNode(2.0, 4.0);
        String json = no.toJson();
        if (!json.contains("\"tamanho\":2.0") || !json.contains("\"custo\":4.0")) {
            throw new RuntimeException("Erro na conversao toJson: " + json);
        }
    }
}
