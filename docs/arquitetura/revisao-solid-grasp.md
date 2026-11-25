# Revisão SOLID e GRASP da Fase 8

## 1. Escopo e método

Esta revisão cobre o código de produção da Fase 8 em `cliente/**/src/main/java` e `servidor/src/main/java`. Testes, arquivos gerados e diretórios de build foram excluídos.

As linhas são linhas físicas do arquivo, incluindo comentários e linhas em branco. Os métodos foram contados pela árvore sintática do compilador Java 17. A contagem considera apenas métodos declarados na classe principal do arquivo. Construtores, métodos herdados, métodos de classes internas e métodos `protected` ou sem modificador não entram nas colunas de métodos públicos e privados.

A classificação de origem usa a Fase 7 importada como referência. O README da Fase 8 registra que essa base veio do repositório do Prof. Marcelo Bezerra e teve uso autorizado em sala.

## 2. Resumo executivo

- O servidor não apresenta classes grandes. Sua maior classe tem 94 linhas e a separação Controller, Service, Repository e DTO está clara.
- O cliente concentra complexidade na camada `gdx`. Oito classes ultrapassam 400 linhas (antes eram sete).
- `GameScreen` continua sendo a maior (1.127 linhas). Após as refatorações, desceu para 1.032 e depois subiu com joystick/multitoque/tablet, mas o saldo total ainda é de redução (-7,1%).
- `LayoutDoHud` (562 linhas) e `CampusLayout` (408 linhas, nova no top 10) acumulam variantes responsivas que fragilizam o OCP. Uma proposta de Strategy de layout está registrada como melhoria futura.
- As oito classes novas avaliadas (joystick, controles de toque, escala, orientação, layouts e medidas) seguem boas práticas: são puras, pequenas, testáveis e desacopladas do libGDX quando possível. `OrientacaoPorTamanhoDeTela` é a fonte unica do critério de tablet, referenciada por `EscalaDeTela` e `MissaoMarteGame`.
- `GameSession` não mudou (498 linhas) e continua protegida por Strategy, eventos e testes amplos.
- A equipe já criou boas fronteiras para regras que mudam: pontuação, destaques, ranking, geometria, tipografia, câmera, efeitos da tempestade, entrada por toque e escala por densidade.
- A melhor sequência de refatoração começa pelas responsabilidades visuais e de composição que ainda ficaram dentro das telas, seguida pela extração de variantes dos layouts. Não há refatoração prioritária no servidor.

## 3. Dez maiores classes

A tabela registra três momentos: o diagnóstico inicial, o estado após as refatorações da Etapa 2 e o estado atual (após joystick virtual, multitoque, layouts de toque na horizontal, modo tablet e escala por densidade).

| Posição | Classe | Camada | Diagnóstico | Pós-refatoração | Atual | Variação total |
|---:|---|---|---:|---:|---:|---|
| 1 | `GameScreen` | cliente, `gdx` | 1.213 | 1.032 | 1.127 | -86 (-7,1%) |
| 2 | `HudRenderer` | cliente, `gdx/hud` | 673 | 673 | 732 | +59 (+8,8%) |
| 3 | `CampusScreen` | cliente, `gdx` | 520 | 520 | 612 | +92 (+17,7%) |
| 4 | `LayoutDoHud` | cliente, `gdx/hud` | 483 | 483 | 562 | +79 (+16,4%) |
| 5 | `GameSession` | cliente, `application` | 498 | 498 | 498 | 0 |
| 6 | `MenuScreen` | cliente, `gdx` | 433 | 433 | 498 | +65 (+15,0%) |
| 7 | `MundoRenderer` | cliente, `gdx/visual` | 493 | 493 | 493 | 0 |
| 8 | `CampusLayout` | cliente, `gdx/campanha` | (nova) | (nova) | 408 | nova |
| 9 | `NovaPartidaScreen` | cliente, `gdx` | 390 | 390 | 390 | 0 |
| 10 | `OpcoesScreen` | cliente, `gdx` | 384 | 384 | 384 | 0 |

Destaques da mudança:

