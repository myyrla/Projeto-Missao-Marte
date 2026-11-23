package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import java.util.List;

/** Conteúdo textual puro apresentado ao fim da partida. */
public record ApresentacaoDoResultadoDaPartida(boolean vitoria, String titulo, String motivo,
                                               String pontos, String rotuloDeSaida,
                                               EstadoDoEnvioDoResultado envio, String textoDoEnvio,
                                               boolean exibeAtalhoDeReenvio) {
    public static ApresentacaoDoResultadoDaPartida criar(EstadoPartida estado, int pontos,
            List<String> mensagens, boolean campanha, EstadoDoEnvioDoResultado envio,
            String erroDeEnvio, boolean logado, boolean modoToque) {
        boolean vitoria = estado == EstadoPartida.VITORIA;
        String titulo = vitoria ? "MISSÃO CUMPRIDA" : "MISSÃO FALHOU";
        String motivoPadrao = vitoria
                ? "Todos os passageiros retornaram à base."
                : "A missão foi encerrada.";
        String motivo = mensagens.isEmpty() ? motivoPadrao : String.join(" ", mensagens);
        String pontosFormatados = String.format("%,d", pontos).replace(',', '.');
        String textoDoEnvio = textoDoEnvio(envio, erroDeEnvio);
        boolean exibeAtalho = envio == EstadoDoEnvioDoResultado.FALHOU && logado && !modoToque;
        return new ApresentacaoDoResultadoDaPartida(vitoria, titulo, motivo, pontosFormatados,
                campanha ? "Campus" : "Menu", envio, textoDoEnvio, exibeAtalho);
    }

    private static String textoDoEnvio(EstadoDoEnvioDoResultado envio, String erroDeEnvio) {
        return switch (envio) {
            case ENVIANDO -> "Enviando o resultado ao servidor...";
            case ENVIADO -> "Resultado enviado ao servidor.";
            case FALHOU -> erroDeEnvio.length() > 52
                    ? erroDeEnvio.substring(0, 49) + "..."
                    : erroDeEnvio;
            case NAO_ENVIA -> "";
        };
    }
}
