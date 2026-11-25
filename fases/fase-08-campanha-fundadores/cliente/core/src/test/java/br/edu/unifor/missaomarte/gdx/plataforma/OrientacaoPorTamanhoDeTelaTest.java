package br.edu.unifor.missaomarte.gdx.plataforma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OrientacaoPorTamanhoDeTelaTest {

    @Test
    void celularUsaRetratoAbaixoDeSeiscentosDp() {
        assertEquals(OrientacaoPorTamanhoDeTela.RETRATO,
                OrientacaoPorTamanhoDeTela.paraMenorLarguraDp(599));
    }

    @Test
    void tabletUsaPaisagemAPartirDeSeiscentosDp() {
        assertEquals(OrientacaoPorTamanhoDeTela.PAISAGEM,
                OrientacaoPorTamanhoDeTela.paraMenorLarguraDp(600));
        assertEquals(OrientacaoPorTamanhoDeTela.PAISAGEM,
                OrientacaoPorTamanhoDeTela.paraMenorLarguraDp(840));
    }

    @Test
    void eTabletUsaOMesmoLimiarDaOrientacao() {
        assertFalse(OrientacaoPorTamanhoDeTela.eTablet(599));
        assertTrue(OrientacaoPorTamanhoDeTela.eTablet(600));
        assertTrue(OrientacaoPorTamanhoDeTela.eTablet(800));
    }
}
