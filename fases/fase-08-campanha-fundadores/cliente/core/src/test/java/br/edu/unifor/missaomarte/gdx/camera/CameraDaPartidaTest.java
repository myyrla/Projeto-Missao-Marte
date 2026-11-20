package br.edu.unifor.missaomarte.gdx.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.gdx.hud.MapaNaInterface;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
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

    @Test
    void mantemPrimeiraEUltimaLinhaDentroDaAreaUtilNoToque() {
        RetanguloDeTela areaUtil = new RetanguloDeTela(0f, 200f, 540f, 650f);
        CameraDaPartida cameraNoInicio = new CameraDaPartida();
        CameraDaPartida cameraNoFim = new CameraDaPartida();

        CameraDaPartida.Centro inicio = cameraNoInicio.atualizar(
                792f, 792f, 540f, 1200f, areaUtil, 36f, 36f);
        CameraDaPartida.Centro fim = cameraNoFim.atualizar(
                792f, 792f, 540f, 1200f, areaUtil, 756f, 756f);

        assertTrue(celulaNaTela(0f, 0f, inicio).estaDentroDe(areaUtil));
        assertTrue(celulaNaTela(720f, 720f, fim).estaDentroDe(areaUtil));
    }

    @Test
    void areaUtilIgualAJanelaPreservaORetanguloHorizontalDoMapa() {
        CameraDaPartida anterior = new CameraDaPartida();
        CameraDaPartida ajustada = new CameraDaPartida();
        CameraDaPartida.Centro centroAnterior = anterior.atualizar(
                720f, 720f, 1280f, 720f, 20f, 700f);
        CameraDaPartida.Centro centroAjustado = ajustada.atualizar(
                720f, 720f, 1280f, 720f,
                new RetanguloDeTela(0f, 0f, 1280f, 720f), 20f, 700f);

        RetanguloDeTela mapa = new RetanguloDeTela(0f, 0f, 720f, 720f);
        RetanguloDeTela antes = MapaNaInterface.projetar(mapa,
                areaVisivel(centroAnterior, 1280f, 720f), 1280f, 720f);
        RetanguloDeTela depois = MapaNaInterface.projetar(mapa,
                areaVisivel(centroAjustado, 1280f, 720f), 1280f, 720f);

        assertRetangulo(antes, 280f, 0f, 720f, 720f);
        assertRetangulo(depois, antes.x(), antes.y(), antes.largura(), antes.altura());
    }

    private static RetanguloDeTela celulaNaTela(float x, float y, CameraDaPartida.Centro centro) {
        return new RetanguloDeTela(x - centro.x() + 270f, y - centro.y() + 600f, 72f, 72f);
    }

    private static RetanguloDeTela areaVisivel(CameraDaPartida.Centro centro, float largura, float altura) {
        return new RetanguloDeTela(centro.x() - largura / 2f, centro.y() - altura / 2f, largura, altura);
    }

    private static void assertRetangulo(RetanguloDeTela atual, float x, float y, float largura, float altura) {
        assertEquals(x, atual.x(), 0.001f);
        assertEquals(y, atual.y(), 0.001f);
        assertEquals(largura, atual.largura(), 0.001f);
        assertEquals(altura, atual.altura(), 0.001f);
    }
}
