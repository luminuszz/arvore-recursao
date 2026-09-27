package test;

import model.TreeBuilder;
import model.TreeNode;
import model.RecurrenceResult;

public class TreeBuilderTest {

    public static void main(String[] args) {
        testaMergeSort();
        testaBuscaBinaria();
        testaStrassen();
        testaKaratsuba();
        testaExcecaoComValoresInvalidos();
        System.out.println("TreeBuilderTest: TODOS OS TESTES PASSARAM.");
    }

    private static void testaMergeSort() {
        RecurrenceResult resultado = TreeBuilder.construirArvore(16, 2, 2, 1, 1);
        TreeNode raiz = resultado.getRaiz();
        
        if (raiz.getTamanho() != 16) throw new RuntimeException("Falha no tamanho da raiz");
        if (raiz.getFilhos().size() != 2) throw new RuntimeException("Falha na quantidade de filhos");
        if (raiz.getFilhos().get(0).getTamanho() != 8) throw new RuntimeException("Falha no tamanho do filho");
        
        if (!resultado.getComplexidade().contains("O(n^1.00 * log(n))")) {
            throw new RuntimeException("Complexidade incorreta para Merge Sort: " + resultado.getComplexidade());
        }
    }

    private static void testaBuscaBinaria() {
        RecurrenceResult resultado = TreeBuilder.construirArvore(16, 1, 2, 1, 0);
        TreeNode raiz = resultado.getRaiz();
        
        if (raiz.getTamanho() != 16) throw new RuntimeException("Falha no tamanho da raiz");
        if (raiz.getFilhos().size() != 1) throw new RuntimeException("Falha na quantidade de filhos");
        if (raiz.getFilhos().get(0).getTamanho() != 8) throw new RuntimeException("Falha no tamanho do filho");
        
        if (!resultado.getComplexidade().contains("log(n)")) {
            throw new RuntimeException("Complexidade incorreta para Busca Binaria: " + resultado.getComplexidade());
        }
    }
    
    private static void testaStrassen() {
        RecurrenceResult resultado = TreeBuilder.construirArvore(16, 7, 2, 1, 2);
        TreeNode raiz = resultado.getRaiz();
        
        if (raiz.getTamanho() != 16) throw new RuntimeException("Falha no tamanho da raiz em Strassen");
        if (raiz.getFilhos().size() != 7) throw new RuntimeException("Falha na quantidade de filhos em Strassen");
        if (raiz.getFilhos().get(0).getTamanho() != 8) throw new RuntimeException("Falha no tamanho do filho em Strassen");
        
        if (!resultado.getComplexidade().contains("O(n^2.81)")) {
            throw new RuntimeException("Complexidade incorreta para Strassen: " + resultado.getComplexidade());
        }
    }

    private static void testaKaratsuba() {
        RecurrenceResult resultado = TreeBuilder.construirArvore(16, 3, 2, 1, 1);
        TreeNode raiz = resultado.getRaiz();
        
        if (raiz.getTamanho() != 16) throw new RuntimeException("Falha no tamanho da raiz em Karatsuba");
        if (raiz.getFilhos().size() != 3) throw new RuntimeException("Falha na quantidade de filhos em Karatsuba");
        if (raiz.getFilhos().get(0).getTamanho() != 8) throw new RuntimeException("Falha no tamanho do filho em Karatsuba");
        
        if (!resultado.getComplexidade().contains("O(n^1.58)")) {
            throw new RuntimeException("Complexidade incorreta para Karatsuba: " + resultado.getComplexidade());
        }
    }
    
    private static void testaExcecaoComValoresInvalidos() {
        try {
            TreeBuilder.construirArvore(16, 0, 2, 1, 1);
            throw new RuntimeException("Deveria ter lancado excecao por quantidade de subproblemas invalida");
        } catch (IllegalArgumentException e) {
        }
        
        try {
            TreeBuilder.construirArvore(16, 2, 1, 1, 1);
            throw new RuntimeException("Deveria ter lancado excecao por divisor de tamanho invalido");
        } catch (IllegalArgumentException e) {
        }
    }
}
