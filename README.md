# TP1 - CRUD AEDS3

Este projeto implementa uma nova entidade chamada **Tarefa**, que utiliza o CRUD (Create, Read, Update, Delete) para gerenciar registros por meio de uma tabela **Hash Extensível**. A classe `IO.java` é responsável por testar o CRUD, utilizando um índice direto para acessar os registros. Abaixo está a descrição das principais classes e funcionalidades do projeto.

## Estrutura das Classes

### Classe `Registro`
Interface que define os seguintes métodos:
- `setId()`: Define o ID.
- `getId()`: Retorna o ID.
- `toByteArray()`: Serializa o objeto em um array de bytes.
- `fromByteArray()`: Desserializa um array de bytes para reconstruir o objeto.
- `compareTo()`: Compara dois registros.

---

### Classe `Arquivo`
Gerencia a leitura, escrita, atualização e exclusão de registros em arquivos.
- **Atributos**: 
  - `RandomAccessFile arquivo`: Acesso ao arquivo.
  - `String nomeArquivo`: Nome do arquivo.
  - `int TAM_CABECALHO`: Tamanho do cabeçalho do arquivo.
  - `Constructor<T> construtor`: Construtor genérico para as entidades.
  - `HashExtensivel<ParIDEndereco> indiceDireto`: Índice direto.

- **Métodos**:
  - `create(T entidade)`: Insere um novo registro.
  - `read(int id)`: Lê um registro usando o índice direto.
  - `delete(int id)`: Remove um registro.
  - `update(T novaEntidade)`: Atualiza um registro.
  - `reorganizar()`: Reorganiza o arquivo usando intercalação balanceada.

---

### Classe `HashExtensivel`
Implementa uma tabela hash extensível, que permite armazenar e recuperar registros de forma eficiente.
- **Atributos**:
  - `String nomeArquivoDiretorio`: Nome do arquivo de diretório.
  - `String nomeArquivoCestos`: Nome do arquivo de cestos.
  - `RandomAccessFile arqDiretorio`: Acesso ao arquivo de diretório.
  - `RandomAccessFile arqCestos`: Acesso ao arquivo de cestos.
  - `Diretorio diretorio`: Gerencia o diretório de cestos.
  - `Constructor<T> construtor`: Construtor genérico.

- **Métodos**:
  - `create(T elem)`: Insere um novo elemento.
  - `read(int chave)`: Busca um elemento pela chave.
  - `update(T elem)`: Atualiza um elemento existente.
  - `delete(int chave)`: Remove um elemento.
  - `print()`: Exibe o conteúdo do diretório e dos cestos.
  - `close()`: Fecha os arquivos abertos.

---

### Classe `Cesto`
Armazena elementos e gerencia sua capacidade em uma tabela hash.
- **Atributos**:
  - `short quantidadeMaxima`: Capacidade máxima do cesto.
  - `ArrayList<T> elementos`: Lista de elementos armazenados.

- **Métodos**:
  - `create(T elem)`: Insere um novo elemento.
  - `read(int chave)`: Busca um elemento pela chave.
  - `update(T elem)`: Atualiza um elemento.
  - `delete(int chave)`: Remove um elemento.
  - `empty()`: Verifica se o cesto está vazio.
  - `full()`: Verifica se o cesto está cheio.
  - `toByteArray()`: Serializa os dados do cesto.
  - `fromByteArray(byte[] ba)`: Desserializa os dados de um array de bytes.

---

### Classe `Diretorio`
Gerencia o mapeamento de chaves para endereços dos cestos.
- **Atributos**:
  - `byte profundidadeGlobal`: Profundidade global do diretório.
  - `long[] enderecos`: Endereços dos cestos.

- **Métodos**:
  - `atualizaEndereco(int p, long e)`: Atualiza o endereço de um cesto.
  - `hash(int chave)`: Calcula o índice de hash.
  - `duplica()`: Duplica o diretório, aumentando sua profundidade global.

---