- `GameScreen` recuou de 1.213 para 1.032 com as refatorações e depois subiu para 1.127 com joystick, multitoque e célula por densidade. A classe continua sendo a maior, mas o saldo total ainda é de redução.
- `LayoutDoHud` saltou de 483 para 562 (+79) por causa da geometria do toque horizontal (joystick, ações, medidas do jogador com `MedidasDoJogador` e subdivisão do cartão do jogador).
- `CampusScreen` subiu de 520 para 612 (+92) com o layout horizontal de toque, medição de texto combinado cenário/fundador e o método `medirConteudoHorizontalDoToque`.
- `CampusLayout` entrou no top 10 (408 linhas) ao ganhar o método `calcularHorizontalToque` (modo tablet).
- `MenuScreen` subiu de 433 para 498 (+65) com o menu horizontal de toque.
- `HudRenderer` subiu de 673 para 732 (+59) com a subdivisão do cartão do jogador no modo toque horizontal.
- `GameSession`, `MundoRenderer`, `NovaPartidaScreen` e `OpcoesScreen` não mudaram.

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
5. Subdividir o cartão do jogador no modo toque horizontal em `tituloDoJogador`, `linhaDoCasco`, `linhaDosPontos`, `areaDasBarrasDoCasco` e `segmentoDoCasco`.
6. Subdividir o cartão do jogador no modo compacto (faixas) em `linhaPrincipalDoJogador`, `linhaSecundariaDoJogador` e suas metades.

Princípios respeitados:

- SRP: a responsabilidade é geometria responsiva do HUD.
- GRASP Pure Fabrication e Information Expert: concentra medidas e relações geométricas sem depender de libGDX.
- Low Coupling: consome apenas valores, `MargensSeguras`, `MedidasDoHud`, `MedidasDoJogador` e `RetanguloDeTela`.
- Alta testabilidade: `calcular` é determinístico e seus resultados são comparados diretamente nos testes.
- Protected Variations: `GameScreen` e `HudRenderer` não precisam conhecer as fórmulas dos modos `COLUNAS`, `FAIXAS` e `RESERVA`.

Princípios fragilizados:

- OCP: cada novo modo responsivo exige alterar a classe e ampliar o enum privado `Modo`. Análise detalhada na seção 6.
- ISP: os 52 métodos públicos (antes 46) formam uma interface de consulta larga, embora sejam getters e projeções imutáveis. As subdivisões do jogador (`tituloDoJogador`, `linhaDoCasco` etc.) adicionaram 6 novos pontos de consulta.
- SRP no limite: a geometria de fim de partida, por `painelDoFim` e `botaoDoFim`, convive com o HUD mesmo depois da criação de `LayoutDoResultadoDaPartida` para toque. As subdivisões do jogador ampliam esse padrão.

Avaliação: cresceu de 483 para 562 linhas (+16,4%). Continua coesa em torno da geometria responsiva, mas a tendência de crescimento e o acúmulo de variantes justificam a proposta de Strategy registrada na seção 6.3.

### 4.7 `MenuScreen`

Responsabilidades atuais:

1. Controlar navegação por teclado e toque em `tratarTecla`, `tratarTeclaDaConfirmacao` e `aoTocar`.
2. Escolher a próxima tela em `executar`.
3. Controlar o modal de saída.
4. Medir e adaptar o modo toque em `atualizarLayoutDoToque`.
5. Desenhar fundo, título, nave, opções, rodapé e confirmação.

Princípios respeitados:

- GRASP Controller: recebe eventos da interface e decide a navegação.
- Indirection: usa `ListaDeBotoes`, `LayoutDoMenu`, `LayoutDoMenuToque`, `LayoutDoMenuHorizontalToque`, `RodapeDeTeclas` e `PoliticaDeAtalhos`.
- Separação de responsabilidades já iniciada: cálculos responsivos e seleção de foco são puros e testáveis fora da tela.

Princípios fragilizados:

- SRP: navegação, modal, medição e desenho ainda estão juntos.
- OCP: `executar` e `desenharOpcoes` dependem dos índices fixos das quatro opções.
- GRASP Creator e Low Coupling: `executar` instancia telas concretas como `CampusScreen`, `NovaPartidaScreen` e `RankingScreen`.

Avaliação: cresceu de 433 para 498 linhas (+15%) com o menu horizontal de toque. A extração de `LayoutDoMenuHorizontalToque` (106 linhas) conteve parte do crescimento. O risco atual é menor que em `GameScreen`.

