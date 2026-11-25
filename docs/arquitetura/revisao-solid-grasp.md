# Revisão SOLID e GRASP da Fase 8

## 1. Escopo e método

Esta revisão cobre o código de produção da Fase 8 em `cliente/**/src/main/java` e `servidor/src/main/java`. Testes, arquivos gerados e diretórios de build foram excluídos.

As linhas são linhas físicas do arquivo, incluindo comentários e linhas em branco. Os métodos foram contados pela árvore sintática do compilador Java 17. A contagem considera apenas métodos declarados na classe principal do arquivo. Construtores, métodos herdados, métodos de classes internas e métodos `protected` ou sem modificador não entram nas colunas de métodos públicos e privados.

A classificação de origem usa a Fase 7 importada como referência. O README da Fase 8 registra que essa base veio do repositório do Prof. Marcelo Bezerra e teve uso autorizado em sala.

## 2. Resumo executivo

- O servidor não apresenta classes grandes. Sua maior classe tem 94 linhas e a separação Controller, Service, Repository e DTO está clara.
- O cliente concentra complexidade na camada `gdx`. Sete classes ultrapassam 400 linhas.
- No diagnóstico inicial, `GameScreen` era o ponto de maior risco, com 1.213 linhas e responsabilidades de entrada, coordenação, câmera, efeitos, ranking, campanha e telas de pausa e resultado. Após as duas refatorações aprovadas, ficou com 1.032 linhas.
- `GameSession` também é grande, mas a maior parte de sua complexidade representa o caso de uso da partida e está protegida por Strategy, eventos e uma suíte ampla de testes.
- `LayoutDoHud` é grande, porém puro e coeso em torno da geometria responsiva. Seu tamanho isolado não justifica uma divisão imediata.
- A equipe já criou boas fronteiras para regras que mudam: pontuação, destaques, ranking, geometria, tipografia, câmera e efeitos da tempestade.
- A melhor sequência de refatoração começa pelas responsabilidades visuais e de composição que ainda ficaram dentro das telas. Não há refatoração prioritária no servidor.

## 3. Dez maiores classes

A tabela registra o estado encontrado no diagnóstico, antes das refatorações da Etapa 2.

| Posição | Classe | Camada | Linhas | Métodos públicos | Métodos privados |
|---:|---|---|---:|---:|---:|
| 1 | `GameScreen` | cliente, `gdx` | 1.213 | 2 | 54 |
| 2 | `HudRenderer` | cliente, `gdx/hud` | 673 | 4 | 36 |
| 3 | `CampusScreen` | cliente, `gdx` | 520 | 1 | 24 |
| 4 | `GameSession` | cliente, `application` | 498 | 25 | 11 |
| 5 | `MundoRenderer` | cliente, `gdx/visual` | 493 | 2 | 20 |
| 6 | `LayoutDoHud` | cliente, `gdx/hud` | 483 | 46 | 9 |
| 7 | `MenuScreen` | cliente, `gdx` | 433 | 1 | 23 |
| 8 | `NovaPartidaScreen` | cliente, `gdx` | 390 | 1 | 19 |
| 9 | `OpcoesScreen` | cliente, `gdx` | 384 | 1 | 21 |
| 10 | `TelaBase` | cliente, `gdx` | 331 | 7 | 6 |

Nenhuma classe do servidor entra nas dez maiores. As maiores são `Partida`, com 94 linhas, e `SegurancaConfig`, com 87 linhas. Todo o código de produção do servidor soma aproximadamente 880 linhas.

## 4. Análise das classes acima de 400 linhas

### 4.1 `GameScreen`

Responsabilidades atuais:

