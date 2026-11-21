package br.edu.unifor.missaomarte.gdx.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LayoutDoMenuToqueTest {
    @ParameterizedTest
    @CsvSource({"540,1200", "540,895"})
    void fatorUmPontoSeisSeparaTituloOpcoesERodape(float larguraFisica, float alturaFisica) {
        float largura = 800f;
        float altura = largura * alturaFisica / larguraFisica;
        MedidasDoMenuToque medidas = new MedidasDoMenuToque(
                32f, 192f, 67.2f, 46.4f, 26.4f, 71.2f);

        LayoutDoMenuToque layout = LayoutDoMenuToque.calcular(
                largura, altura, MargensSeguras.NENHUMA, medidas);

        RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, largura, altura);
        List<RetanguloDeTela> blocos = new ArrayList<>();
        blocos.add(layout.expedicao());
        blocos.add(layout.missao());
        blocos.add(layout.marte());
        blocos.add(layout.frase());
        for (int i = 0; i < 4; i++) blocos.add(layout.botao(i));
        blocos.add(layout.conta());
        blocos.add(layout.instrucao());

        for (RetanguloDeTela bloco : blocos) assertTrue(bloco.estaDentroDe(tela), bloco.toString());
        for (int i = 0; i < blocos.size(); i++) {
            for (int j = i + 1; j < blocos.size(); j++) {
                assertFalse(blocos.get(i).sobrepoe(blocos.get(j)),
                        blocos.get(i) + " x " + blocos.get(j));
            }
        }
        assertTrue(layout.missao().y() >= layout.marte().topo());
        assertTrue(layout.conta().x() == layout.instrucao().x());
    }
}
