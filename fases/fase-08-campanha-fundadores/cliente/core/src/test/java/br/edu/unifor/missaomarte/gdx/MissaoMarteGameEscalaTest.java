package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.gdx.layout.EscalaDeTela;
import br.edu.unifor.missaomarte.gdx.plataforma.Plataforma;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MissaoMarteGameEscalaTest {
    @Test
    void fatorDeTextoDoDesktopSemToqueEExatamenteUm() {
        assertEquals(1f, MissaoMarteGame.fatorDeTexto(
                Plataforma.desktop(), 4f, 0.5f, 0), 0f);
    }

    @Test
    void fatorDeTextoDoDesktopComToqueEExatamenteUm() {
        assertEquals(1f, MissaoMarteGame.fatorDeTexto(
                Plataforma.desktop(true), 4f, 0.5f, 0), 0f);
    }

    @Test
    void fatorDeTextoDoAndroidRespeitaOLimite() {
        assertEquals(EscalaDeTela.FATOR_MAXIMO_DE_TEXTO,
                MissaoMarteGame.fatorDeTexto(Plataforma.android(), 4f, 0.5f, 415), 0f);
    }

    @Test
    void fatorForcadoNoDesktopPrevaleceSobreAPoliticaDeDensidade() {
        assertEquals(1.6f, MissaoMarteGame.fatorDeTexto(
                Plataforma.desktop(true, 1.6f), 4f, 0.5f, 0), 0f);
    }

    @Test
    void instanciaDoJogoTambemUsaOFatorForcadoNoDesktop() {
        MissaoMarteGame jogo = new MissaoMarteGame(
                null, null, null, null, Plataforma.desktop(true, 1.6f),
                new Random(1), null);

        assertEquals(1.6f, jogo.fatorDeTexto(), 0f);
    }

    @Test
    void tabletAndroidComDensidadeDoisProduzeUmPontoTres() {
        // 2560x1600, densidade 2.0: ppu = 2560/960 = 2.667; menorLarguraDp = 1600/2 = 800
        assertEquals(EscalaDeTela.FATOR_DE_TEXTO_PARA_TABLET,
                MissaoMarteGame.fatorDeTexto(Plataforma.android(), 2f, 2.667f, 800), 0f);
    }

    @Test
    void celularAndroidNaoUsaOFatorDeTablet() {
        // 1080x2340, densidade 2.6: menorLarguraDp = 1080/2.6 = 415
        float fator = MissaoMarteGame.fatorDeTexto(Plataforma.android(), 2.6f, 1.35f, 415);

        assertEquals(EscalaDeTela.fatorDeTexto(2.6f, 1.35f), fator, 0f);
    }
}