1. Traduzir teclado e toque em ações, em `tratarTecla`, `teclaDinamica`, `aoTocar` e `toqueDinamico`.
2. Orquestrar a aplicação, em `executar`, `atirar`, `tratarResultado` e `aplicarEstado`.
3. Reagir a eventos com som e efeitos, em `reagirAosEventos`.
4. Integrar campanha, ranking local e servidor, em `registrarResultadoDaCampanha`, `enviarResultado` e `resultadoDoEnvio`.
5. Calcular câmera, recorte e coordenadas, em `atualizarCameraELayout`, `desenharMundoRecortado`, `paraCoordenadasDaJanela` e `paraCoordenadasDoMundo`.
6. Montar destaques visuais, em `calcularDestaques` e `areasDoHudNoMundo`.
7. Desenhar pausa, contagem e fim de partida, em `desenharPausa`, `desenharContagem`, `desenharFimDePartida` e `desenharFimDePartidaNoToque`.

Princípios respeitados:

- GRASP Controller: recebe as ações da interface e delega as regras a `GameSession` por `executar` e `atirar`.
- GRASP Indirection e Protected Variations: usa `PoliticaDeDestaque`, `PoliticaDeRegistroNoRanking`, `RegistroAlternativoNoRankingLocal`, `CameraDaPartida`, `LayoutDoHud` e renderers especializados.
- Observer simplificado: `reagirAosEventos` responde a `EventoJogo` sem pedir ao domínio que toque som ou produza efeitos.
- DIP entre apresentação e aplicação: a tela usa a porta `ServidorDoJogo` exposta pelo jogo, sem conhecer detalhes HTTP.
- Separação de camadas: não calcula pontuação, colisão, vitória ou tempestade. Essas decisões vêm de `GameSession` e das mecânicas da campanha.

Princípios fragilizados:

- SRP: a classe muda por alterações de controle, câmera, HUD, campanha, ranking, rede e desenho do resultado.
- GRASP High Cohesion: métodos de transporte, navegação e desenho compartilham a mesma classe sem formarem uma única responsabilidade de apresentação.
- OCP: novos modos ou novos destinos de resultado ainda exigem editar condicionais como `teclaDinamica`, `aplicarEstado` e `emCampanha`.
- GRASP Creator: `reiniciarPartida` e `encerrar` conhecem telas concretas e a recriação da sessão. Esse acoplamento é aceitável no composition root da interface, mas amplia o papel da tela.

Avaliação: é a principal candidata a redução. A classe já delega regras importantes, portanto a dívida está na coordenação e apresentação, não no domínio.

### 4.2 `HudRenderer`

Responsabilidades atuais:

1. Medir conteúdo tipográfico em `medir`.
2. Desenhar painéis, casco e faixas em `desenharPecas` e `desenharFaixasOpacas`.
3. Desenhar o radar em `desenharRadar`, `ponto`, `quadrado` e `desenharJanelaVisivel`.
4. Desenhar jogador, objetivo e fundadores em `desenharPiloto`, `desenharObjetivo` e `desenharFundador`.
5. Montar status, atalhos, avisos e tempestade em `desenharRodape`, `linhasDoStatus`, `desenharAviso` e `desenharSeloDaTempestade`.
6. Formatar textos de apresentação em `tituloDoPiloto`, `formatarMunicaoEMira` e `textoDoSinal`.

Princípios respeitados:

- SRP no limite da camada: a classe somente projeta estado e não altera regras da partida.
- GRASP Information Expert da apresentação: possui fontes, estilo e layout necessários para medir e desenhar o HUD.
- Low Coupling: recebe `LayoutDoHud` pronto e usa `RadarGeometria`, `LinhasDoStatus`, `AtalhosDoRodape` e `RotulosDaInterface`.
- Robustez de estado gráfico: `desenhar` restaura cores das fontes em `finally`, reduzindo efeitos colaterais globais.

Princípios fragilizados:

- SRP: radar, objetivo, status, alertas e medição tipográfica já são componentes visuais independentes.
- OCP: `corDoPassageiro` usa `instanceof` e precisa mudar para cada novo tipo de passageiro.
- GRASP High Cohesion: a coesão é média, pois todos os métodos são visuais, mas vários cartões não compartilham lógica além de fontes e renderizadores.
- DIP: a classe conhece diretamente `Campanha`, `EtapaDaCampanha`, subtipos de passageiros e detalhes de `GameSession`.

Avaliação: o desenho do radar é a fronteira mais pronta para extração, pois já usa uma geometria própria e possui poucas dependências com os outros cartões.

