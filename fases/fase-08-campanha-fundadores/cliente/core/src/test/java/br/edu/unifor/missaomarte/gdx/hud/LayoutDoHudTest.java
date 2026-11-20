package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.List;
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
        assertFalse(layout.checkDoFundador(0).sobrepoe(layout.textoDaFichaDoObjetivo(0)));
        assertRetangulo(layout.radar(), 1036f, 546f, 220f, 150f);
        assertRetangulo(layout.seloDaTempestade(), 520f, 546f, 240f, 34f);
        assertFalse(layout.seloDaTempestade().sobrepoe(layout.objetivo()));
        assertFalse(layout.seloDaTempestade().sobrepoe(layout.aviso()));
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

    @Test
    void usaColunasLivresSemCobrirMapaEm1280x720E1920x1080() {
        LayoutDoHud hd = calcular(1280f, 720f, new RetanguloDeTela(280f, 0f, 720f, 720f), false);
        LayoutDoHud fullHd = calcular(1920f, 1080f, new RetanguloDeTela(564f, 144f, 792f, 792f), false);

        assertEquals(220f, LayoutDoHud.LARGURA_MINIMA_DA_COLUNA, 0.001f);
        assertEquals(244f, hd.larguraDaColuna(), 0.001f);
        assertEquals(510f, fullHd.larguraDaColuna(), 0.001f);
        assertFalse(hd.usaReserva());
        assertFalse(fullHd.usaReserva());
        assertSemSobreposicaoComMapa(hd);
        assertSemSobreposicaoComMapa(fullHd);
        assertTrue(hd.textosCabemSemTruncar());
        assertTrue(fullHd.textosCabemSemTruncar());
        assertTrue(hd.fichasDoObjetivoSaoVerticais());
    }

    @Test
    void usaFaixasLivresSemCobrirMapaNemControlesEm540x1200() {
        LayoutDoHud layout = calcular(540f, 1200f, new RetanguloDeTela(0f, 204f, 540f, 792f), true);

        assertFalse(layout.usaReserva());
        assertSemSobreposicaoComMapa(layout);
        assertTrue(layout.textosCabemSemTruncar());
        for (RetanguloDeTela painel : layout.areasDoHud(true, true)) {
            if (painel != layout.controles()) {
                assertFalse(painel.sobrepoe(layout.controles()));
            }
        }
    }

    @Test
    void acionaReservaQuandoAsColunasSaoEstreitasDemais() {
        LayoutDoHud layout = calcular(1000f, 720f, new RetanguloDeTela(180f, 0f, 640f, 720f), false);

        assertTrue(layout.usaReserva());
    }

    @Test
    void posicionaOsChipsAbaixoDoTextoDoObjetivoComUmaEDuasLinhas() {
        RetanguloDeTela mapa = new RetanguloDeTela(280f, 0f, 720f, 720f);
        LayoutDoHud umaLinha = calcular(1280f, 720f, mapa, false,
                new MedidasDoHud(14f, 12f, 28f, 5, 18f));
        LayoutDoHud duasLinhas = calcular(1280f, 720f, mapa, false,
                new MedidasDoHud(14f, 24f, 28f, 5, 18f));

        assertFalse(umaLinha.textoDoObjetivo().sobrepoe(umaLinha.fichaDoObjetivo(0)));
        assertFalse(duasLinhas.textoDoObjetivo().sobrepoe(duasLinhas.fichaDoObjetivo(0)));
        assertTrue(umaLinha.fichaDoObjetivo(0).topo() < umaLinha.textoDoObjetivo().y());
        assertTrue(duasLinhas.fichaDoObjetivo(0).topo() < duasLinhas.textoDoObjetivo().y());
        assertEquals(12f, duasLinhas.objetivo().altura() - umaLinha.objetivo().altura(), 0.001f);
    }

    @Test
    void ajustaAAlturaDosCartoesAoConteudoEUsaOMesmoEspacamento() {
        RetanguloDeTela mapa = new RetanguloDeTela(280f, 0f, 720f, 720f);
        LayoutDoHud curto = calcular(1280f, 720f, mapa, false,
                new MedidasDoHud(14f, 12f, 28f, 4, 18f));
        LayoutDoHud longo = calcular(1280f, 720f, mapa, false,
                new MedidasDoHud(14f, 12f, 70f, 5, 36f));

        assertEquals(42f, longo.piloto().altura() - curto.piloto().altura(), 0.001f);
        assertTrue(longo.rodapeDireito().altura() > curto.rodapeDireito().altura());
        assertEquals(curto.piloto().y() - curto.objetivo().topo(),
                curto.radar().y() - curto.rodapeDireito().topo(), 0.001f);
        assertTrue(curto.rodapeEsquerdo().estaDentroDe(curto.piloto()));
    }

    @Test
    void preservaORetanguloDoMapaHorizontalAntesEDepoisDoLayoutMedido() {
        RetanguloDeTela mapaAtual = new RetanguloDeTela(280f, 0f, 720f, 720f);
        LayoutDoHud antes = calcular(1280f, 720f, mapaAtual, false);
        LayoutDoHud depois = calcular(1280f, 720f, mapaAtual, false,
                new MedidasDoHud(14f, 24f, 56f, 5, 28f));

        assertRetangulo(depois.mapa(), antes.mapa().x(), antes.mapa().y(),
                antes.mapa().largura(), antes.mapa().altura());
    }

    @Test
    void modoToqueExpoeAreaUtilEntreFaixaSuperiorEControles() {
        LayoutDoHud layout = calcular(540f, 1200f,
                new RetanguloDeTela(0f, 204f, 540f, 792f), true,
                new MedidasDoHud(10f, 10f, 24f, 0, 16f));

        assertEquals(layout.controles().topo(), layout.areaUtilDoMapa().y(), 0.001f);
        assertEquals(layout.objetivo().y(), layout.areaUtilDoMapa().topo(), 0.001f);
        assertFalse(layout.areaUtilDoMapa().sobrepoe(layout.controles()));
        assertFalse(layout.areaUtilDoMapa().sobrepoe(layout.objetivo()));
    }

    @Test
    void modoToqueUsaFaixasForaDaAreaUtilELevaOAvisoParaOTopoDoMapa() {
        LayoutDoHud layout = calcular(540f, 1200f,
                new RetanguloDeTela(0f, 204f, 540f, 792f), true,
                new MedidasDoHud(10f, 10f, 24f, 0, 22f, true, true));
        RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, 540f, 1200f);

        assertTrue(layout.faixaSuperior().estaDentroDe(tela));
        assertTrue(layout.faixaInferior().estaDentroDe(tela));
        assertEquals(layout.areaUtilDoMapa().topo(), layout.faixaSuperior().y(), 0.001f);
        assertEquals(layout.areaUtilDoMapa().y(), layout.faixaInferior().topo(), 0.001f);
        assertTrue(layout.aviso().estaDentroDe(layout.areaUtilDoMapa()));
        assertEquals(layout.areaUtilDoMapa().topo() - 8f * layout.escala(),
                layout.aviso().topo(), 0.001f);
        assertFalse(layout.aviso().sobrepoe(layout.objetivo()));
        assertFalse(layout.aviso().sobrepoe(layout.controles()));
    }

    private static LayoutDoHud calcular(float largura, float altura, RetanguloDeTela mapa, boolean toque) {
        return LayoutDoHud.calcular(largura, altura, CELULAS, toque, MargensSeguras.NENHUMA, mapa);
    }

    private static LayoutDoHud calcular(float largura, float altura, RetanguloDeTela mapa, boolean toque,
            MedidasDoHud medidas) {
        return LayoutDoHud.calcular(largura, altura, CELULAS, toque, MargensSeguras.NENHUMA, mapa, medidas);
    }

    private static void assertSemSobreposicaoComMapa(LayoutDoHud layout) {
        List<RetanguloDeTela> areas = layout.areasDoHud(true, true);
        for (RetanguloDeTela painel : areas) {
            assertFalse(painel.sobrepoe(layout.mapa()), painel + " cobre o mapa");
        }
    }

    private static void assertRetangulo(RetanguloDeTela atual, float x, float y, float largura, float altura) {
        assertEquals(x, atual.x(), 0.001f);
        assertEquals(y, atual.y(), 0.001f);
        assertEquals(largura, atual.largura(), 0.001f);
        assertEquals(altura, atual.altura(), 0.001f);
    }
}
