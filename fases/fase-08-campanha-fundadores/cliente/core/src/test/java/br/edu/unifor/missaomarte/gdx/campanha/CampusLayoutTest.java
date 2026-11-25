package br.edu.unifor.missaomarte.gdx.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.List;
import org.junit.jupiter.api.Test;

class CampusLayoutTest {
    @Test
    void mapaMantemAProporcaoDasCamadasDoCampus() {
        CampusLayout layout = CampusLayout.padrao();
        RetanguloDeTela mapa = layout.mapa();

        assertEquals(1056f / 864f, mapa.largura() / mapa.altura(), 0.001f);
        assertEquals(284f, mapa.centroX(), 0.001f);
        assertEquals(259f, mapa.centroY(), 0.001f);
    }

    @Test
    void layoutVerticalNaoSobrepoeBlocosEm540x1200() {
        validarLayoutVertical(CampusLayout.calcular(540f, 1200f, true), 540f, 1200f);
    }

    @Test
    void layoutVerticalNaoSobrepoeBlocosEm540x895() {
        validarLayoutVertical(CampusLayout.calcular(540f, 895f, true), 540f, 895f);
    }

    @Test
    void layoutVerticalComTextoUmPontoSeisMantemConteudoDentroDosPaineis() {
        MedidasDoCampus.MedidasDaEtapa etapa = new MedidasDoCampus.MedidasDaEtapa(
                21f * 1.6f, 17f * 1.6f, 17f * 1.6f);
        MedidasDoCampus medidas = new MedidasDoCampus(
                29f * 1.6f, 21f * 1.6f, 17f * 1.6f,
                true, true, List.of(etapa, etapa, etapa), 82f * 1.6f);

        float larguraDoMundo = 800f;
        validarCampusComTextoAmpliado(CampusLayout.calcular(
                larguraDoMundo, larguraDoMundo * 1200f / 540f, true, medidas), medidas);
        validarCampusComTextoAmpliado(CampusLayout.calcular(
                larguraDoMundo, larguraDoMundo * 895f / 540f, true, medidas), medidas);
    }

    @Test
    void pontuacaoVerticalFicaEmLinhaPropriaAlinhadaAEsquerda() {
        CampusLayout layout = CampusLayout.calcular(800f, 1400f, true,
                medidasVerticais(1.6f, 107.2f));

        assertEquals(layout.linhaDosFundadores().x(), layout.linhaDaPontuacao().x(), 0.001f);
        assertEquals(layout.linhaDosFundadores().largura(),
                layout.linhaDaPontuacao().largura(), 0.001f);
        assertTrue(layout.linhaDaPontuacao().topo() < layout.linhaDosFundadores().y());
        assertTrue(layout.linhaDaDemonstracao().topo() < layout.linhaDaPontuacao().y());
    }

    @Test
    void layoutHorizontalPreservaOMapaAtual() {
        CampusLayout layout = CampusLayout.calcular(800f, 600f, false);

        assertEquals(1056f / 864f, layout.mapa().largura() / layout.mapa().altura(), 0.001f);
        assertEquals(400f, layout.mapa().altura(), 0.001f);
    }

    @Test
    void conjuntoHorizontalFicaCentralizadoESeparaTituloDoMapa() {
        CampusLayout layout = CampusLayout.calcular(800f, 600f, false);
        float base = Math.min(layout.mapa().y(), layout.progresso().y());
        float topo = Math.max(layout.titulo().topo(), layout.progresso().topo());

        assertEquals(base, 600f - topo, 0.001f);
        assertTrue(layout.titulo().y() - layout.mapa().topo() >= 12f);
        assertFalse(layout.mapa().sobrepoe(layout.progresso()));
    }

    @Test
    void painelHorizontalSeparaCabecalhoEtapasETextosSemTruncar() {
        MedidasDoCampus medidas = medidasHorizontais(true, true, 104f);
        CampusLayout layout = CampusLayout.calcular(800f, 600f, false, medidas);

        assertFalse(layout.linhaDosFundadores().sobrepoe(layout.linhaDaDemonstracao()));
        assertFalse(layout.linhaDaPontuacao().sobrepoe(layout.linhaDaDemonstracao()));
        for (int i = 0; i < 3; i++) {
            assertFalse(layout.cenarioDaEtapa(i).sobrepoe(layout.fundadorDaEtapa(i)));
            assertTrue(layout.tituloDaEtapa(i).estaDentroDe(layout.progresso()));
            assertTrue(layout.cenarioDaEtapa(i).estaDentroDe(layout.progresso()));
            assertTrue(layout.fundadorDaEtapa(i).estaDentroDe(layout.progresso()));
            assertEquals(layout.cenarioDaEtapa(i).largura(), layout.fundadorDaEtapa(i).largura(), 0.001f);
        }
        assertTrue(layout.cartaoDaProximaMissao().estaDentroDe(layout.progresso()));
        assertFalse(layout.cartaoDaProximaMissao().sobrepoe(layout.botaoPrincipal()));
    }