### Classe `MinHeap`
Implementa um heap mínimo para gerenciamento de dados em um ArrayList.
- **Atributos**:
  - `ArrayList<T> heap`: Estrutura que armazena os elementos do heap.

- **Métodos**:
  - `insert(T value)`: Insere um novo elemento no heap.
  - `extractMin()`: Remove e retorna o menor elemento.
  - `heapifyUp(int index)`: Corrige o heap após a inserção.
  - `heapifyDown(int index)`: Corrige o heap após a remoção.
  - `isEmpty()`: Verifica se o heap está vazio.
  - `size()`: Retorna o tamanho do heap.
  - `peek()`: Retorna o menor elemento sem removê-lo.

---

### Classe `ParIDEndereco`
Gerencia o ID e o endereço de um registro.
- **Atributos**:
  - `int ID`: Identificador único.
  - `long endereco`: Endereço do registro.

- **Métodos**:
  - `toByteArray()`: Serializa os atributos.
  - `fromByteArray(byte[] ba)`: Desserializa um array de bytes.
  - `hashCode()`: Gera o código hash baseado no ID.

---

### Classe `Tarefa`
Representa a entidade principal gerenciada pelo CRUD.
- **Atributos**:
  - `int id`: Identificador da tarefa.
  - `String nome`: Nome da tarefa.
  - `LocalDate dataCriacao`: Data de criação.
  - `LocalDate dataConclusao`: Data de conclusão.
  - `byte status`: Status da tarefa (0 = Pendente, 1 = Em progresso, 2 = Concluída).
  - `byte prioridade`: Prioridade (0 = Baixa, 1 = Média, 2 = Alta).

- **Métodos**:
  - `setNome(String nome)`: Define o nome da tarefa.
  - `getNome()`: Retorna o nome da tarefa.
  - `setDataCriacao(LocalDate dataCriacao)`: Define a data de criação da tarefa.
  - `getDataCriacao()`: Retorna a data de criação da tarefa.
  - `setDataConclusao(LocalDate dataConclusao)`: Define a data de conclusão da tarefa.
  - `getDataConclusao()`: Retorna a data de conclusão da tarefa.
  - `setStatus(byte status)`: Define o status da tarefa. (0 = Pendente, 1 = Em progresso, 2 = Concluída).
  - `getStatus()`: Retorna o status da tarefa.
  - `setPrioridade(byte prioridade)`: Define a prioridade da tarefa. (0 = Baixa, 1 = Média, 2 = Alta).
  - `getPrioridade()`: Retorna a prioridade da tarefa.
  - `setId(int id)`: Define o ID da tarefa.
  - `getId()`: Retorna o ID da tarefa.
  - `toByteArray()`: Serializa os atributos da tarefa (ID, nome, datas, status e prioridade) em um array de bytes para armazenamento em arquivo.
  - `fromByteArray(byte[] b)`: Desserializa um array de bytes e preenche os atributos da tarefa (ID, nome, datas, status e prioridade) com os valores lidos.
  - `toString()`: Retorna uma representação textual dos atributos da tarefa, incluindo a formatação do status e da prioridade.
  - `compareTo(Object p)`: Compara a tarefa atual com outra tarefa pelo ID. Utilizado para ordenação ou busca.

---

## Experiência

- **Dificuldades**: A compreensão da **Hash Extensível** e a implementação do método `reorganizar()` foram os maiores desafios. No entanto, o trabalho foi interessante e recompensador de se fazer.
  
- **Funcionamento**: Todas as funcionalidades do CRUD, incluindo busca, atualização e exclusão, estão implementadas com sucesso, utilizando um índice direto e uma tabela hash extensível.

---

## Perguntas e Respostas

- O trabalho possui um índice direto implementado com a tabela hash extensível? **Sim**.
- A operação de inclusão insere um novo registro no fim do arquivo e no índice? **Sim**.
- A operação de busca retorna os dados corretamente usando o índice direto? **Sim**.
- A operação de exclusão remove o registro do índice direto? **Sim**.
- O trabalho está completo e funcionando corretamente? **Sim**.
- O trabalho é original? **Sim**.
