package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.gdx.layout.EscalaDeTela;
import br.edu.unifor.missaomarte.gdx.plataforma.Plataforma;
import org.junit.jupiter.api.Test;

class MissaoMarteGameEscalaTest {
    @Test
    void fatorDeTextoDoDesktopSemToqueEExatamenteUm() {
        assertEquals(1f, MissaoMarteGame.fatorDeTexto(
                Plataforma.desktop(), 4f, 0.5f), 0f);
    }

    @Test
    void fatorDeTextoDoDesktopComToqueEExatamenteUm() {
        assertEquals(1f, MissaoMarteGame.fatorDeTexto(
                Plataforma.desktop(true), 4f, 0.5f), 0f);
    }

    @Test
    void fatorDeTextoDoAndroidRespeitaOLimite() {
        assertEquals(EscalaDeTela.FATOR_MAXIMO_DE_TEXTO,
                MissaoMarteGame.fatorDeTexto(Plataforma.android(), 4f, 0.5f), 0f);
    }
}
