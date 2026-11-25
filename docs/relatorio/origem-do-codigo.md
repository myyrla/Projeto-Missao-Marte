# Origem do código por fase

## Objetivo

Este relatório compara as fases versionadas neste repositório com a base do Prof. Marcelo Bezerra. A finalidade é tornar explícito o que foi reaproveitado, o que foi alterado e o que foi criado pela equipe do Grupo 01.

A referência foi lida em `../base-professor/missaoMarteUnifor/projetoExtensaoFinal/exercicio10/fases/`. Nenhum arquivo do repositório do professor foi alterado durante a medição.

## Metodologia

A medição foi feita em 24 de novembro de 2026 com estas regras:

- somente arquivos versionados, obtidos com `git ls-files`, entram na contagem;
- artefatos ignorados, como `build/`, `.gradle/`, `out/`, bancos locais e arquivos de IDE, não entram na contagem;
- um arquivo é **idêntico** quando existe nos dois lados, no mesmo caminho relativo, e seus bytes são iguais;
- um arquivo é **alterado** quando existe nos dois lados, no mesmo caminho relativo, mas seu conteúdo difere;
- um arquivo é **criado pela equipe** quando existe apenas na fase local;
- um arquivo é **ausente na fase local** quando existe apenas na referência do professor;
- linhas adicionadas e removidas seguem o resultado de `git diff --no-index --no-renames --numstat`;
- arquivos binários contam como arquivos, mas não como linhas de texto;
- na coluna “Linhas contabilizadas”, as categorias idêntico, alterado e criado usam o conteúdo local; a categoria ausente usa o conteúdo da referência.

As Fases 0 a 7 foram comparadas com a fase correspondente do professor. Como não existe uma Fase 8 na referência, a Campanha dos Fundadores foi comparada com a Fase 7 do professor, seu ponto de derivação declarado.

Esta é uma medição por caminho e conteúdo, não uma atribuição automática de autoria intelectual. Em especial, a Fase 1 usa uma estrutura de pacotes própria, então arquivos funcionalmente comparáveis aparecem como criados localmente e ausentes na referência.

## Síntese

| Fase local | Referência do professor | Arquivos locais | Linhas locais de texto | Idênticos | Alterados | Criados pela equipe | Ausentes |
|---|---|---:|---:|---:|---:|---:|---:|
| 00 | `fase-0-oopuro` | 12 | 902 | 11 | 0 | 1 | 0 |
| 01 | `fase-01-refatoracao` | 40 | 1.321 | 0 | 2 | 38 | 30 |
| 02 | `fase-02-gradle` | 43 | 3.262 | 41 | 2 | 0 | 0 |
| 03 | `fase-03-libgdx` | 55 | 4.250 | 53 | 2 | 0 | 0 |
| 04 | `fase-04-interface-grafica` | 101 | 6.013 | 99 | 2 | 0 | 0 |
| 05 | `fase-05-servidor-ranking` | 162 | 9.337 | 161 | 1 | 0 | 0 |
| 06 | `fase-06-android` | 194 | 11.278 | 193 | 1 | 0 | 0 |
| 07 | `fase-07-evolucao-livre` | 216 | 13.764 | 215 | 1 | 0 | 0 |
| 08 | `fase-07-evolucao-livre` | 412 | 25.636 | 152 | 64 | 196 | 0 |

## Fase 00: jogo original

| Categoria | Arquivos | Linhas contabilizadas | Linhas adicionadas | Linhas removidas |
|---|---:|---:|---:|---:|
| Idênticos à base | 11 | 883 | 0 | 0 |
| Alterados pela equipe | 0 | 0 | 0 | 0 |
| Criados pela equipe | 1 | 19 | 19 | 0 |
| Ausentes na fase local | 0 | 0 | 0 | 0 |

Os 11 arquivos do jogo são cópias idênticas de `fase-0-oopuro`. O único arquivo criado localmente é o `README.md`, que registra a origem e os comandos usados pela equipe.

