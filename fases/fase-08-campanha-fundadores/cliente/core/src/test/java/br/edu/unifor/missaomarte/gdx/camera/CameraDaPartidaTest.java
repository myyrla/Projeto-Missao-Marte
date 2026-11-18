package br.edu.unifor.missaomarte.gdx.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CameraDaPartidaTest {
    @Test
    void prendeOCentroNasBordasQuandoOMapaEMaiorQueAJanela() {
        CameraDaPartida esquerda = new CameraDaPartida();
        CameraDaPartida.Centro inicio = esquerda.atualizar(1056f, 1056f, 720f, 600f, 0f, 0f);
        assertEquals(360f, inicio.x(), 0.001f);
        assertEquals(300f, inicio.y(), 0.001f);

        CameraDaPartida direita = new CameraDaPartida();
        CameraDaPartida.Centro fim = direita.atualizar(1056f, 1056f, 720f, 600f, 1056f, 1056f);
        assertEquals(696f, fim.x(), 0.001f);
        assertEquals(756f, fim.y(), 0.001f);
    }

    @Test
    void centralizaMapaMenorQueAJanela() {
        CameraDaPartida camera = new CameraDaPartida();

        CameraDaPartida.Centro centro = camera.atualizar(500f, 400f, 720f, 600f, 20f, 380f);

        assertEquals(250f, centro.x(), 0.001f);
        assertEquals(200f, centro.y(), 0.001f);
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
