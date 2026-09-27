package test;

import model.TreeBuilder;
import model.RecurrenceResult;

public class AlgoritmosFamososTest {

    public static void main(String[] args) {
        System.out.println("--- Testando Algoritmos Famosos (Teorema Mestre) ---");
        testarAlgoritmos();
        System.out.println("----------------------------------------------------");
        System.out.println("✅ Todos os algoritmos clássicos foram resolvidos corretamente!");
    }

    private static void testarAlgoritmos() {
        validarComplexidade("Busca Binária", 16, 1, 2, 1, 0, "log(n)");

        validarComplexidade("Merge Sort", 16, 2, 2, 1, 1, "O(n^1.00 * log(n))");

        validarComplexidade("Multipl. Matrizes (Padrão)", 16, 8, 2, 1, 2, "O(n^3.00)");

        validarComplexidade("Multipl. Matrizes (Strassen)", 16, 7, 2, 1, 2, "O(n^2.81)");

        validarComplexidade("Multipl. Inteiros (Karatsuba)", 16, 3, 2, 1, 1, "O(n^1.58)");

        validarComplexidade("Travessia de Árvore (DFS/BFS)", 16, 2, 2, 1, 0, "O(n^1.00)");

        validarComplexidade("Stooge Sort", 16, 3, 1.5, 1, 0, "O(n^2.71)");
    }

    private static void validarComplexidade(String nomeDoAlgoritmo, double tamanho, int a, double b, double c, double k, String textoEsperado) {
        RecurrenceResult resultado = TreeBuilder.construirArvore(tamanho, a, b, c, k);
        String complexidadeCalculada = resultado.getComplexidade();
        
        if (!complexidadeCalculada.contains(textoEsperado)) {
            throw new RuntimeException(String.format("Falha no algoritmo %s.\nEsperado conter: %s\nObteve: %s", nomeDoAlgoritmo, textoEsperado, complexidadeCalculada));
        } else {
            System.out.printf("✅ %-30s -> Complexidade: %s%n", nomeDoAlgoritmo, complexidadeCalculada);
        }
    }
}
