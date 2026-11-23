package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import java.util.List;
import org.junit.jupiter.api.Test;

class ApresentacaoDoResultadoDaPartidaTest {
    @Test
    void apresentaVitoriaForaDaCampanhaComPontuacaoFormatada() {
        ApresentacaoDoResultadoDaPartida apresentacao = ApresentacaoDoResultadoDaPartida.criar(
                EstadoPartida.VITORIA, 12_345, List.of(), false,
                EstadoDoEnvioDoResultado.NAO_ENVIA, "", false, false);

        assertTrue(apresentacao.vitoria());
        assertEquals("MISSÃO CUMPRIDA", apresentacao.titulo());
        assertEquals("Todos os passageiros retornaram à base.", apresentacao.motivo());
        assertEquals("12.345", apresentacao.pontos());
        assertEquals("Menu", apresentacao.rotuloDeSaida());
    }

    @Test
    void apresentaDerrotaDaCampanhaComMotivoPadrao() {
        ApresentacaoDoResultadoDaPartida apresentacao = ApresentacaoDoResultadoDaPartida.criar(
                EstadoPartida.DERROTA, 900, List.of(), true,
                EstadoDoEnvioDoResultado.NAO_ENVIA, "", false, true);

        assertFalse(apresentacao.vitoria());
        assertEquals("MISSÃO FALHOU", apresentacao.titulo());
        assertEquals("A missão foi encerrada.", apresentacao.motivo());
        assertEquals("Campus", apresentacao.rotuloDeSaida());
    }

    @Test
    void combinaMensagensDaPartidaNoMotivo() {
        ApresentacaoDoResultadoDaPartida apresentacao = ApresentacaoDoResultadoDaPartida.criar(
                EstadoPartida.DERROTA, 900, List.of("Casco destruído.", "Fim da missão."), true,
                EstadoDoEnvioDoResultado.NAO_ENVIA, "", false, true);

        assertEquals("Casco destruído. Fim da missão.", apresentacao.motivo());
    }

    @Test
    void apresentaSituacaoDoEnvioAoServidor() {
        ApresentacaoDoResultadoDaPartida enviando = criarComEnvio(
                EstadoDoEnvioDoResultado.ENVIANDO, "", true, false);
        ApresentacaoDoResultadoDaPartida enviado = criarComEnvio(
                EstadoDoEnvioDoResultado.ENVIADO, "", true, false);

        assertEquals("Enviando o resultado ao servidor...", enviando.textoDoEnvio());
        assertEquals("Resultado enviado ao servidor.", enviado.textoDoEnvio());
    }

    @Test
    void limitaErroDeEnvioEOfereceReenvioSomenteNoDesktopLogado() {
        String erroLongo = "x".repeat(60);
        ApresentacaoDoResultadoDaPartida desktop = criarComEnvio(
                EstadoDoEnvioDoResultado.FALHOU, erroLongo, true, false);
        ApresentacaoDoResultadoDaPartida toque = criarComEnvio(
                EstadoDoEnvioDoResultado.FALHOU, erroLongo, true, true);
        ApresentacaoDoResultadoDaPartida convidado = criarComEnvio(
                EstadoDoEnvioDoResultado.FALHOU, erroLongo, false, false);

        assertEquals("x".repeat(49) + "...", desktop.textoDoEnvio());
        assertTrue(desktop.exibeAtalhoDeReenvio());
        assertFalse(toque.exibeAtalhoDeReenvio());
        assertFalse(convidado.exibeAtalhoDeReenvio());
    }

    private static ApresentacaoDoResultadoDaPartida criarComEnvio(
            EstadoDoEnvioDoResultado envio, String erro, boolean logado, boolean modoToque) {
        return ApresentacaoDoResultadoDaPartida.criar(
                EstadoPartida.VITORIA, 100, List.of(), false,
                envio, erro, logado, modoToque);
    }
}
