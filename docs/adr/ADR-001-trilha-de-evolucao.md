# ADR-001: Trilha de evolução

- Estado: aceita no plano da Etapa 1.
- Contexto: prazo da disciplina e autorização em sala para uso da base do Prof. Marcelo Bezerra.
- Decisão: preservar a Fase 0 da base, completar a Fase 1 da equipe e importar Fases 2 a 7 em etapa própria. A melhoria da equipe será a Fase 8, Campanha dos Fundadores.
- Alternativa descartada: evoluir o código da equipe até a Fase 7 antes de começar a melhoria. Exigiria reconstruir integrações já disponíveis e reduziria o tempo para o jogo.
- Consequências: Fase 1 usa `missao.*`; Fases 2 em diante usam os pacotes da base. Não há migração automática de arquivos de ranking entre todas as fases. Cada pasta é independente e as versões anteriores são preservadas.
- Origem: importações possuem commit próprio, revisão da base e aviso no README. Código reutilizado e alterações da equipe devem ser identificáveis. Nesta sessão os commits estão pendentes por restrição de escrita em `.git`.
