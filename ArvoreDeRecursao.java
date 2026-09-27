import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class TreeNode {
    private final double tamanho;
    private final double custo;
    private final List<TreeNode> filhos;

    TreeNode(double tamanho, double custo) {
        this.tamanho = tamanho;
        this.custo = custo;
        this.filhos = new ArrayList<>();
    }

    void adicionarFilho(TreeNode filho) {
        this.filhos.add(filho);
    }

    double getTamanho() {
        return tamanho;
    }

    double getCusto() {
        return custo;
    }

    List<TreeNode> getFilhos() {
        return filhos;
    }

    String toJson() {
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

class RecurrenceResult {
    private final TreeNode raiz;
    private final Map<Integer, Double> custosPorNivel;
    private final String complexidade;

    RecurrenceResult(TreeNode raiz, Map<Integer, Double> custosPorNivel, String complexidade) {
        this.raiz = raiz;
        this.custosPorNivel = custosPorNivel;
        this.complexidade = complexidade;
    }

    TreeNode getRaiz() { return raiz; }
    Map<Integer, Double> getCustosPorNivel() { return custosPorNivel; }
    String getComplexidade() { return complexidade; }
}

class TreeBuilder {

    static RecurrenceResult construirArvore(double tamanhoInicialDoProblema, int quantidadeDeSubproblemas, double divisorDoTamanhoDoSubproblema, double constanteDeTrabalho, double expoenteDoPolinomioDeTrabalho) {
        if (quantidadeDeSubproblemas < 1 || divisorDoTamanhoDoSubproblema <= 1 || tamanhoInicialDoProblema <= 0) {
            throw new IllegalArgumentException("Parametros invalidos");
        }
        Map<Integer, Double> custosPorNivelDaArvore = new HashMap<>();
        int[] contadorDeNos = {0};
        TreeNode raiz = gerarNos(tamanhoInicialDoProblema, quantidadeDeSubproblemas, divisorDoTamanhoDoSubproblema, constanteDeTrabalho, expoenteDoPolinomioDeTrabalho, 0, custosPorNivelDaArvore, contadorDeNos);
        String complexidade = calcularComplexidade(quantidadeDeSubproblemas, divisorDoTamanhoDoSubproblema, expoenteDoPolinomioDeTrabalho);
        return new RecurrenceResult(raiz, custosPorNivelDaArvore, complexidade);
    }

    private static TreeNode gerarNos(double tamanhoAtualDoProblema, int quantidadeDeSubproblemas, double divisorDoTamanhoDoSubproblema, double constanteDeTrabalho, double expoenteDoPolinomioDeTrabalho, int nivelDeProfundidadeDaArvore, Map<Integer, Double> custosPorNivelDaArvore, int[] contadorDeNos) {
        if (contadorDeNos[0] > 5000) {
            throw new RuntimeException("A arvore gerou mais de 5000 nos! Reduza os parametros.");
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

public class ArvoreDeRecursao {

    public static void main(String[] args) {
        Scanner leitorDeEntrada = new Scanner(System.in);

        System.out.println("=== Visualizador de Arvore de Recursao ===");
        System.out.println("Formula: T(n) = a * T(n/b) + c * n^k");
        System.out.println();

        System.out.print("Quantidade de subproblemas (a): ");
        int quantidadeDeSubproblemas = leitorDeEntrada.nextInt();

        System.out.print("Divisor do tamanho (b): ");
        double divisorDoTamanhoDoSubproblema = leitorDeEntrada.nextDouble();

        System.out.print("Constante de trabalho (c): ");
        double constanteDeTrabalho = leitorDeEntrada.nextDouble();

        System.out.print("Expoente do polinomio (k): ");
        double expoenteDoPolinomioDeTrabalho = leitorDeEntrada.nextDouble();

        System.out.print("Tamanho da entrada (n): ");
        double tamanhoInicialDoProblema = leitorDeEntrada.nextDouble();

        leitorDeEntrada.close();

        System.out.println();

        try {
            RecurrenceResult resultado = TreeBuilder.construirArvore(tamanhoInicialDoProblema, quantidadeDeSubproblemas, divisorDoTamanhoDoSubproblema, constanteDeTrabalho, expoenteDoPolinomioDeTrabalho);

            System.out.println("Complexidade: " + resultado.getComplexidade());
            System.out.println();

            System.out.println("Custos por nivel:");
            Map<Integer, Double> custosPorNivel = resultado.getCustosPorNivel();
            for (int nivel = 0; nivel < custosPorNivel.size(); nivel++) {
                System.out.printf("  Nivel %d: %.2f%n", nivel, custosPorNivel.get(nivel));
            }
            System.out.println();

            System.out.println("Arvore de recursao:");
            imprimirArvore(resultado.getRaiz(), "", true);

        } catch (Exception excecao) {
            System.out.println("Erro: " + excecao.getMessage());
        }
    }

    private static void imprimirArvore(TreeNode noDaArvore, String prefixo, boolean ehUltimoFilho) {
        String conectador = ehUltimoFilho ? "└── " : "├── ";
        System.out.printf("%s%sn=%.1f  custo=%.2f%n", prefixo, conectador, noDaArvore.getTamanho(), noDaArvore.getCusto());

        String novoPrefixo = prefixo + (ehUltimoFilho ? "    " : "│   ");
        List<TreeNode> filhos = noDaArvore.getFilhos();
        for (int indice = 0; indice < filhos.size(); indice++) {
            imprimirArvore(filhos.get(indice), novoPrefixo, indice == filhos.size() - 1);
        }
    }
}
