package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class DicaDaNamiTest {
    @Test
    void apareceUmaUnicaVezSomenteEmPartidaComNami() {
        DicaDaNami dica = new DicaDaNami();

        assertNull(dica.proxima(false));
        assertEquals("NAMI: passe por aqui para reparar o casco", dica.proxima(true));
        assertNull(dica.proxima(true));
    }
}
