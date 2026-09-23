# Visualizador de Arvore de Recursao

Este projeto resolve relacoes de recorrencia gerando uma arvore visual. Ele calcula o custo de algoritmos de divisao e conquista (como Merge Sort ou Strassen) e desenha cada etapa do particionamento no navegador.

O codigo fonte nao possui comentarios por restricao do desafio original. Os nomes das variaveis e classes explicam o comportamento.

## Estrutura do Projeto

```
src/
├── Main.java                  # Ponto de entrada da aplicacao
├── adapter/
│   └── HttpAdapter.java       # Servidor HTTP nativo (porta 8081)
├── model/
│   ├── TreeNode.java          # No individual da arvore
│   ├── TreeBuilder.java       # Motor matematico (recursao + Teorema Mestre)
│   └── RecurrenceResult.java  # Empacota o resultado completo
├── test/
│   ├── TreeNodeTest.java      # Testes unitarios do no
│   └── TreeBuilderTest.java   # Testes unitarios da construcao
└── web/
    └── index.html             # Frontend (Canvas + Tailwind + animacao)
```

## A Formula

O sistema trabalha com a formula geral de recorrencia:

```
T(n) = a * T(n / b) + c * n^k
```

Cada letra corresponde a um parametro no codigo:

| Simbolo | Parametro no codigo                  | Significado                             |
|---------|--------------------------------------|-----------------------------------------|
| `a`     | `quantidadeDeSubproblemas`           | Quantos subproblemas a recursao gera    |
| `b`     | `divisorDoTamanhoDoSubproblema`      | Por quanto o tamanho do problema encolhe|
| `c`     | `constanteDeTrabalho`                | Constante multiplicativa do custo local |
| `k`     | `expoenteDoPolinomioDeTrabalho`      | Expoente do custo fora da recursao      |
| `n`     | `tamanhoInicialDoProblema`           | Tamanho da entrada inicial              |

---

## Classes do Backend

### 1. TreeNode (o no da arvore)

Cada no guarda o tamanho do subproblema naquele ponto e o custo de processamento local. Ele tambem mantem uma lista de filhos.

```java
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
}
```

> **Arquivo:** [`TreeNode.java`](src/model/TreeNode.java)

O projeto proibe bibliotecas externas como Gson ou Jackson. Por isso, o metodo `toJson()` monta o JSON na mao, percorrendo a lista de filhos recursivamente:

```java
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
```

Para um Merge Sort com `n=4`, essa funcao produz algo como:

```json
{
  "tamanho": 4.0,
  "custo": 4.0,
  "filhos": [
    {
      "tamanho": 2.0,
      "custo": 2.0,
      "filhos": [
        { "tamanho": 1.0, "custo": 1.0, "filhos": [] },
        { "tamanho": 1.0, "custo": 1.0, "filhos": [] }
      ]
    },
    {
      "tamanho": 2.0,
      "custo": 2.0,
      "filhos": [
        { "tamanho": 1.0, "custo": 1.0, "filhos": [] },
        { "tamanho": 1.0, "custo": 1.0, "filhos": [] }
      ]
    }
  ]
}
```

---

### 2. TreeBuilder (construcao da arvore)

Esta classe recebe os parametros da formula, constroi a arvore recursivamente e calcula a complexidade pelo Teorema Mestre.

> **Arquivo:** [`TreeBuilder.java`](src/model/TreeBuilder.java)

#### Ponto de entrada

A funcao `construirArvore` valida os parametros, dispara a recursao e retorna o resultado empacotado:

```java
public static RecurrenceResult construirArvore(
        double tamanhoInicialDoProblema,
        int quantidadeDeSubproblemas,
        double divisorDoTamanhoDoSubproblema,
        double constanteDeTrabalho,
        double expoenteDoPolinomioDeTrabalho) {

    if (quantidadeDeSubproblemas < 1
        || divisorDoTamanhoDoSubproblema <= 1
        || tamanhoInicialDoProblema <= 0) {
        throw new IllegalArgumentException("Parametros invalidos");
    }

    Map<Integer, Double> custosPorNivelDaArvore = new HashMap<>();
    int[] contadorDeNos = {0};

    TreeNode raiz = gerarNos(
        tamanhoInicialDoProblema, quantidadeDeSubproblemas,
        divisorDoTamanhoDoSubproblema, constanteDeTrabalho,
        expoenteDoPolinomioDeTrabalho, 0,
        custosPorNivelDaArvore, contadorDeNos
    );

    String complexidade = calcularComplexidade(
        quantidadeDeSubproblemas, divisorDoTamanhoDoSubproblema,
        expoenteDoPolinomioDeTrabalho
    );

    return new RecurrenceResult(raiz, custosPorNivelDaArvore, complexidade);
}
```

