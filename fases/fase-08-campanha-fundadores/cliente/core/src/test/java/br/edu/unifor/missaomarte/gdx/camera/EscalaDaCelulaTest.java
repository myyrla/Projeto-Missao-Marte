package br.edu.unifor.missaomarte.gdx.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EscalaDaCelulaTest {
    @Test
    void usaSomenteAAlturaTotalDaJanelaELimitaEntreQuarentaEOitoESetentaEDois() {
        assertEquals(48f, EscalaDaCelula.calcular(320f, 11), 0.001f);
        assertEquals(600f / 11f, EscalaDaCelula.calcular(600f, 11), 0.001f);
        assertEquals(720f / 11f, EscalaDaCelula.calcular(720f, 11), 0.001f);
        assertEquals(72f, EscalaDaCelula.calcular(1200f, 11), 0.001f);
    }

    @Test
    void noToqueVerticalOcupaTodaALarguraUtilSemAplicarOLimiteHorizontal() {
        assertEquals(540f / 11f, EscalaDaCelula.calcularNoToqueVertical(540f, 11), 0.001f);
        assertEquals(1080f / 11f, EscalaDaCelula.calcularNoToqueVertical(1080f, 11), 0.001f);
    }

    @Test
    void comDensidadeDoTabletOMaximoEscalaEmDp() {
        // Tablet 2560x1600, densidade 2.0: maxima = 72 * 2 = 144; desejada = 1600/11 = 145.5 → 144
        assertEquals(144f, EscalaDaCelula.calcularComDensidade(1600f, 11, 2.0f), 0.001f);
    }

    @Test
    void comDensidadeDoDesktopOMaximoNaoMuda() {
        // Desktop densidade 1.0: maxima = 72 * 1 = 72; desejada = 800/11 = 72.7 → 72
        assertEquals(72f, EscalaDaCelula.calcularComDensidade(800f, 11, 1.0f), 0.001f);
    }

    @Test
    void comDensidadeDoCelularNaoUltrapassaODesejado() {
        // Celular 2340x1080, paisagem, densidade 2.6: maxima = 72*2.6 = 187.2; desejada = 1080/11 = 98.2 → 98.2
        assertEquals(1080f / 11f, EscalaDaCelula.calcularComDensidade(1080f, 11, 2.6f), 0.001f);
    }

    @Test
    void comDensidadeRespeitaOMinimoEm320Pixels() {
        assertEquals(48f, EscalaDaCelula.calcularComDensidade(320f, 11, 2.0f), 0.001f);
    }
}
