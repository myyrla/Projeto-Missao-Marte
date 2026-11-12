# Fase 01: refatoração da equipe

Implementação da equipe a partir do código registrado em `6330869`, mantendo o comportamento de `fase-0-oopuro` do Prof. Marcelo Bezerra. A base foi usada como referência autorizada em sala. A Fase 1 do professor orientou a separação de responsabilidades, mas não foi importada: seus controles e regras diferentes não foram adotados.

## Executar e testar

Na pasta `fases/fase-01-refatoracao`, com JDK 17:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
java -version
mkdir -p out
find src tests -name '*.java' -print0 | xargs -0 javac --release 17 -encoding UTF-8 -d out
java -ea -cp out missao.TodosOsTestes
java -cp out missao.app.Main
```

Não há Gradle nem bibliotecas externas nesta fase. O teste sinaliza falhas por `AssertionError`, mesmo sem `-ea`. Os testes de JSON usam diretório temporário e não alteram o ranking de jogadores.

## Controles e regras

- Menu: iniciar missão, visualizar ranking, resetar com confirmação e sair.
- w/a/s/d movimentam; c embarca; q aborta. A entrada usa a primeira letra, sem diferenciar maiúsculas.
- Nave começa na plataforma L em (0,0), com 3 vidas e capacidade 5.
- Fácil: 4 passageiros, 1 asteroide, 1 inimigo, 30 pontos. Médio: 5/2/2 e 20 pontos. Difícil: 5/3/3 e 45 pontos.
- Professor vale 10, Engenheiro 15, Astronauta 20. No fácil a composição original é P/E/P/E; nas demais é P/E/P/E/T.
- Cada movimento custa 1 ponto, mesmo bloqueado pela borda. Embarque não custa ponto, mas movimenta inimigos mesmo quando não há passageiro.
- Colisão custa uma vida por turno. Perigos permanecem no mapa e não causam desconto extra de pontos.
- Vitória exige todos a bordo e retorno à base com vida e pontos. Derrota tem prioridade se pontos ou vidas acabam nesse turno.
- Só vitórias entram no Top 5; empate com o quinto não o substitui. Não há desembarque na base.

## Estrutura e decisões

`model` contém entidades; `engine` contém aplicação e `engine/port` suas interfaces; `ui` lê e apresenta; `persistence` adapta arquivos; `app/Main` monta dependências. Consulte o [tutorial](docs/fase-01-tutorial.md), o [diagnóstico](../../docs/diagnostico.md) e as [ADRs](../../docs/adr/ADR-002-camadas-e-portas.md).

## Persistência e limitações

O arquivo `ranking.json` é relativo ao diretório de execução e não é versionado. Execute cada fase em sua pasta para não misturar dados. O leitor aceita `name` e `nome`, e duração ausente assume zero; a escrita usa o formato original com `tempoJogo`. Erros de arquivo/JSON são informados ao jogador. O parser atende o formato plano de ranking, não JSON arbitrário.

Correção aprovada: mapas sem espaço para todos os objetos são rejeitados antes do sorteio. A interface solicita outro tamanho. O sorteio de mapas válidos e a ordem dos passageiros seguem a Fase 0. Não há garantia de que toda configuração aleatória resulte em uma missão fácil de vencer. Mapas enormes podem consumir muita memória de renderização; não foi introduzido limite de tamanho arbitrário.

## Validação

Compilação em Temurin 17.0.21 e seis grupos de testes: domínio, fábrica, motor, política de ranking, JSON e console. O motor é exercitado em vitória, derrota, abandono, borda, embarque vazio, colisão, prioridade de derrota, repetição após término e duração congelada. A fábrica é determinística por semente; o console trata EOF e mapas pequenos.

Comparação adicional com a Fase 0: 150 cenários (50 sementes por dificuldade) e 3000 turnos de inimigos produziram posições, nomes e pontuações idênticos. Após compilar também a Fase 0, execute nesta pasta:

```bash
java -cp out:../fase-00-original/out missao.ComparacaoFase0
```

Os commits estão pendentes por restrição de escrita da sessão em `.git`; os fontes foram movidos pelo sistema de arquivos preservando o estado original no commit `6330869`. A Etapa 2 não foi iniciada.
