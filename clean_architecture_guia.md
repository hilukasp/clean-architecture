# Guia de estudo — Clean Architecture no projeto `rest-clean-arch-example`

Este documento explica a função dos arquivos do projeto e sugere uma ordem prática para implementar uma aplicação seguindo Clean Architecture.

> O projeto contém um exemplo de cadastro e consulta de `Lead` — um possível cliente interessado em algum produto ou serviço.

## 1. Visão geral da arquitetura

```text
HTTP
 │
 ▼
infrastructure/web/LeadController
 │
 ▼
application/lead/*UseCase
 │
 ▼
application/lead/*Interactor
 │
 ▼
domain/lead/Lead
 │
 ▼
domain/lead/LeadGateway
 │
 ├── infrastructure/persistence/jpa/LeadJpaAdapter
 │      └── LeadJpaRepository → banco de dados
 │
 └── infrastructure/persistence/memory/LeadInMemoryAdapter
        └── Map em memória
```

A regra principal é que as camadas mais internas não dependem das mais externas:

```text
infrastructure → application → domain
```

O domínio não deve conhecer Spring, JPA, HTTP ou banco de dados.

---

## 2. Estrutura principal

```text
src/main/java/school/sptech/exemplo_lead/
├── ExemploLeadApplication.java
├── domain/
│   ├── exception/
│   │   ├── InvalidParamException.java
│   │   └── LeadNotFoundException.java
│   └── lead/
│       ├── Email.java
│       ├── Lead.java
│       ├── LeadGateway.java
│       └── LeadName.java
├── application/
│   └── lead/
│       ├── CreateLeadInteractor.java
│       ├── CreateLeadUseCase.java
│       ├── GetLeadInteractor.java
│       └── GetLeadUseCase.java
└── infrastructure/
    ├── config/
    │   └── LeadUseCaseConfig.java
    ├── persistence/
    │   ├── jpa/
    │   │   ├── LeadEntity.java
    │   │   ├── LeadJpaAdapter.java
    │   │   └── LeadJpaRepository.java
    │   └── memory/
    │       └── LeadInMemoryAdapter.java
    └── web/
        ├── ApiErrorHandler.java
        ├── LeadController.java
        ├── LeadRequestDto.java
        └── LeadResponseDto.java
```

---

# 3. Arquivos do domínio

O domínio contém as regras e os conceitos centrais do sistema. Ele deve ser independente de frameworks.

## `domain/lead/Lead.java`

Representa o objeto principal do negócio: um Lead.

Responsabilidades:

- Guardar `id`, nome e e-mail.
- Criar um Lead novo por meio de `create`.
- Reconstruir um Lead que já existe por meio de `reconstitute`.
- Usar `LeadName` e `Email` para validar os dados.
- Expor os valores necessários por meio de métodos `get`.

Métodos importantes:

```java
Lead.create(String name, String email)
Lead.reconstitute(Integer id, String name, String email)
```

A diferença é que `create` cria um objeto novo sem ID, enquanto `reconstitute` representa um registro que já veio de algum armazenamento.

## `domain/lead/LeadName.java`

É um **Value Object** responsável pelo nome do Lead.

Responsabilidades:

- Recusar nome `null`.
- Recusar nome vazio ou composto apenas por espaços.
- Remover espaços extras nas extremidades com `trim()`.
- Limitar o nome a 120 caracteres.
- Guardar o valor já validado.

O método `build` funciona como uma fábrica:

```java
LeadName.build(" Ana Silva ")
```

O valor armazenado será `"Ana Silva"`.

## `domain/lead/Email.java`

É um **Value Object** para o e-mail.

Responsabilidades:

- Recusar e-mail `null`.
- Verificar se o texto contém `@`.
- Guardar o e-mail validado.

A validação é simples e didática; em um projeto real, provavelmente seria necessário validar o formato com mais cuidado.

## `domain/lead/LeadGateway.java`

É uma **porta de saída** da aplicação.

Define as operações de armazenamento que o domínio/aplicação precisa:

```java
Lead save(Lead lead);
Optional<Lead> findById(int id);
```

Essa interface não sabe se os dados serão salvos em:

- Banco de dados;
- Memória;
- API externa;
- Outro tipo de armazenamento.

As implementações concretas ficam na infraestrutura.

## `domain/exception/InvalidParamException.java`

Exceção usada quando um dado viola uma regra de validação do domínio, como:

- Nome nulo;
- Nome vazio;
- Nome muito longo;
- E-mail inválido.