## Fase 01: refatoração do console

| Categoria | Arquivos | Linhas contabilizadas | Linhas adicionadas | Linhas removidas |
|---|---:|---:|---:|---:|
| Idênticos à base | 0 | 0 | 0 | 0 |
| Alterados pela equipe | 2 | 212 | 134 | 249 |
| Criados pela equipe | 38 | 1.109 | 1.109 | 0 |
| Ausentes na fase local | 30 | 1.675 | 0 | 1.675 |

Esta fase é uma contribuição própria da equipe e não uma importação da Fase 1 do professor. O código local usa os pacotes `missao.app`, `missao.engine`, `missao.model`, `missao.persistence` e `missao.ui`, enquanto a referência usa `br.edu.unifor.missaomarte`. Por isso, a comparação por caminho classifica os 38 arquivos locais como criados e os 30 arquivos da implementação do professor como ausentes.

Os dois caminhos compartilhados e alterados são `README.md` e `docs/fase-01-tutorial.md`. Entre os arquivos criados pela equipe estão o motor `GameEngine`, a fábrica `MissaoFactory`, as portas `RankingRepository` e `UI`, o repositório JSON, a interface de console e os testes automatizados.

## Fase 02: Gradle

| Categoria | Arquivos | Linhas contabilizadas | Linhas adicionadas | Linhas removidas |
|---|---:|---:|---:|---:|
| Idênticos à base | 41 | 2.498 | 0 | 0 |
| Alterados pela equipe | 2 | 764 | 27 | 12 |
| Criados pela equipe | 0 | 0 | 0 | 0 |
| Ausentes na fase local | 0 | 0 | 0 | 0 |

Todo o código e a configuração do projeto permanecem idênticos à base. As alterações estão em `README.md` e `docs/fase-02-tutorial.md`, para registrar a origem, ajustar os comandos ao macOS e documentar a validação local.

## Fase 03: libGDX

| Categoria | Arquivos | Linhas contabilizadas | Linhas adicionadas | Linhas removidas |
|---|---:|---:|---:|---:|
| Idênticos à base | 53 | 3.376 | 0 | 0 |
| Alterados pela equipe | 2 | 874 | 20 | 5 |
| Criados pela equipe | 0 | 0 | 0 | 0 |
| Ausentes na fase local | 0 | 0 | 0 | 0 |

Todo o código e a configuração do projeto permanecem idênticos à base. As diferenças estão em `README.md` e `docs/fase-03-tutorial.md`, com atribuição de origem, comandos para macOS e registro da validação.

## Fase 04: interface gráfica

| Categoria | Arquivos | Linhas contabilizadas | Linhas adicionadas | Linhas removidas |
|---|---:|---:|---:|---:|
| Idênticos à base | 99 | 5.349 | 0 | 0 |
| Alterados pela equipe | 2 | 664 | 23 | 8 |
| Criados pela equipe | 0 | 0 | 0 | 0 |
| Ausentes na fase local | 0 | 0 | 0 | 0 |

Todo o código, os recursos e a configuração do projeto permanecem idênticos à base. As diferenças estão em `README.md` e `docs/fase-04-tutorial.md`, pelos mesmos ajustes documentais das fases anteriores.

## Fase 05: servidor e ranking

| Categoria | Arquivos | Linhas contabilizadas | Linhas adicionadas | Linhas removidas |
|---|---:|---:|---:|---:|
| Idênticos à base | 161 | 9.201 | 0 | 0 |
| Alterados pela equipe | 1 | 136 | 25 | 11 |
| Criados pela equipe | 0 | 0 | 0 | 0 |
| Ausentes na fase local | 0 | 0 | 0 | 0 |

Os 161 arquivos de cliente, servidor, testes e configuração são idênticos à base. A única diferença é o `README.md`, atualizado para atribuir a origem, usar comandos adequados ao ambiente da equipe e registrar a validação.