O `contadorDeNos` usa um array de um elemento (`int[]`) em vez de um `int` simples. Variaveis primitivas em Java sao passadas por valor, entao um `int` comum nao funcionaria aqui. O array permite que todas as chamadas recursivas incrementem o mesmo contador.

#### Geracao recursiva dos nos

A funcao `gerarNos` se chama recursivamente ate que o tamanho do subproblema chegue a 1 ou menos:

```java
private static TreeNode gerarNos(
        double tamanhoAtualDoProblema,
        int quantidadeDeSubproblemas,
        double divisorDoTamanhoDoSubproblema,
        double constanteDeTrabalho,
        double expoenteDoPolinomioDeTrabalho,
        int nivelDeProfundidadeDaArvore,
        Map<Integer, Double> custosPorNivelDaArvore,
        int[] contadorDeNos) {

    if (contadorDeNos[0] > 1000) {
        throw new RuntimeException(
            "A arvore gerou mais de 1000 nos!"
        );
    }
    contadorDeNos[0]++;

    double custoDeTrabalhoDoNo = constanteDeTrabalho
        * Math.pow(tamanhoAtualDoProblema, expoenteDoPolinomioDeTrabalho);

    TreeNode noDaArvore = new TreeNode(
        tamanhoAtualDoProblema, custoDeTrabalhoDoNo
    );

    custosPorNivelDaArvore.put(
        nivelDeProfundidadeDaArvore,
        custosPorNivelDaArvore.getOrDefault(
            nivelDeProfundidadeDaArvore, 0.0
        ) + custoDeTrabalhoDoNo
    );

    if (tamanhoAtualDoProblema > 1) {
        double tamanhoDoProximoSubproblema =
            tamanhoAtualDoProblema / divisorDoTamanhoDoSubproblema;

        for (int indiceDoRamo = 0;
             indiceDoRamo < quantidadeDeSubproblemas;
             indiceDoRamo++) {
            noDaArvore.adicionarFilho(
                gerarNos(tamanhoDoProximoSubproblema, ...)
            );
        }
    }

    return noDaArvore;
}
```

O que acontece passo a passo com Merge Sort (`a=2, b=2, k=1, n=8`):

```
Nivel 0:  n=8  custo = 1 * 8^1 = 8     -> cria 2 filhos com n=4
Nivel 1:  n=4  custo = 1 * 4^1 = 4     -> cria 2 filhos com n=2 (x2 nos)
Nivel 2:  n=2  custo = 1 * 2^1 = 2     -> cria 2 filhos com n=1 (x4 nos)
Nivel 3:  n=1  custo = 1 * 1^1 = 1     -> sem filhos (caso base)
```

O mapa `custosPorNivelDaArvore` acumula a soma dos custos em cada nivel:

```
Nivel 0:  8
Nivel 1:  4 + 4 = 8
Nivel 2:  2 + 2 + 2 + 2 = 8
Nivel 3:  1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 = 8
```

Cada nivel soma 8. Sao `log2(8) = 3` niveis. Custo total: `8 * 3 = 24`, que corresponde a `O(n log n)`.

#### Calculo da complexidade (Teorema Mestre)

O Teorema Mestre compara `log_b(a)` com `k` para decidir qual dos tres casos se aplica:

```java
private static String calcularComplexidade(
        int quantidadeDeSubproblemas,
        double divisorDoTamanhoDoSubproblema,
        double expoenteDoPolinomioDeTrabalho) {

    double logaritmoBaseBDeA = Math.log(quantidadeDeSubproblemas)
        / Math.log(divisorDoTamanhoDoSubproblema);

    double toleranciaDeComparacaoDeFloat = 0.0001;

    if (Math.abs(logaritmoBaseBDeA - expoenteDoPolinomioDeTrabalho)
            < toleranciaDeComparacaoDeFloat) {
        // Caso 2: log_b(a) == k
        return "O(n^... * log(n))";
    } else if (logaritmoBaseBDeA > expoenteDoPolinomioDeTrabalho) {
        // Caso 1: log_b(a) > k
        return "O(n^log_b(a))";
    } else {
        // Caso 3: log_b(a) < k
        return "O(n^k)";
    }
}
```

| Caso | Condicao           | Resultado              | Exemplo           |
|------|--------------------|------------------------|--------------------|
| 1    | `log_b(a) > k`     | `O(n^log_b(a))`       | Strassen: `O(n^2.81)` |
| 2    | `log_b(a) == k`    | `O(n^k * log n)`      | Merge Sort: `O(n log n)` |
| 3    | `log_b(a) < k`     | `O(n^k)`              | (custo local domina) |

A comparacao usa uma tolerancia de `0.0001` em vez de `==` direto porque numeros de ponto flutuante em Java quase nunca sao exatamente iguais.

---

### 3. RecurrenceResult (o resultado)

