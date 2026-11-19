# ADR-003: Remoção da mecânica do rover

## Status

Aceita em 18/11/2026.

## Contexto

As paradas do rover transportavam a nave entre dois pontos do mapa. A mecânica foi implementada e testada, mas o teletransporte confundia o jogador e prejudicava a leitura espacial da partida.

## Decisão

Remover o rover por completo da Fase 8, incluindo domínio, aplicação, eventos, configuração, recursos visuais, rotas e testes específicos. A etapa 3 mantém a Estação NAMI e a tempestade de areia. Ao vencer essa etapa, o Engenheiro libera Prefeitura e NAMI no campus.

O carregamento do progresso aceita campos extras de versões anteriores e ignora nomes de construções que não existem mais, como `REDE_DO_ROVER`.

## Consequências

- A nave não muda mais de posição por teletransporte.
- A etapa 3 fica mais simples de compreender sem perder suas outras mecânicas.
- O progresso já salvo continua válido.
- A Rede do Rover não recebe uma construção substituta.