## Fase 06: Android

| Categoria | Arquivos | Linhas contabilizadas | Linhas adicionadas | Linhas removidas |
|---|---:|---:|---:|---:|
| Idênticos à base | 193 | 11.154 | 0 | 0 |
| Alterados pela equipe | 1 | 124 | 30 | 16 |
| Criados pela equipe | 0 | 0 | 0 | 0 |
| Ausentes na fase local | 0 | 0 | 0 | 0 |

Os 193 arquivos de código, recursos, testes e configuração são idênticos à base. A única diferença é o `README.md`, com atribuição, comandos para macOS e resultado da validação.

## Fase 07: evolução livre

| Categoria | Arquivos | Linhas contabilizadas | Linhas adicionadas | Linhas removidas |
|---|---:|---:|---:|---:|
| Idênticos à base | 215 | 13.625 | 0 | 0 |
| Alterados pela equipe | 1 | 139 | 29 | 10 |
| Criados pela equipe | 0 | 0 | 0 | 0 |
| Ausentes na fase local | 0 | 0 | 0 | 0 |

Os 215 arquivos de código, recursos, testes e configuração são idênticos à base. A única diferença é o `README.md`, usado para registrar a procedência, os comandos locais e a validação.

## Fase 08: Campanha dos Fundadores

| Categoria | Arquivos | Linhas contabilizadas | Linhas adicionadas | Linhas removidas |
|---|---:|---:|---:|---:|
| Idênticos à Fase 7 do professor | 152 | 7.601 | 0 | 0 |
| Alterados pela equipe | 64 | 8.253 | 2.821 | 712 |
| Criados pela equipe | 196 | 9.782 | 9.782 | 0 |
| Ausentes na fase local | 0 | 0 | 0 | 0 |

A Fase 8 é a principal contribuição da equipe. Ela parte da Fase 7 do professor, preserva 152 arquivos sem nenhuma alteração, modifica 64 arquivos existentes e acrescenta 196 arquivos. Considerando somente texto, são 12.603 linhas adicionadas e 712 removidas em relação ao ponto de partida.

As contribuições incluem a Campanha dos Fundadores, o progresso persistente, o campus em camadas, a Estação NAMI, a tempestade de areia, a estratégia própria de pontuação, as políticas de registro no ranking, os layouts responsivos, a interface visual para desktop e Android, recursos gráficos e fontes, testes e documentação. O servidor continua derivado da base e recebeu ajustes de publicação, documentação e ocultação de um campo interno no Swagger. No cliente, a equipe também acrescentou o salvamento local quando o envio ao servidor falha.

Nenhum caminho da Fase 7 do professor foi simplesmente omitido na Fase 8. Isso permite rastrear a evolução sobre a base e separar as extensões da equipe dos arquivos preservados.

## Leitura de autoria e reuso

- A Fase 0 é a referência original do professor, acompanhada de um README local.
- A Fase 1 é a refatoração de console desenvolvida pela equipe, com arquitetura, implementação e testes próprios.
- As Fases 2 a 7 reutilizam a base do professor. As medições confirmam que as alterações locais nessas fases são documentais e que o código permanece idêntico.
- A Fase 8 deriva da Fase 7 e concentra o desenvolvimento autoral da Campanha dos Fundadores, sem apagar a procedência dos componentes reaproveitados.

## Autorização para o reuso

O professor autorizou em sala o reuso do código das Fases 2 a 7 devido ao prazo do projeto e orientou a equipe a concentrar o esforço no jogo e na evolução da Fase 8.

> Confirmação por escrito: pendente de anexação pela equipe.
>
> Espaço reservado para o link ou referência do anexo: ______________________________

Quando a confirmação escrita estiver disponível, a equipe deve anexá-la fora das pastas das fases e substituir o espaço acima por um link relativo, preservando este histórico de medição.

