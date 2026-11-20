package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import org.junit.jupiter.api.Test;

class MapaNaInterfaceTest {
    @Test
    void projetaERecortaOMapaNasTresJanelasDeReferencia() {
        assertRetangulo(MapaNaInterface.projetar(
                new RetanguloDeTela(0f, 0f, 720f, 720f),
                new RetanguloDeTela(-280f, 0f, 1280f, 720f), 1280f, 720f),
                280f, 0f, 720f, 720f);
        assertRetangulo(MapaNaInterface.projetar(
                new RetanguloDeTela(0f, 0f, 792f, 792f),
                new RetanguloDeTela(-564f, -144f, 1920f, 1080f), 1920f, 1080f),
                564f, 144f, 792f, 792f);
        assertRetangulo(MapaNaInterface.projetar(
                new RetanguloDeTela(0f, 0f, 792f, 792f),
                new RetanguloDeTela(126f, -204f, 540f, 1200f), 540f, 1200f),
                0f, 204f, 540f, 792f);
    }

    private static void assertRetangulo(RetanguloDeTela atual, float x, float y, float largura, float altura) {
        assertEquals(x, atual.x(), 0.001f);
        assertEquals(y, atual.y(), 0.001f);
        assertEquals(largura, atual.largura(), 0.001f);
        assertEquals(altura, atual.altura(), 0.001f);
    }
}