### 4.3 `CampusScreen`

Responsabilidades atuais:

1. Medir texto e recalcular layout em `layoutAtual`, `medirConteudoHorizontal`, `medirConteudoVertical` e `medirCartao`.
2. Controlar teclado, toque e confirmação em `tratarTecla`, `aoTocar`, `acionarPrincipal` e `confirmarNovaCampanha`.
3. Criar a partida da campanha em `iniciarMissao`.
4. Montar botões em `atualizarBotoes`.
5. Desenhar mapa, camadas, etapas, cartão e modal em `desenharCampus`, `desenharTextos`, `desenharConteudoDoCartao` e `desenharConfirmacao`.

Princípios respeitados:

- GRASP Controller: recebe ações e usa `ControleDoCampus` para decidir iniciar, reiniciar ou cancelar.
- Indirection e Pure Fabrication: delega estado visual a `ResumoVisualDaCampanha`, conteúdo a `ConteudoDoCartaoDaCampanha`, geometria a `CampusLayout` e cálculo tipográfico a `MetricasTipograficasDoCampus`.
- Separação de camadas: o estado persistente continua em `Campanha` e nos repositórios da aplicação.
- GRASP Information Expert: `Campus` decide o estado formal das construções, enquanto a tela apenas escolhe texturas.

Princípios fragilizados:

- SRP: entrada, medição, renderização, navegação e criação de `GameSession` mudam pela mesma classe.
- GRASP Creator: `iniciarMissao` instancia `RegraDePontuacaoPadrao`, `RegraDePontuacaoDaCampanha` e `GameSession`. A composição é válida, mas estaria mais coesa em um preparador da aplicação.
- DIP: `iniciarMissao` depende de implementações concretas das regras de pontuação.
- High Cohesion: a renderização do campus e a preparação da missão têm poucos dados em comum.

Avaliação: a criação da sessão é a dívida arquitetural mais clara. O restante da classe é apresentação e já está apoiado por objetos puros.

### 4.4 `GameSession`

Responsabilidades atuais:

1. Processar turnos clássicos em `processarAcao`.
2. Processar embarque, entrega, colisão, pontuação e vitória em `embarcarPassageiro`, `processarBase`, `verificarColisao`, `verificarPontuacao` e `verificarVitoria`.
3. Controlar entrada contínua e pausa em `pressionar`, `soltar`, `pausar` e `retomar`.
4. Controlar o relógio do modo dinâmico em `avancar`.
5. Processar tiro em `atirar`.
6. Expor o estado da partida e a visibilidade da campanha.

Princípios respeitados:

- GRASP Controller: é o controlador do caso de uso da partida.
- GRASP Information Expert: coordena `Missao`, `Nave`, pontuação e estado porque possui a sessão corrente.
- Strategy e DIP: depende de `RegraDePontuacao`; `pontosDoResgate`, `custoDoTiro` e demais valores não ficam codificados na sessão.
- Protected Variations: `MecanicasDaPartida` encapsula NAMI e tempestade, e `Temporizador` encapsula os intervalos.
- Observer simplificado: devolve `ResultadoTurno` e `EventoJogo` em vez de importar libGDX, imprimir ou tocar áudio.
- LSP e Polymorphism: `embarcarPassageiro` usa `Passageiro`, enquanto a pontuação concreta vem da sobrescrita em cada subtipo.
- Separação de camadas: não há importação de `com.badlogic.*`.

Princípios fragilizados:

- SRP: turno clássico, laço de tempo real, pausa, tiro e ciclo de vida da sessão são motivos distintos para mudança.
- OCP: `processarAcao` e `avancar` selecionam comportamentos por `ModoDeJogo`; um terceiro modo exigiria modificar a classe.
- High Cohesion: a coesão ainda é maior que a de `GameScreen`, mas os 25 métodos públicos expõem uma superfície ampla.

Avaliação: apesar do tamanho, a classe está fortemente testada e contém regras sensíveis. Uma divisão imediata teria risco maior que o benefício nesta fase. As Strategies já retiraram sua fonte mais volátil, a pontuação.

