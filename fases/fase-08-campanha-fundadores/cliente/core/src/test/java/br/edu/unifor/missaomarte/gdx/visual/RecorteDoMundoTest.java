package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import org.junit.jupiter.api.Test;

class RecorteDoMundoTest {
    @Test
    void converteAAreaUtilParaOBackbufferEm540x1200() {
        RecorteDoMundo recorte = RecorteDoMundo.calcular(
                new RetanguloDeTela(0f, 240f, 540f, 720f),
                540f, 1200f, 0, 0, 540, 1200, 540, 1200, 1080, 2400);

        assertEquals(0, recorte.x());
        assertEquals(480, recorte.y());
        assertEquals(1080, recorte.largura());
        assertEquals(1440, recorte.altura());
        assertTrue(recorte.contem(540, 1200));
        assertFalse(recorte.contem(540, 200));
        assertFalse(recorte.contem(540, 2200));
    }

    @Test
    void converteAAreaUtilParaOBackbufferEm540x895() {
        RecorteDoMundo recorte = RecorteDoMundo.calcular(
                new RetanguloDeTela(0f, 195f, 540f, 500f),
                540f, 895f, 0, 0, 540, 895, 540, 895, 540, 895);

        assertEquals(0, recorte.x());
        assertEquals(195, recorte.y());
        assertEquals(540, recorte.largura());
        assertEquals(500, recorte.altura());
        assertTrue(recorte.contem(270, 400));
        assertFalse(recorte.contem(270, 100));
        assertFalse(recorte.contem(270, 800));
    }
}
