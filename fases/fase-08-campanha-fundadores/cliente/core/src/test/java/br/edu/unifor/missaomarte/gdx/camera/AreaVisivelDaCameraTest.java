package br.edu.unifor.missaomarte.gdx.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import org.junit.jupiter.api.Test;

class AreaVisivelDaCameraTest {
    @Test
    void converteAFaixaLivreDoLayoutParaPixelsDaJanela() {
        RetanguloDeTela area = AreaVisivelDaCamera.converter(
                new RetanguloDeTela(20f, 400f, 760f, 800f),
                800f, 1600f,
                0, 0, 540, 1200);

        assertEquals(13.5f, area.x(), 0.001f);
        assertEquals(300f, area.y(), 0.001f);
        assertEquals(513f, area.largura(), 0.001f);
        assertEquals(600f, area.altura(), 0.001f);
        assertEquals(270f, area.centroX(), 0.001f);
        assertEquals(600f, area.centroY(), 0.001f);
    }

    @Test
    void consideraDeslocamentoDaViewportNaJanela() {
        RetanguloDeTela area = AreaVisivelDaCamera.converter(
                new RetanguloDeTela(100f, 100f, 200f, 300f),
                800f, 600f,
                12, 18, 1600, 1200);

        assertEquals(212f, area.x(), 0.001f);
        assertEquals(218f, area.y(), 0.001f);
        assertEquals(400f, area.largura(), 0.001f);
        assertEquals(600f, area.altura(), 0.001f);
    }
}
