package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.EstadoPartida;

/** Decide o destino de um resultado sem acoplar a decisao aos adaptadores local e HTTP. */
final class PoliticaDeRegistroNoRanking {
    enum Destino { NENHUM, LOCAL, GLOBAL }

    private PoliticaDeRegistroNoRanking() {
    }

    static Destino destino(boolean campanha, boolean logado, EstadoPartida estado) {
        if (campanha || estado == EstadoPartida.ABORTADA || estado == EstadoPartida.EM_ANDAMENTO) {
            return Destino.NENHUM;
        }
        if (logado && (estado == EstadoPartida.VITORIA || estado == EstadoPartida.DERROTA)) {
            return Destino.GLOBAL;
        }
        return estado == EstadoPartida.VITORIA ? Destino.LOCAL : Destino.NENHUM;
    }
}
