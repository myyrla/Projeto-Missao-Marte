package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.ModoDeJogo;
import org.junit.jupiter.api.Test;

class AtalhosDoRodapeTest {
    @Test
    void modoClassicoContemEEmbarcar() {
        assertTrue(AtalhosDoRodape.para(ModoDeJogo.CLASSICO).stream()
                .anyMatch(atalho -> "E Embarcar".equals(atalho.texto())));
    }

    @Test
    void modoDinamicoNaoContemEEmbarcar() {
        assertFalse(AtalhosDoRodape.para(ModoDeJogo.DINAMICO).stream()
                .anyMatch(atalho -> "E Embarcar".equals(atalho.texto())));
    }
}
