package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import org.junit.jupiter.api.Test;

class RadarGeometriaTest {
    @Test
    void projetaOsPontosProporcionalmenteEInverteOEixoVerticalDoDominio() {
        RadarGeometria radar = new RadarGeometria(new RetanguloDeTela(100f, 200f, 192f, 100f), -5, 5);

        assertEquals(100f, radar.x(-5), 0.001f);
        assertEquals(292f, radar.x(5), 0.001f);
        assertEquals(300f, radar.y(-5), 0.001f);
        assertEquals(200f, radar.y(5), 0.001f);
        assertEquals(196f, radar.x(0), 0.001f);
        assertEquals(250f, radar.y(0), 0.001f);
    }
}
