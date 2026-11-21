package br.edu.unifor.missaomarte.gdx.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LayoutDaNovaPartidaTest {
    @ParameterizedTest
    @CsvSource({"540,1200", "540,895"})
    void toqueVerticalComFatorUmPontoSeisNaoSobrepoeTextosCartoesNemBotoes(
            float larguraFisica, float alturaFisica) {
        float largura = 800f;
        float altura = largura * alturaFisica / larguraFisica;
        MedidasDaNovaPartida medidas = new MedidasDaNovaPartida(
                71.2f, 32f, 46.4f, 67.2f, 67.2f, 71.2f);

        LayoutDaNovaPartida layout = LayoutDaNovaPartida.calcular(
                largura, altura, MargensSeguras.NENHUMA, medidas);

        RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, largura, altura);
        List<RetanguloDeTela> blocos = new ArrayList<>();
        blocos.add(layout.titulo());
        for (int i = 0; i < 3; i++) blocos.add(layout.cartao(i));
        blocos.add(layout.resumo());
        blocos.add(layout.descricao());
        blocos.add(layout.decolar());
        blocos.add(layout.voltar());

        for (RetanguloDeTela bloco : blocos) assertTrue(bloco.estaDentroDe(tela), bloco.toString());
        for (RetanguloDeTela a : blocos) {
            for (RetanguloDeTela b : blocos) {
                if (a != b) assertFalse(a.sobrepoe(b), a + " x " + b);
            }
        }
        for (int i = 1; i <= 2; i++) {
            assertTrue(layout.anterior(i).largura() >= medidas.alvoMinimo());
            assertTrue(layout.proximo(i).largura() >= medidas.alvoMinimo());
            assertFalse(layout.anterior(i).sobrepoe(layout.valor(i)));
            assertFalse(layout.proximo(i).sobrepoe(layout.valor(i)));
        }
        assertEquals(layout.cartao(0).largura(), layout.decolar().largura(), 0.001f);
        assertTrue(layout.voltar().topo() < layout.decolar().y());
    }
}
