package model;

import java.util.HashMap;
import java.util.Map;

public class TreeBuilder {

    public static RecurrenceResult construirArvore(double tamanhoInicialDoProblema, int quantidadeDeSubproblemas, double divisorDoTamanhoDoSubproblema, double constanteDeTrabalho, double expoenteDoPolinomioDeTrabalho) {
        if (quantidadeDeSubproblemas < 1 || divisorDoTamanhoDoSubproblema <= 1 || tamanhoInicialDoProblema <= 0) {
            throw new IllegalArgumentException("Parâmetros inválidos");
        }
        Map<Integer, Double> custosPorNivelDaArvore = new HashMap<>();
        int[] contadorDeNos = {0};
        TreeNode raiz = gerarNos(tamanhoInicialDoProblema, quantidadeDeSubproblemas, divisorDoTamanhoDoSubproblema, constanteDeTrabalho, expoenteDoPolinomioDeTrabalho, 0, custosPorNivelDaArvore, contadorDeNos);
        String complexidade = calcularComplexidade(quantidadeDeSubproblemas, divisorDoTamanhoDoSubproblema, expoenteDoPolinomioDeTrabalho);
        return new RecurrenceResult(raiz, custosPorNivelDaArvore, complexidade);
    }

    private static TreeNode gerarNos(double tamanhoAtualDoProblema, int quantidadeDeSubproblemas, double divisorDoTamanhoDoSubproblema, double constanteDeTrabalho, double expoenteDoPolinomioDeTrabalho, int nivelDeProfundidadeDaArvore, Map<Integer, Double> custosPorNivelDaArvore, int[] contadorDeNos) {
        if (contadorDeNos[0] > 1000) {
            throw new RuntimeException("A árvore gerou mais de 1000 nós! Reduza os parâmetros para não travar o visualizador.");
        }
        contadorDeNos[0]++;
        double custoDeTrabalhoDoNo = constanteDeTrabalho * Math.pow(tamanhoAtualDoProblema, expoenteDoPolinomioDeTrabalho);
        TreeNode noDaArvore = new TreeNode(tamanhoAtualDoProblema, custoDeTrabalhoDoNo);
        
        custosPorNivelDaArvore.put(nivelDeProfundidadeDaArvore, custosPorNivelDaArvore.getOrDefault(nivelDeProfundidadeDaArvore, 0.0) + custoDeTrabalhoDoNo);
        
        if (tamanhoAtualDoProblema > 1) {
            double tamanhoDoProximoSubproblema = tamanhoAtualDoProblema / divisorDoTamanhoDoSubproblema;
            for (int indiceDoRamo = 0; indiceDoRamo < quantidadeDeSubproblemas; indiceDoRamo++) {
                noDaArvore.adicionarFilho(gerarNos(tamanhoDoProximoSubproblema, quantidadeDeSubproblemas, divisorDoTamanhoDoSubproblema, constanteDeTrabalho, expoenteDoPolinomioDeTrabalho, nivelDeProfundidadeDaArvore + 1, custosPorNivelDaArvore, contadorDeNos));
            }
        }
        return noDaArvore;
    }
    
    private static String calcularComplexidade(int quantidadeDeSubproblemas, double divisorDoTamanhoDoSubproblema, double expoenteDoPolinomioDeTrabalho) {
        double logaritmoBaseBDeA = Math.log(quantidadeDeSubproblemas) / Math.log(divisorDoTamanhoDoSubproblema);
        double toleranciaDeComparacaoDeFloat = 0.0001;
        
        if (Math.abs(logaritmoBaseBDeA - expoenteDoPolinomioDeTrabalho) < toleranciaDeComparacaoDeFloat) {
            return "O(n^" + String.format(java.util.Locale.US, "%.2f", expoenteDoPolinomioDeTrabalho) + " * log(n))";
        } else if (logaritmoBaseBDeA > expoenteDoPolinomioDeTrabalho) {
            return "O(n^" + String.format(java.util.Locale.US, "%.2f", logaritmoBaseBDeA) + ")";
        } else {
            return "O(n^" + String.format(java.util.Locale.US, "%.2f", expoenteDoPolinomioDeTrabalho) + ")";
        }
    }
}