Junta a raiz da arvore, o mapa de custos por nivel e a string de complexidade em um unico objeto que o `HttpAdapter` serializa para JSON.

> **Arquivo:** [`RecurrenceResult.java`](src/model/RecurrenceResult.java)

```java
public class RecurrenceResult {
    private final TreeNode raiz;
    private final Map<Integer, Double> custosPorNivel;
    private final String complexidade;

    public RecurrenceResult(
            TreeNode raiz,
            Map<Integer, Double> custosPorNivel,
            String complexidade) {
        this.raiz = raiz;
        this.custosPorNivel = new java.util.HashMap<>(custosPorNivel);
        this.complexidade = complexidade;
    }
}
```

O construtor faz uma copia defensiva do mapa (`new HashMap<>(custosPorNivel)`) para que alteracoes externas nao afetem o resultado ja calculado. O getter tambem devolve uma versao somente leitura:

```java
public Map<Integer, Double> getCustosPorNivel() {
    return java.util.Collections.unmodifiableMap(custosPorNivel);
}
```

---

### 4. HttpAdapter (o servidor)

Um servidor HTTP construido com `com.sun.net.httpserver`, que ja vem embutido no JDK.

> **Arquivo:** [`HttpAdapter.java`](src/adapter/HttpAdapter.java)

Ele registra duas rotas:

```java
public static void startServer(int port) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

    server.createContext("/", new StaticFileHandler());
    server.createContext("/api/solve", new SolveHandler());

    server.setExecutor(null);
    server.start();
}
```

| Rota           | Handler             | O que faz                                  |
|----------------|---------------------|---------------------------------------------|
| `/`            | `StaticFileHandler` | Serve o `index.html` da pasta `src/web/`   |
| `/api/solve`   | `SolveHandler`      | Recebe os parametros, chama `TreeBuilder` e devolve JSON |

O `SolveHandler` extrai os parametros da URL, chama `construirArvore`, e devolve o JSON da arvore:

```java
int quantidadeDeSubproblemas = Integer.parseInt(
    params.get("quantidadeDeSubproblemas")
);
double divisorDoTamanhoDoSubproblema = Double.parseDouble(
    params.get("divisorDoTamanhoDoSubproblema")
);

RecurrenceResult resultado = TreeBuilder.construirArvore(
    tamanhoInicialDoProblema,
    quantidadeDeSubproblemas,
    divisorDoTamanhoDoSubproblema,
    constanteDeTrabalho,
    expoenteDoPolinomioDeTrabalho
);

String respostaJson = resultado.toJson();
```

Se a chamada falhar, o catch devolve um JSON de erro com status 400:

```java
} catch (Exception e) {
    String response = "{\"error\":\"" + e.getMessage() + "\"}";
    exchange.sendResponseHeaders(400, response.getBytes().length);
}
```

---

## Frontend: Animacao da Arvore

> **Arquivo:** [`index.html`](src/web/index.html)

### Chamada a API

Quando o usuario clica em "Gerar Arvore", o frontend monta a URL com todos os parametros e chama o backend:

```javascript
const triggerCompute = async () => {
    const url = `/api/solve`
        + `?quantidadeDeSubproblemas=${inputA.value}`
        + `&divisorDoTamanhoDoSubproblema=${inputB.value}`
        + `&constanteDeTrabalho=${inputC.value}`
        + `&expoenteDoPolinomioDeTrabalho=${inputK.value}`
        + `&tamanhoInicialDoProblema=${sliderN.value}`;

    const response = await fetch(url);
    const dados = await response.json();

    if (dados.error) {
        alert('Erro: ' + dados.error);
    } else {
        renderizarArvore(dados.raiz);
    }
};
```

### Montagem dos elementos (BFS)

Antes de desenhar, `renderizarArvore` percorre a arvore inteira em BFS e coleta todos os nos e arestas numa lista. Cada elemento recebe um numero de ordem que define quando ele aparece na animacao:

```javascript
const fila = [{
    no: raiz,
    x: canvas.width / 2,
    y: padding,
    larguraDisponivel: W
}];

while (fila.length > 0) {
    const { no, x, y, larguraDisponivel } = fila.shift();

    elementos.push({
        tipo: 'no', x, y, raio,
        texto: `n=${no.tamanho.toFixed(1)}`,
        ordem: ordem++
    });

    const quantidadeFilhos = no.filhos ? no.filhos.length : 0;
    if (quantidadeFilhos > 0) {
        const espacoPorFilho = larguraDisponivel / quantidadeFilhos;
        const inicioX = x - larguraDisponivel / 2 + espacoPorFilho / 2;

        for (let i = 0; i < quantidadeFilhos; i++) {
            const filhoX = inicioX + (i * espacoPorFilho);
            const filhoY = y + espacoVertical;

            elementos.push({
                tipo: 'aresta',
                x1: x, y1: y + raio,
                x2: filhoX, y2: filhoY - raio,
                ordem: ordem++
            });

            fila.push({
                no: no.filhos[i], x: filhoX,
                y: filhoY, larguraDisponivel: espacoPorFilho
            });
        }
    }
}
```