    @Test
    void painelHorizontalMantemSeparacaoEm800x600E1280x720() {
        validarProjecaoHorizontal(800f, 600f);
        validarProjecaoHorizontal(1280f, 720f);
    }

    @Test
    void cartaoDaProximaMissaoTambemTemAlturaMedidaNoHorizontal() {
        CampusLayout layout = CampusLayout.calcular(800f, 600f, false,
                medidasHorizontais(true, false, 82f));

        assertEquals(82f, layout.cartaoDaProximaMissao().altura(), 0.001f);
        assertTrue(layout.cartaoDaProximaMissao().largura() > 0f);
        assertFalse(layout.cartaoDaProximaMissao().sobrepoe(layout.etapa(2)));
    }

    @Test
    void etapasVerticaisFicamEmpilhadasComEspacamentoConstante() {
        CampusLayout layout = CampusLayout.calcular(540f, 895f, true);

        assertEquals(6f, layout.etapa(0).y() - layout.etapa(1).topo(), 0.001f);
        assertEquals(6f, layout.etapa(1).y() - layout.etapa(2).topo(), 0.001f);
        assertEquals(layout.etapa(0).altura(), layout.etapa(1).altura(), 0.001f);
        assertEquals(layout.etapa(1).altura(), layout.etapa(2).altura(), 0.001f);
    }

    @Test
    void cartaoDaProximaMissaoVerticalUsaAAlturaMedidaDoConteudo() {
        CampusLayout layout = CampusLayout.calcular(540f, 895f, true,
                MedidasDoCampus.padrao());

        assertTrue(layout.cartaoDaProximaMissao().estaDentroDe(layout.progresso()));
        assertFalse(layout.cartaoDaProximaMissao().sobrepoe(layout.etapa(2)));
        assertEquals(MedidasDoCampus.padrao().alturaDoCartao(),
                layout.cartaoDaProximaMissao().altura(), 0.001f);
        assertEquals(10f, layout.etapa(2).y() - layout.cartaoDaProximaMissao().topo(), 0.001f);
        assertTrue(layout.cartaoDaProximaMissao().y() > layout.progresso().y());
    }

    @Test
    void calculoVerticalPadraoNaoEsticaOCartaoAteORodape() {
        CampusLayout layout = CampusLayout.calcular(540f, 1200f, true);

        assertEquals(MedidasDoCampus.padrao().alturaDoCartao(),
                layout.cartaoDaProximaMissao().altura(), 0.001f);
        assertTrue(layout.cartaoDaProximaMissao().y() > layout.progresso().y());
    }

    @Test
    void cartaoConcluidoUsaAAlturaMedidaNoVertical() {
        CampusLayout layout = CampusLayout.calcular(540f, 895f, true, 64f);
        CampusLayout alto = CampusLayout.calcular(540f, 1200f, true, 64f);

        assertEquals(64f, layout.cartaoDaProximaMissao().altura(), 0.001f);
        assertEquals(64f, alto.cartaoDaProximaMissao().altura(), 0.001f);
        assertEquals(10f, layout.etapa(2).y() - layout.cartaoDaProximaMissao().topo(), 0.001f);
        assertTrue(layout.cartaoDaProximaMissao().estaDentroDe(layout.progresso()));
        assertTrue(alto.cartaoDaProximaMissao().estaDentroDe(alto.progresso()));
    }

    @Test
    void cartaoConcluidoUsaAAlturaMedidaNoHorizontal() {
        CampusLayout layout = CampusLayout.calcular(800f, 600f, false, 180f);

        assertEquals(180f, layout.cartaoDaProximaMissao().altura(), 0.001f);
        assertTrue(layout.cartaoDaProximaMissao().estaDentroDe(layout.progresso()));
        assertFalse(layout.cartaoDaProximaMissao().sobrepoe(layout.botaoPrincipal()));
        assertFalse(layout.cartaoDaProximaMissao().sobrepoe(layout.etapa(2)));
    }

