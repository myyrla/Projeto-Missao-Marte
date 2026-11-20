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

    private static int[] tamanhoDaJanela(boolean toque) throws Exception {
        Method metodo = Lwjgl3Launcher.class.getDeclaredMethod("tamanhoDaJanela", boolean.class);
        metodo.setAccessible(true);
        return (int[]) metodo.invoke(null, toque);
    }
}
