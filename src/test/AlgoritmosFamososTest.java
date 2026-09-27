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
        // 1. Busca Binária: T(n) = 1T(n/2) + O(1)
        // a=1, b=2, k=0 (constante de trabalho c=1)
        // Log_2(1) = 0 == k -> O(n^0.00 * log(n)) -> Que se traduz para O(log n)
        validarComplexidade("Busca Binária", 16, 1, 2, 1, 0, "log(n)");

        // 2. Merge Sort / Quick Sort (Caso Médio): T(n) = 2T(n/2) + O(n)
        // a=2, b=2, k=1 
        // Log_2(2) = 1 == k -> O(n^1.00 * log(n))
        validarComplexidade("Merge Sort", 16, 2, 2, 1, 1, "O(n^1.00 * log(n))");

        // 3. Multiplicação de Matriz Padrão: T(n) = 8T(n/2) + O(n^2)
        // a=8, b=2, k=2
        // Log_2(8) = 3 > 2 -> O(n^3.00)
        validarComplexidade("Multipl. Matrizes (Padrão)", 16, 8, 2, 1, 2, "O(n^3.00)");

        // 4. Algoritmo de Strassen: T(n) = 7T(n/2) + O(n^2)
        // a=7, b=2, k=2
        // Log_2(7) = 2.81 > 2 -> O(n^2.81)
        validarComplexidade("Multipl. Matrizes (Strassen)", 16, 7, 2, 1, 2, "O(n^2.81)");

        // 5. Algoritmo de Karatsuba: T(n) = 3T(n/2) + O(n)
        // a=3, b=2, k=1
        // Log_2(3) = 1.58 > 1 -> O(n^1.58)
        validarComplexidade("Multipl. Inteiros (Karatsuba)", 16, 3, 2, 1, 1, "O(n^1.58)");

        // 6. Travessia Completa de Árvore Binária (DFS): T(n) = 2T(n/2) + O(1)
        // a=2, b=2, k=0
        // Log_2(2) = 1 > 0 -> O(n^1.00) -> Ou seja, O(n)
        validarComplexidade("Travessia de Árvore (DFS/BFS)", 16, 2, 2, 1, 0, "O(n^1.00)");

        // 7. Stooge Sort: T(n) = 3T(2n/3) + O(1)
        // a=3, b=1.5 (pois n / (3/2)), k=0
        // Log_1.5(3) = 2.71 -> O(n^2.71)
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
