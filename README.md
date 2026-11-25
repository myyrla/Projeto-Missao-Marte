# Missão Marte Unifor

Projeto Integrador da disciplina Projeto e Arquitetura de Sistemas da Universidade de Fortaleza. O repositório registra a evolução de um jogo Java de console até um jogo libGDX para desktop e Android, integrado a um servidor Spring Boot de autenticação e ranking.

## Equipe

Grupo 01:

- Myrla Rodrigues
- Cristiano Gonçalves
- Iandeyara Farias

O planejamento e o acompanhamento da Sprint estão no [Kanban do projeto](https://github.com/users/myyrla/projects/3).

## Mapa das fases

Cada fase é independente e mantém seu próprio código, README e, quando aplicável, Gradle Wrapper. As pastas preservam o histórico didático da evolução.

| Fase | Conteúdo | Tecnologia principal | Origem |
|---|---|---|---|
| [00: jogo original](fases/fase-00-original/README.md) | Referência inicial do jogo de console | Java puro | Base do professor |
| [01: refatoração](fases/fase-01-refatoracao/README.md) | Refatoração do console com camadas, portas, SOLID e GRASP | Java puro | Equipe |
| [02: Gradle](fases/fase-02-gradle/README.md) | Automação do build e testes | Gradle | Base do professor |
| [03: libGDX](fases/fase-03-libgdx/README.md) | Primeira versão desktop com libGDX | libGDX e LWJGL3 | Base do professor |
| [04: interface gráfica](fases/fase-04-interface-grafica/README.md) | Telas, recursos visuais, áudio e entrada gráfica | libGDX | Base do professor |
| [05: servidor e ranking](fases/fase-05-servidor-ranking/README.md) | Cadastro, login, partidas e ranking remoto | Spring Boot e H2 | Base do professor |
| [06: Android](fases/fase-06-android/README.md) | Cliente Android, toque e publicação do servidor | Android e libGDX | Base do professor |
| [07: evolução livre](fases/fase-07-evolucao-livre/README.md) | Modo dinâmico, tiro, perseguidores e layout responsivo | libGDX | Base do professor |
| [08: Campanha dos Fundadores](fases/fase-08-campanha-fundadores/README.md) | Campanha, Campus UNIFOR Marte, NAMI, tempestade, nova pontuação e interface própria | libGDX, Android e Spring Boot | Equipe, a partir da Fase 7 |

A Fase 8 é a entrega principal da equipe e deve ser usada nas demonstrações atuais.

## Requisitos

- JDK 17 para todos os projetos.
- OpenGL 2.0 ou superior para o cliente desktop.
- Android SDK com plataforma 35 e Build Tools 34.0.0 para gerar o APK.
- Gradle Wrapper da própria fase. Não é necessário instalar Gradle globalmente.
- `openssl` para gerar o segredo temporário do servidor nos exemplos abaixo.

No macOS, confira o JDK ativo:

```bash
/usr/libexec/java_home -V
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
java -version
```

## Início rápido da Fase 8

### Modos disponíveis

A **partida rápida** preserva o comportamento do jogo original e registra vitórias no ranking local ou global, conforme o login e a disponibilidade do servidor. A **Campanha dos Fundadores** é a evolução criada pela equipe: possui três etapas, progresso do campus e pontuação própria, e suas partidas não entram em nenhum ranking.

### Cliente desktop

```bash
cd fases/fase-08-campanha-fundadores/cliente
./gradlew clean build
./gradlew :lwjgl3:run --console=plain
```

O servidor é opcional para jogar. Sem login ou sem conexão, o cliente continua disponível e usa o ranking local nas partidas rápidas.

### Opções de desenvolvimento

As opções são propriedades da JVM repassadas pelo launcher desktop:

| Propriedade | Exemplo | Efeito |
|---|---|---|
| `missaomarte.campanha.etapa` | `-Dmissaomarte.campanha.etapa=2` | Abre a campanha na etapa indicada, de 1 a 3, com progresso de desenvolvimento |
| `missaomarte.campanha.concluida` | `-Dmissaomarte.campanha.concluida=true` | Abre o campus com uma campanha concluída em memória, sem apagar o progresso salvo |
| `missaomarte.toque` | `-Dmissaomarte.toque=true` | Ativa no desktop a interface e os controles de toque |
| `missaomarte.janela` | `-Dmissaomarte.janela=540x1200` | Define o tamanho da janela para reproduzir telas de celular |
| `missaomarte.fatorTexto` | `-Dmissaomarte.fatorTexto=1.6` | Força a escala de texto informada no desktop |

Exemplo para reproduzir a interface vertical do Android e abrir a etapa da NAMI:

```bash
./gradlew :lwjgl3:run --console=plain \
  -Dmissaomarte.toque=true \
  -Dmissaomarte.janela=540x1200 \
  -Dmissaomarte.fatorTexto=1.6 \
  -Dmissaomarte.campanha.etapa=2
```

Exemplo para conferir a inauguração do campus em uma janela horizontal:

```bash
./gradlew :lwjgl3:run --console=plain \
  -Dmissaomarte.campanha.concluida=true
```

### Android

Configure o SDK em `cliente/local.properties`, que é ignorado pelo Git:

```properties
sdk.dir=/Users/SEU_USUARIO/Library/Android/sdk
```

Gere o APK de depuração:

```bash
cd fases/fase-08-campanha-fundadores/cliente
./gradlew :android:assembleDebug
```

O arquivo será gerado em `android/build/outputs/apk/debug/android-debug.apk`. O aplicativo da Fase 8 usa orientação retrato e a interface de toque.

### Servidor

Em outro terminal:

```bash
cd fases/fase-08-campanha-fundadores/servidor
export MISSAOMARTE_JWT_SEGREDO=$(openssl rand -base64 48)
./gradlew test
./gradlew bootRun --console=plain
```

O servidor escuta por padrão em `127.0.0.1:8080`. O Swagger fica em [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html). O segredo gerado muda a cada execução, portanto tokens e logins anteriores deixam de valer após a troca.

Para acesso pela rede, HTTPS, persistência, backup e Docker, consulte o [guia de publicação](docs/servidor/publicacao-servidor.md). Para uma apresentação passo a passo, consulte o [roteiro de demonstração](docs/servidor/demonstracao-servidor.md).

## Outras fases

As Fases 0 e 1 usam Java puro. Seus READMEs contêm os comandos de compilação com `javac` e execução dos testes. As Fases 2 a 7 possuem Wrappers próprios e devem ser executadas a partir da pasta indicada na tabela.

Exemplo geral de uma fase com projeto Gradle único:

```bash
cd fases/fase-03-libgdx
./gradlew clean build
./gradlew :lwjgl3:run
```

Nas Fases 5 a 8, cliente e servidor são projetos Gradle independentes. Execute os comandos dentro de `cliente/` ou `servidor/`, nunca na pasta pai da fase.

## Documentação

| Área | Documento | Conteúdo |
|---|---|---|
| Arquitetura | [Diagnóstico da Fase 1](docs/arquitetura/diagnostico.md) | Estado inicial, dependências, violações e decisões da refatoração do console |
| Arquitetura | [Revisão SOLID e GRASP da Fase 8](docs/arquitetura/revisao-solid-grasp.md) | Responsabilidades, pontos fortes, dívida e refatorações aprovadas |
| Decisão | [ADR-001](docs/adr/ADR-001-trilha-de-evolucao.md) | Trilha de evolução e uso da base do professor |
| Decisão | [ADR-002](docs/adr/ADR-002-camadas-e-portas.md) | Organização em camadas e portas |
| Decisão | [ADR-003](docs/adr/ADR-003-remocao-do-rover.md) | Remoção da mecânica do rover |
| Servidor | [Demonstração](docs/servidor/demonstracao-servidor.md) | Cadastro, login, envio de partida, ranking e comportamento offline |
| Servidor | [Publicação](docs/servidor/publicacao-servidor.md) | Pacote executável, variáveis, rede, HTTPS, H2, Docker e clientes |
| Validação | [Etapa 2](docs/validacao/validacao-etapa-02.md) | Registro histórico da importação e validação das Fases 2 a 7 |
| Relatório | [Origem do código](docs/relatorio/origem-do-codigo.md) | Comparação quantitativa de arquivos e linhas com a base do professor |

Os relatórios da Sprint 5 serão reunidos em `docs/relatorio/`. Os tutoriais e enunciados específicos permanecem dentro da pasta `docs/` de cada fase.

## Origem do código

- Fase 1: refatoração do console desenvolvida pela equipe.
- Fases 0 e 2 a 7: base do repositório do Prof. Marcelo Bezerra, com uso autorizado em sala.
- Fase 8: evolução da equipe construída sobre a Fase 7, com Campanha dos Fundadores, campus, mecânicas, pontuação, interface e documentação próprias.

A base de comparação fica fora deste repositório, em `../base-professor/missaoMarteUnifor/projetoExtensaoFinal/`, e deve permanecer somente para leitura. A medição detalhada está no relatório [Origem do código](docs/relatorio/origem-do-codigo.md).

## Estrutura principal

```text
JogoMissaoMarte/
  AGENTS.md
  CLAUDE.md
  README.md
  docs/
    arquitetura/
    adr/
    servidor/
    validacao/
    relatorio/
  fases/
    fase-00-original/
    fase-01-refatoracao/
    ...
    fase-08-campanha-fundadores/
```

Artefatos de build, dados locais, configurações de IDE, bancos H2, rankings locais e `local.properties` não são versionados.
