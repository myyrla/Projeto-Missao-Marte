package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class GeometriaDoRastroTest {
    @Test
    void ligaCentrosComPontosACadaDezUnidadesEAlfaCrescente() {
        List<GeometriaDoRastro.Ponto> pontos = GeometriaDoRastro.calcular(
                List.of(new RastroDaNave.Casa(0, 0), new RastroDaNave.Casa(1, 0)),
                0, 0, 20f, 0f, 0f, 10f);

        assertEquals(3, pontos.size());
        assertEquals(10f, pontos.get(0).x(), 0.001f);
        assertEquals(20f, pontos.get(1).x(), 0.001f);
        assertEquals(30f, pontos.get(2).x(), 0.001f);
        assertEquals(10f, pontos.get(0).y(), 0.001f);
        assertEquals(0.15f, pontos.get(0).alfa(), 0.001f);
        assertEquals(0.425f, pontos.get(1).alfa(), 0.001f);
        assertEquals(0.70f, pontos.get(2).alfa(), 0.001f);
    }

    @Test
    void umaCasaProduzUmPontoComAlfaMaisNovo() {
        List<GeometriaDoRastro.Ponto> pontos = GeometriaDoRastro.calcular(
                List.of(new RastroDaNave.Casa(3, 4)), 0, 10, 64f, 0f, 0f, 10f);

        assertEquals(1, pontos.size());
        assertEquals(0.70f, pontos.get(0).alfa(), 0.001f);
    }

    @Test
    void inverteOEixoYComoOMapaDoJogo() {
        List<GeometriaDoRastro.Ponto> pontos = GeometriaDoRastro.calcular(
                List.of(new RastroDaNave.Casa(0, 0), new RastroDaNave.Casa(0, 1)),
                0, 10, 20f, 0f, 0f, 10f);

        assertEquals(210f, pontos.get(0).y(), 0.001f);
        assertEquals(190f, pontos.get(pontos.size() - 1).y(), 0.001f);
    }
}
