package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PoliticaDeAtalhosTest {
    @Test
    void escondeAtalhosDeTecladoNoModoToque() {
        assertFalse(PoliticaDeAtalhos.exibeRodapeDeTeclado(true));
    }

    @Test
    void preservaAtalhosDeTecladoForaDoModoToque() {
        assertTrue(PoliticaDeAtalhos.exibeRodapeDeTeclado(false));
    }
}
