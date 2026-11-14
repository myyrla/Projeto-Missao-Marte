package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.campanha.MecanicasDaPartida;
import br.edu.unifor.missaomarte.domain.EstacaoNami;
import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.ParadaDoRover;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class GameSessionCampanhaTest {
    private static final int MIN = -5;
    private static final int MAX = 5;

    @Test
    void movimentoAtivaNamiDepoisDeEntrarNaCasa() {
        Nave nave = new Nave(0, 0, 3, 5);
        nave.perderVida();
        Missao missao = new Missao(nave);
        missao.adicionarEstacaoNami(new EstacaoNami(1, 0));
        GameSession sessao = novaSessao(missao, MecanicasDaPartida.para(missao, false));

        passarContagem(sessao);
        sessao.pressionar(Acao.DIREITA);
        ResultadoTurno resultado = sessao.avancar(0.1f);
        sessao.soltar(Acao.DIREITA);

        assertEquals(3, nave.getVidas());
        assertTrue(resultado.getEventos().contains(EventoJogo.NAMI_ATIVADA));
    }

    @Test
    void movimentoUsaRoverUmaVezNoMesmoPasso() {
        Nave nave = new Nave(0, 0, 3, 5);
        Missao missao = new Missao(nave);
        missao.adicionarParadaDoRover(new ParadaDoRover("A", new Posicao(1, 0), new Posicao(4, 0)));
        missao.adicionarParadaDoRover(new ParadaDoRover("A", new Posicao(4, 0), new Posicao(1, 0)));
        GameSession sessao = novaSessao(missao, MecanicasDaPartida.para(missao, false));

        passarContagem(sessao);
        sessao.pressionar(Acao.DIREITA);
        ResultadoTurno resultado = sessao.avancar(0.1f);
        sessao.soltar(Acao.DIREITA);

        assertEquals(new Posicao(4, 0), nave.getPosicao());
        assertEquals(1, sessao.getMovimentos());
        assertTrue(resultado.getEventos().contains(EventoJogo.ROVER_UTILIZADO));
    }

    @Test
    void tempestadeContaSomenteTempoEfetivoENaoAvancaPausada() {
        Nave nave = new Nave(0, 0, 3, 5);
        Missao missao = new Missao(nave);
        missao.adicionarPassageiro(new Astronauta("Fundador", 5, 5));
        GameSession sessao = novaSessao(missao, MecanicasDaPartida.para(missao, true));

        avancar(sessao, 2.9f);
        assertTrue(sessao.estaVisivel(new Posicao(5, 5)));
        while (sessao.getContagemRestante() > 0f) {
            sessao.avancar(0.1f);
        }
        avancar(sessao, 8f);
        assertTrue(sessao.estaVisivel(new Posicao(5, 5)));
        avancar(sessao, 3f);
        assertFalse(sessao.estaVisivel(new Posicao(5, 5)));

        sessao.pausar();
        avancar(sessao, 20f);
        assertFalse(sessao.estaVisivel(new Posicao(5, 5)));
    }

    private static GameSession novaSessao(Missao missao, MecanicasDaPartida mecanicas) {
        return new GameSession("Piloto", br.edu.unifor.missaomarte.domain.Dificuldade.FACIL,
                missao, 100, MIN, MAX, new Random(1), ModoDeJogo.DINAMICO, mecanicas);
    }

    private static void passarContagem(GameSession sessao) {
        avancar(sessao, 3f);
    }

    private static void avancar(GameSession sessao, float segundos) {
        int passos = Math.round(segundos / 0.1f);
        for (int i = 0; i < passos; i++) {
            sessao.avancar(0.1f);
        }
    }
}
