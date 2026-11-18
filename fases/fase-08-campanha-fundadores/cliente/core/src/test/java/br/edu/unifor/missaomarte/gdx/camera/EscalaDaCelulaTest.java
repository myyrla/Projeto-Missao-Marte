package br.edu.unifor.missaomarte.gdx.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EscalaDaCelulaTest {
    @Test
    void usaSomenteAAlturaTotalDaJanelaELimitaEntreQuarentaEOitoESetentaEDois() {
        assertEquals(48f, EscalaDaCelula.calcular(320f, 11), 0.001f);
        assertEquals(600f / 11f, EscalaDaCelula.calcular(600f, 11), 0.001f);
        assertEquals(720f / 11f, EscalaDaCelula.calcular(720f, 11), 0.001f);
        assertEquals(72f, EscalaDaCelula.calcular(1200f, 11), 0.001f);
    }
}
