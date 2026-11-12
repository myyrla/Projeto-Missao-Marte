# Missão Marte Unifor

Projeto Integrador da disciplina Projeto e Arquitetura de Sistemas da UNIFOR, Grupo 01.

**Equipe:** Myrla Rodrigues, Cristiano Gonçalves e Iandeyara Farias.

O projeto evolui um jogo Java de console para um jogo libGDX desktop e Android, com servidor independente de ranking em Spring Boot e a melhoria da equipe: Campanha dos Fundadores.

## Situação atual

A Etapa 0 foi registrada no commit `6330869`. A Etapa 1 implementa o console da equipe em `fases/fase-01-refatoracao/` e preserva a Fase 0 em `fases/fase-00-original/`. Veja o [diagnóstico](docs/diagnostico.md), as [decisões](docs/adr/ADR-001-trilha-de-evolucao.md) e o [tutorial](fases/fase-01-refatoracao/docs/fase-01-tutorial.md).

A implementação da Etapa 1 está validada localmente; branch e commits estão pendentes pela restrição de escrita da sessão em `.git`. A Etapa 2 não foi iniciada.

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

Fases 0 e 1 estão disponíveis. Fases 2 a 8 são destinos planejados.

| Fase | Pasta prevista | Situação | Origem |
|---|---|---|---|
| 00: original | `fases/fase-00-original/` | Importada; fontes preservados | Professor: `fase-0-oopuro` |
| 01: refatoração | `fases/fase-01-refatoracao/` | Console implementado; seis grupos de testes | Equipe |
| 02: Gradle | `fases/fase-02-gradle/` | Prevista na Etapa 2 | Base do professor |
| 03: libGDX | `fases/fase-03-libgdx/` | Prevista na Etapa 2 | Base do professor |
| 04: interface gráfica | `fases/fase-04-interface-grafica/` | Prevista na Etapa 2 | Base do professor |
| 05: servidor e ranking | `fases/fase-05-servidor-ranking/` | Prevista na Etapa 2 | Base do professor |
| 06: Android | `fases/fase-06-android/` | Prevista na Etapa 2 | Base do professor |
| 07: evolução livre | `fases/fase-07-evolucao-livre/` | Prevista na Etapa 2 | Base do professor |
| 08: Campanha dos Fundadores | `fases/fase-08-campanha-fundadores/` | Prevista na Etapa 3 | Equipe, a partir da Fase 7 |

## Ambiente

- JDK **17** para compilar e executar.
- macOS e IntelliJ IDEA usados pela equipe.
- Gradle: usar o Wrapper de cada fase quando importada, sem trocar versões.
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

### Fases 2 a 8, após importação ou implementação

Os comandos abaixo são referências para validação futura. Antes de executá-los, conferir módulos e tarefas no Wrapper da fase importada. Nenhum build dessas fases foi executado neste repositório.

| Fase | Diretório de execução | Build e testes previstos | Execução prevista |
|---|---|---|---|
| 02 | `fases/fase-02-gradle` | `./gradlew build` e `./gradlew test` | `./gradlew run` |
| 03 | `fases/fase-03-libgdx` | `./gradlew build` e `./gradlew test` | `./gradlew :lwjgl3:run` |
| 04 | `fases/fase-04-interface-grafica` | `./gradlew build` e `./gradlew test` | `./gradlew :lwjgl3:run` |
| 05 | `fases/fase-05-servidor-ranking/cliente` e `servidor` | `./gradlew build` e `./gradlew test`, em cada subprojeto | Cliente: `./gradlew :lwjgl3:run`; servidor: `./gradlew bootRun` |
| 06 | `fases/fase-06-android/cliente` e `servidor` | `./gradlew build` e `./gradlew test`, em cada subprojeto | Cliente: `./gradlew :lwjgl3:run`; servidor: `./gradlew bootRun` |
| 07 | `fases/fase-07-evolucao-livre/cliente` e `servidor` | `./gradlew build` e `./gradlew test`, em cada subprojeto | Cliente: `./gradlew :lwjgl3:run`; servidor: `./gradlew bootRun` |
| 08 | `fases/fase-08-campanha-fundadores/cliente` e `servidor` | `./gradlew build` e `./gradlew test`, em cada subprojeto | Cliente: `./gradlew :lwjgl3:run`; servidor: `./gradlew bootRun` |

Se necessário, torne o Wrapper executável com `chmod +x gradlew`. Nas fases Android, o APK de debug será gerado por `./gradlew :android:assembleDebug`, no cliente, com o SDK configurado. No macOS, verificar o tratamento de `-XstartOnFirstThread` pelo launcher LWJGL3.

O servidor usa as variáveis de ambiente `MISSAOMARTE_JWT_SEGREDO` e `MISSAOMARTE_DADOS`. A configuração detalhada deve seguir o README da fase importada; nunca versionar segredos ou dados locais.

## Origem do código

- **Equipe:** código em `fases/fase-01-refatoracao/src/missao/` e sua refatoração; documentação específica da equipe e melhoria da Fase 8.
- **Professor:** Fase 0 importada, referência de estudo e futuras importações das Fases 2 a 7 do [repositório do Prof. Marcelo Bezerra](https://github.com/marcelobezerra-dotcom/T200-ProjArqSist/tree/main/missaoMarteUnifor/projetoExtensaoFinal), com uso autorizado em sala conforme registrado nas instruções do projeto.
- Cada importação terá commit próprio e aviso de origem no README da fase. Alterações da equipe serão identificadas separadamente.

A base deve ficar fora deste repositório, em `../base-professor/missaoMarteUnifor/projetoExtensaoFinal/`, somente para leitura, sem submódulo. Para preparar a consulta, se o clone ainda não existir:

```bash
git clone https://github.com/marcelobezerra-dotcom/T200-ProjArqSist.git ../base-professor
git -C ../base-professor rev-parse HEAD
```

## Organização e próximos passos

As instruções de trabalho estão em [CLAUDE.md](CLAUDE.md) e [AGENTS.md](AGENTS.md), na raiz. Ambos registram os três tipos de passageiro e o comportamento da Fase 0 aprovado para a Fase 1 da equipe. Cada etapa exige aprovação própria. A Etapa 1 foi aprovada e implementada. A próxima é a Etapa 2, que exige autorização própria.

Commits seguem `tipo(escopo): descrição`, em português, com referência à issue quando identificada. A issue de organização ainda não foi informada. Não fazer push sem solicitação da equipe.

### Validação local da Etapa 1 (12/11/2026)

- JDK Temurin 17.0.21 selecionado explicitamente por JAVA_HOME e PATH.
- Compilação das duas fases e execução de seis grupos de testes da Fase 1.
- Comparação com a Fase 0: 150 cenários e 3000 turnos de inimigos idênticos.
- Fontes importados da Fase 0 comparados com a base, sem alterações.
- Base externa na revisão `4b43e59eacfa57798d55b68c5d018015c804193e`, mantida somente para leitura.
- Git ainda não permite criar branch, index ou commits nesta sessão. `6330869` permanece como base da Etapa 0; as alterações da Etapa 1 aguardam registro. Nenhum push foi feito.
