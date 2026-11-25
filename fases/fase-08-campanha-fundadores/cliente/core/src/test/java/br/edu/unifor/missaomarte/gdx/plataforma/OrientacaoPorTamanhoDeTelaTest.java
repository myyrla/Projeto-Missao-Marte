package br.edu.unifor.missaomarte.gdx.plataforma;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
