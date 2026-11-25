package br.edu.unifor.missaomarte.gdx.entrada;

import static br.edu.unifor.missaomarte.gdx.entrada.JoystickVirtual.TipoDeComando.EXECUTAR;
import static br.edu.unifor.missaomarte.gdx.entrada.JoystickVirtual.TipoDeComando.PRESSIONAR;
import static br.edu.unifor.missaomarte.gdx.entrada.JoystickVirtual.TipoDeComando.SOLTAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.Acao;
import java.util.List;
import org.junit.jupiter.api.Test;

class JoystickVirtualTest {
    private static final float CENTRO = 100f;
    private static final float RAIO = 80f;
    private static final float RAIO_DO_BOTAO = 28f;

    @Test
    void mantemZonaMortaAteVinteECincoPorCentoDoRaio() {
        JoystickVirtual joystick = dinamico();

        JoystickVirtual.Resultado resultado = joystick.pressionar(3, CENTRO + 20f, CENTRO);

        assertTrue(resultado.tratado());
        assertTrue(resultado.comandos().isEmpty());
        assertEquals(Acao.INVALIDA, joystick.direcao());
    }

    @Test
    void escolheOEixoDominanteSemDiagonais() {
        JoystickVirtual joystick = dinamico();

        assertComando(joystick.pressionar(1, CENTRO + 60f, CENTRO + 30f), PRESSIONAR, Acao.DIREITA);
        assertComandos(joystick.arrastar(1, CENTRO - 25f, CENTRO + 70f),
                new JoystickVirtual.Comando(SOLTAR, Acao.DIREITA),
                new JoystickVirtual.Comando(PRESSIONAR, Acao.CIMA));
        assertComandos(joystick.arrastar(1, CENTRO - 65f, CENTRO - 20f),
                new JoystickVirtual.Comando(SOLTAR, Acao.CIMA),
                new JoystickVirtual.Comando(PRESSIONAR, Acao.ESQUERDA));
        assertComandos(joystick.arrastar(1, CENTRO + 10f, CENTRO - 70f),
                new JoystickVirtual.Comando(SOLTAR, Acao.ESQUERDA),
                new JoystickVirtual.Comando(PRESSIONAR, Acao.BAIXO));
    }

    @Test
    void limitaOBotaoDentroDaBaseEVoltaAoCentroAoSoltar() {
        JoystickVirtual joystick = dinamico();
        joystick.pressionar(0, CENTRO, CENTRO);
        joystick.arrastar(0, CENTRO + 300f, CENTRO);

        assertEquals(CENTRO + RAIO - RAIO_DO_BOTAO, joystick.botaoX(), 0.001f);
        assertEquals(CENTRO, joystick.botaoY(), 0.001f);

        JoystickVirtual.Resultado resultado = joystick.soltar(0);

        assertComando(resultado, SOLTAR, Acao.DIREITA);
        assertEquals(CENTRO, joystick.botaoX(), 0.001f);
        assertEquals(CENTRO, joystick.botaoY(), 0.001f);
        assertEquals(-1, joystick.ponteiroAtivo());
    }

    @Test
    void dinamicoMantemDirecaoSemRepetirEMudaComSoltarEPressionar() {
        JoystickVirtual joystick = dinamico();

        assertComando(joystick.pressionar(4, CENTRO + 70f, CENTRO), PRESSIONAR, Acao.DIREITA);
        assertTrue(joystick.arrastar(4, CENTRO + 75f, CENTRO + 5f).comandos().isEmpty());
        assertComandos(joystick.arrastar(4, CENTRO, CENTRO + 70f),
                new JoystickVirtual.Comando(SOLTAR, Acao.DIREITA),
                new JoystickVirtual.Comando(PRESSIONAR, Acao.CIMA));
    }

    @Test
    void classicoIniciaMovimentoESoltaAoVoltarParaAZonaMorta() {
        JoystickVirtual joystick = classico();

        assertComando(joystick.pressionar(2, CENTRO + 70f, CENTRO), EXECUTAR, Acao.DIREITA);
        assertTrue(joystick.arrastar(2, CENTRO + 75f, CENTRO).comandos().isEmpty());
        assertComando(joystick.arrastar(2, CENTRO, CENTRO), SOLTAR, Acao.DIREITA);
        assertComando(joystick.arrastar(2, CENTRO + 70f, CENTRO), EXECUTAR, Acao.DIREITA);
    }

    @Test
    void classicoReiniciaMovimentoAoMudarDiretamenteDeDirecao() {
        JoystickVirtual joystick = classico();

        assertComando(joystick.pressionar(5, CENTRO + 70f, CENTRO), EXECUTAR, Acao.DIREITA);
        assertComandos(joystick.arrastar(5, CENTRO, CENTRO + 70f),
                new JoystickVirtual.Comando(SOLTAR, Acao.DIREITA),
                new JoystickVirtual.Comando(EXECUTAR, Acao.CIMA));
    }

    @Test
    void classicoSoltaOMovimentoAoLevantarODedo() {
        JoystickVirtual joystick = classico();
        joystick.pressionar(6, CENTRO - 70f, CENTRO);

        assertComando(joystick.soltar(6), SOLTAR, Acao.ESQUERDA);
    }

    @Test
    void ignoraPonteiroQueNaoIniciouOJoystick() {
        JoystickVirtual joystick = dinamico();

        assertFalse(joystick.pressionar(1, CENTRO + RAIO + 1f, CENTRO).tratado());
        assertTrue(joystick.pressionar(2, CENTRO, CENTRO).tratado());
        assertFalse(joystick.arrastar(3, CENTRO + 70f, CENTRO).tratado());
        assertFalse(joystick.soltar(3).tratado());
        assertEquals(2, joystick.ponteiroAtivo());
    }

    private static JoystickVirtual dinamico() {
        return new JoystickVirtual(CENTRO, CENTRO, RAIO, RAIO_DO_BOTAO, true);
    }

    private static JoystickVirtual classico() {
        return new JoystickVirtual(CENTRO, CENTRO, RAIO, RAIO_DO_BOTAO, false);
    }

    private static void assertComando(JoystickVirtual.Resultado resultado,
            JoystickVirtual.TipoDeComando tipo, Acao acao) {
        assertTrue(resultado.tratado());
        assertEquals(List.of(new JoystickVirtual.Comando(tipo, acao)), resultado.comandos());
    }

    private static void assertComandos(JoystickVirtual.Resultado resultado,
            JoystickVirtual.Comando... comandos) {
        assertTrue(resultado.tratado());
        assertEquals(List.of(comandos), resultado.comandos());
    }
}
