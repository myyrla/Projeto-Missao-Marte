# CLAUDE.md · Missão Marte Unifor (Projeto Integrador, Grupo 01)

Este arquivo orienta o Claude Code neste repositório. Leia-o inteiro antes de qualquer ação.

## 1. Contexto

- Jogo: **Missão Marte Unifor**. A nave anda em um grid (casa por casa), resgata passageiros dos três tipos (Professor, Engenheiro, Astronauta) e volta à plataforma de pouso `L` em (0,0), desviando de asteroides e inimigos, com 3 vidas e ranking. Na Fase 0 e na Fase 1 da equipe, o mapa cria 4 passageiros no fácil e 5 no médio ou difícil, com capacidade 5 na nave.
- Narrativa: expedição da UNIFOR a Marte em 2087 para fundar o primeiro campus interplanetário. Os **três fundadores** representam os três tipos de passageiro, não a quantidade de passageiros por partida.
- Referência de comportamento da Fase 1: `fase-0-oopuro` do professor, com `w a s d` para mover, `c` para embarcar, `q` para abortar e ranking Top 5. Pontuação inicial fácil/médio/difícil: 30/20/45; passageiros: 4/5/5; asteroides: 1/2/3; inimigos: 1/2/3. Professor/Engenheiro/Astronauta valem 10/15/20 pontos. Corrigir os valores locais do difícil na Etapa 1, sem adotar as mudanças de regras da Fase 1 do professor.
- Disciplina: Projeto e Arquitetura de Sistemas (UNIFOR). Equipe: Myrla Rodrigues, Cristiano Gonçalves, Iandeyara Farias.
- Enunciado: evoluir o jogo de console até um jogo libGDX multiplataforma (desktop + Android), com servidor Spring Boot de ranking e uma **melhoria própria da equipe**, organizado em Scrum/Kanban no GitHub Projects.
- O professor **autorizou em sala** o uso do código do repositório dele por causa do prazo e pediu para a equipe **focar no jogo**. Todo código reaproveitado deve ter a origem registrada (seção 3).

### Cronograma (fixo)

| Sprint | Período | Foco neste repositório |
|---|---|---|
| 2 | até 15/11/2026 | Etapas 0, 1 e 2 (organização, Fase 1 da equipe, importação das Fases 2 a 7) |
| 3 | 16/11 a 22/11 | Servidor rodando localmente + domínio da Fase 8 com testes |
| 4 | 23/11 a 29/11 | Telas e visual da Fase 8, APK de debug, documentação |
| 5 | 30/11 a 03/12 | Congelamento: só correções que impeçam a demonstração. Relatório e apresentação |

Nenhuma funcionalidade nova depois de 29/11.

## 2. Regras de trabalho (obrigatórias)

1. **Analise antes de alterar.** Em cada etapa, leia o código envolvido e apresente: diagnóstico, escopo, arquivos e classes afetados, padrões que serão usados e por quê, riscos, critérios de conclusão e como vai validar.
2. **Espere aprovação explícita** antes de criar, mover, copiar ou editar arquivos. Aprovar uma etapa não autoriza a próxima. Se o escopo precisar mudar no meio, pare e peça nova aprovação.
3. **Preserve o histórico das fases.** Nunca altere a fase original nem fases anteriores. Cada fase nova nasce de uma cópia da anterior, em pasta própria.
4. **Java 17** em todo o projeto. Use os Gradle Wrappers que já existem em cada fase. Não troque versões de Gradle, libGDX, AGP ou Spring Boot sem pedir.
5. **Regras do jogo ficam no domínio/aplicação**, nunca nas telas. O domínio não importa nada de `com.badlogic.*`, de `java.util.Scanner` nem de `System.out`.
6. **Cliente e servidor são independentes.** O servidor não depende do código do cliente. A API nunca expõe entidades JPA (use DTOs).
7. **Segredos:** nunca grave senhas, tokens, chaves JWT ou o token do GitHub em código, arquivos versionados, logs ou capturas. Use variáveis de ambiente (`MISSAOMARTE_JWT_SEGREDO`, `MISSAOMARTE_DADOS`).
8. **Testes:** depois de implementar, rode o build e os testes da pasta afetada e informe exatamente o que passou, o que falhou e o que não pôde ser executado (por exemplo, falta de Android SDK), com o motivo.
9. **Commits pequenos e frequentes**, mensagens em português no formato `tipo(escopo): descrição` (ex.: `feat(fase-08): adiciona Estação NAMI ao domínio`). Um commit por mudança lógica. Não faça push sem a equipe pedir. Não reescreva histórico.
10. **Textos e documentação** em português do Brasil. **Nunca use o travessão "—"** em nenhum texto (README, tutoriais, comentários, relatório, mensagens de commit). Use dois-pontos, vírgula ou parênteses.
11. Exemplos de código na documentação sempre em **Java**.
12. Não invente funcionalidades que já existem nem reescreva o projeto sem necessidade. Evolua o que existe.