    @Test
    void toqueHorizontalAmpliaOPainelEReservaOCartaoAcimaDosBotoes() {
        for (float alvoMinimo : new float[] {36f, 24f}) {
            for (float fator : new float[] {1f, 1.3f, 1.6f}) {
                MedidasDoCampus.MedidasDaEtapa etapa = new MedidasDoCampus.MedidasDaEtapa(
                        21f * fator, 17f * fator, 17f * fator);
                MedidasDoCampus conteudo = new MedidasDoCampus(
                        29f * fator, 21f * fator, 17f * fator,
                        true, true, List.of(etapa, etapa, etapa), 82f * fator);
                MedidasDoCampusHorizontalToque.LargurasDaEtapa largurasDaEtapa =
                        new MedidasDoCampusHorizontalToque.LargurasDaEtapa(
                                112f * fator, 78f * fator);
                MedidasDoCampusHorizontalToque medidas = new MedidasDoCampusHorizontalToque(
                        conteudo, List.of(largurasDaEtapa, largurasDaEtapa, largurasDaEtapa),
                        300f * fator, 34f * fator, 17f * fator,
                        29f * fator + 20f, alvoMinimo);

                CampusLayout layout = CampusLayout.calcularHorizontalToque(960f, 600f, medidas);
                RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, 960f, 600f);

                assertTrue(layout.progresso().estaDentroDe(tela));
                assertTrue(layout.progresso().largura() + 0.001f
                        >= Math.min(960f * 0.48f, medidas.larguraMinimaDoPainel()));
                assertTrue(layout.titulo().estaDentroDe(tela));
                assertFalse(layout.titulo().sobrepoe(layout.progresso()));
                assertFalse(layout.titulo().sobrepoe(layout.mapa()));
                assertFalse(layout.mapa().sobrepoe(layout.progresso()));
                assertEquals(1056f / 864f,
                        layout.mapa().largura() / layout.mapa().altura(), 0.001f);
                assertTrue(layout.linhaDosFundadores().estaDentroDe(layout.progresso()));
                for (int i = 0; i < 3; i++) {
                    assertTrue(layout.cenarioDaEtapa(i).largura() + 0.001f
                            >= largurasDaEtapa.cenario());
                    assertTrue(layout.fundadorDaEtapa(i).largura() + 0.001f
                            >= largurasDaEtapa.fundador());
                    assertFalse(layout.cenarioDaEtapa(i).sobrepoe(layout.fundadorDaEtapa(i)));
                }
                assertTrue(layout.cartaoDaProximaMissao().estaDentroDe(layout.progresso()));
                assertFalse(layout.cartaoDaProximaMissao().sobrepoe(layout.botaoPrincipal()));
                assertFalse(layout.cartaoDaProximaMissao().sobrepoe(layout.botaoVoltar()));
                assertTrue(layout.cartaoDaProximaMissao().y()
                        >= layout.botaoPrincipal().topo() + 8f - 0.001f);
                assertTrue(layout.botaoPrincipal().estaDentroDe(layout.progresso()));
                assertTrue(layout.botaoVoltar().estaDentroDe(layout.progresso()));
            }
        }
    }

    private static void validarLayoutVertical(CampusLayout layout, float largura, float altura) {
        RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, largura, altura);

        assertTrue(layout.vertical());
        assertTrue(layout.titulo().estaDentroDe(tela));
        assertTrue(layout.mapa().estaDentroDe(tela));
        assertTrue(layout.progresso().estaDentroDe(tela));
        assertTrue(layout.rodape().estaDentroDe(tela));
        assertTrue(layout.botaoPrincipal().estaDentroDe(layout.rodape()));
        assertTrue(layout.botaoVoltar().estaDentroDe(layout.rodape()));
        assertTrue(layout.botaoPrincipal().altura() >= 48f);
        assertTrue(layout.botaoVoltar().altura() >= 48f);
        assertEquals(1056f / 864f, layout.mapa().largura() / layout.mapa().altura(), 0.001f);
        assertEquals(largura, layout.mapa().largura(), 0.001f);
        assertFalse(layout.titulo().sobrepoe(layout.mapa()));
        assertFalse(layout.mapa().sobrepoe(layout.progresso()));
        assertFalse(layout.progresso().sobrepoe(layout.rodape()));
        assertFalse(layout.botaoPrincipal().sobrepoe(layout.botaoVoltar()));
        for (int i = 0; i < 3; i++) {
            assertTrue(layout.etapa(i).estaDentroDe(layout.progresso()));
        }
        assertTrue(layout.cartaoDaProximaMissao().estaDentroDe(layout.progresso()));
        assertEquals(altura, layout.titulo().topo(), 0.001f);
        assertEquals(0f, layout.rodape().y(), 0.001f);
        assertTrue(layout.titulo().y() - layout.mapa().topo() <= 12f);
        assertTrue(layout.mapa().y() - layout.progresso().topo() <= 12f);
        assertTrue(layout.progresso().y() - layout.rodape().topo() <= 12f);
    }

    private static void validarCampusComTextoAmpliado(CampusLayout layout, MedidasDoCampus medidas) {
        validarLayoutVertical(layout, layout.largura(), layout.altura());
        assertTrue(layout.linhaDosFundadores().largura() > 0f);
        assertTrue(layout.linhaDaPontuacao().largura() > 0f);
        assertTrue(layout.linhaDaDemonstracao().largura() > 0f);
        assertEquals(layout.linhaDosFundadores().x(), layout.linhaDaPontuacao().x(), 0.001f);
        assertEquals(layout.linhaDosFundadores().largura(), layout.linhaDaPontuacao().largura(), 0.001f);
        assertTrue(layout.linhaDaPontuacao().topo() < layout.linhaDosFundadores().y());
        assertTrue(layout.linhaDaDemonstracao().topo() < layout.linhaDaPontuacao().y());
        assertTrue(layout.linhaDosFundadores().estaDentroDe(layout.progresso()));
        assertTrue(layout.linhaDaPontuacao().estaDentroDe(layout.progresso()));
        assertTrue(layout.linhaDaDemonstracao().estaDentroDe(layout.progresso()));
        for (int i = 0; i < 3; i++) {
            MedidasDoCampus.MedidasDaEtapa etapa = medidas.etapas().get(i);
            float alturaDoTexto = etapa.alturaDoTitulo()
                    + Math.max(etapa.alturaDoCenario(), etapa.alturaDoFundador());
            assertTrue(layout.etapa(i).altura() > alturaDoTexto,
                    "texto ampliado não cabe na etapa " + (i + 1));
            assertTrue(layout.tituloDaEtapa(i).y() > layout.cenarioDaEtapa(i).topo(),
                    "não há respiro entre as linhas da etapa " + (i + 1));
            if (i + 1 < 3) {
                assertFalse(layout.etapa(i).sobrepoe(layout.etapa(i + 1)));
            }
        }
        assertFalse(layout.etapa(2).sobrepoe(layout.cartaoDaProximaMissao()));
        assertTrue(layout.cartaoDaProximaMissao().estaDentroDe(layout.progresso()));
        assertEquals(medidas.alturaDoCartao(), layout.cartaoDaProximaMissao().altura(), 0.001f);
    }

    private static MedidasDoCampus medidasVerticais(float fator, float alturaDoCartao) {
        MedidasDoCampus.MedidasDaEtapa etapa = new MedidasDoCampus.MedidasDaEtapa(
                21f * fator, 17f * fator, 0f);
        return new MedidasDoCampus(29f * fator, 21f * fator, 17f * fator,
                true, true, List.of(etapa, etapa, etapa), alturaDoCartao);
    }

    private static MedidasDoCampus medidasHorizontais(boolean pontuacao, boolean demonstracao,
            float alturaDoCartao) {
        MedidasDoCampus.MedidasDaEtapa etapa = new MedidasDoCampus.MedidasDaEtapa(21f, 17f, 17f);
        return new MedidasDoCampus(29f, 21f, 17f, pontuacao, demonstracao,
                List.of(etapa, etapa, etapa), alturaDoCartao);
    }

    private static void validarProjecaoHorizontal(float larguraDaJanela, float alturaDaJanela) {
        CampusLayout layout = CampusLayout.calcular(800f, 600f, false,
                medidasHorizontais(true, true, 104f));
        float escala = Math.min(larguraDaJanela / 800f, alturaDaJanela / 600f);
        float deslocamentoX = (larguraDaJanela - 800f * escala) / 2f;
        float deslocamentoY = (alturaDaJanela - 600f * escala) / 2f;
        RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, larguraDaJanela, alturaDaJanela);
        RetanguloDeTela painel = projetar(layout.progresso(), escala, deslocamentoX, deslocamentoY);

        assertTrue(painel.estaDentroDe(tela));
        assertFalse(projetar(layout.mapa(), escala, deslocamentoX, deslocamentoY).sobrepoe(painel));
        assertFalse(projetar(layout.linhaDosFundadores(), escala, deslocamentoX, deslocamentoY)
                .sobrepoe(projetar(layout.linhaDaDemonstracao(), escala,
                        deslocamentoX, deslocamentoY)));
        for (int i = 0; i < 3; i++) {
            assertTrue(projetar(layout.tituloDaEtapa(i), escala, deslocamentoX, deslocamentoY)
                    .estaDentroDe(painel));
            assertFalse(projetar(layout.cenarioDaEtapa(i), escala, deslocamentoX, deslocamentoY)
                    .sobrepoe(projetar(layout.fundadorDaEtapa(i), escala,
                            deslocamentoX, deslocamentoY)));
        }
        assertTrue(projetar(layout.cartaoDaProximaMissao(), escala,
                deslocamentoX, deslocamentoY).estaDentroDe(painel));
    }

    private static RetanguloDeTela projetar(RetanguloDeTela area, float escala,
            float deslocamentoX, float deslocamentoY) {
        return new RetanguloDeTela(deslocamentoX + area.x() * escala,
                deslocamentoY + area.y() * escala,
                area.largura() * escala, area.altura() * escala);
    }
}
