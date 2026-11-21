package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LayoutDoResultadoDaPartidaTest {
    @ParameterizedTest
    @CsvSource({"540,1200", "540,895"})
    void toqueComFatorUmPontoSeisMantemTituloEBotoesInteirosNoPainel(
            float larguraFisica, float alturaFisica) {
        float largura = 800f;
        float altura = largura * alturaFisica / larguraFisica;
        MedidasDoResultadoDaPartida medidas = new MedidasDoResultadoDaPartida(
                430f, 71.2f, 67.2f, 32f, 71.2f, 26.4f,
                238f, 108f, 46.4f, 71.2f);

        LayoutDoResultadoDaPartida layout = LayoutDoResultadoDaPartida.calcular(
                largura, altura, MargensSeguras.NENHUMA, medidas);

        RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, largura, altura);
        assertTrue(layout.painel().estaDentroDe(tela));
        assertTrue(layout.titulo().estaDentroDe(layout.painel()));
        assertTrue(layout.titulo().largura() >= medidas.larguraDoTitulo());
        assertTrue(layout.motivo().estaDentroDe(layout.painel()));
        assertTrue(layout.botao(0).estaDentroDe(layout.painel()));
        assertTrue(layout.botao(1).estaDentroDe(layout.painel()));
        assertTrue(layout.botao(0).largura() >= medidas.larguraDoPrimeiroBotao() + 32f);
        assertTrue(layout.botao(1).largura() >= medidas.larguraDoSegundoBotao() + 32f);
        assertTrue(layout.botao(0).altura() >= medidas.alvoMinimo());
        assertTrue(layout.botao(1).altura() >= medidas.alvoMinimo());
        assertFalse(layout.botao(0).sobrepoe(layout.botao(1)));
        assertSemSobreposicao(List.of(layout.titulo(), layout.motivo(), layout.rotuloDosPontos(),
                layout.pontos(), layout.situacao(), layout.botao(0), layout.botao(1)));
    }

    @Test
    void empilhaBotoesQuandoOsDoisRotulosNaoCabemNaMesmaLinha() {
        MedidasDoResultadoDaPartida medidas = new MedidasDoResultadoDaPartida(
                430f, 71.2f, 67.2f, 32f, 71.2f, 26.4f,
                500f, 250f, 46.4f, 71.2f);

        LayoutDoResultadoDaPartida layout = LayoutDoResultadoDaPartida.calcular(
                800f, 1325f, MargensSeguras.NENHUMA, medidas);

        assertTrue(layout.botao(0).y() >= layout.botao(1).topo());
        assertTrue(layout.botao(0).largura() >= medidas.larguraDoPrimeiroBotao() + 32f);
        assertTrue(layout.botao(1).largura() >= medidas.larguraDoSegundoBotao() + 32f);
    }

    private static void assertSemSobreposicao(List<RetanguloDeTela> areas) {
        for (int i = 0; i < areas.size(); i++) {
            for (int j = i + 1; j < areas.size(); j++) {
                RetanguloDeTela a = areas.get(i);
                RetanguloDeTela b = areas.get(j);
                assertFalse(a.sobrepoe(b), a + " x " + b);
            }
        }
    }
}