### Ambiente

- A equipe usa **macOS** (IntelliJ IDEA). Os READMEs do professor trazem comandos PowerShell; aqui use `./gradlew` (rode `chmod +x gradlew` se precisar).
- Confira o JDK: `java -version` deve mostrar 17. No macOS: `/usr/libexec/java_home -V` e `export JAVA_HOME=$(/usr/libexec/java_home -v 17)`.
- Android SDK é opcional. Sem ele, o módulo `android` é ignorado e isso deve ser registrado como limitação.

## 3. Fontes de referência

### Repositório do professor (somente leitura)

- URL: https://github.com/marcelobezerra-dotcom/T200-ProjArqSist/tree/main/missaoMarteUnifor/projetoExtensaoFinal
- Clone **fora** deste repositório, ao lado dele:
  ```bash
  git clone https://github.com/marcelobezerra-dotcom/T200-ProjArqSist.git ../base-professor
  ```
  Caminho da base: `../base-professor/missaoMarteUnifor/projetoExtensaoFinal/`
- Nunca edite `../base-professor`. Nunca o adicione como submódulo.
- Arquivos úteis da base:
  - `ENUNCIADO_AVA.md`: enunciado oficial.
  - `PROMPT_MESTRE.md`: regras de cada fase e estrutura do tutorial de fase (12 seções).
  - `Issues_GitHub_Missao_Marte.md` e `.json`: backlog MM-01 a MM-15 com critérios de aceite.
  - `tools/github-issues-importer/`: importador das issues para o GitHub Project.
  - `exercicio10/fases/fase-0-oopuro` até `fase-07-evolucao-livre`: implementação de referência.

### Como registrar código reaproveitado

- No topo do `README.md` de cada fase importada:
  `> Base: Fase XX do repositório do Prof. Marcelo Bezerra (T200-ProjArqSist), uso autorizado em sala. Alterações da equipe listadas abaixo.`
- No `README.md` da raiz e no relatório técnico, uma seção "Origem do código" deixando claro o que é da base e o que é da equipe.
- Commit de importação separado, sem misturar com alterações da equipe: `chore(fase-XX): importa base do professor`.

### Stack da base (Fase 7)

- Cliente: Gradle 8.7, libGDX 1.12.1, AGP 8.6.1, JUnit 5.10.2, módulos `core`, `lwjgl3`, `android`, pacote `br.edu.unifor.missaomarte`.
  - `core/domain`: `Nave`, `Missao`, `Asteroide`, `Inimigo`, `Passageiro` (`Professor`, `Engenheiro`, `Astronauta`), `Posicao`, `Base`, `Perigo`, `ObjetoRenderizavel`, `MovimentoDoInimigo` (Strategy: `MovimentoAleatorio`, `MovimentoPerseguidor`), `ResultadoDoTiro`.
  - `core/application`: `GameSession` (controlador da partida), `MissaoFactory`, `ModoDeJogo`, `Temporizador`, `RegrasDeTempoReal`, `RegrasDificuldade`, `EventoJogo`, `ResultadoTurno`, `RankingService`, `ranking/`, `servidor/` (porta `ServidorDoJogo`, `Sessao`, `ArmazemDeTexto`).
  - `core/gdx`: telas (`MenuScreen`, `GameScreen`, `NovaPartidaScreen`, `RankingScreen`, `LoginScreen`, `HistoricoScreen`), `hud/`, `visual/`, `layout/`, `entrada/`, `recursos/`, `audio/`, `rede/`.
  - `tools/`: geradores de assets (`GeradorDeAssets`, `GeradorDeNovosAssets`).
  - Cerca de 208 testes no cliente.
