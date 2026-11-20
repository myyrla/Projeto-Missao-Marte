package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.EstacaoNami;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Professor;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class CenaVisivelDaTempestadeTest {
    @Test
    void tempestadeOcultaTodasAsPecasForaDoRaioEMantemABase() {
        Missao missao = missaoComObjetosDentroEForaDoRaio();
        Predicate<Posicao> dentroDoRaio = posicao -> Math.max(
                Math.abs(posicao.getX() - 4), Math.abs(posicao.getY() - 4)) <= 2;

        CenaVisivelDaTempestade cena = CenaVisivelDaTempestade.projetar(missao, true, dentroDoRaio);

        assertEquals(missao.getBase(), cena.base());
        assertEquals(new Posicao(0, 0), cena.base().getPosicao());
        assertEquals(1, cena.passageiros().size());
        assertEquals(new Posicao(3, 4), cena.passageiros().get(0).getPosicao());
        assertEquals(1, cena.asteroides().size());
        assertEquals(new Posicao(4, 2), cena.asteroides().get(0).getPosicao());
        assertEquals(1, cena.inimigos().size());
        assertEquals(new Posicao(5, 5), cena.inimigos().get(0).getPosicao());
        assertEquals(1, cena.estacoesNami().size());
        assertEquals(new Posicao(6, 4), cena.estacoesNami().get(0).getPosicao());
    }

    @Test
    void semTempestadeNenhumaPecaEFiltrada() {
        Missao missao = missaoComObjetosDentroEForaDoRaio();

        CenaVisivelDaTempestade cena = CenaVisivelDaTempestade.projetar(
                missao, false, posicao -> false);

        assertEquals(missao.getPassageiros(), cena.passageiros());
        assertEquals(missao.getAsteroides(), cena.asteroides());
        assertEquals(missao.getInimigos(), cena.inimigos());
        assertEquals(missao.getEstacoesNami(), cena.estacoesNami());
        assertTrue(cena.nave() == missao.getNave());
    }

    private static Missao missaoComObjetosDentroEForaDoRaio() {
        Missao missao = new Missao(new Nave(4, 4, 3, 5));
        missao.adicionarPassageiro(new Professor("visível", 3, 4));
        missao.adicionarPassageiro(new Professor("oculto", -3, -3));
        missao.adicionarAsteroide(new Asteroide(4, 2));
        missao.adicionarAsteroide(new Asteroide(-4, 4));
        missao.adicionarInimigo(new Inimigo(5, 5));
        missao.adicionarInimigo(new Inimigo(0, 5));
        missao.adicionarEstacaoNami(new EstacaoNami(6, 4));
        missao.adicionarEstacaoNami(new EstacaoNami(-5, -5));
        return missao;
    }
}
