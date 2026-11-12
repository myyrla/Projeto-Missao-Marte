# Missão Marte Unifor

Projeto Integrador da disciplina Projeto e Arquitetura de Sistemas da UNIFOR, Grupo 01.

**Equipe:** Myrla Rodrigues, Cristiano Gonçalves e Iandeyara Farias.

O projeto evolui um jogo Java de console para um jogo libGDX desktop e Android, com servidor independente de ranking em Spring Boot e a melhoria da equipe: Campanha dos Fundadores.

## Situação atual

A Etapa 0 organiza o repositório. O código da equipe permanece em `MissaoMarte/src/missao/`, com entidades e persistência parcialmente implementadas. O `Main`, o motor e a interface de console ainda estão vazios: não há partida jogável nem testes automatizados. A Etapa 1 depende de aprovação própria.

## Comportamento de referência para a Fase 1

A referência é a Fase 0 do professor (`fase-0-oopuro`). Os três fundadores representam os **tipos** Professor, Engenheiro e Astronauta, não a quantidade de passageiros de uma partida. O mapa tem 4 passageiros no fácil e 5 no médio ou difícil; a capacidade da nave é 5.

| Dificuldade | Passageiros | Asteroides | Inimigos | Pontuação inicial |
|---|---:|---:|---:|---:|
| Fácil | 4 | 1 | 1 | 30 |
| Médio | 5 | 2 | 2 | 20 |
| Difícil | 5 | 3 | 3 | 45 |

Controles: `w`, `a`, `s`, `d` para mover; `c` para embarcar; `q` para abortar. Ranking Top 5; nave com 3 vidas; vitória ao resgatar todos e retornar à plataforma `L` em `(0,0)`. Professor, Engenheiro e Astronauta valem, respectivamente, 10, 15 e 20 pontos.

Esses valores são o contrato aprovado para a futura Etapa 1. A correção dos valores locais do difícil será feita nessa etapa, sem alterar os fontes durante a organização do repositório.

## Índice das fases

As pastas abaixo são destinos planejados. Nenhuma fase foi importada ou movida nesta etapa.

| Fase | Pasta prevista | Situação | Origem |
|---|---|---|---|
| 00: original | `fases/fase-00-original/` | Importação prevista na Etapa 1 | Professor: `fase-0-oopuro` |
| 01: refatoração | `fases/fase-01-refatoracao/` | Código parcial em `MissaoMarte/`; aguardando Etapa 1 | Equipe |
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

### Código atual da equipe

Na raiz, com JDK 17 selecionado:

```bash
mkdir -p out
find MissaoMarte/src/missao -name '*.java' -print0 |
  xargs -0 javac --release 17 -encoding UTF-8 -d out
java -cp out missao.app.Main
```

Atualmente o `Main` não executa ações. Compilar com sucesso não comprova uma partida funcional. Ainda não existe comando de testes automatizados.

### Fases 0 e 1, após a Etapa 1

Os comandos abaixo são previstos e ainda não foram validados nas pastas de destino, que não existem.

Na pasta `fases/fase-00-original/`:

```bash
mkdir -p out
find src -name '*.java' -print0 |
  xargs -0 javac --release 17 -encoding UTF-8 -d out
java -cp out Main
```

Na pasta `fases/fase-01-refatoracao/`:

```bash
mkdir -p out
find src -name '*.java' -print0 |
  xargs -0 javac --release 17 -encoding UTF-8 -d out
java -cp out missao.app.Main
```

O comando de testes da Fase 1 será documentado quando a suíte for implementada e validada.

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

- **Equipe:** código atual em `MissaoMarte/src/missao/` e futura refatoração da Fase 1; documentação específica da equipe e melhoria da Fase 8.
- **Professor:** referência de estudo e futuras importações da Fase 0 e das Fases 2 a 7 do [repositório do Prof. Marcelo Bezerra](https://github.com/marcelobezerra-dotcom/T200-ProjArqSist/tree/main/missaoMarteUnifor/projetoExtensaoFinal), com uso autorizado em sala conforme registrado nas instruções do projeto.
- Cada importação terá commit próprio e aviso de origem no README da fase. Alterações da equipe serão identificadas separadamente.

A base deve ficar fora deste repositório, em `../base-professor/missaoMarteUnifor/projetoExtensaoFinal/`, somente para leitura, sem submódulo. Para preparar a consulta, se o clone ainda não existir:

```bash
git clone https://github.com/marcelobezerra-dotcom/T200-ProjArqSist.git ../base-professor
git -C ../base-professor rev-parse HEAD
```

## Organização e próximos passos

As instruções de trabalho estão em [CLAUDE.md](CLAUDE.md) e [AGENTS.md](AGENTS.md), na raiz. Ambos registram os três tipos de passageiro e o comportamento da Fase 0 aprovado para a Fase 1 da equipe. Cada etapa exige aprovação própria. A próxima é a Etapa 1: diagnóstico documentado, importação da Fase 0 e conclusão da Fase 1 da equipe. Não há autorização para executá-la junto com a Etapa 0.

Commits seguem `tipo(escopo): descrição`, em português, com referência à issue quando identificada. A issue de organização ainda não foi informada. Não fazer push sem solicitação da equipe.

### Conferência da Etapa 0 nesta sessão (12/11/2026)

- Repositório Git novo em `main`, ainda sem commits. Não foi necessário reinicializá-lo.
- `.gitignore` conferido com `git check-ignore` para saídas de compilação, caches, dados locais, Android e IntelliJ.
- JDK Temurin 17.0.21 instalado e selecionado com `JAVA_HOME` e `PATH` nos comandos desta validação; `java -version` e `javac -version` confirmaram 17.0.21. O shell inicia com Java 26.0.1, portanto a seleção explícita continua necessária.
- Base externa conferida em `../base-professor/missaoMarteUnifor/projetoExtensaoFinal/`, revisão `4b43e59eacfa57798d55b68c5d018015c804193e`, sem alterações locais. Nenhum arquivo da base foi importado ou editado.
- Os 22 fontes Java compilaram com `javac --release 17 -encoding UTF-8 -d out`. `java -cp out missao.app.Main` terminou com código 0, sem saída, pois o `Main` ainda está vazio. Isso não comprova uma partida funcional.
- Não há suíte de testes automatizados nesta versão. Nenhum fonte Java foi alterado e a Etapa 1 não foi iniciada.
- O registro dos commits continua impedido pela restrição de escrita da sessão em `.git/index.lock`. A organização está preparada, mas o aceite de commits e `git status` limpo permanece pendente.
