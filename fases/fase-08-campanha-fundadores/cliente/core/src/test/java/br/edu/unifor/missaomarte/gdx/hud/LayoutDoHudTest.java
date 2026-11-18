package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import org.junit.jupiter.api.Test;

class LayoutDoHudTest {
    private static final int CELULAS = 11;

    @Test
    void reproduzAsMedidasDaReferenciaEm1280x720() {
        LayoutDoHud layout = LayoutDoHud.calcular(1280f, 720f, CELULAS, false, MargensSeguras.NENHUMA);

        assertEquals(1f, layout.escala(), 0.001f);
        assertRetangulo(layout.piloto(), 24f, 580f, 250f, 116f);
        assertRetangulo(layout.objetivo(), 360f, 588f, 560f, 108f);
        assertRetangulo(layout.fichaDoObjetivo(0), 376f, 600f, 170f, 52f);
        assertRetangulo(layout.fichaDoObjetivo(1), 554f, 600f, 170f, 52f);
        assertRetangulo(layout.fichaDoObjetivo(2), 732f, 600f, 170f, 52f);
        assertRetangulo(layout.radar(), 1036f, 546f, 220f, 150f);
        assertRetangulo(layout.mapa(), 395f, 80f, 490f, 490f);
        assertRetangulo(layout.painelDoFim(), 400f, 210f, 480f, 300f);
        assertRetangulo(layout.botaoDoFim(0), 450f, 240f, 180f, 50f);
        assertRetangulo(layout.botaoDoFim(1), 650f, 240f, 180f, 50f);
        assertTrue(layout.exibeRadar());
        assertTrue(layout.exibeAtalhos());
    }

    @Test
    void rodapeEsquerdoSomaTrintaEDoisDeMargemALarguraMedidaDoTexto() {
        LayoutDoHud layout = LayoutDoHud.calcular(1280f, 720f, CELULAS, false, MargensSeguras.NENHUMA);

        assertRetangulo(layout.rodapeEsquerdo(400f), 24f, 24f, 432f, 44f);
    }

    @Test
    void escalaAReferenciaParaOMundoDoViewportDe1280x720() {
        LayoutDoHud layout = LayoutDoHud.calcular(1066.6666f, 600f, CELULAS, false, MargensSeguras.NENHUMA);

        assertEquals(5f / 6f, layout.escala(), 0.001f);
        assertEquals(490f * 5f / 6f, layout.mapa().largura(), 0.001f);
        assertEquals(layout.mapa().largura() / CELULAS, layout.celula(), 0.001f);
        assertEquals(layout.mapa().x(), layout.origemX(), 0.001f);
        assertEquals(layout.mapa().y(), layout.origemY(), 0.001f);
    }

    @Test
    void noToqueEmRetratoEmpilhaCabecalhoMapaRodapeEControles() {
        LayoutDoHud layout = LayoutDoHud.calcular(800f, 1422f, CELULAS, true,
                new MargensSeguras(12f, 8f, 24f, 16f));
        RetanguloDeTela mundo = new RetanguloDeTela(0f, 0f, 800f, 1422f);

        assertTrue(layout.emRetrato());
        assertFalse(layout.exibeRadar());
        assertFalse(layout.exibeAtalhos());
        assertTrue(layout.piloto().estaDentroDe(mundo));
        assertTrue(layout.objetivo().estaDentroDe(mundo));
        assertTrue(layout.mapa().estaDentroDe(mundo));
        assertTrue(layout.rodapeEsquerdo().estaDentroDe(mundo));
        assertTrue(layout.controles().estaDentroDe(mundo));
        assertFalse(layout.piloto().sobrepoe(layout.objetivo()));
        assertFalse(layout.objetivo().sobrepoe(layout.mapa()));
        assertFalse(layout.mapa().sobrepoe(layout.rodapeEsquerdo()));
        assertFalse(layout.rodapeEsquerdo().sobrepoe(layout.controles()));
    }

    private static void assertRetangulo(RetanguloDeTela atual, float x, float y, float largura, float altura) {
        assertEquals(x, atual.x(), 0.001f);
        assertEquals(y, atual.y(), 0.001f);
        assertEquals(largura, atual.largura(), 0.001f);
        assertEquals(altura, atual.altura(), 0.001f);
    }
}
