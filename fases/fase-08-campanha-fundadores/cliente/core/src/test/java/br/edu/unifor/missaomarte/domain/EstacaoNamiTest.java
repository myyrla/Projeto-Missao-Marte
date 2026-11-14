package br.edu.unifor.missaomarte.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EstacaoNamiTest {
    @Test
    void recuperaUmaVidaESoPodeSerUsadaUmaVez() {
        Nave nave = new Nave(1, 1, 3, 5);
        nave.perderVida();
        nave.perderVida();
        EstacaoNami nami = new EstacaoNami(1, 1);

        assertTrue(nami.ativar(nave));
        assertEquals(2, nave.getVidas());
        assertTrue(nami.isUsada());
        assertFalse(nami.ativar(nave));
        assertEquals(2, nave.getVidas());
    }

    @Test
    void naoUltrapassaOMaximoENaoConsomeAEstacaoSemCurar() {
        Nave nave = new Nave(1, 1, 3, 5);
        EstacaoNami nami = new EstacaoNami(1, 1);

        assertFalse(nami.ativar(nave));
        assertEquals(3, nave.getVidas());
        assertFalse(nami.isUsada());
    }

    @Test
    void naoEPerigoNemContaComoColisao() {
        Nave nave = new Nave(1, 1, 3, 5);
        Missao missao = new Missao(nave);
        missao.adicionarEstacaoNami(new EstacaoNami(1, 1));

        assertFalse(missao.houveColisaoComPerigo());
        assertEquals(1, missao.getEstacoesNami().size());
    }
}
