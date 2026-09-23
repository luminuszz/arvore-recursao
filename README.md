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

## Como executar

1. Dê permissão ao arquivo de script:
   chmod +x run.sh
2. Inicie a compilação e o servidor:
   ./run.sh
3. Abra o navegador em:
   http://localhost:8081

Para desligar o servidor, digite Ctrl + C no terminal.
