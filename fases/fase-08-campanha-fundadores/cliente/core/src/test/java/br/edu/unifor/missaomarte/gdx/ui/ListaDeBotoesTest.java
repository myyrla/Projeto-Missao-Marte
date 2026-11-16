package br.edu.unifor.missaomarte.gdx.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ListaDeBotoesTest {
    @Test
    void focoComecaNoPrimeiroEVaiAteAsExtremidadesEmCiclo() throws Exception {
        ListaDeBotoes lista = new ListaDeBotoes(4);

        assertEquals(0, lista.foco());
        lista.subir();
        assertEquals(3, lista.foco());
        lista.descer();
        assertEquals(0, lista.foco());
        lista.descer();
        assertEquals(1, lista.foco());
    }

    @Test
    void cliqueSoMudaOFocoQuandoOIndiceExiste() throws Exception {
        ListaDeBotoes lista = new ListaDeBotoes(4);

        assertTrue(lista.focar(2));
        assertEquals(2, lista.foco());
        assertFalse(lista.focar(-1));
        assertFalse(lista.focar(4));
        assertEquals(2, lista.foco());
    }
}
