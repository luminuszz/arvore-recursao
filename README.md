# Visualizador de Árvore de Recursão

Este projeto resolve relações de recorrência gerando uma árvore visual. Ele calcula o custo de algoritmos de divisão e conquista (como Merge Sort ou Strassen) e desenha cada etapa do particionamento no navegador.

O sistema não possui comentários no código fonte por restrição técnica do desafio original, então os nomes das variáveis e classes explicam o comportamento.

## Arquitetura

A aplicação é dividida em um backend Java puro (sem frameworks) que processa a matemática e um frontend em HTML5 Canvas que cuida da exibição.

### 1. TreeNode
Representa um nó individual da árvore. Ele guarda o tamanho do subproblema e o custo de processamento local daquela etapa. A classe mantém uma lista de filhos para permitir o aninhamento natural da recursão. A serialização para JSON é feita manualmente via concatenação de strings, já que o projeto proíbe bibliotecas externas.

### 2. RecurrenceResult
Agrupa o resultado completo de uma execução. Ela encapsula a raiz da árvore (TreeNode), um mapa que soma os custos de cada nível, e a complexidade final calculada pelo Teorema Mestre. O método toJson junta esses três elementos no texto de resposta da API.

### 3. TreeBuilder
O motor matemático do projeto. 
A função gerarNos recebe os parâmetros da fórmula matemática e se chama recursivamente. A cada chamada, ela calcula o trabalho exigido pelo nó e repassa um problema de tamanho menor para os filhos. Para evitar estouro de memória com árvores exponenciais (como a do algoritmo de Strassen para entradas grandes), um array contador limita a execução a 1000 nós e lança um erro tratado caso o limite seja excedido.
A função calcularComplexidade avalia os parâmetros sob as regras do Teorema Mestre e devolve a resposta no formato de notação Big-O.

### 4. HttpAdapter
Um servidor HTTP nativo construído com a biblioteca embutida do Java (com.sun.net.httpserver). Ele escuta na porta 8081, serve o arquivo estático da pasta src/web e processa as chamadas para o endpoint da API, encaminhando a matemática para o TreeBuilder.

### 5. Frontend e Animação
O frontend na pasta src/web consome a API e desenha a estrutura em um elemento canvas. Para dar a percepção de crescimento da árvore, a renderização utiliza um algoritmo de Busca em Largura (BFS). O javascript lista todos os nós e arestas em ordem de nível e dispara a animação usando requestAnimationFrame. O espaço horizontal da tela é sempre dividido igualmente pela quantidade de filhos de cada galho. Isso impede que os círculos ultrapassem as margens laterais da tela quando a árvore cresce.

## Exemplos de Execução Prática

Para entender como a matemática se traduz no código, considere a fórmula geral de recorrência:
`T(n) = a * T(n / b) + O(n^k)`

O sistema recebe esses valores através dos seguintes parâmetros na classe `TreeBuilder`:
- `quantidadeDeSubproblemas` (representa o `a`)
- `divisorDoTamanhoDoSubproblema` (representa o `b`)
- `expoenteDoPolinomioDeTrabalho` (representa o `k`)
- `tamanhoInicialDoProblema` (representa o `n` inicial)

### Exemplo 1: Merge Sort
O Merge Sort divide um array na metade e resolve as duas metades recursivamente. Seu custo de junção é linear.
- **Fórmula:** `T(n) = 2T(n/2) + O(n)`
- **Parâmetros no sistema:** `quantidade = 2`, `divisor = 2`, `expoente = 1`.
- **Comportamento no TreeBuilder:** A função `gerarNos` cria um nó raiz. O laço de repetição roda `2` vezes (pois `a=2`). Em cada volta, repassa o tamanho do problema atual dividido por `2`.
- **Complexidade Resultante:** O logaritmo na base 2 de 2 é igual a 1. Como `k` também é 1, cai na regra de empate do Teorema Mestre. O sistema retorna `O(n^1.00 * log(n))`.

### Exemplo 2: Busca Binária
A Busca Binária divide o problema na metade, mas só precisa explorar uma delas. O custo por etapa é constante.
- **Fórmula:** `T(n) = 1T(n/2) + O(1)`
- **Parâmetros no sistema:** `quantidade = 1`, `divisor = 2`, `expoente = 0`.
- **Comportamento no TreeBuilder:** O laço repete apenas `1` vez, gerando uma árvore que parece uma linha reta reta para baixo. O tamanho cai pela metade em cada nível.
- **Complexidade Resultante:** Logaritmo na base 2 de 1 é 0. O expoente `k` também é 0. Cai na regra de empate. O sistema retorna `O(n^0.00 * log(n))`, que representa a classe Logarítmica padrão.

### Exemplo 3: Algoritmo de Strassen (Multiplicação de Matrizes)
O algoritmo de Strassen quebra as matrizes em blocos menores (dividindo o tamanho por 2), mas executa 7 multiplicações recursivas em vez das tradicionais 8.
- **Fórmula:** `T(n) = 7T(n/2) + O(n^2)`
- **Parâmetros no sistema:** `quantidade = 7`, `divisor = 2`, `expoente = 2`.
- **Comportamento no TreeBuilder:** O nó raiz gera `7` filhos de uma só vez. A árvore explode exponencialmente de largura. Uma entrada inicial `n=64` atinge o nível 6 da árvore, gerando mais de 100.000 nós no total. É aqui que entra o limitador de 1000 nós do sistema, barrando o processamento excessivo.
- **Complexidade Resultante:** Logaritmo base 2 de 7 é aproximadamente `2.81`. Como `2.81` é maior que o expoente local `2`, a complexidade cai no terceiro caso do Teorema Mestre. O sistema retorna `O(n^2.81)`.

## Como executar

1. Dê permissão ao arquivo de script:
   chmod +x run.sh
2. Inicie a compilação e o servidor:
   ./run.sh
3. Abra o navegador em:
   http://localhost:8081

Para desligar o servidor, digite Ctrl + C no terminal.