- Servidor: Spring Boot 4.1.2, Spring Security + OAuth2 Resource Server (JWT), JPA + H2 em arquivo, springdoc (Swagger em `/swagger-ui.html`), cerca de 36 testes.
  - Endpoints: `/api/usuarios` (cadastro), `/api/auth/login`, `/api/partidas` e `/api/partidas/minhas`, `/api/ranking`, `/api/saude`.

## 4. Estado atual deste projeto (ponto de partida)

Projeto IntelliJ `JogoMissaoMarte`, código da equipe em `MissaoMarte/src/missao/`, já parcialmente refatorado:

```text
missao/
  app/          Main
  engine/       (motor da partida: verificar o conteúdo)
  model/        Asteroide, Astronauta, Colisao, Dificuldade, Engenheiro, EntidadeMapa,
                Inimigo, Missao, Nave, Passageiro, Professor, RankingEntry
  persistence/  JsonRankingRepository, RankingRepository
  ui/           ConsoleUI, MapaRenderer, OpcaoMenu, UI
```

Ainda não tem: Gradle, testes automatizados, documentação de arquitetura, estrutura de fases, libGDX, servidor, Android.

## 5. Estrutura alvo do repositório

```text
JogoMissaoMarte/                      raiz do repositório Git
  CLAUDE.md
  README.md                           índice das fases, como executar cada uma, origem do código
  .gitignore                          build/, .gradle/, out/, *.iml locais, local.properties, ranking*.json, dados H2
  docs/
    diagnostico.md                    gerado na Etapa 1
    adr/                              decisões arquiteturais (ADR-001, ADR-002...)
    relatorio-tecnico.md              entregável final (2 a 4 páginas)
    retrospectiva.md
  fases/
    fase-00-original/                 jogo de console original (cópia de fase-0-oopuro da base)
    fase-01-refatoracao/              código DA EQUIPE (hoje em MissaoMarte/src), com SOLID e GRASP
    fase-02-gradle/                   base do professor
    fase-03-libgdx/                   base do professor
    fase-04-interface-grafica/        base do professor
    fase-05-servidor-ranking/         base do professor (cliente/ e servidor/)
    fase-06-android/                  base do professor
    fase-07-evolucao-livre/           base do professor
    fase-08-campanha-fundadores/      MELHORIA DA EQUIPE, a partir da fase-07 (cliente/ e servidor/)
```

`MissaoMarte/` só é movida para `fases/fase-01-refatoracao/` com `git mv` depois de aprovado, para preservar o histórico dos arquivos. Ajuste o `.iml`/configuração do IntelliJ se necessário.

**Decisão de trilha (registrar em `docs/adr/ADR-001-trilha-de-evolucao.md`):** por causa do prazo, a Fase 1 é o trabalho da própria equipe e as Fases 2 a 7 vêm da base do professor (autorizado). O esforço da equipe se concentra na Fase 8. A ADR deve explicar o motivo, a alternativa descartada (evoluir o código da equipe até a Fase 7) e as consequências (pacotes diferentes entre Fase 1 e Fase 2+).

## 6. Arquitetura e princípios

Camadas (mesma ideia na Fase 1 e na base):

| Camada | Responsabilidade | Não pode depender de |
|---|---|---|
| `domain` | Entidades e regras (movimento, colisão, embarque, vidas, vitória/derrota) | aplicação, UI, libGDX, arquivos, rede |
| `application` | Orquestra a partida e os casos de uso, define portas (interfaces) | UI concreta, libGDX, implementações de infraestrutura |
| `infrastructure` | Implementa as portas (arquivo de ranking, HTTP, preferências) | UI |
| `console` / `gdx` | Entrada e apresentação | regras de jogo (só chama a aplicação) |

Onde cada princípio deve aparecer (use isto para o checklist da Fase 1 e para o relatório):

