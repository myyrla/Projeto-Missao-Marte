> Base: Fase 02 do repositório do Prof. Marcelo Bezerra (T200-ProjArqSist), uso autorizado em sala. Alterações da equipe listadas abaixo.

## Registro da importação pela equipe

- Revisão da base: `4b43e59eacfa57798d55b68c5d018015c804193e`; pasta `missaoMarteUnifor/projetoExtensaoFinal/exercicio10/fases/fase-02-gradle`.
- Alterações da equipe: aviso de origem, comandos e caminhos deste README adaptados para macOS e permissão de execução dos scripts `gradlew`. Fontes, testes, assets, dependências e versões dos Wrappers preservados.
- O link do tutorial para a referência geral de Gradle/libGDX foi apontado para a revisão acima no GitHub, pois o arquivo fica fora das fases importadas.
- Validação em 12/11/2026: build interrompido no download do Gradle por `UnknownHostException: services.gradle.org`. Compilação, testes e execução do jogo não foram realizados nesta sessão. Veja o [registro da Etapa 2](../../docs/validacao-etapa-02.md).

**Leitura da documentação herdada:** as contagens de testes e os relatos de verificações abaixo pertencem à base do professor; não são resultados da equipe nesta importação. As referências a fases anteriores também descrevem a sequência do professor, que usa pacotes e regras diferentes da Fase 1 da equipe.

Selecione o JDK 17 conforme o [README da raiz](../../README.md#ambiente). Cada bloco que começa com `cd fases/` deve partir da raiz deste repositório, em um terminal próprio. Nos demais blocos, mantenha o terminal na pasta da fase ou do subprojeto indicado. Os Wrappers já estão executáveis.

---

# Fase 02 - Migração para Gradle (Console)

O mesmo jogo da Fase 1 do professor, agora compilado, testado e executado pelo Gradle. Nenhuma classe do jogo foi alterada: os fontes foram apenas movidos para o layout padrão `src/main/java`.

## Requisitos

- JDK 17 (`java -version` e `JAVA_HOME`).
- Não é preciso instalar o Gradle: o projeto inclui o Gradle Wrapper (versão 8.7). Na primeira execução ele baixa a distribuição, que fica em cache em `~/.gradle`.

## Comandos

Execute na pasta `fases/fase-02-gradle`:

```bash
./gradlew --version                 # Gradle e JVM em uso
./gradlew build                     # compila, testa e empacota
./gradlew test                      # apenas os testes
./gradlew run --console=plain       # executa o jogo
./gradlew clean                     # remove a pasta build
./gradlew tasks                     # lista as tarefas
```

Os arquivos `gradlew.bat` da base foram preservados para uso no Windows.

O `ranking.json` é gravado na pasta `fase-02-gradle` (ignorado pelo Git).

## Estrutura

```text
fase-02-gradle/
├── settings.gradle
├── build.gradle
├── gradlew, gradlew.bat
├── gradle/wrapper/
├── docs/
└── src/
    ├── main/java/br/edu/unifor/missaomarte/...   # código do jogo (igual à Fase 1 do professor)
    └── test/java/br/edu/unifor/missaomarte/   # testes JUnit 5
```

## Documentação

- [Planejamento](docs/fase-02-planejamento.md)
- [Enunciado para o aluno](docs/fase-02-enunciado.md)
- [Tutorial](docs/fase-02-tutorial.md)
