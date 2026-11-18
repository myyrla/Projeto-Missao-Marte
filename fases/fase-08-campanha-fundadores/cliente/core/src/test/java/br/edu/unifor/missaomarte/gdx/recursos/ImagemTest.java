package br.edu.unifor.missaomarte.gdx.recursos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ImagemTest {
    @Test
    void catalogaOsSpritesNaResolucaoDobrada() {
        assertEquals(2560, Imagem.FUNDO.larguraQuadro());
        assertEquals(1440, Imagem.FUNDO.alturaQuadro());

        assertEquals(96, Imagem.TERRENO.larguraQuadro());
        assertEquals(96, Imagem.TERRENO.alturaQuadro());
        assertEquals(96, Imagem.EXPLOSAO.larguraQuadro());
        assertEquals(96, Imagem.EXPLOSAO.alturaQuadro());

        assertEquals(48, Imagem.ICONE_VIDA.larguraQuadro());
        assertEquals(48, Imagem.ICONE_VIDA.alturaQuadro());
        assertEquals(48, Imagem.ICONE_VAGA.larguraQuadro());
        assertEquals(48, Imagem.ICONE_VAGA.alturaQuadro());

        assertEquals(96, Imagem.NAMI.larguraQuadro());
        assertEquals(96, Imagem.NAMI.alturaQuadro());
        assertEquals(2, Imagem.NAMI.quadros());
        assertEquals(96, Imagem.PARADA_ROVER.larguraQuadro());
        assertEquals(96, Imagem.PARADA_ROVER.alturaQuadro());
        assertEquals(2, Imagem.PARADA_ROVER.quadros());
        assertEquals(512, Imagem.POEIRA.larguraQuadro());
        assertEquals(512, Imagem.POEIRA.alturaQuadro());
        assertEquals(1, Imagem.POEIRA.quadros());
    }
}