| Princípio | Onde | Exemplo esperado |
|---|---|---|
| SRP | Cada classe com um motivo para mudar | Renderização separada de regra; ranking separado da partida |
| OCP | Novas mecânicas sem editar as existentes | Novo tipo de casa (Estação NAMI) implementa interface do domínio; `MovimentoDoInimigo` |
| LSP | Subtipos substituíveis | `Professor`, `Engenheiro`, `Astronauta` usados como `Passageiro` sem `instanceof` |
| ISP | Interfaces pequenas | `ObjetoRenderizavel`, `Perigo`, `RankingRepository` separadas |
| DIP | Aplicação depende de abstrações | `GameSession` recebe `RankingRepository`/`ServidorDoJogo` por construtor |
| GRASP Controller | Recebe as ações do jogador | `GameSession` (ou o motor em `engine/` na Fase 1) |
| GRASP Creator | Quem monta o mapa | `MissaoFactory` |
| GRASP Information Expert | Quem tem a informação decide | `Missao` sabe se uma casa está livre; `Nave` sabe suas vidas |
| GRASP Polymorphism | Comportamento por tipo | Passageiros, perigos, estratégias de movimento |
| GRASP Pure Fabrication | Classes técnicas sem equivalente no domínio | Repositórios, `Temporizador` |
| GRASP Indirection / Protected Variations | Portas entre camadas | Interface `UI` (Fase 1), `ServidorDoJogo`, `Plataforma` |
| GRASP Low Coupling / High Cohesion | Todas as camadas | Telas sem regra; domínio sem dependências externas |

Padrões GoF já presentes ou previstos: Strategy (movimento dos inimigos, mecânicas de etapa), Factory (`MissaoFactory`), Observer/eventos (`EventoJogo`), Adapter (repositórios e cliente HTTP).

## 7. Roteiro por etapas

Cada etapa abaixo vira issues no GitHub Project (colunas Backlog, Em andamento, Em revisão, Concluído), com responsável, sprint e critérios de aceite. Indique a issue (MM-xx ou nova) no commit e no PR.

### Etapa 0 · Organização do repositório (Sprint 2)

1. Verificar se a raiz já é um repositório Git; se não for, propor `git init -b main`.
2. Criar `.gitignore` (seção 5) e garantir que nada de `build/`, `out/`, `.gradle/`, `ranking*.json`, `local.properties` ou dados do H2 esteja versionado.
3. Clonar a base em `../base-professor` (seção 3).
4. Criar o `README.md` da raiz com: objetivo, equipe, índice das fases (tabela fase, pasta, situação, origem), requisitos de ambiente, comandos de build/teste/execução por fase, origem do código.

Aceite: `git status` limpo depois do commit; README com o índice; nenhum artefato de build versionado.

### Etapa 1 · Fase 0 e Fase 1 da equipe (Sprint 2, issues MM-01 e MM-02)

1. Copiar `fase-0-oopuro` da base para `fases/fase-00-original/` (é o jogo de console original, ponto de comparação).
2. Gerar `docs/diagnostico.md` sobre o código da equipe (`MissaoMarte/src/missao`):
   - classes, responsabilidades e dependências entre pacotes (diagrama Mermaid de pacotes e de classes);
   - fluxo de uma partida (diagrama de sequência Mermaid);
   - comparação com `fase-01-refatoracao` da base: o que está igual, o que falta, o que a equipe fez diferente e por quê;
   - violações encontradas, com arquivo e linha.
3. Checklist que a Fase 1 da equipe precisa cumprir (corrigir o que faltar, sem mudar regras):
   - [ ] Nenhuma classe de `model/` ou `engine/` usa `Scanner`, `System.out` ou classes de `ui/`.
   - [ ] O motor da partida recebe ações (ex.: enum de ações) e devolve resultado/estado; a `ConsoleUI` traduz teclas em ações e desenha o estado.
   - [ ] O motor depende de `RankingRepository` e `UI` como interfaces, injetadas no `Main` (composition root).
   - [ ] A criação do mapa fica em uma fábrica (Creator), não espalhada no motor.
   - [ ] Passageiros tratados por polimorfismo, sem cadeias de `instanceof`/`if` por tipo.
   - [ ] Regras de dificuldade isoladas (enum ou classe de configuração).
   - [ ] Comportamento observável igual ao original: mesmos comandos (`w a s d`, embarque, abortar), mesmas regras de vitória, derrota e pontuação, mesmo menu.