### 4.5 `MundoRenderer`

Responsabilidades atuais:

1. Desenhar terreno, grade, limites e véu em `desenharTerreno`, `desenharGradeELimites` e `desenharVeuExterno`.
2. Desenhar rastro, zonas de alcance e passageiros em `desenharRastro`, `desenharAlcances` e `desenharPassageiros`.
3. Desenhar objetos, NAMI e nave em `desenharObjetos`, `desenharEstacoesNami`, `regiao` e `desenharNave`.
4. Desenhar tempestade em `desenharEscuridao`, `desenharPoeira` e `desenharParticulas`.
5. Desenhar efeitos, etiqueta e textos flutuantes.

Princípios respeitados:

- Separação de camadas: somente desenha dados decididos pelo domínio e pela aplicação.
- DIP parcial: recebe `ObjetoRenderizavel` e `Predicate<Posicao>`, sem depender de uma tela concreta.
- Protected Variations: geometria e decisões foram extraídas para `CenaVisivelDaTempestade`, `MascaraDaTempestade`, `ParticulasDaTempestade`, `GeometriaDoRastro`, `AnimacaoDoPulso` e `PoliticaDeDestaque`.
- Robustez gráfica: restaura a cor do `SpriteBatch` em blocos `finally`.

Princípios fragilizados:

- SRP: terreno, entidades, nave, tempestade, efeitos e etiquetas já são sub-renderers independentes.
- OCP: `SimboloParaImagem.de` e `regiao` contêm escolhas centrais por símbolo e por enum. Um objeto animado novo exige editar os dois pontos.
- GRASP Polymorphism: a abstração `ObjetoRenderizavel` reduz acoplamento, mas a seleção visual por `char` transfere a escolha para um `switch`.
- High Cohesion: a coesão é de apresentação do mundo, mas o gerenciamento de várias passagens de `SpriteBatch`, `ShapeRenderer` e blend torna a classe sensível à ordem de desenho.

Avaliação: a tempestade possui os colaboradores puros necessários para virar um renderer próprio, mas a extração precisa preservar rigorosamente ordem, blend e restauração de estado.

### 4.6 `LayoutDoHud`

Responsabilidades atuais:

1. Calcular colunas horizontais em `calcularColunas`.
2. Calcular faixas verticais em `calcularFaixas`.
3. Calcular layouts de reserva em `calcularReservaPaisagem` e `calcularReservaRetrato`.
4. Guardar e derivar as áreas de todos os cartões, mapa, controles e fim de partida.

Princípios respeitados:

- SRP: a responsabilidade é geometria responsiva do HUD.
- GRASP Pure Fabrication e Information Expert: concentra medidas e relações geométricas sem depender de libGDX.
- Low Coupling: consome apenas valores, `MargensSeguras`, `MedidasDoHud` e `RetanguloDeTela`.
- Alta testabilidade: `calcular` é determinístico e seus resultados são comparados diretamente nos testes.
- Protected Variations: `GameScreen` e `HudRenderer` não precisam conhecer as fórmulas dos modos `COLUNAS`, `FAIXAS` e `RESERVA`.

Princípios fragilizados:

- OCP: cada novo modo responsivo exige alterar a classe e ampliar o enum privado `Modo`.
- ISP: os 46 métodos públicos formam uma interface de consulta larga, embora sejam getters e projeções imutáveis.
- SRP no limite: a geometria de fim de partida, por `painelDoFim` e `botaoDoFim`, convive com o HUD mesmo depois da criação de `LayoutDoResultadoDaPartida` para toque.

Avaliação: é grande, mas coesa e segura. Dividir apenas para reduzir linhas criaria mais coordenação sem benefício imediato. A retirada futura das áreas do fim de partida pode ser feita junto da extração do renderer correspondente.

### 4.7 `MenuScreen`

Responsabilidades atuais:

1. Controlar navegação por teclado e toque em `tratarTecla`, `tratarTeclaDaConfirmacao` e `aoTocar`.
2. Escolher a próxima tela em `executar`.
3. Controlar o modal de saída.
4. Medir e adaptar o modo toque em `atualizarLayoutDoToque`.
5. Desenhar fundo, título, nave, opções, rodapé e confirmação.

Princípios respeitados:

- GRASP Controller: recebe eventos da interface e decide a navegação.
- Indirection: usa `ListaDeBotoes`, `LayoutDoMenu`, `LayoutDoMenuToque`, `RodapeDeTeclas` e `PoliticaDeAtalhos`.
- Separação de responsabilidades já iniciada: cálculos responsivos e seleção de foco são puros e testáveis fora da tela.

Princípios fragilizados:

- SRP: navegação, modal, medição e desenho ainda estão juntos.
- OCP: `executar` e `desenharOpcoes` dependem dos índices fixos das quatro opções.
- GRASP Creator e Low Coupling: `executar` instancia telas concretas como `CampusScreen`, `NovaPartidaScreen` e `RankingScreen`.

Avaliação: é grande, porém o risco atual é menor que em `GameScreen`. As extrações de layout e foco já contêm as partes mais sujeitas a regressão.

## 5. Avaliação do servidor

O servidor apresenta uma estrutura mais equilibrada que o cliente:

| Evidência | SOLID e GRASP |
|---|---|
| `PartidaController.registrar` recebe `PartidaRequisicao` e devolve `PartidaResposta` | SRP, GRASP Controller e proteção da entidade JPA |
| `PartidaService` recebe `PartidaRepository` e `UsuarioService` pelo construtor | DIP e Low Coupling |
| `UsuarioService` concentra cadastro e acesso ao usuário | Information Expert e High Cohesion |
| `PartidaRepository` e `UsuarioRepository` são interfaces pequenas | ISP e Repository |
| `TokenService` concentra emissão de JWT, enquanto `SegurancaConfig` monta os componentes | SRP e Pure Fabrication |
| O usuário de uma partida vem do `Jwt` e não do corpo recebido | Information Expert e proteção contra troca de proprietário |
| Controllers não devolvem `Usuario` ou `Partida` diretamente | separação API, aplicação e persistência |

Não há classe do servidor acima de 100 linhas nem dependência do servidor para o cliente. A única alteração de produção da equipe na cópia da Fase 8 foi esconder `isSenhaCabeNoBcrypt` do JSON e do OpenAPI com `@JsonIgnore`. Não há justificativa para uma refatoração estrutural do servidor nesta etapa.

## 6. Pontos fortes do projeto

### 6.1 Strategy e Decorator de pontuação

`GameSession` depende de `RegraDePontuacao`, não de números específicos. `RegraDePontuacaoPadrao` preserva a partida rápida. `RegraDePontuacaoDaCampanha` decora outra regra, multiplica seus valores e acrescenta somente os bônus da campanha. Isso respeita OCP, DIP e Strategy. Os testes `RegraDePontuacaoPadraoTest`, `RegraDePontuacaoDaCampanhaTest` e `GameSessionPontuacaoTest` protegem a decisão.

### 6.2 Eventos sem dependência da interface

`GameSession` devolve `ResultadoTurno` com uma lista de `EventoJogo`. A aplicação emite `COLISAO`, `ENTREGA`, `NAMI_ATIVADA` e outros eventos, enquanto `GameScreen.reagirAosEventos` decide som, tremor e efeitos. É uma aplicação simples de Observer, Low Coupling e Protected Variations. `GameSessionEventosTest` e `GameSessionCampanhaTest` cobrem o contrato.

### 6.3 Política pura de destaque

`PoliticaDeDestaque.decidir` escolhe o alvo, opacidades e zonas sem usar libGDX. `posicionarEtiqueta` recebe retângulos e devolve uma posição opcional. A mesma instância de `AlvoDoObjetivo` alimenta HUD e mapa, eliminando cálculos divergentes. Isso aplica Pure Fabrication, Information Expert e alta testabilidade. `PoliticaDeDestaqueTest` cobre seleção, resgatados, alcance, colisão com HUD e nave.

