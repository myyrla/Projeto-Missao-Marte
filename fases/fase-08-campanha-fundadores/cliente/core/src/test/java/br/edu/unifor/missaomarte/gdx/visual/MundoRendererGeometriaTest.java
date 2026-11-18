package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MundoRendererGeometriaTest {
    @Test
    void objetosOcupamNoventaPorCentoDaCelula() {
        assertEquals(43.2f, MundoRenderer.tamanhoDoObjeto(48f), 0.001f);
        assertEquals(64.8f, MundoRenderer.tamanhoDoObjeto(72f), 0.001f);
    }
}
