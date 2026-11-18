package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.application.pontuacao.RegraDePontuacaoDaCampanha;
import br.edu.unifor.missaomarte.application.pontuacao.RegraDePontuacaoPadrao;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Professor;
import java.util.Random;
import org.junit.jupiter.api.Test;

class GameSessionPontuacaoTest {
    private static final int MIN = -5;
    private static final int MAX = 5;

    @Test
    void campanhaAplicaRegraPropriaAteOBonusFinalUmaUnicaVez() {
        Nave nave = new Nave(0, 0, 3, 1);
        Missao missao = new Missao(nave);
        missao.adicionarPassageiro(new Professor("Fundador", 1, 0));
        GameSession sessao = new GameSession("Teste", Dificuldade.FACIL, missao,
                new RegraDePontuacaoDaCampanha(new RegraDePontuacaoPadrao(100)),
                MIN, MAX, new Random(1), ModoDeJogo.CLASSICO);

        sessao.processarAcao(Acao.DIREITA);
        sessao.processarAcao(Acao.EMBARCAR);
        sessao.processarAcao(Acao.ESQUERDA);

        assertEquals(EstadoPartida.VITORIA, sessao.getEstado());
        assertEquals(1530, sessao.getPontuacao());

        sessao.processarAcao(Acao.EMBARCAR);
        assertEquals(1530, sessao.getPontuacao());
    }

    @Test
    void partidaRapidaMantemPontuacaoOriginal() {
        Nave nave = new Nave(0, 0, 3, 1);
        Missao missao = new Missao(nave);
        missao.adicionarPassageiro(new Professor("Passageiro", 1, 0));
        GameSession sessao = new GameSession("Teste", Dificuldade.FACIL, missao,
                100, MIN, MAX, new Random(1));

        sessao.processarAcao(Acao.DIREITA);
        sessao.processarAcao(Acao.EMBARCAR);
        sessao.processarAcao(Acao.ESQUERDA);

        assertEquals(EstadoPartida.VITORIA, sessao.getEstado());
        assertEquals(118, sessao.getPontuacao());
    }
}