### 6.4 Política de ranking explícita

`PoliticaDeRegistroNoRanking.destino` centraliza a decisão entre `NENHUM`, `LOCAL` e `GLOBAL`. A campanha não entra em ranking, o convidado registra vitória local e o jogador autenticado usa o servidor. `RegistroAlternativoNoRankingLocal` trata a falha de conexão uma única vez. Isso reduz condicionais espalhadas e aplica Pure Fabrication e Protected Variations. `PoliticaDeRegistroNoRankingTest` e `RegistroAlternativoNoRankingLocalTest` protegem os casos.

### 6.5 Layouts puros e medidos

`LayoutDoHud`, `CampusLayout`, `LayoutDoMenu`, `LayoutDaNovaPartida` e `LayoutDoResultadoDaPartida` não dependem de libGDX. As medidas tipográficas entram como objetos de dados calculados com a escala ativa, por exemplo `MedidasDoHud` e `MedidasDoCampus`. Isso aplica Pure Fabrication, Low Coupling e Information Expert. Os testes de layout verificam tamanhos, áreas úteis, não sobreposição, modo reserva e fatores de texto diferentes.

### 6.6 Separação `domain`, `application` e `gdx`

O domínio possui `Missao`, `Nave`, passageiros, perigos e `EstacaoNami`, sem importar libGDX. A aplicação coordena casos de uso, pontuação, campanha, eventos e portas. `gdx` traduz entrada e desenha. O servidor possui processo e modelo independentes do cliente e usa DTOs na API. Essa separação aplica DIP, Indirection e Protected Variations.

### 6.7 Pequenas classes de geometria e efeitos

`CameraDaPartida`, `EscalaDaCelula`, `MascaraDaTempestade`, `ParticulasDaTempestade`, `CenaVisivelDaTempestade`, `GeometriaDoRastro`, `AparenciaDaNami` e `RecorteDoMundo` retiram decisões dos renderers. São classes puras, pequenas e cobertas por testes diretos.

## 7. Dívida herdada e dívida criada pela equipe

### 7.1 Dívida herdada da base do professor

| Dívida | Evidência na Fase 7 | Situação na Fase 8 |
|---|---|---|
| `GameScreen` já concentrava entrada, tempo real, renderização e fim de partida | 727 linhas na Fase 7 | a equipe acrescentou campanha, câmera e novo visual, chegando a 1.213 linhas |
| `GameSession` já reunia turno clássico, modo dinâmico, tiro, pausa e temporizadores | 458 linhas na Fase 7 | a equipe acrescentou Strategy de pontuação, NAMI e visibilidade, chegando a 498 linhas |
| `SimboloParaImagem` escolhe imagem por `char` em um `switch` | a classe e os símbolos já existiam na Fase 7 | a equipe apenas acrescentou o símbolo `H` da NAMI |
| `ObjetoRenderizavel.getSimbolo` leva uma identidade de apresentação textual para o contrato do domínio | contrato importado desde as fases da base | continua evitando libGDX no domínio, mas um tipo novo exige editar o mapa central de imagens |
| As telas libGDX misturam navegação e desenho | `MenuScreen` tinha 182 linhas e `HudRenderer` 246 linhas | o padrão foi mantido e ampliado pela nova interface |

O comentário de `SimboloParaImagem` afirma OCP, mas a inclusão da NAMI exigiu editar o `switch`. A classe evita `instanceof`, porém não está fechada para novos símbolos. A dívida é herdada; a extensão `H` feita pela equipe apenas evidencia a limitação.

### 7.2 Dívida criada ou ampliada pela equipe

