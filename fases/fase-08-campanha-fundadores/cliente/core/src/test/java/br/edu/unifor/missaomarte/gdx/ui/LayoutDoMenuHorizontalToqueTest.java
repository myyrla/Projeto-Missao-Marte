package br.edu.unifor.missaomarte.gdx.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.List;
import org.junit.jupiter.api.Test;

class LayoutDoMenuHorizontalToqueTest {
    @Test
    void textosAmpliadosCabemNosBotoesENoRodapeDosTablets() {
        for (float alvoMinimo : new float[] {36f, 24f}) {
            for (float fator : new float[] {1f, 1.3f, 1.6f}) {
                float escalaDaReferencia = 0.75f;
                MedidasDoMenuHorizontalToque medidas = new MedidasDoMenuHorizontalToque(
                        List.of(180f * escalaDaReferencia * fator,
                                118f * escalaDaReferencia * fator,
                                76f * escalaDaReferencia * fator,
                                70f * escalaDaReferencia * fator),
                        54f * escalaDaReferencia * fator,
                        29f * escalaDaReferencia * fator,
                        17f * escalaDaReferencia * fator,
                        17f * escalaDaReferencia * fator,
                        164f * escalaDaReferencia * fator,
                        214f * escalaDaReferencia * fator,
                        alvoMinimo);

                LayoutDoMenuHorizontalToque layout = LayoutDoMenuHorizontalToque.calcular(
                        960f, 600f, MargensSeguras.NENHUMA, medidas);
                RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, 960f, 600f);

                for (int i = 0; i < 4; i++) {
                    assertTrue(layout.botao(i).estaDentroDe(tela));
                    assertTrue(layout.rotulo(i).estaDentroDe(layout.botao(i)));
                    assertTrue(layout.rotulo(i).largura() + 0.001f
                            >= medidas.largurasDasOpcoes().get(i));
                    if (i > 0) {
                        assertFalse(layout.botao(i - 1).sobrepoe(layout.botao(i)));
                    }
                }
                assertTrue(layout.detalheDaCampanha().estaDentroDe(layout.botao(0)));
                assertTrue(layout.detalheDaCampanha().largura() + 0.001f
                        >= medidas.larguraDoDetalhe());
                assertFalse(layout.rotulo(0).sobrepoe(layout.detalheDaCampanha()));
                assertTrue(layout.conta().estaDentroDe(tela));
                assertTrue(layout.instrucao().estaDentroDe(tela));
                assertTrue(layout.conta().largura() + 0.001f >= medidas.larguraDaConta());
                assertTrue(layout.instrucao().largura() + 0.001f >= medidas.larguraDaInstrucao());
                assertFalse(layout.conta().sobrepoe(layout.instrucao()));
                assertTrue(layout.conta().x() == layout.instrucao().x());
                assertFalse(layout.botao(3).sobrepoe(layout.conta()));
            }
        }
    }
}
