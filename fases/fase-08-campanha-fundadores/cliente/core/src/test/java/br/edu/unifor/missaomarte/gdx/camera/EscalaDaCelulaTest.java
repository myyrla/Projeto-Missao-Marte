package br.edu.unifor.missaomarte.gdx.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EscalaDaCelulaTest {
    @Test
    void respeitaEscalaMinimaIntermediariaEMaxima() {
        assertEquals(64f, EscalaDaCelula.calcular(320f, 320f, 11, 11), 0.001f);
        assertEquals(800f / 11f, EscalaDaCelula.calcular(800f, 600f, 11, 11), 0.001f);
        assertEquals(96f, EscalaDaCelula.calcular(1280f, 720f, 11, 11), 0.001f);
    }
}