| Dívida | Evidência | Consequência |
|---|---|---|
| Crescimento de `GameScreen` | novos métodos de câmera, recorte, destaques, campanha, rede e resultado | maior risco de regressão ao alterar qualquer parte da partida |
| Crescimento de `HudRenderer` | radar, cartões, status, avisos e medição na mesma classe | mudanças independentes disputam o mesmo estado gráfico |
| Composição da partida dentro de `CampusScreen` | `iniciarMissao` instancia duas regras de pontuação e `GameSession` | apresentação conhece detalhes de criação da aplicação |
| `CampusScreen` reúne medição, controle e desenho | `layoutAtual`, `tratarTecla`, `iniciarMissao` e `desenharCampus` | SRP e High Cohesion ficam fragilizados |
| `MundoRenderer` incorpora todas as camadas visuais novas | tempestade, rastro, alcance, NAMI, pulsos e etiquetas | ordem de desenho e restauração de estado ficam centralizadas e frágeis |
| Mapeamento de tipo repetido na apresentação | `PoliticaDeDestaque.Tipo.de`, `HudRenderer.corDoPassageiro` e cores de fundador | novo tipo visual exige alterações em vários pontos |
| `LayoutDoHud` mantém também o resultado horizontal | `painelDoFim` e `botaoDoFim` coexistem com `LayoutDoResultadoDaPartida` | responsabilidade residual e API pública maior |

Também há dívida reduzida pela própria equipe: `PoliticaDeDestaque`, layouts puros, classes de tempestade e políticas de ranking impediram que toda a lógica nova permanecesse dentro das telas.

## 8. Refatorações propostas e situação

As propostas estão ordenadas pelo equilíbrio entre benefício e risco. Nenhuma altera regra, texto, protocolo ou visual.

| Prioridade | Refatoração | Benefício | Risco | Situação |
|---:|---|---|---|---|
| 1 | Extrair o desenho do resultado de `GameScreen` | alto | baixo a médio | concluída |
| 2 | Extrair a preparação da partida de `CampusScreen` | alto | médio | concluída |
| 3 | Extrair o radar de `HudRenderer` | médio | baixo | melhoria futura |
| 4 | Extrair a tempestade de `MundoRenderer` | médio | médio | melhoria futura |

### 8.1 Extrair `ResultadoDaPartidaRenderer` (concluída)

Extrair de `GameScreen` os métodos `atualizarLayoutDoResultado`, `medirLargura`, `medirAlturaQuebrada`, `desenharFimDePartida`, `desenharFimDePartidaNoToque`, `desenharTextoQuebradoCentralizado`, `desenharRotuloDoBotao`, `motivoDoFim` e `desenharSituacaoDoEnvio`.

Destino: `gdx/hud/ResultadoDaPartidaRenderer`, recebendo um objeto imutável com título, motivo, situação do envio, campanha, foco e áreas dos botões. `GameScreen` continuará responsável por ações, transições e reenvio.

Melhorias:

- SRP e High Cohesion em `GameScreen`.
- Information Expert no renderer do modal.
- ISP, pois a renderização recebe somente os dados necessários.

Testes de segurança:

- `LayoutDoResultadoDaPartidaTest` nos tamanhos e fatores de texto atuais.
- `ControleDoFimDaPartidaTest` para foco e ações.
- `PoliticaDeRegistroNoRankingTest` e `RegistroAlternativoNoRankingLocalTest` para o texto de estado não alterar o fluxo.
- Novo teste puro do modelo visual do resultado, cobrindo vitória, derrota, campanha, erro e sucesso de envio.
- `./gradlew clean build` do cliente e conferência das duas orientações com fator 1,6.

### 8.2 Extrair `PreparadorDaPartidaDaCampanha` (concluída)

Extrair de `CampusScreen.iniciarMissao` e do reinício da campanha em `GameScreen` a criação de `PreparacaoDaEtapa`, `RegraDePontuacaoPadrao`, `RegraDePontuacaoDaCampanha` e `GameSession`.

Destino: `application/campanha/PreparadorDaPartidaDaCampanha`. A classe receberá `MissaoFactory`, etapa, piloto, limites e `Random`, e devolverá a `GameSession` pronta. A tela apenas solicitará a sessão e abrirá `GameScreen`.

Melhorias:

- SRP e DIP em `CampusScreen`.
- GRASP Creator e Pure Fabrication na aplicação.
- High Cohesion da montagem de uma missão de campanha.

Testes de segurança:

- Novo `PreparadorDaPartidaDaCampanhaTest` para dificuldade, modo, piloto, regra de pontuação, NAMI e tempestade.
- `MissaoFactoryCampanhaTest` para semente, casas livres e mecânicas.
- `RegraDePontuacaoDaCampanhaTest` e `GameSessionCampanhaTest`.
- `CampusTest` e `CampusLayoutTest` para provar que progresso e visual não mudaram.
- `./gradlew clean build` do cliente.

### 8.3 Extrair `RadarRenderer` (melhoria futura)

Extrair de `HudRenderer` os métodos `desenharRadar`, `ponto`, `quadrado`, `desenharJanelaVisivel`, `linhaTracejadaHorizontal` e `linhaTracejadaVertical`.

Destino: `gdx/hud/RadarRenderer`, recebendo `ShapeRenderer`, `LayoutDoHud`, `GameSession` e a janela visível. `HudRenderer` apenas decide se o cartão existe e delega o desenho.

Melhorias:

- SRP e High Cohesion em `HudRenderer`.
- Information Expert no componente que conhece a projeção do radar.
- Menor superfície de alteração ao incluir um marcador.

Testes de segurança:

- `RadarGeometriaTest` para projeção de coordenadas.
- `JanelaDoRadarTest` para área visível.
- `VisibilidadeDaTempestadeTest` para filtragem.
- Novo teste do modelo de marcadores, cobrindo nave, base, passageiros, inimigos, NAMI e tempestade.
- `LayoutDoHudTest`, `HudRendererTextoTest` e `./gradlew clean build`.

### 8.4 Extrair `TempestadeRenderer` (melhoria futura)

Extrair de `MundoRenderer` os métodos `desenharEscuridao`, `desenharPoeira` e `desenharParticulas`, além das cores e constantes exclusivas da tempestade.

Destino: `gdx/visual/TempestadeRenderer`, recebendo recursos gráficos e a `MapaGeometria`. `MundoRenderer` preservará a ordem atual: objetos visíveis, máscara, base, poeira, partículas, etiqueta e mensagens.

Melhorias:

- SRP e High Cohesion em `MundoRenderer`.
- Protected Variations para trocar a aparência da tempestade sem tocar nas entidades.
- Encapsulamento da configuração de blend e restauração do estado gráfico.

Testes de segurança:

- `MascaraDaTempestadeTest` para a transição de opacidade.
- `ParticulasDaTempestadeTest` para deslocamento e limites.
- `CenaVisivelDaTempestadeTest` para ocultação de entidades e permanência da base.
- `MundoRendererGeometriaTest` para tamanhos e quadros.
- Novo teste do plano de camadas da tempestade, fixando a ordem de composição e a restauração da cor.
- `./gradlew clean build` e comparação visual nas etapas 2 e 3.

### 8.5 Resultado das refatorações aprovadas

| Momento | Linhas de `GameScreen` |
|---|---:|
| Antes das refatorações | 1.213 |
| Após `ResultadoDaPartidaRenderer` | 1.037 |
| Após `PreparadorDaPartidaDaCampanha` | 1.032 |

As duas extrações reduziram `GameScreen` em 181 linhas, aproximadamente 14,9%, sem transferir regras de negócio para a camada `gdx`. A primeira concentrou a apresentação do resultado em `gdx/hud`. A segunda concentrou a montagem da sessão de campanha em `application/campanha` e passou a ser usada tanto no início da missão quanto na tentativa de novo.

As propostas 3 e 4 permanecem registradas somente como melhorias futuras. Elas não fazem parte do escopo aprovado da Etapa 2.

## 9. Conclusão

A Fase 8 preserva uma base arquitetural sólida: regras fora do libGDX, servidor independente, DTOs, portas, Strategies, eventos e políticas puras. A principal dívida está na quantidade de responsabilidades reunidas em algumas classes de apresentação.

Não é recomendável dividir `GameSession` ou `LayoutDoHud` somente pelo número de linhas. As duas refatorações aprovadas retiraram blocos com fronteiras visíveis e boa cobertura. Radar e tempestade continuam como possibilidades futuras, sem autorização de implementação nesta etapa.