## 5. Avaliação das classes introduzidas pelo joystick, multitoque, layouts de toque e modo tablet

### 5.1 `JoystickVirtual` (148 linhas, `gdx/entrada`)

Traduz o arrasto de um ponteiro em comandos de movimento (`Acao`). Mantém o estado do ponteiro ativo, a posição visual do botão e a direção corrente. Devolve `Resultado` com uma lista imutável de `Comando`.

Princípios respeitados:

- SRP: a classe tem uma responsabilidade (traduzir gesto em direção). Não desenha, não toca som, não conhece a partida.
- GRASP Information Expert: possui centro, raio, zona morta e ponteiro, e decide a direção com base nesses dados (`direcaoDe`).
- GRASP Pure Fabrication: não existe "joystick" no domínio. A classe é uma fabricação técnica da camada de entrada.
- Low Coupling: depende apenas de `Acao` (application). Não importa libGDX.
- Protected Variations: `pressionar`, `arrastar` e `soltar` isolam a mecânica de ponteiros de quem consome as ações.
- OCP: `Resultado` e `Comando` usam records imutáveis, e `TipoDeComando` permite estender sem editar.

Princípios fragilizados:

- Nenhum ponto significativo. A classe é coesa, pequena e bem limitada.

### 5.2 `ControleDeToque` (312 linhas, `gdx/entrada`)

Monta o layout dos controles virtuais (joystick + botões circulares) para retrato e paisagem, e roteia ponteiros para o joystick ou para o botão tocado.

Princípios respeitados:

- SRP: a classe monta geometria e roteia toques. Não desenha (isso fica em `ControleDeToqueRenderer`).
- GRASP Creator: cria `JoystickVirtual` e `BotaoVirtual` a partir da área disponível, do modo (dinâmico/clássico) e do alvo mínimo.
- GRASP Pure Fabrication e Low Coupling: não depende de libGDX. Recebe `RetanguloDeTela` e `MedidasDosRotulos` como dados.
- Protected Variations: `MedidasDosRotulos` desacopla a medição tipográfica (feita no renderer) do cálculo geométrico.
- DIP: `acaoDe` converte IDs de string em `Acao` sem depender da tela ou da sessão.

Princípios fragilizados:

- SRP no limite: `montarEmRetrato` e `montarAcoesEmPaisagem` são dois algoritmos de layout na mesma classe. Se um terceiro layout surgir (por exemplo, joystick bilateral para dois jogadores), a classe precisará ser editada (OCP fragilizado).
- OCP: `acaoDe` usa switch por string. Um botão novo exige editar o switch. O risco é baixo, pois o conjunto de ações é estável.

### 5.3 `ControleDeToqueRenderer` (132 linhas, `gdx/entrada`)

Desenha o joystick e os botões circulares. É a única classe de entrada que depende de libGDX (`SpriteBatch`, `ShapeRenderer`, `FontesDoJogo`).

Princípios respeitados:

- SRP: apenas desenha. Toda decisão de toque está em `ControleDeToque` e `JoystickVirtual`.
- GRASP Indirection: a classe media entre o libGDX e os dados puros de `ControleDeToque`, separando apresentação de lógica.
- Low Coupling: recebe `ControleDeToque` pronto, não conhece `GameSession`, `Campanha` nem `GameScreen`.
- Robustez gráfica: restaura cores das fontes e do `SpriteBatch` em blocos `finally`.

Princípios fragilizados:

- OCP: `desenharRotulo` decide a cor do texto por comparação de ID (`ATIRAR`/`EMBARCAR`). Um botão com cor especial exigiria editar o método. O risco é baixo dado o conjunto fixo de botões.

### 5.4 `OrientacaoPorTamanhoDeTela` (18 linhas, `gdx/plataforma`)

Enum com dois valores (`RETRATO`, `PAISAGEM`) e fonte de verdade unica do critério "é tablet" (`smallestScreenWidthDp >= 600`).

Princípios respeitados:

- SRP: uma responsabilidade (classificar tela por tamanho).
- GRASP Information Expert: possui a constante `MENOR_LARGURA_DE_TABLET_DP` e decide tanto orientação quanto `eTablet`.
- DIP e Protected Variations: `EscalaDeTela` e `MissaoMarteGame` consultam `eTablet` em vez de duplicar o limiar.
- OCP: adicionar um terceiro limiar (por exemplo, "é dobrável") pode ser feito com novo método, sem alterar `eTablet` nem `paraMenorLarguraDp`.

Princípios fragilizados:

- Nenhum. A classe é mínima e estável.

### 5.5 `EscalaDeTela` (70 linhas, `gdx/layout`)

Utilitária de conversão dp para mundo, fator de texto e alvo mínimo de toque. Classe final com construtor privado.

Princípios respeitados:

- SRP: todas as funções giram em torno de uma unica responsabilidade (escala entre dp, pixels e unidades do mundo).
- GRASP Pure Fabrication: não corresponde a nenhuma entidade do domínio.
- DIP: o método `fatorDeTexto(Plataforma, float, float, int)` recebe `Plataforma` como abstração, e delega o critério de tablet a `OrientacaoPorTamanhoDeTela.eTablet`, evitando duplicar regras.
- OCP: a constante `FATOR_DE_TEXTO_PARA_TABLET` é nomeada e documentada com a conta física. Trocar o valor não exige entender a fórmula.
- Low Coupling: não depende de libGDX. Recebe primitivos e `Plataforma`.

Princípios fragilizados:

- OCP no limite: se um terceiro perfil de densidade surgir (por exemplo, fator especifico para dobráveis), será necessário outro `if` no método com quatro parâmetros. A progressão natural seria uma Strategy de fator, mas neste momento o custo supera o benefício.

### 5.6 `LayoutDoMenuHorizontalToque` (106 linhas, `gdx/ui`)

Geometria pura das opções e do rodapé do menu horizontal de toque. Calcula posição dos quatro botões, rótulos, detalhe da campanha, conta e instrução.

Princípios respeitados:

- SRP: a classe calcula exclusivamente a geometria horizontal do menu de toque.
- GRASP Pure Fabrication: não corresponde a nenhuma entidade do domínio.
- Low Coupling: depende apenas de `MargensSeguras`, `RetanguloDeTela` e `MedidasDoMenuHorizontalToque`, sem libGDX.
- Protected Variations: isola `MenuScreen` das fórmulas responsivas. A tela apenas consulta `botao(i)`, `rotulo(i)`, etc.
- Alta testabilidade: a saída é determinística a partir de largura, altura, margens e medidas.

Princípios fragilizados:

- SRP no limite: o cálculo decide se o detalhe da campanha fica em linha ou abaixo do primeiro botão. Uma variante do layout exigiria editar a mesma classe (OCP).

### 5.7 `MedidasDoJogador` (9 linhas, `gdx/hud`)

Record com três medidas tipográficas do cartão do jogador (`alturaDoTitulo`, `alturaDaLinha`, `larguraDoRotuloDoCasco`) e um factory method `padrao(escala)`.

Princípios respeitados:

- SRP: encapsula um unico conceito (métricas do jogador para o HUD).
- GRASP Information Expert: possui as medidas e sabe calculá-las a partir de uma escala.
- ISP: a interface é mínima, três campos e um factory.
- Imutabilidade: record garante que ninguém altera os valores depois da construção.

Princípios fragilizados:

- Nenhum. A classe é um dado puro.

### 5.8 `MedidasDoCampusHorizontalToque` (22 linhas, `gdx/campanha`)

Record com métricas do layout horizontal de toque do campus: conteudo (`MedidasDoCampus`), larguras das etapas (`LargurasDaEtapa` com cenário e fundador), largura mínima do painel, alturas do título/subtítulo/botão e alvo mínimo.

Princípios respeitados:

- SRP: encapsula métricas tipográficas medidas pela tela e consumidas pelo layout puro.
- GRASP Protected Variations: desacopla `CampusLayout.calcularHorizontalToque` da medição real de texto (que usa `BitmapFont` no libGDX).
- Validação no construtor compacto: exige exatamente três etapas, falhando rápido.
- Imutabilidade: record com `List.copyOf`.

Princípios fragilizados:

- OCP: o construtor rejeita listas que não tenham exatamente 3 etapas. Se a campanha ganhar uma quarta etapa, a classe precisa mudar. O risco é baixo, pois o numero de etapas é uma regra de negócio estável.

## 6. Acúmulo de variantes em `LayoutDoHud` e `CampusLayout`

### 6.1 Situação atual

`LayoutDoHud` (562 linhas, 52 métodos públicos) possui cinco variantes internas, selecionadas pelo enum privado `Modo`:

| Variante | Método | Quando |
|---|---|---|
| Colunas horizontais | `calcularColunas` | paisagem com espaço lateral suficiente |
| Faixas verticais | `calcularFaixas` | retrato com espaço vertical suficiente |
| Reserva paisagem | `calcularReservaPaisagem` | paisagem quando colunas não cabem |
| Reserva retrato | `calcularReservaRetrato` | retrato quando faixas não cabem |
| Reserva (despacho) | `calcularReserva` | delega para paisagem ou retrato |

Desde a revisão anterior, a classe cresceu 79 linhas com medidas do jogador (`MedidasDoJogador`), subdivisão do cartão em linhas (`tituloDoJogador`, `linhaDoCasco`, `linhaDosPontos`, `areaDasBarrasDoCasco`, `segmentoDoCasco`) e subdivisão compacta para faixas (`linhaPrincipalDoJogador`, `linhaSecundariaDoJogador` etc.). Essas subdivisões ficam nos getters, não nas fábricas, mas a API pública saltou de 46 para 52 métodos.

`CampusLayout` (408 linhas, 28 métodos públicos) possui quatro variantes:

| Variante | Método | Quando |
|---|---|---|
| Horizontal (desktop) | `horizontal` | paisagem sem toque |
| Vertical (retrato) | `vertical` | retrato com toque |
| Horizontal de toque | `calcularHorizontalToque` | paisagem com toque (tablets) |
| Padrão | `padrao` | 800x600, atalho |

A variante `calcularHorizontalToque` foi acrescentada inteira neste ciclo e é a maior (100 linhas), responsável pelo crescimento da classe.

### 6.2 Avaliação frente ao OCP

Ambas as classes acumulam variantes de forma que fere o princípio aberto/fechado:

- **Cada novo modo responsivo exige editar a classe.** Se surgisse um modo "colunas empilhadas para dobráveis" em `LayoutDoHud` ou "campus em duas colunas" em `CampusLayout`, o novo código entraria na mesma classe, ampliando o enum `Modo` ou adicionando mais um factory estático.
- **Os getters derivados crescem junto.** `LayoutDoHud.fichaDoObjetivo` já contém um `if` por `Modo`, e `tituloDoJogador`/`linhaDoCasco` verificam `toqueHorizontal()`. Cada variante nova pode exigir novos ramos nesses métodos.
- **Os construtores são pesados.** `LayoutDoHud` tem 24 parâmetros e `CampusLayout` tem 21. Variantes futuras ampliam essa assinatura.

Pontos atenuantes:

- Os dois objetos são imutáveis e puros (sem libGDX), o que limita o impacto de uma mudança a uma classe bem testada.
- O contrato público (retângulos por nome) é estável: quem consome `LayoutDoHud` não precisa saber qual variante foi escolhida.
- Na prática, novas variantes responsivas são raras. As cinco de `LayoutDoHud` já cobrem celular retrato, celular paisagem, tablet paisagem e desktop.

### 6.3 Proposta de solução (sem implementar)

Extrair a seleção de variante para um padrão Strategy de layout:

```java
// Contrato da Strategy
public interface EstrategiaDeLayoutDoHud {
    LayoutDoHud calcular(float largura, float altura, int celulas,
            boolean toque, MargensSeguras margens, RetanguloDeTela mapa,
            MedidasDoHud medidas);
}

// Cada variante vira uma classe pequena e fechada
public final class LayoutEmColunas implements EstrategiaDeLayoutDoHud { ... }
public final class LayoutEmFaixas implements EstrategiaDeLayoutDoHud { ... }
public final class LayoutDeReserva implements EstrategiaDeLayoutDoHud { ... }
```

`LayoutDoHud` continuaria como o objeto imutável com getters, mas perderia os factories. A seleção entre estratégias ficaria em um `SeletorDeLayoutDoHud` que testa cada uma em cascata (como o `calcular` público já faz hoje).