O espaco horizontal e dividido igualmente pela quantidade de filhos (`larguraDisponivel / quantidadeFilhos`). Sem isso, arvores com muitos ramos (como Strassen, com 7 filhos por no) jogam nos para fora da area visivel.

### Loop de animacao

Cada elemento entra na tela com atraso proporcional a sua ordem. Nos usam uma curva elastica (`easeOutBack`) que faz o circulo "brotar" com um leve repique. Arestas crescem progressivamente do pai ate o filho usando uma curva cubica:

```javascript
const easeOutBack = (x) => {
    const c1 = 1.70158;
    const c3 = c1 + 1;
    return 1 + c3 * Math.pow(x - 1, 3)
             + c1 * Math.pow(x - 1, 2);
};

const animate = (timestamp) => {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    for (const el of elementos) {
        const tempoInicioEl = startTimestamp
            + (el.ordem * atrasoPorOrdem);

        if (timestamp < tempoInicioEl) continue;

        let progresso = (timestamp - tempoInicioEl) / duracaoAnimacao;
        progresso = Math.min(1.0, progresso);

        if (el.tipo === 'aresta') {
            const prog = 1 - Math.pow(1 - progresso, 3);
            const atualX = el.x1 + (el.x2 - el.x1) * prog;
            const atualY = el.y1 + (el.y2 - el.y1) * prog;
            // ... desenha linha parcial de (x1,y1) ate (atualX,atualY)
        } else if (el.tipo === 'no') {
            const raioAtual = el.raio * easeOutBack(progresso);
            // ... desenha circulo com raioAtual
            // ... texto aparece em fade-in apos 40% do progresso
        }
    }

    if (!todosCompletos) {
        currentAnimationId = requestAnimationFrame(animate);
    }
};

currentAnimationId = requestAnimationFrame(animate);
```

O atraso entre elementos varia conforme o total de nos: arvores pequenas animam devagar, arvores grandes comprimem os atrasos para caber em cerca de 1.2 segundos.

---

## Exemplos praticos

### Merge Sort

```
T(n) = 2T(n/2) + O(n)
Parametros: a=2, b=2, k=1
```

O `TreeBuilder` cria 2 filhos por no e divide o tamanho por 2 a cada nivel. O `calcularComplexidade` calcula `log_2(2) = 1.0`, compara com `k = 1`, e cai no caso de empate.

**Resultado:** `O(n^1.00 * log(n))`

### Busca Binaria

```
T(n) = 1T(n/2) + O(1)
Parametros: a=1, b=2, k=0
```

O laco de repeticao roda apenas 1 vez, gerando uma arvore que e uma linha reta para baixo. Cada nivel tem um unico no.

**Resultado:** `O(n^0.00 * log(n))`

### Strassen (Multiplicacao de Matrizes)

```
T(n) = 7T(n/2) + O(n^2)
Parametros: a=7, b=2, k=2
```

A raiz gera 7 filhos de uma vez. Com `n=64`, a arvore teria mais de 100.000 nos, por isso o limitador de 1000 nos (`contadorDeNos[0] > 1000`) corta a execucao e devolve um erro tratado para o frontend.

`log_2(7) = 2.81`, que e maior que `k = 2`. Cai no caso 1 do Teorema Mestre.

**Resultado:** `O(n^2.81)`

### Karatsuba (Multiplicacao de Inteiros)

```
T(n) = 3T(n/2) + O(n)
Parametros: a=3, b=2, k=1
```

`log_2(3) = 1.58`, que e maior que `k = 1`. Cai no caso 1.

**Resultado:** `O(n^1.58)`

---

## Como executar

```bash
chmod +x run.sh
./run.sh
```

O script compila todos os arquivos Java e inicia o servidor:

```bash
#!/bin/bash
mkdir -p bin
javac -d bin src/model/*.java src/adapter/*.java src/Main.java

if [ $? -eq 0 ]; then
    java -cp bin Main
else
    echo "Erro na compilacao."
fi
```

Depois disso, abra o navegador em `http://localhost:8081`.

Para desligar o servidor, pressione `Ctrl + C` no terminal.

## Como rodar os testes

Os testes usam metodos `main` nativos (sem JUnit ou qualquer framework externo):

```bash
javac -d bin src/model/*.java src/test/*.java
java -cp bin test.TreeNodeTest
java -cp bin test.TreeBuilderTest
```

Se passar, a saida e `TreeNodeTest: TODOS OS TESTES PASSARAM.` e `TreeBuilderTest: TODOS OS TESTES PASSARAM.` respectivamente.
