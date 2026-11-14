package br.edu.unifor.missaomarte.gdx.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import org.junit.jupiter.api.Test;

class CampusLayoutTest {
    @Test
    void mapaMantemAProporcaoDasCamadasDoCampus() {
        CampusLayout layout = CampusLayout.padrao();
        RetanguloDeTela mapa = layout.mapa();

        assertEquals(1056f / 864f, mapa.largura() / mapa.altura(), 0.001f);
        assertEquals(284f, mapa.centroX(), 0.001f);
        assertEquals(330f, mapa.centroY(), 0.001f);
    }
}
