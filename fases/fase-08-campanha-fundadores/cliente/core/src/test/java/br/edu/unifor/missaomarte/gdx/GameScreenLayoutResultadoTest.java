package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import org.junit.jupiter.api.Test;

class GameScreenLayoutResultadoTest {
    @Test
    void qualquerResultadoNoToqueUsaLayoutMedidoSemDependerDaOrientacao() {
        assertTrue(GameScreen.usaLayoutMedidoDoResultado(true, EstadoPartida.VITORIA));
        assertTrue(GameScreen.usaLayoutMedidoDoResultado(true, EstadoPartida.DERROTA));
        assertTrue(GameScreen.usaLayoutMedidoDoResultado(true, EstadoPartida.ABORTADA));
        assertFalse(GameScreen.usaLayoutMedidoDoResultado(true, EstadoPartida.EM_ANDAMENTO));
        assertFalse(GameScreen.usaLayoutMedidoDoResultado(false, EstadoPartida.VITORIA));
    }
}
