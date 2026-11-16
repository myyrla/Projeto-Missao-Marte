package br.edu.unifor.missaomarte.gdx.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.ToDoubleFunction;
import org.junit.jupiter.api.Test;

class TextoResponsivoTest {
    @Test
    void limitaTextoComReticenciasUsandoALarguraMedida() {
        ToDoubleFunction<String> largura = texto -> texto.length();

        String curto = TextoResponsivo.limitarComReticencias("Piloto: Ana", 20f, largura);
        String longo = TextoResponsivo.limitarComReticencias("Piloto: WWWWWWWWWWWWWWWWWWWW", 16f, largura);

        assertEquals("Piloto: Ana", curto);
        assertTrue(largura.applyAsDouble(longo) <= 16f);
        assertTrue(longo.endsWith("..."));
    }
}
