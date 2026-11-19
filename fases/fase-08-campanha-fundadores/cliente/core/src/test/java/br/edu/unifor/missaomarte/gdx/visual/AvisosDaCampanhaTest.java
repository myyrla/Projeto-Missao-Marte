package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.edu.unifor.missaomarte.application.EventoJogo;
import org.junit.jupiter.api.Test;

class AvisosDaCampanhaTest {
    @Test
    void informaRecuperacaoDeCasco() {
        assertEquals("+1 casco", AvisosDaCampanha.texto(EventoJogo.NAMI_ATIVADA));
        assertNull(AvisosDaCampanha.texto(EventoJogo.MOVIMENTO));
    }
}
