# Missão Marte Unifor

Projeto Integrador da disciplina Projeto e Arquitetura de Sistemas da UNIFOR, Grupo 01.

**Equipe:** Myrla Rodrigues, Cristiano Gonçalves e Iandeyara Farias.

O projeto evolui um jogo Java de console para um jogo libGDX desktop e Android, com servidor independente de ranking em Spring Boot e a melhoria da equipe: Campanha dos Fundadores.

## Situação atual

A Etapa 0 foi registrada no commit `6330869`. A Etapa 1 implementa o console da equipe em `fases/fase-01-refatoracao/` e preserva a Fase 0 em `fases/fase-00-original/`. Veja o [diagnóstico](docs/diagnostico.md), as [decisões](docs/adr/ADR-001-trilha-de-evolucao.md) e o [tutorial](fases/fase-01-refatoracao/docs/fase-01-tutorial.md).

A Etapa 1 está registrada nos commits `36af8b6` e `8842ac4`, presentes em `main` e na referência local `origin/main`. A Etapa 2 foi aprovada: as Fases 2 a 7 foram importadas, com documentação de origem e comandos para macOS. Os nove builds tentados pararam no download do Gradle por falha de DNS (`services.gradle.org`), antes da compilação e dos testes. Os commits da Etapa 2 estão pendentes pela restrição de escrita em `.git` nesta sessão. Consulte a [validação da Etapa 2](docs/validacao-etapa-02.md).

## Comportamento de referência para a Fase 1

A referência é a Fase 0 do professor (`fase-0-oopuro`). Os três fundadores representam os **tipos** Professor, Engenheiro e Astronauta, não a quantidade de passageiros de uma partida. O mapa tem 4 passageiros no fácil e 5 no médio ou difícil; a capacidade da nave é 5.

| Dificuldade | Passageiros | Asteroides | Inimigos | Pontuação inicial |
|---|---:|---:|---:|---:|
| Fácil | 4 | 1 | 1 | 30 |
| Médio | 5 | 2 | 2 | 20 |
| Difícil | 5 | 3 | 3 | 45 |

Controles: `w`, `a`, `s`, `d` para mover; `c` para embarcar; `q` para abortar. Ranking Top 5; nave com 3 vidas; vitória ao resgatar todos e retornar à plataforma `L` em `(0,0)`. Professor, Engenheiro e Astronauta valem, respectivamente, 10, 15 e 20 pontos.

Esses valores foram aplicados à Fase 1. Mapas pequenos demais para a dificuldade são recusados antes do sorteio, conforme correção aprovada.

## Índice das fases

Fases 0 a 7 estão disponíveis. A Fase 8 permanece planejada e não foi iniciada. Nas Fases 2 a 7, compilação e testes ainda dependem da resolução do bloqueio de download.

| Fase | Pasta | Situação | Origem |
|---|---|---|---|
| 00: original | `fases/fase-00-original/` | Importada; fontes preservados | Professor: `fase-0-oopuro` |
| 01: refatoração | `fases/fase-01-refatoracao/` | Console implementado; seis grupos de testes | Equipe |
| 02: Gradle | `fases/fase-02-gradle/` | Importada; build bloqueado no download | Base do professor |
| 03: libGDX | `fases/fase-03-libgdx/` | Importada; build bloqueado no download | Base do professor |
| 04: interface gráfica | `fases/fase-04-interface-grafica/` | Importada; build bloqueado no download | Base do professor |
| 05: servidor e ranking | `fases/fase-05-servidor-ranking/` | Importada; build bloqueado no download | Base do professor |
| 06: Android | `fases/fase-06-android/` | Importada; build bloqueado no download | Base do professor |
| 07: evolução livre | `fases/fase-07-evolucao-livre/` | Importada; build bloqueado no download | Base do professor |
| 08: Campanha dos Fundadores | `fases/fase-08-campanha-fundadores/` | Prevista na Etapa 3 | Equipe, a partir da Fase 7 |

## Ambiente

- JDK **17** para compilar e executar.
- macOS e IntelliJ IDEA usados pela equipe.
- Gradle: usar o Wrapper de cada fase, sem trocar versões.
- Android SDK: opcional; sua ausência deve ser registrada ao validar o módulo Android.

Confira e selecione o JDK no terminal:

```bash
/usr/libexec/java_home -V
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
java -version
javac -version
```

Confirme que ambos mostram 17 antes de compilar. No IntelliJ, selecione o mesmo JDK no projeto e no Gradle JVM, quando aplicável.

## Compilação e execução

### Fase 0

Na pasta `fases/fase-00-original/`:

```bash
mkdir -p out
find src -name '*.java' -print0 | xargs -0 javac --release 17 -encoding UTF-8 -d out
java -cp out Main
```

A Fase 0 não possui suíte automatizada. Use mapa de tamanho 5; a limitação de mapas pequenos foi preservada nessa fase.

### Fase 1 da equipe

Na pasta `fases/fase-01-refatoracao/`:

```bash
mkdir -p out
find src tests -name '*.java' -print0 | xargs -0 javac --release 17 -encoding UTF-8 -d out
java -ea -cp out missao.TodosOsTestes
java -cp out missao.app.Main
```

Os testes usam Java puro e incluem domínio, fábrica, motor, ranking, persistência e console. Execute o jogo dentro da pasta de cada fase para manter seus rankings separados.

