package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.EstacaoNami;
import br.edu.unifor.missaomarte.domain.Nave;
import org.junit.jupiter.api.Test;

class AparenciaDaNamiTest {
    @Test
    void mantemDisponivelOpacaEApagaAUsada() {
        EstacaoNami disponivel = new EstacaoNami(1, 1);
        EstacaoNami usada = new EstacaoNami(1, 1);
        Nave nave = new Nave(1, 1, 3, 5);
        nave.perderVida();
        assertTrue(usada.ativar(nave));

        assertEquals(1f, AparenciaDaNami.opacidade(disponivel), 0.001f);
        assertEquals(0.30f, AparenciaDaNami.opacidade(usada), 0.001f);
    }
}
