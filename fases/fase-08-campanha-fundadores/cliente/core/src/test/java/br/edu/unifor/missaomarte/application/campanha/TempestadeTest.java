package br.edu.unifor.missaomarte.application.campanha;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Posicao;
import org.junit.jupiter.api.Test;

class TempestadeTest {
    private static final Posicao NAVE = new Posicao(0, 0);

    @Test
    void comecaAosDezSegundosETerminaAosVinteEDois() {
        TempestadeDeAreia tempestade = TempestadeDeAreia.padrao();

        tempestade.avancar(9.99f);
        assertFalse(tempestade.estaAtiva());
        tempestade.avancar(0.01f);
        assertTrue(tempestade.estaAtiva());
        tempestade.avancar(11.99f);
        assertTrue(tempestade.estaAtiva());
        tempestade.avancar(0.01f);
        assertFalse(tempestade.estaAtiva());
    }

    @Test
    void repeteTrintaSegundosDepoisDoInicioAnterior() {
        TempestadeDeAreia tempestade = TempestadeDeAreia.padrao();
        tempestade.avancar(39.99f);
        assertFalse(tempestade.estaAtiva());

        tempestade.avancar(0.01f);

        assertTrue(tempestade.estaAtiva());
        tempestade.avancar(12f);
        assertFalse(tempestade.estaAtiva());
    }

    @Test
    void duranteATempestadeUsaRaioChebyshevDois() {
        TempestadeDeAreia tempestade = TempestadeDeAreia.padrao();
        tempestade.avancar(10f);

        assertTrue(tempestade.estaVisivel(NAVE, new Posicao(2, 2)));
        assertTrue(tempestade.estaVisivel(NAVE, NAVE));
        assertFalse(tempestade.estaVisivel(NAVE, new Posicao(3, 0)));
        assertFalse(tempestade.estaVisivel(NAVE, new Posicao(-3, -3)));
    }

    @Test
    void foraDaTempestadeTodoOMapaFicaVisivelEDeltaInvalidoEIgnorado() {
        TempestadeDeAreia tempestade = TempestadeDeAreia.padrao();
        tempestade.avancar(-5f);
        tempestade.avancar(0f);

        assertFalse(tempestade.estaAtiva());
        assertTrue(tempestade.estaVisivel(NAVE, new Posicao(5, 5)));
    }
}
