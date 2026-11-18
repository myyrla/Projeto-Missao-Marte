package br.edu.unifor.missaomarte.gdx.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CameraDaPartidaTest {
    @Test
    void moveSomenteNoEixoEmQueOMapaNaoCabeNaJanela() {
        CameraDaPartida esquerda = new CameraDaPartida();
        CameraDaPartida.Centro inicio = esquerda.atualizar(792f, 792f, 540f, 1200f, 0f, 0f);
        assertEquals(270f, inicio.x(), 0.001f);
        assertEquals(396f, inicio.y(), 0.001f);

        CameraDaPartida direita = new CameraDaPartida();
        CameraDaPartida.Centro fim = direita.atualizar(792f, 792f, 540f, 1200f, 792f, 792f);
        assertEquals(522f, fim.x(), 0.001f);
        assertEquals(396f, fim.y(), 0.001f);
    }

    @Test
    void centralizaENaoSegueOAlvoQuandoOMapaCabeNaJanela() {
        CameraDaPartida camera = new CameraDaPartida();

        CameraDaPartida.Centro inicio = camera.atualizar(720f, 720f, 1280f, 720f, 20f, 700f);
        CameraDaPartida.Centro depois = camera.atualizar(720f, 720f, 1280f, 720f, 700f, 20f);

        assertEquals(360f, inicio.x(), 0.001f);
        assertEquals(360f, inicio.y(), 0.001f);
        assertEquals(360f, depois.x(), 0.001f);
        assertEquals(360f, depois.y(), 0.001f);
    }

    @Test
    void suavizaDozePorCentoDaDistanciaPorQuadro() {
        CameraDaPartida camera = new CameraDaPartida();
        camera.atualizar(1600f, 1200f, 600f, 600f, 600f, 600f);

        CameraDaPartida.Centro centro = camera.atualizar(1600f, 1200f, 600f, 600f, 700f, 650f);

        assertEquals(612f, centro.x(), 0.001f);
        assertEquals(606f, centro.y(), 0.001f);
    }
}