## `domain/exception/LeadNotFoundException.java`

Exceção usada quando um Lead solicitado não é encontrado.

No exemplo, ela é lançada pelo caso de uso de consulta.

---

# 4. Arquivos da aplicação

A camada `application` contém os **casos de uso**. Ela coordena o que deve acontecer, mas não deve depender diretamente de JPA, HTTP ou de uma implementação concreta de banco.

## `application/lead/CreateLeadUseCase.java`

Define o contrato do caso de uso de criação de Lead.

Possui:

- `Command`: dados de entrada do caso de uso.
- `Response`: dados de saída do caso de uso.
- `execute`: operação que executa a criação.

```java
Response execute(Command command);
```

A interface permite que o Controller dependa de uma abstração, em vez de depender diretamente da classe que implementa o caso de uso.

## `application/lead/CreateLeadInteractor.java`

É a implementação do caso de uso de criação.

Fluxo:

1. Recebe nome e e-mail.
2. Chama `Lead.create(...)`.
3. Envia o Lead para `LeadGateway.save(...)`.
4. Recebe o Lead salvo, agora com ID.
5. Constrói e retorna o `Response`.

Ele depende de:

```java
private final LeadGateway gateway;
```

Ele não depende de `LeadJpaRepository` nem de `LeadEntity`.

## `application/lead/GetLeadUseCase.java`

Define o contrato do caso de uso de consulta por ID.

Possui:

- `Command`, contendo o ID procurado;
- `Response`, contendo os dados retornados;
- `execute`, que realiza a consulta.

## `application/lead/GetLeadInteractor.java`

Implementa a consulta de um Lead.

Fluxo:

1. Recebe o ID.
2. Chama `gateway.findById(...)`.
3. Se não encontrar, lança `LeadNotFoundException`.
4. Se encontrar, transforma o resultado em `Response`.

---

# 5. Arquivos da infraestrutura

A infraestrutura conecta a aplicação ao mundo externo: Spring Boot, HTTP, JPA, banco de dados e armazenamento em memória.

## `ExemploLeadApplication.java`

É a classe de inicialização do Spring Boot.

O método `main` inicia a aplicação:

```java
SpringApplication.run(ExemploLeadApplication.class, args);
```

A anotação `@SpringBootApplication` habilita a configuração automática, a varredura de componentes e a configuração da aplicação.

## `infrastructure/config/LeadUseCaseConfig.java`

Configura os casos de uso como objetos gerenciados pelo Spring.

Cria os Beans:

- `CreateLeadUseCase` → `CreateLeadInteractor`;
- `GetLeadUseCase` → `GetLeadInteractor`.

O Spring injeta um `LeadGateway` nesses objetos. A implementação escolhida dependerá do Profile ativo.

Esse arquivo é importante porque mostra como conectar interfaces às implementações sem colocar anotações do Spring dentro da camada de aplicação.

## `infrastructure/web/LeadController.java`

É a porta de entrada HTTP da aplicação.

Rotas disponíveis:

| Método | Rota | Finalidade |
|---|---|---|
| `POST` | `/leads` | Criar um Lead |
| `GET` | `/leads/{id}` | Buscar um Lead pelo ID |

O Controller:

1. Recebe a requisição HTTP;
2. Lê o DTO de entrada;
3. Monta o `Command` do caso de uso;
4. Chama a interface `CreateLeadUseCase` ou `GetLeadUseCase`;
5. Converte o `Response` em DTO HTTP;
6. Retorna o status HTTP apropriado.

Ele não contém a regra principal de criação ou consulta.

## `infrastructure/web/LeadRequestDto.java`

DTO usado para receber os dados enviados no `POST /leads`.

Campos:

- `name`;
- `email`.

O `@Schema` fornece exemplos para documentação OpenAPI/Swagger.

## `infrastructure/web/LeadResponseDto.java`

DTO usado para devolver os dados do Lead na resposta HTTP.

Campos:

- `id`;
- `name`;
- `email`.

Ele evita expor diretamente objetos internos do domínio na API.

## `infrastructure/web/ApiErrorHandler.java`

Centraliza o tratamento de exceções da API usando `@RestControllerAdvice`.

Converte exceções em respostas HTTP:

| Exceção | Status |
|---|---:|
| `InvalidParamException` | `400 Bad Request` |
| `LeadNotFoundException` | `404 Not Found` |

O registro `ApiError` padroniza o corpo do erro com:

