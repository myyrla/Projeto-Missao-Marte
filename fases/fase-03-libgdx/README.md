> Base: Fase 03 do repositório do Prof. Marcelo Bezerra (T200-ProjArqSist), uso autorizado em sala. Alterações da equipe listadas abaixo.

## Registro da importação pela equipe

- Revisão da base: `4b43e59eacfa57798d55b68c5d018015c804193e`; pasta `missaoMarteUnifor/projetoExtensaoFinal/exercicio10/fases/fase-03-libgdx`.
- Alterações da equipe: aviso de origem, comandos e caminhos deste README adaptados para macOS e permissão de execução dos scripts `gradlew`. Fontes, testes, assets, dependências e versões dos Wrappers preservados.
- O link do tutorial para a referência geral de Gradle/libGDX foi apontado para a revisão acima no GitHub, pois o arquivo fica fora das fases importadas.
- Validação em 12/11/2026: build interrompido no download do Gradle por `UnknownHostException: services.gradle.org`. Compilação, testes e execução do jogo não foram realizados nesta sessão. Veja o [registro da Etapa 2](../../docs/validacao-etapa-02.md).

**Leitura da documentação herdada:** as contagens de testes e os relatos de verificações abaixo pertencem à base do professor; não são resultados da equipe nesta importação. As referências a fases anteriores também descrevem a sequência do professor, que usa pacotes e regras diferentes da Fase 1 da equipe.

Selecione o JDK 17 conforme o [README da raiz](../../README.md#ambiente). Cada bloco que começa com `cd fases/` deve partir da raiz deste repositório, em um terminal próprio. Nos demais blocos, mantenha o terminal na pasta da fase ou do subprojeto indicado. Os Wrappers já estão executáveis.

---

# Fase 03 - Introdução ao libGDX

Jogo Missão Marte com janela gráfica em libGDX, reaproveitando as regras da Fase 2. O jogo continua por turnos e usa formas geométricas e a fonte padrão (sem imagens ou sons, que ficam para a Fase 4).

## Estrutura

```text
fase-03-libgdx/
  core/     jogo independente de plataforma (domain, application, infrastructure, gdx)
  lwjgl3/   launcher desktop (Lwjgl3Launcher)
  docs/     planejamento e tutorial
```

## Requisitos

- JDK 17 no PATH e em `JAVA_HOME`.
- Placa de vídeo com suporte a OpenGL 2.0 ou superior.
- Gradle instalado é opcional: use o Gradle Wrapper (8.7).

## Como executar

```bash
cd fases/fase-03-libgdx
./gradlew clean build          # compila e executa os 39 testes
./gradlew :lwjgl3:run --console=plain
```

O `ranking.json` é gravado nesta pasta.

## Controles

| Tela | Teclas |
|---|---|
| Menu | `1` Jogar, `2` Ver ranking, `3` Limpar ranking, `4` Sair |
| Nova partida | digitar o nome, `Backspace` apaga, setas esquerda/direita mudam a dificuldade, `Enter` decola, `Esc` volta |
| Partida | `W/A/S/D` ou setas movem, `E` embarca, `Q` ou `Esc` aborta |
| Fim de partida e ranking | `Enter` continua |

## Documentação

- [Documentação oficial do libGDX](https://libgdx.com/wiki/)
- [Planejamento](docs/fase-03-planejamento.md)
- [Enunciado para o aluno](docs/fase-03-enunciado.md)
- [Tutorial](docs/fase-03-tutorial.md)
