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
    void cartaoDaProximaMissaoOcupaOEspacoAbaixoDasEtapas() {
        CampusLayout layout = CampusLayout.calcular(540f, 895f, true);

        assertTrue(layout.cartaoDaProximaMissao().estaDentroDe(layout.progresso()));
        assertFalse(layout.cartaoDaProximaMissao().sobrepoe(layout.etapa(2)));
        assertTrue(layout.cartaoDaProximaMissao().altura() >= 48f);
        assertEquals(10f, layout.etapa(2).y() - layout.cartaoDaProximaMissao().topo(), 0.001f);
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