4. Mover o código para `fases/fase-01-refatoracao/` (com `git mv`), com `README.md` (compilar e executar com `javac`, como na base) e `docs/fase-01-tutorial.md` seguindo as 12 seções do `PROMPT_MESTRE.md`.
5. `docs/adr/ADR-001-trilha-de-evolucao.md` (seção 5) e `ADR-002-camadas-e-portas.md`.

Aceite: o jogo de console da equipe compila e roda; checklist completo; diagramas antes/depois no tutorial.

### Etapa 2 · Importação das Fases 2 a 7 (Sprint 2, issues MM-03 a MM-14 como referência)

1. Copiar `fase-02-gradle` até `fase-07-evolucao-livre` da base para `fases/`, **sem** `build/`, `.gradle/`, `out/`, `ranking.json`, `local.properties` ou bancos H2. Um commit por fase.
2. Adicionar o aviso de origem no topo de cada README (seção 3) e corrigir os comandos para macOS (`./gradlew`).
3. Validar cada fase e registrar o resultado no README da raiz:
   ```bash
   cd fases/fase-02-gradle && ./gradlew build
   cd fases/fase-04-interface-grafica && ./gradlew build && ./gradlew :lwjgl3:run
   cd fases/fase-07-evolucao-livre/servidor && ./gradlew test
   cd fases/fase-07-evolucao-livre/cliente && ./gradlew clean build
   ```
4. No macOS, a janela LWJGL3 pode precisar de `-XstartOnFirstThread`. Se `:lwjgl3:run` falhar por isso, verificar se o launcher da base já trata; se não, registrar a correção como alteração da equipe.

Aceite: todas as fases compilam e os testes passam (ou a falha está documentada com motivo).

### Etapa 3 · Fase 8: Campanha dos Fundadores (Sprints 3 e 4, melhoria da equipe)

Criar `fases/fase-08-campanha-fundadores/` como cópia de `fase-07-evolucao-livre` (cliente e servidor). O servidor **não muda** nesta fase.

#### 3.1 Ideia

A partida continua com **as mesmas regras** (resgatar os passageiros e voltar à `L`). A campanha é uma camada **acima** da `GameSession` (OCP): uma sequência de missões em locais de Marte. Cada vitória "resgata" o fundador daquela etapa e libera construções do **Campus UNIFOR Marte**, inspirado no campus real de Fortaleza.

| Etapa | Local | Fundador | Configuração | Construções liberadas |
|---|---|---|---|---|
| 1 | Planície Utopia | Astronauta | Fácil, modo dinâmico, sem perseguidor (missão de aprendizado) | Torre de Comunicação, Zonas de pouso (acessos A1 a A7) |
| 2 | Cratera Jezero | Professor | Médio, 1 Estação NAMI no mapa | Blocos de aula, Biblioteca |
| 3 | Valles Marineris | Engenheiro | Difícil, 1 Estação NAMI, 2 pares de paradas do rover, tempestade de areia | Prefeitura, NAMI, Rede do rover |
| Final | Inauguração | Os 3 | Tela de encerramento, sem partida | Reitoria |

Regras da campanha:
- Vitória avança para a próxima etapa; derrota ou abandono mantém a etapa atual (pode tentar de novo).
- Pontuação da campanha = soma das melhores pontuações de cada etapa.
- Cada partida da campanha é enviada ao servidor como uma partida normal (sem alterar a API). Registrar como limitação: o ranking global não distingue partidas de campanha.
- O progresso fica salvo localmente pela porta de armazenamento que a base já usa (`ArmazemDeTexto`/preferências), funcionando também no modo convidado.

#### 3.2 Novas mecânicas (domínio, sem libGDX)