- Data/hora;
- Status;
- Mensagem;
- Caminho da requisição.

## `infrastructure/persistence/jpa/LeadEntity.java`

É o modelo usado pelo JPA para representar a tabela do banco.

Características:

- `@Entity` indica que a classe é uma entidade persistida;
- `@Table(name = "lead")` define o nome da tabela;
- `@Id` define a chave primária;
- `@GeneratedValue` permite geração automática do ID.

Essa classe pertence à infraestrutura, e não ao domínio, porque contém detalhes de persistência.

## `infrastructure/persistence/jpa/LeadJpaRepository.java`

É a interface do Spring Data JPA responsável pelo acesso básico ao banco.

Ao estender:

```java
JpaRepository<LeadEntity, Integer>
```

ela recebe operações como:

- `save`;
- `findById`;
- `findAll`;
- `deleteById`.

## `infrastructure/persistence/jpa/LeadJpaAdapter.java`

É o adaptador que implementa a porta `LeadGateway` usando JPA.

Responsabilidades:

- Receber um `Lead` do domínio;
- Converter o objeto de domínio em `LeadEntity`;
- Usar `LeadJpaRepository` para salvar ou buscar;
- Converter `LeadEntity` novamente em `Lead`.

Esse arquivo faz a ponte entre:

```text
Domínio ↔ JPA
```

A anotação `@Profile("!memory")` faz com que esse adaptador seja usado quando o Profile `memory` não estiver ativo.

## `infrastructure/persistence/memory/LeadInMemoryAdapter.java`

É uma implementação alternativa de `LeadGateway` que não usa banco de dados.

Usa:

- `ConcurrentHashMap` para armazenar os Leads;
- `AtomicInteger` para gerar IDs.

É ativado com:

```java
@Profile("memory")
```

Esse adaptador é útil para testes e para executar a aplicação sem configurar um banco.

---

# 6. Como as partes se conectam

## Criação de um Lead

```text
POST /leads
    │
    ▼
LeadController
    │ cria CreateLeadUseCase.Command
    ▼
CreateLeadUseCase
    │ implementação: CreateLeadInteractor
    ▼
Lead.create(name, email)
    │ valida LeadName e Email
    ▼
LeadGateway.save(lead)
    │ implementação escolhida pelo Spring
    ├── LeadJpaAdapter → LeadJpaRepository → banco
    └── LeadInMemoryAdapter → Map
    ▼
Lead salvo com ID
    ▼
Response do caso de uso
    ▼
LeadResponseDto
    ▼
HTTP 201 Created
```

## Consulta de um Lead

```text
GET /leads/{id}
    │
    ▼
LeadController
    ▼
GetLeadUseCase
    │ implementação: GetLeadInteractor
    ▼
LeadGateway.findById(id)
    ▼
Lead encontrado ou LeadNotFoundException
    ▼
Response do caso de uso
    ▼
LeadResponseDto
    ▼
HTTP 200 OK
```

---

# 7. Por quais arquivos começar a implementar Clean Architecture?

Se você estiver criando um projeto do zero, não comece pelo Controller nem pelo Repository. Comece definindo o que o sistema faz e quais são suas regras.

## Ordem recomendada

### Etapa 1 — Defina a entidade principal do domínio

Comece por:

```text
domain/lead/Lead.java
```

Pergunte:

- O que é um Lead?
- Quais dados ele possui?
- O que é obrigatório?
- Quais estados são válidos?
- Quais operações ele pode realizar?

Nesse momento, não use `@Entity`, `@Table`, `@Service` ou `@Repository`.

### Etapa 2 — Crie os Value Objects e validações

Depois implemente:

```text
domain/lead/LeadName.java
domain/lead/Email.java
domain/exception/InvalidParamException.java
```

Coloque aqui as validações que são regras do próprio domínio.

### Etapa 3 — Defina as portas de saída

Crie:

```text
domain/lead/LeadGateway.java
```

Liste somente as operações de que os casos de uso precisam, por exemplo:

```java
Lead save(Lead lead);
Optional<Lead> findById(int id);
```

Não crie a implementação JPA ainda. Primeiro defina o contrato.

### Etapa 4 — Escreva os contratos dos casos de uso

Crie:

```text
application/lead/CreateLeadUseCase.java
application/lead/GetLeadUseCase.java
```

Defina:

- Dados de entrada;
- Dados de saída;
- Operações disponíveis.

