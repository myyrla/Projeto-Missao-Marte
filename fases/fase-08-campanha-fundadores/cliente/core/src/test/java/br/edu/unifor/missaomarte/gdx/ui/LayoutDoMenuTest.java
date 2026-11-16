package br.edu.unifor.missaomarte.gdx.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import org.junit.jupiter.api.Test;

class LayoutDoMenuTest {
    @Test
    void em1280x720ReproduzOsRetangulosDasReferencias() throws Exception {
        LayoutDoMenu layout = LayoutDoMenu.calcular(1280f, 720f, 48f);

        assertFalse(layout.emRetrato());
        assertRetangulo(96f, 294f, 320f, 50f, layout.botaoMenu(0));
        assertRetangulo(96f, 120f, 320f, 50f, layout.botaoMenu(3));
        assertRetangulo(340f, 160f, 600f, 440f, layout.painelOpcoes());
        assertRetangulo(380f, 450f, 520f, 56f, layout.itemOpcoes(0));
        assertRetangulo(380f, 194f, 520f, 56f, layout.itemOpcoes(4));
    }

    @Test
    void emRetratoBotoesEItensFicamDentroDaTelaESemSobreposicao() throws Exception {
        LayoutDoMenu layout = LayoutDoMenu.calcular(540f, 1200f, 48f);
        RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, 540f, 1200f);

        assertTrue(layout.emRetrato());
        validarSequencia(layout, true, 4, tela);
        RetanguloDeTela painel = layout.painelOpcoes();
        assertTrue(painel.estaDentroDe(tela));
        validarSequencia(layout, false, 5, painel);
    }

    @Test
    void emRetratoPainelCresceParaAlvosDeToqueAmpliados() {
        LayoutDoMenu layout = LayoutDoMenu.calcular(540f, 1200f, 72f);
        RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, 540f, 1200f);

        assertTrue(layout.painelOpcoes().estaDentroDe(tela));
        validarSequencia(layout, false, 5, layout.painelOpcoes());
    }

    private static void validarSequencia(LayoutDoMenu layout, boolean menu, int quantidade, RetanguloDeTela limite) {
        RetanguloDeTela anterior = null;
        for (int i = 0; i < quantidade; i++) {
            RetanguloDeTela atual = menu ? layout.botaoMenu(i) : layout.itemOpcoes(i);
            assertTrue(atual.estaDentroDe(limite), "item fora da area: " + i);
            if (anterior != null) {
                assertFalse(anterior.sobrepoe(atual), "item sobreposto: " + i);
            }
            anterior = atual;
        }
    }

    private static void assertRetangulo(float x, float y, float largura, float altura, RetanguloDeTela atual) {
        assertEquals(x, atual.x(), 0.001f);
        assertEquals(y, atual.y(), 0.001f);
        assertEquals(largura, atual.largura(), 0.001f);
        assertEquals(altura, atual.altura(), 0.001f);
    }
}