1. **Estação NAMI:** casa especial que recupera 1 vida da nave. Uso único por partida. Não ultrapassa o máximo de vidas. Não é obstáculo nem perigo.
2. **Paradas do rover elétrico:** casas em pares. Ao entrar em uma parada, a nave vai para a parada par. Não teleporta de volta no mesmo turno/passo (evitar loop). Inimigos ignoram as paradas.
3. **Tempestade de areia:** evento de etapa que reduz o raio de visão do jogador por um tempo. O domínio/aplicação expõe algo como `estaVisivel(Posicao)`; a tela só escurece o que não é visível. O tempo entra como parâmetro (`Temporizador`), para testar sem relógio real.

Sugestão de classes (ajuste aos nomes e interfaces reais da base depois de lê-las):

```java
// application/campanha
public enum Fundador { ASTRONAUTA, PROFESSOR, ENGENHEIRO }

public enum Construcao {
    TORRE_DE_COMUNICACAO(Fundador.ASTRONAUTA), ZONAS_DE_POUSO(Fundador.ASTRONAUTA),
    BLOCOS_DE_AULA(Fundador.PROFESSOR), BIBLIOTECA(Fundador.PROFESSOR),
    PREFEITURA(Fundador.ENGENHEIRO), NAMI(Fundador.ENGENHEIRO), REDE_DO_ROVER(Fundador.ENGENHEIRO),
    REITORIA(null); // inaugurada quando os 3 fundadores forem resgatados

    private final Fundador responsavel;
    Construcao(Fundador responsavel) { this.responsavel = responsavel; }
    public Fundador responsavel() { return responsavel; }
}

public record EtapaDaCampanha(int numero, String local, Fundador fundador,
                              Dificuldade dificuldade, MecanicasDaEtapa mecanicas) { }

public final class Campanha {               // Information Expert do progresso
    public EtapaDaCampanha etapaAtual() { ... }
    public void registrarResultado(boolean vitoria, int pontos) { ... }
    public boolean concluida() { ... }
    public Campus campus() { ... }
}

public interface RepositorioDeProgresso {   // porta (DIP); implementação em infrastructure
    Optional<ProgressoDaCampanha> carregar();
    void salvar(ProgressoDaCampanha progresso);
}
```

- `MissaoFactory`: adicionar a criação a partir de uma `EtapaDaCampanha` **sem mudar** os sorteios dos modos clássico e dinâmico existentes (os testes da Fase 7 provam isso). Estações e paradas só em casas livres e alcançáveis a partir da `L`.
- `GameSession`: integrar NAMI, rover e tempestade como regras plugáveis (Strategy ou lista de regras de etapa), sem `if` por número de etapa.

#### 3.3 Telas e visual (gdx)

- `MenuScreen`: nova opção **Campanha** (a Partida rápida continua como antes).
- `CampusScreen`: mapa visto de cima do Campus UNIFOR Marte, inspirado na disposição real do campus de Fortaleza (avenidas nas bordas, Blocos A a E e Reitoria à direita, Biblioteca ao lado, blocos centrais, Blocos N a R embaixo, Estádio e Ginásio à esquerda, lago virando "gelo de água"). Estados: **construído** (cores acesas), **planta** (contorno tracejado na cor do fundador), **inauguração** (Reitoria). Mostra a lista das etapas, o progresso (x de 3 fundadores) e o botão para iniciar a próxima missão. Não usar a imagem do mapa oficial nem o logo da UNIFOR: redesenhar no estilo Marte.
- `GameScreen`/`MundoRenderer`: desenhar Estação NAMI, paradas do rover (com a rota pontilhada) e a tempestade (escurecer fora do raio de visão). HUD com o objetivo da etapa e os 3 fundadores (resgatado / pendente).
- Direção de arte (mockups no canvas da equipe): terreno marciano com sombras, sinais pulsantes nos passageiros, raio de alcance dos inimigos.
  - Paleta: fundo `#0A0612`, painel `#0C0814` (82% opacidade), terreno `#C25A2E` a `#7A2F16`, destaque âmbar `#FFB000`, ciano `#4CC9F0`, verde `#9EF01A`, vermelho inimigo `#FF3B5C`, lilás inauguração `#B388FF`, texto `#F2EEF7`, texto secundário `#B9B2C6`.
  - Cores por fundador: Astronauta ciano, Professor âmbar, Engenheiro verde.
  - Fontes de referência: Chakra Petch (títulos/HUD) e Inter (texto). Se for usar TTF, avaliar `gdx-freetype` (pedir aprovação, é dependência nova) ou gerar `BitmapFont` com Hiero.