Isso ajuda a deixar claro o comportamento da aplicação antes de pensar em HTTP ou banco.

### Etapa 5 — Implemente os casos de uso

Depois crie:

```text
application/lead/CreateLeadInteractor.java
application/lead/GetLeadInteractor.java
```

Eles devem usar:

- Objetos do domínio;
- Interfaces/portas;
- Regras de negócio;
- Exceções do domínio.

Eles não devem importar classes de JPA ou Controller.

### Etapa 6 — Faça testes do domínio e da aplicação

Antes de criar a API, teste:

- Criação de Lead válido;
- Nome vazio;
- Nome muito longo;
- E-mail inválido;
- Criação usando um gateway falso;
- Consulta de Lead existente;
- Consulta de Lead inexistente.

Um `FakeLeadGateway` ou implementação em memória ajuda a testar sem banco.

### Etapa 7 — Implemente a persistência

Agora crie os arquivos de infraestrutura:

```text
infrastructure/persistence/jpa/LeadEntity.java
infrastructure/persistence/jpa/LeadJpaRepository.java
infrastructure/persistence/jpa/LeadJpaAdapter.java
```

O adaptador deve implementar a interface criada anteriormente:

```java
public class LeadJpaAdapter implements LeadGateway
```

A entidade JPA pode ter uma estrutura própria. Ela não precisa ser a mesma classe do domínio.

### Etapa 8 — Configure a injeção de dependências

Crie:

```text
infrastructure/config/LeadUseCaseConfig.java
```

Ligue as interfaces às implementações concretas por meio de Beans do Spring.

### Etapa 9 — Crie a entrada HTTP

Só depois implemente:

```text
infrastructure/web/LeadRequestDto.java
infrastructure/web/LeadResponseDto.java
infrastructure/web/LeadController.java
```

O Controller deve apenas traduzir HTTP para comandos da aplicação e respostas da aplicação para HTTP.

### Etapa 10 — Trate os erros e teste a integração

Finalize com:

```text
infrastructure/web/ApiErrorHandler.java
```

Depois faça testes de integração da API, verificando:

- Status HTTP;
- JSON de entrada;
- JSON de saída;
- Erros de validação;
- Lead inexistente;
- Persistência real.

---

# 8. Ordem resumida para implementar

```text
1. Lead.java
2. LeadName.java e Email.java
3. Exceções do domínio
4. LeadGateway.java
5. CreateLeadUseCase.java
6. GetLeadUseCase.java
7. CreateLeadInteractor.java
8. GetLeadInteractor.java
9. Testes do domínio e dos casos de uso
10. LeadEntity.java
11. LeadJpaRepository.java
12. LeadJpaAdapter.java
13. LeadUseCaseConfig.java
14. LeadRequestDto.java
15. LeadResponseDto.java
16. LeadController.java
17. ApiErrorHandler.java
18. Testes de integração
```

---

# 9. O que evitar

## Não coloque JPA no domínio

Evite:

```java
@Entity
public class Lead {
}
```

se a intenção é manter o domínio independente de persistência.

Prefira uma entidade de domínio simples e uma classe JPA separada.

## Não faça o Controller acessar o Repository

Evite este fluxo:

```text
Controller → Repository
```

Prefira:

```text
Controller → UseCase → Gateway → Adapter → Repository
```

## Não faça o caso de uso depender do JPA

Evite:

```java
private final LeadJpaRepository repository;
```

na camada `application`.

Prefira:

```java
private final LeadGateway gateway;
```

## Não crie interfaces sem necessidade

A interface deve representar um contrato útil, como um caso de uso ou uma porta de saída. Criar uma interface para cada classe apenas por hábito pode deixar o projeto mais difícil de entender.

---

# 10. Comparação com arquitetura acoplada

## Arquitetura tradicional do projeto `acoplado`

```text
LeadController
    ↓
LeadService
    ↓
LeadRepository
    ↓
Banco de dados
```

É uma estrutura válida e simples, mas o Service pode acabar conhecendo muitos detalhes de persistência e o modelo JPA.

## Clean Architecture

```text
LeadController
    ↓
CreateLeadUseCase
    ↓
CreateLeadInteractor
    ↓
LeadGateway
    ↓
LeadJpaAdapter
    ↓
LeadJpaRepository
    ↓
Banco de dados
```

A principal diferença não é ter mais pastas. É separar responsabilidades e fazer a aplicação depender de abstrações, permitindo trocar detalhes externos sem alterar as regras centrais.