Benefícios:

- OCP: adicionar uma variante é criar uma classe, sem editar `LayoutDoHud`.
- SRP: cada estratégia contém um unico algoritmo de posicionamento.
- Testabilidade: cada estratégia pode ser testada isoladamente.

Riscos e custo:

- Os getters derivados (`fichaDoObjetivo`, `tituloDoJogador`) ainda precisariam do `Modo` ou de um campo para decidir a variante. A Strategy não elimina esse if, apenas move-o.
- O construtor de 24 parâmetros poderia usar um Builder, mas acrescentaria uma camada sem reduzir a complexidade real.
- Prioridade baixa: o numero de variantes é estável e novas variantes responsivas não estão previstas.

A mesma abordagem se aplica a `CampusLayout`, com `EstrategiaDeCampusLayout` e implementações `CampusHorizontal`, `CampusVertical` e `CampusHorizontalToque`.

## 7. Avaliação do servidor

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

## 8. Pontos fortes do projeto

### 8.1 Strategy e Decorator de pontuação

`GameSession` depende de `RegraDePontuacao`, não de números específicos. `RegraDePontuacaoPadrao` preserva a partida rápida. `RegraDePontuacaoDaCampanha` decora outra regra, multiplica seus valores e acrescenta somente os bônus da campanha. Isso respeita OCP, DIP e Strategy. Os testes `RegraDePontuacaoPadraoTest`, `RegraDePontuacaoDaCampanhaTest` e `GameSessionPontuacaoTest` protegem a decisão.

### 8.2 Eventos sem dependência da interface

`GameSession` devolve `ResultadoTurno` com uma lista de `EventoJogo`. A aplicação emite `COLISAO`, `ENTREGA`, `NAMI_ATIVADA` e outros eventos, enquanto `GameScreen.reagirAosEventos` decide som, tremor e efeitos. É uma aplicação simples de Observer, Low Coupling e Protected Variations. `GameSessionEventosTest` e `GameSessionCampanhaTest` cobrem o contrato.

### 8.3 Política pura de destaque

`PoliticaDeDestaque.decidir` escolhe o alvo, opacidades e zonas sem usar libGDX. `posicionarEtiqueta` recebe retângulos e devolve uma posição opcional. A mesma instância de `AlvoDoObjetivo` alimenta HUD e mapa, eliminando cálculos divergentes. Isso aplica Pure Fabrication, Information Expert e alta testabilidade. `PoliticaDeDestaqueTest` cobre seleção, resgatados, alcance, colisão com HUD e nave.

### 8.4 Política de ranking explícita

`PoliticaDeRegistroNoRanking.destino` centraliza a decisão entre `NENHUM`, `LOCAL` e `GLOBAL`. A campanha não entra em ranking, o convidado registra vitória local e o jogador autenticado usa o servidor. `RegistroAlternativoNoRankingLocal` trata a falha de conexão uma única vez. Isso reduz condicionais espalhadas e aplica Pure Fabrication e Protected Variations. `PoliticaDeRegistroNoRankingTest` e `RegistroAlternativoNoRankingLocalTest` protegem os casos.

### 8.5 Layouts puros e medidos

`LayoutDoHud`, `CampusLayout`, `LayoutDoMenu`, `LayoutDaNovaPartida`, `LayoutDoResultadoDaPartida`, `LayoutDoMenuHorizontalToque` e `ControleDeToque` não dependem de libGDX. As medidas tipográficas entram como objetos de dados calculados com a escala ativa, por exemplo `MedidasDoHud`, `MedidasDoCampus`, `MedidasDoCampusHorizontalToque`, `MedidasDoJogador` e `MedidasDosRotulos`. Isso aplica Pure Fabrication, Low Coupling e Information Expert. Os testes de layout verificam tamanhos, áreas úteis, não sobreposição, modo reserva e fatores de texto diferentes.

### 8.6 Separação `domain`, `application` e `gdx`

O domínio possui `Missao`, `Nave`, passageiros, perigos e `EstacaoNami`, sem importar libGDX. A aplicação coordena casos de uso, pontuação, campanha, eventos e portas. `gdx` traduz entrada e desenha. O servidor possui processo e modelo independentes do cliente e usa DTOs na API. Essa separação aplica DIP, Indirection e Protected Variations.