- Novos sprites e sons pelo mesmo caminho da base (`tools/GeradorDeNovosAssets` ou arquivos em `assets/`), carregados e liberados no ciclo de vida (`dispose`).
- Layout responsivo e toque: reaproveitar `layout/` e `entrada/` da base; alvos de toque com pelo menos 48 dp.

#### 3.4 Testes obrigatórios da Fase 8

- `CampanhaTest`: começa na etapa 1; vitória avança; derrota não avança; não pula etapa; após vencer a etapa 3 a campanha fica concluída e a Reitoria é inaugurada; pontuação total soma as melhores de cada etapa.
- `CampusTest`: todas as construções começam em planta; vencer a etapa do Astronauta constrói só as dele.
- `EstacaoNamiTest`: recupera 1 vida; não passa do máximo; uso único; não conta como colisão.
- `ParadaDoRoverTest`: entra em uma parada e sai no par; não volta no mesmo passo; inimigos não teleportam.
- `TempestadeTest`: casa dentro do raio é visível, fora não; a tempestade termina após a duração (tempo como parâmetro).
- `MissaoFactoryCampanhaTest`: mesma semente gera o mesmo mapa; estações e paradas em casas livres e alcançáveis.
- `RepositorioDeProgressoTest`: salva e carrega o progresso.
- **Regressão:** todos os testes herdados da Fase 7 continuam passando.

Aceite da Fase 8: `./gradlew clean build` verde no cliente; campanha jogável do início à inauguração no desktop; partida rápida igual à Fase 7; `docs/fase-08-tutorial.md` (12 seções) e README da fase com controles, regras novas e limitações.

### Etapa 4 · Android e servidor (Sprint 4, issues MM-10 a MM-12)

1. `./gradlew :android:assembleDebug` na Fase 8 (se houver SDK). Sem SDK: registrar a limitação e mostrar evidência alternativa (modo toque no desktop com `-Dmissaomarte.toque=true` e `-Dmissaomarte.janela=540x1200`).
2. Servidor da Fase 8 rodando com `./gradlew bootRun`, `MISSAOMARTE_JWT_SEGREDO` definido por variável de ambiente; demonstrar cadastro, login, envio de partida e ranking pelo Swagger e pelo jogo.

### Etapa 5 · Fechamento (Sprint 5)

1. Revisar todos os comandos do README rodando-os do zero.
2. `docs/relatorio-tecnico.md` (2 a 4 páginas): arquitetura e decisões com trechos de código; padrões e princípios (tabela da seção 6 com classes reais); testes e resultados (números); dificuldades, limitações (incluindo: o servidor não valida se a pontuação é legítima, o ranking não distingue campanha) e melhorias futuras; origem do código; participação de cada integrante.
3. `docs/retrospectiva.md`; Kanban com todas as issues fechadas ou justificadas.

## 8. Fora do escopo agora

- Port para Nintendo Switch (homebrew `.nro`, C++ com libnx) e para ESP32 (CYD). São bônus para depois de 29/11, em pasta própria (`bonus/`), e não devem atrasar nenhuma etapa acima.
- Personagens extras (médica do NAMI, artista do Teatro, atleta do Ginásio) e coleta de gelo de água: listar como melhorias futuras.

## 9. Primeira execução

1. Leia este arquivo, a estrutura atual do projeto e o `ENUNCIADO_AVA.md` da base (clone a base se ainda não existir em `../base-professor`; clonar não altera este repositório).
2. **Não altere nada ainda.** Apresente:
   - o diagnóstico resumido do código atual (seção 4) contra o checklist da Etapa 1;
   - o plano detalhado das Etapas 0 e 1 (arquivos, comandos, commits previstos);
   - dúvidas que bloqueiam.
3. Aguarde aprovação.
