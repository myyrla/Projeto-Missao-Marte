package br.edu.unifor.missaomarte.lwjgl3;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class Lwjgl3LauncherTest {
    @AfterEach
    void limparPropriedade() {
        System.clearProperty("missaomarte.janela");
        System.clearProperty("missaomarte.campanha.etapa");
        System.clearProperty("missaomarte.campanha.concluida");
        System.clearProperty("missaomarte.fatorTexto");
    }

    @Test
    void janelaPadraoUsaFormatoDezesseisPorNove() throws Exception {
        System.clearProperty("missaomarte.janela");

        assertArrayEquals(new int[] {1280, 720}, tamanhoDaJanela(false));
    }

    @Test
    void propriedadeDaJanelaContinuaPrevalecendo() throws Exception {
        System.setProperty("missaomarte.janela", "540x1200");

        assertArrayEquals(new int[] {540, 1200}, tamanhoDaJanela(true));
    }

    @Test
    void tituloDaJanelaTemAcento() throws Exception {
        Method metodo = Lwjgl3Launcher.class.getDeclaredMethod("tituloDaJanela");
        metodo.setAccessible(true);

        assertEquals("Missão Marte", metodo.invoke(null));
    }

    @Test
    void conclusaoDeDemonstracaoTemPrecedenciaSobreAEtapa() throws Exception {
        System.setProperty("missaomarte.campanha.etapa", "2");
        System.setProperty("missaomarte.campanha.concluida", "true");

        Method metodo = Lwjgl3Launcher.class.getDeclaredMethod("selecaoDaCampanha");
        metodo.setAccessible(true);

        assertEquals("concluida", metodo.invoke(null));
    }

    @Test
    void fatorDeTextoDoDesktopPermaneceUmSemAPropriedade() throws Exception {
        System.clearProperty("missaomarte.fatorTexto");

        assertEquals(1f, fatorDeTexto(), 0f);
    }

    @Test
    void fatorDeTextoDoDesktopPodeSerForcadoParaUmPontoSeis() throws Exception {
        System.setProperty("missaomarte.fatorTexto", "1.6");

        assertEquals(1.6f, fatorDeTexto(), 0f);
    }

    @Test
    void fatorDeTextoInvalidoMantemOPadraoDoDesktop() throws Exception {
        System.setProperty("missaomarte.fatorTexto", "invalido");

        assertEquals(1f, fatorDeTexto(), 0f);
    }

    private static int[] tamanhoDaJanela(boolean toque) throws Exception {
        Method metodo = Lwjgl3Launcher.class.getDeclaredMethod("tamanhoDaJanela", boolean.class);
        metodo.setAccessible(true);
        return (int[]) metodo.invoke(null, toque);
    }

    private static float fatorDeTexto() throws Exception {
        Method metodo = Lwjgl3Launcher.class.getDeclaredMethod("fatorDeTextoDoDesktop");
        metodo.setAccessible(true);
        return (float) metodo.invoke(null);
    }
}