### 8.7 Pequenas classes de geometria e efeitos

`CameraDaPartida`, `EscalaDaCelula`, `EscalaDeTela`, `OrientacaoPorTamanhoDeTela`, `JoystickVirtual`, `MascaraDaTempestade`, `ParticulasDaTempestade`, `CenaVisivelDaTempestade`, `GeometriaDoRastro`, `AparenciaDaNami` e `RecorteDoMundo` retiram decisões dos renderers e das telas. São classes puras, pequenas e cobertas por testes diretos. `EscalaDeTela` centraliza a conversão dp/mundo e o fator de texto, `OrientacaoPorTamanhoDeTela` é a fonte unica do critério de tablet, e `JoystickVirtual` traduz gestos em comandos sem depender de libGDX.

## 9. Dívida herdada e dívida criada pela equipe

### 9.1 Dívida herdada da base do professor

| Dívida | Evidência na Fase 7 | Situação na Fase 8 |
|---|---|---|
| `GameScreen` já concentrava entrada, tempo real, renderização e fim de partida | 727 linhas na Fase 7 | a equipe acrescentou campanha, câmera e novo visual, chegando a 1.213 linhas |
| `GameSession` já reunia turno clássico, modo dinâmico, tiro, pausa e temporizadores | 458 linhas na Fase 7 | a equipe acrescentou Strategy de pontuação, NAMI e visibilidade, chegando a 498 linhas |
| `SimboloParaImagem` escolhe imagem por `char` em um `switch` | a classe e os símbolos já existiam na Fase 7 | a equipe apenas acrescentou o símbolo `H` da NAMI |
| `ObjetoRenderizavel.getSimbolo` leva uma identidade de apresentação textual para o contrato do domínio | contrato importado desde as fases da base | continua evitando libGDX no domínio, mas um tipo novo exige editar o mapa central de imagens |
| As telas libGDX misturam navegação e desenho | `MenuScreen` tinha 182 linhas e `HudRenderer` 246 linhas | o padrão foi mantido e ampliado pela nova interface |

O comentário de `SimboloParaImagem` afirma OCP, mas a inclusão da NAMI exigiu editar o `switch`. A classe evita `instanceof`, porém não está fechada para novos símbolos. A dívida é herdada; a extensão `H` feita pela equipe apenas evidencia a limitação.

### 9.2 Dívida criada ou ampliada pela equipe

| Dívida | Evidência | Consequência |
|---|---|---|
| Crescimento de `GameScreen` | novos métodos de joystick, multitoque, câmera, recorte, destaques, campanha, rede e resultado. Subiu de 1.032 para 1.127 desde a refatoração | maior risco de regressão ao alterar qualquer parte da partida |
| Crescimento de `HudRenderer` | radar, cartões, status, avisos, subdivisão do jogador e medição. Subiu de 673 para 732 | mudanças independentes disputam o mesmo estado gráfico |
| Acúmulo de variantes em `LayoutDoHud` e `CampusLayout` | 5 variantes em `LayoutDoHud` (562 linhas, 52 métodos) e 4 em `CampusLayout` (408 linhas). Cada nova variante exige editar a mesma classe | OCP fragilizado; getters derivados com `if` por modo crescem junto (seção 6) |
| `CampusScreen` reúne medição, controle e desenho | 612 linhas (+92), `layoutHorizontalDoToque`, `medirConteudoHorizontalDoToque`, `tratarTecla` e `desenharCampus` | SRP e High Cohesion ficam fragilizados |
| Composição da partida dentro de `CampusScreen` | `iniciarMissao` instancia duas regras de pontuação e `GameSession` | apresentação conhece detalhes de criação da aplicação |
| `MundoRenderer` incorpora todas as camadas visuais novas | tempestade, rastro, alcance, NAMI, pulsos e etiquetas | ordem de desenho e restauração de estado ficam centralizadas e frágeis |
| Mapeamento de tipo repetido na apresentação | `PoliticaDeDestaque.Tipo.de`, `HudRenderer.corDoPassageiro` e cores de fundador | novo tipo visual exige alterações em vários pontos |
| `LayoutDoHud` mantém também o resultado horizontal | `painelDoFim` e `botaoDoFim` coexistem com `LayoutDoResultadoDaPartida` | responsabilidade residual e API pública maior |
| Dois modos de layout em `ControleDeToque` | `montarEmRetrato` e `montarAcoesEmPaisagem` na mesma classe (312 linhas) | um terceiro layout exigiria editar a classe (OCP no limite) |

