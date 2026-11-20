package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.gdx.MapaGeometria;
import org.junit.jupiter.api.Test;

class MascaraDaTempestadeTest {
    private static final float ALFA_MAXIMA = 0.82f;
    private static final MapaGeometria MAPA = new MapaGeometria(-5, 5, 10f, 0f, 0f);

    @Test
    void escureceGradualmenteAoAfastarDaAreaVisivel() {
        MascaraDaTempestade.Campo campo = MascaraDaTempestade.calcular(
                MAPA, MascaraDaTempestadeTest::dentroDoRaio, -5, 5, ALFA_MAXIMA);

        float naBorda = campo.alfa(80f, 55f);
        float perto = campo.alfa(82f, 55f);
        float noMeio = campo.alfa(85f, 55f);
        float longe = campo.alfa(90f, 55f);

        assertEquals(0f, campo.alfa(55f, 55f), 0.0001f);
        assertEquals(0f, naBorda, 0.0001f);
        assertTrue(perto > naBorda && perto < noMeio);
        assertTrue(noMeio > perto && noMeio < longe);
        assertEquals(ALFA_MAXIMA, longe, 0.0001f);
    }

    @Test
    void distanciaDiagonalSuavizaOsCantosDaTransicao() {
        MascaraDaTempestade.Campo campo = MascaraDaTempestade.calcular(
                MAPA, MascaraDaTempestadeTest::dentroDoRaio, -5, 5, ALFA_MAXIMA);

        float lateral = campo.alfa(85f, 80f);
        float diagonal = campo.alfa(85f, 85f);

        assertTrue(diagonal > lateral);
        assertTrue(diagonal < ALFA_MAXIMA);
    }

    @Test
    void semCasaVisivelTodoOMapaFicaEscuro() {
        MascaraDaTempestade.Campo campo = MascaraDaTempestade.calcular(
                MAPA, posicao -> false, -5, 5, ALFA_MAXIMA);

        assertEquals(ALFA_MAXIMA, campo.alfa(55f, 55f), 0.0001f);
    }

    private static boolean dentroDoRaio(Posicao posicao) {
        return Math.max(Math.abs(posicao.getX()), Math.abs(posicao.getY())) <= 2;
    }
}