### Fases 2 a 7 importadas

Os nove projetos Gradle receberam a tentativa de `./gradlew clean build --console=plain --no-daemon` com JDK 17. Todos retornaram código 1 ao baixar o Wrapper, sem executar tarefas ou testes. Os comandos abaixo permitem retomar a validação em um ambiente com acesso às distribuições e dependências. Execute cada um no diretório indicado. Cliente e servidor são builds independentes.

| Fase | Diretório de execução | Build e testes | Execução |
|---|---|---|---|
| 02 | `fases/fase-02-gradle` | `./gradlew build` e `./gradlew test` | `./gradlew run` |
| 03 | `fases/fase-03-libgdx` | `./gradlew build` e `./gradlew test` | `./gradlew :lwjgl3:run` |
| 04 | `fases/fase-04-interface-grafica` | `./gradlew build` e `./gradlew test` | `./gradlew :lwjgl3:run` |
| 05 | `fases/fase-05-servidor-ranking/cliente` e `servidor` | `./gradlew build` e `./gradlew test`, em cada subprojeto | Cliente: `./gradlew :lwjgl3:run`; servidor: `./gradlew bootRun` |
| 06 | `fases/fase-06-android/cliente` e `servidor` | `./gradlew build` e `./gradlew test`, em cada subprojeto | Cliente: `./gradlew :lwjgl3:run`; servidor: `./gradlew bootRun` |
| 07 | `fases/fase-07-evolucao-livre/cliente` e `servidor` | `./gradlew build` e `./gradlew test`, em cada subprojeto | Cliente: `./gradlew :lwjgl3:run`; servidor: `./gradlew bootRun` |

Os nove scripts `gradlew` importados estão executáveis. Os cinco módulos `lwjgl3` já configuram `-XstartOnFirstThread` no macOS; nenhuma alteração de build foi necessária. Nas Fases 6 e 7, use `./gradlew :android:assembleDebug` no cliente somente com Android SDK configurado. Não foi encontrado SDK configurado nesta sessão e nenhum APK foi gerado. A janela desktop e a API também não foram executadas, pois dependem do build.

As fases importadas preservam as regras do professor: embarque com `e` no console ou `E` no modo clássico e ranking Top 10. A Fase 7 também tem modo dinâmico com embarque automático. Esses comportamentos diferem do `c` e Top 5 da Fase 1 da equipe; a importação não altera nenhuma das duas trilhas.

O servidor usa as variáveis de ambiente `MISSAOMARTE_JWT_SEGREDO` e `MISSAOMARTE_DADOS`. A configuração detalhada deve seguir o README da fase importada; nunca versionar segredos ou dados locais.

## Origem do código

- **Equipe:** código em `fases/fase-01-refatoracao/src/missao/` e sua refatoração; documentação específica da equipe. A melhoria da Fase 8 está planejada.
- **Professor:** Fase 0 e Fases 2 a 7 importadas do [repositório do Prof. Marcelo Bezerra](https://github.com/marcelobezerra-dotcom/T200-ProjArqSist/tree/main/missaoMarteUnifor/projetoExtensaoFinal), com uso autorizado em sala conforme registrado nas instruções do projeto.
- Fases 2 a 7 copiadas da revisão `4b43e59eacfa57798d55b68c5d018015c804193e`, de `exercicio10/fases/`. Cada README contém o aviso de origem e a lista de adaptações da equipe. Fontes, assets, testes e configurações de build foram preservados.
- Os commits de importação por fase e os commits separados de adaptação estão previstos no [registro da Etapa 2](docs/validacao-etapa-02.md#commits-pendentes). Eles não puderam ser criados nesta sessão.

A base deve ficar fora deste repositório, em `../base-professor/missaoMarteUnifor/projetoExtensaoFinal/`, somente para leitura, sem submódulo. Para preparar a consulta, se o clone ainda não existir:

```bash
git clone https://github.com/marcelobezerra-dotcom/T200-ProjArqSist.git ../base-professor
git -C ../base-professor rev-parse HEAD
```

## Organização e próximos passos

As instruções de trabalho estão em [CLAUDE.md](CLAUDE.md) e [AGENTS.md](AGENTS.md), na raiz. Ambos registram os três tipos de passageiro e o comportamento da Fase 0 aprovado para a Fase 1 da equipe. Cada etapa exige aprovação própria. As Etapas 1 e 2 foram aprovadas; a Etapa 1 foi registrada no Git e a Etapa 2 está importada, com os bloqueios de validação e de commits documentados. A Etapa 3 exige autorização própria.

Commits seguem `tipo(escopo): descrição`, em português, com referência à issue quando identificada. A issue de organização ainda não foi informada. Não fazer push sem solicitação da equipe.

### Validação local da Etapa 1 (12/11/2026)

- JDK Temurin 17.0.21 selecionado explicitamente por JAVA_HOME e PATH.
- Compilação das duas fases e execução de seis grupos de testes da Fase 1.
- Comparação com a Fase 0: 150 cenários e 3000 turnos de inimigos idênticos.
- Fontes importados da Fase 0 comparados com a base, sem alterações.
- Base externa na revisão `4b43e59eacfa57798d55b68c5d018015c804193e`, mantida somente para leitura.
- Registro posterior da equipe: `36af8b6` (código) e `8842ac4` (documentação), confirmados no histórico local e na referência local `origin/main` antes da Etapa 2.