Também há dívida reduzida pela própria equipe: `PoliticaDeDestaque`, layouts puros, classes de entrada desacopladas (`JoystickVirtual`, `ControleDeToque` sem libGDX), `EscalaDeTela` como ponto unico de conversão, `OrientacaoPorTamanhoDeTela` como fonte unica do critério de tablet, classes de tempestade e políticas de ranking impediram que toda a lógica nova permanecesse dentro das telas.

## 10. Refatorações propostas e situação

As propostas estão ordenadas pelo equilíbrio entre benefício e risco. Nenhuma altera regra, texto, protocolo ou visual.

| Prioridade | Refatoração | Benefício | Risco | Situação |
|---:|---|---|---|---|
| 1 | Extrair o desenho do resultado de `GameScreen` | alto | baixo a médio | concluída |
| 2 | Extrair a preparação da partida de `CampusScreen` | alto | médio | concluída |
| 3 | Extrair o radar de `HudRenderer` | médio | baixo | melhoria futura |
| 4 | Extrair a tempestade de `MundoRenderer` | médio | médio | melhoria futura |
| 5 | Extrair variantes de `LayoutDoHud` em Strategies | médio | médio | melhoria futura |
| 6 | Extrair variantes de `CampusLayout` em Strategies | baixo | médio | melhoria futura |

### 10.1 Extrair `ResultadoDaPartidaRenderer` (concluída)

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

### 10.2 Extrair `PreparadorDaPartidaDaCampanha` (concluída)

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

### 10.3 Extrair `RadarRenderer` (melhoria futura)

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

### 10.4 Extrair `TempestadeRenderer` (melhoria futura)

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

### 10.5 Resultado das refatorações aprovadas

| Momento | Linhas de `GameScreen` |
|---|---:|
| Antes das refatorações | 1.213 |
| Após `ResultadoDaPartidaRenderer` | 1.037 |
| Após `PreparadorDaPartidaDaCampanha` | 1.032 |
| Após joystick, multitoque, layouts e modo tablet | 1.127 |

As duas extrações reduziram `GameScreen` em 181 linhas. Depois, o joystick virtual, multitoque, célula por densidade e integração com `ControleDeToque` acrescentaram 95 linhas. O saldo total é de redução de 86 linhas (-7,1%).

As propostas 3 a 6 permanecem registradas somente como melhorias futuras. Elas não fazem parte do escopo aprovado.

## 11. Conclusão

A Fase 8 preserva uma base arquitetural sólida: regras fora do libGDX, servidor independente, DTOs, portas, Strategies, eventos e políticas puras. As oito classes novas avaliadas nesta revisão (joystick, controles de toque, escala, orientação, layouts e medidas) seguem os mesmos padrões: são puras, pequenas, testáveis e desacopladas do libGDX quando não precisam dele.

A principal dívida está no acúmulo de variantes responsivas dentro de `LayoutDoHud` e `CampusLayout`. Ambas cresceram significativamente (562 e 408 linhas) e cada nova variante exige editar a mesma classe, fragilizando o OCP. A proposta de Strategies de layout (seção 6.3) oferece uma saída, mas o custo supera o benefício enquanto o conjunto de variantes permanecer estável.

`GameScreen` continua sendo a classe mais complexa, mas com saldo de redução (-7,1% desde o diagnóstico). As classes de entrada (`JoystickVirtual`, `ControleDeToque`) e de escala (`EscalaDeTela`, `OrientacaoPorTamanhoDeTela`) retiraram decisões da tela e concentraram regras em pontos testáveis.

Não é recomendável dividir `GameSession` somente pelo número de linhas. As refatorações concluídas retiraram blocos com fronteiras visíveis e boa cobertura. Radar, tempestade e Strategies de layout continuam como possibilidades futuras, sem autorização de implementação nesta etapa.
