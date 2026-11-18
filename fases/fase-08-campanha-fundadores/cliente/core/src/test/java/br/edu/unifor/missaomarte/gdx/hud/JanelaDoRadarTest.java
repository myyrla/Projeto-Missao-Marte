package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import org.junit.jupiter.api.Test;

class JanelaDoRadarTest {
    @Test
    void projetaAIntersecaoDaCameraComOMapa() {
        RetanguloDeTela radar = new RetanguloDeTela(10f, 20f, 200f, 100f);

        RetanguloDeTela janela = JanelaDoRadar.projetar(radar, 1000f, 1000f, 250f, 500f, 500f, 400f);

        assertEquals(10f, janela.x(), 0.001f);
        assertEquals(50f, janela.y(), 0.001f);
        assertEquals(100f, janela.largura(), 0.001f);
        assertEquals(40f, janela.altura(), 0.001f);
    }

    @Test
    void limitaJanelaAoMapaQuandoACameraMostraTerrenoExterno() {
        RetanguloDeTela radar = new RetanguloDeTela(0f, 0f, 200f, 100f);

        RetanguloDeTela janela = JanelaDoRadar.projetar(radar, 1000f, 1000f, 100f, 100f, 600f, 600f);

        assertEquals(0f, janela.x(), 0.001f);
        assertEquals(0f, janela.y(), 0.001f);
        assertEquals(80f, janela.largura(), 0.001f);
        assertEquals(40f, janela.altura(), 0.001f);
    }
}
