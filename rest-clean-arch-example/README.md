# exemplo-lead

API mínima de captura e consulta de leads (nome e email), usada como material de aula para uma
turma iniciante em programação. Se você está lendo isso sem ter acompanhado a aula: a
funcionalidade em si é trivial de propósito, o que importa é a arquitetura por trás dela.

## O que é isso, exatamente

Este repositório existe para mostrar Clean Architecture e Ports & Adapters aplicados em código
real: código que compila, que tem testes, e que tem uma regra de build que quebra quando a
arquitetura é violada. Não é um conjunto de slides, é um exemplo que dá para rodar, testar e
"tentar quebrar" para ver o que acontece.

**É um exemplo didático, não um guia de como implementar Clean Architecture.** As decisões
aqui existem para dar suporte a uma discussão em sala sobre tradeoffs (camadas, portas, modelo
rico, quando vale a pena aplicar cada padrão e quando não vale). Não é um template para copiar
em qualquer projeto novo assumindo que "essa é a forma correta de fazer". A seção Discussão, no
fim deste documento, explica caso a caso quando cada decisão compensa e quando não compensa.

## Estrutura de pastas

```
domain/           entidades, value objects, exceções, porta de saída (LeadGateway). Sem framework.
application/       portas de entrada (UseCase) e suas implementações (Interactor). Sem framework.
infrastructure/    tudo que depende de Spring: controller, DTOs, JPA, configuração de beans.
```

## Fluxo de uma requisição

```
HTTP request
  -> LeadController            (infrastructure/web, adapter primário)
  -> CreateLeadUseCase          (application/lead, porta de entrada: interface)
  -> CreateLeadInteractor        (application/lead, implementação da porta)
  -> LeadGateway                  (domain/lead, porta de saída: interface)
  -> LeadJpaAdapter ou LeadInMemoryAdapter  (infrastructure/persistence, adapter secundário)
```

Regra de dependência: `infrastructure` conhece `application` e `domain`. `application` conhece
só `domain`. `domain` não conhece nada de fora. Nenhuma classe de fora chama o `Interactor`
diretamente, só a interface (`UseCase`). Isso é verificado por teste, não só por convenção
(ver seção ArchUnit).

## Endpoints

| Método | Rota | Status |
|---|---|---|
| POST | `/leads` | `201` com o lead criado, `400` se nome ou email inválido |
| GET | `/leads/{id}` | `200` com o lead, `404` se não existir |

Body do POST: `{"name": "...", "email": "..."}`.

## Como rodar

```bash
./mvnw spring-boot:run                                   # perfil padrão, persiste em H2
SPRING_PROFILES_ACTIVE=memory ./mvnw spring-boot:run      # persiste em memória (Map), some ao reiniciar
```

`LeadJpaAdapter` (ativo com `@Profile("!memory")`) e `LeadInMemoryAdapter` (ativo com
`@Profile("memory")`) implementam o mesmo `LeadGateway`. A aplicação nunca sabe qual dos dois
está ativo, e trocar de um para o outro não muda uma linha de `application` ou `domain`.

Documentação interativa: `http://localhost:8080/swagger-ui.html` (spec em `/v3/api-docs`).

## Testes

```bash
./mvnw clean test
```

- **Domínio** (`LeadTest`, `LeadNameTest`, `EmailTest`): testam invariantes, sem Spring.
- **Aplicação** (`CreateLeadInteractorTest`, `GetLeadInteractorTest`): usam `FakeLeadGateway`,
  um dublê escrito à mão, em vez de mock. O `Interactor` não sabe que está sendo testado.
- **Adapter** (`LeadInMemoryAdapterTest`): idem, sem Spring.
- **Arquitetura** (`LayeredArchitectureTest`): ver seção seguinte.

## ArchUnit: por que existe

`LayeredArchitectureTest` roda junto com `mvn test` e falha o build se:

1. a direção de dependência entre `domain`, `application` e `infrastructure` for violada
   (por exemplo, `domain` importando algo de `application`);
2. qualquer classe em `infrastructure.web` depender diretamente de um `Interactor`, em vez de
   depender só da interface `UseCase`.

Sem esse teste, a separação entre porta e implementação é apenas uma convenção. O compilador
Java sozinho não impede `LeadController` de importar `CreateLeadInteractor`: estão em pacotes
diferentes, mas no mesmo módulo Maven, e todas as classes são `public`. O ArchUnit transforma
essa convenção em uma regra que o build derruba. Ele já pegou a violação quando testamos de
propósito: a mensagem de erro aponta a classe, o método e a linha exatos.

## Discussão

Esta seção é para quem for reler este projeto mais tarde sem lembrar da aula, ou para outra
turma que só encontrou o repositório.

### O foco principal: separação de responsabilidades e SOLID

Antes de qualquer padrão de nomenclatura (Interactor, Gateway, Value Object), o que este
exemplo tenta mostrar de verdade é SOLID aplicado em código, e Clean Architecture como a forma
de organizar isso em camadas:

- **Responsabilidade única**: `Lead` só sabe validar a si mesmo, `LeadJpaAdapter` só sabe
  traduzir entre `Lead` e a tabela, `LeadController` só sabe traduzir entre HTTP e `UseCase`.
- **Aberto para extensão, fechado para modificação**: dá para trocar `LeadJpaAdapter` por
  `LeadInMemoryAdapter`, ou colocar um decorator de log/cache na frente de um `Interactor`,
  sem alterar o código já existente.
- **Substituição de Liskov**: qualquer implementação de `LeadGateway` pode substituir outra sem
  quebrar o `Interactor` que depende dela.
- **Segregação de interface**: `CreateLeadUseCase` e `GetLeadUseCase` são interfaces pequenas,
  com um método cada, em vez de uma interface única e genérica com vários métodos.
- **Inversão de dependência**: `CreateLeadInteractor` depende da abstração `LeadGateway`, não
  do adapter concreto. `LeadController` depende da abstração `CreateLeadUseCase`, não do
  `Interactor` concreto.

Os padrões de nomenclatura (Gateway, UseCase, Interactor, Value Object) são só um jeito comum
de nomear essas ideias. O que importa aprender é o princípio por trás, não o nome da classe.

### Níveis de abstração

Cada camada só enxerga o nível abaixo dela através de uma interface, nunca do tipo concreto.
`LeadController` conhece `CreateLeadUseCase` (interface), não `CreateLeadInteractor`.
`CreateLeadInteractor` conhece `LeadGateway` (interface), não `LeadJpaAdapter`. É isso que
permite trocar a implementação de persistência via profile sem tocar em `application` ou
`domain`, e testar o `Interactor` sem precisar de mock de framework nenhum.

### Modelo rico e modelo anêmico

Um modelo anêmico teria `Lead` com `getNome()`/`setNome()` públicos, e a validação espalhada
em vários lugares: um pouco no controller, um pouco em algum `LeadService`, às vezes duplicada.
Nada impede criar um `Lead` com nome vazio se alguém esquecer de validar em um desses lugares.

Neste projeto, `Lead` só nasce por `Lead.create(...)` ou `Lead.reconstitute(...)`, delegando a
validação para `LeadName` e `Email` (Value Objects). Não existe setter público. Não existe
construtor público. Não tem como existir um `Lead` inválido em memória, porque a regra mora
junto com o dado que ela protege, em vez de espalhada em quem usa o dado.

### Risco 1: over-engineering

Para duas rotas e uma entidade, `UseCase` (interface) mais `Interactor` (implementação) mais
`LeadUseCaseConfig` (fiação manual) mais um teste de arquitetura é mais estrutura do que o
problema, sozinho, pediria. Um `@RestController` chamando um `@Service` resolveria em bem
menos código, e para um protótipo descartável seria a escolha certa.

A justificativa aqui é didática: mostrar o padrão com espaço para crescer, não afirmar que todo
CRUD de dois endpoints deveria se parecer com isso. Esse tipo de indireção compensa quando
existe mais de uma implementação de verdade (produção, teste, um segundo tipo de storage),
quando algum dia precisa entrar um decorator (log, cache, transação) sem tocar em quem chama,
ou quando o time cresce e a fronteira entre módulos vira uma trava que realmente importa. Numa
tela de protótipo, é peso sem retorno. Vale reconhecer isso antes de aplicar o padrão em todo
canto só por hábito.

### Risco 2: primitive obsession

O projeto original (antes deste refactor) tinha exatamente esse cheiro: o campo de nome era um
`String` cru, sem validação nenhuma, podendo ser `null` ou vazio sem que ninguém percebesse até
quebrar em produção. `LeadName` resolve isso.

Dito isso, é importante ser honesto sobre este exemplo específico: **`Name` e `Email` como
Value Objects definitivamente não seriam necessários num projeto real deste tamanho.** As
regras que eles protegem (nome não vazio, email com formato mínimo válido) são simples o
suficiente para caber numa validação direta, sem precisar de uma classe própria para cada
campo. Eles foram implementados como VO aqui só para completar o exemplo e mostrar o padrão
Value Object na prática, não porque o campo exigisse esse nível de proteção. O foco principal
deste projeto sempre foi separação de responsabilidades e SOLID, como explicado acima; Value
Object é um padrão tático de DDD que entrou de carona, não o ponto central da aula.

Isso não invalida o padrão: ele compensa quando o campo carrega uma regra de negócio real, que
precisa valer sempre, em todo lugar por onde o dado passa, e que tende a crescer com o tempo
(formato de documento, moeda, faixa de datas válida). Não vale a pena empacotar todo `String`
ou `int` do sistema em um Value Object só por princípio; isso também é over-engineering, só que
disfarçado de boa prática. O critério prático é: essa primitiva tem uma invariante que, se
quebrada, corrompe o sistema? Se sim, considere um Value Object. Se é só um rótulo sem regra
nenhuma, um `String` está ótimo.
