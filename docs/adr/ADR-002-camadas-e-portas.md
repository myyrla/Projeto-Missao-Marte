# ADR-002: Camadas e portas

- Estado: aceita no plano da Etapa 1.
- Contexto: havia entidades e persistência, mas motor e console vazios. O checklist exige motor dependente de UI e RankingRepository, sem importar o pacote concreto de UI.
- Decisão: manter `model` como domínio, `engine` como aplicação, `engine.port` como contratos, `persistence` como infraestrutura, `ui` como apresentação e `app.Main` como composição. Mover as duas interfaces para `engine.port`.
- Comunicação: ConsoleUI converte texto em Acao; GameEngine retorna ResultadoTurno e fornece EstadoJogo imutável. A porta UI recebe mensagens e resultado final. ConsoleUI controla o loop de leitura; GameEngine decide regras e estados. RankingService decide classificação e JsonRankingRepository trata arquivos.
- Dependências: Main conhece implementações; aplicação conhece interfaces, domínio, fábrica, Random e Clock; domínio não conhece aplicação, console, arquivos ou rede.
- Alternativa descartada: importar `missao.ui.UI` no motor. Embora fosse uma interface, violaria a fronteira explícita do checklist. Também foi descartada a cópia integral de GameSession do professor, que altera regras.
- Consequências: testes determinísticos com Random e Clock injetados; testes de persistência em diretório temporário; nenhuma dependência adicional. DTOs de estado copiam listas e não expõem entidades mutáveis.
- Padrões: Factory/Creator, Controller, Information Expert, Repository, injeção por construtor e polimorfismo. Dificuldade é tabela de configuração, não Strategy.
