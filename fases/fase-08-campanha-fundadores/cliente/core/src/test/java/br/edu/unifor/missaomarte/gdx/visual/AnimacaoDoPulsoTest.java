package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AnimacaoDoPulsoTest {
    @Test
    void cresceEDesapareceAoLongoDeUmCicloDeUmVirgulaSeisSegundos() {
        AnimacaoDoPulso.Estado inicio = AnimacaoDoPulso.calcular(0f, 0f);
        AnimacaoDoPulso.Estado metade = AnimacaoDoPulso.calcular(0.8f, 0f);
        AnimacaoDoPulso.Estado reinicio = AnimacaoDoPulso.calcular(1.6f, 0f);

        assertEquals(0.6f, inicio.escala(), 0.001f);
        assertEquals(0.6f, inicio.alfa(), 0.001f);
        assertEquals(1f, metade.escala(), 0.001f);
        assertEquals(0.3f, metade.alfa(), 0.001f);
        assertEquals(inicio.escala(), reinicio.escala(), 0.001f);
        assertEquals(inicio.alfa(), reinicio.alfa(), 0.001f);
    }

    @Test
    void segundoAnelComecaDefasadoEmMeioCiclo() {
        AnimacaoDoPulso.Estado segundo = AnimacaoDoPulso.calcular(0f, 0.8f);

        assertEquals(1f, segundo.escala(), 0.001f);
        assertEquals(0.3f, segundo.alfa(), 0.001f);
    }
}
