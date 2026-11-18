package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import org.junit.jupiter.api.Test;

class PoliticaDeRegistroNoRankingTest {
    @Test
    void campanhaNuncaVaiParaRankingLocalNemGlobal() {
        for (EstadoPartida estado : EstadoPartida.values()) {
            assertEquals(PoliticaDeRegistroNoRanking.Destino.NENHUM,
                    PoliticaDeRegistroNoRanking.destino(true, false, estado));
            assertEquals(PoliticaDeRegistroNoRanking.Destino.NENHUM,
                    PoliticaDeRegistroNoRanking.destino(true, true, estado));
        }
    }

    @Test
    void partidaRapidaPreservaOsDestinosAnteriores() {
        assertEquals(PoliticaDeRegistroNoRanking.Destino.LOCAL,
                PoliticaDeRegistroNoRanking.destino(false, false, EstadoPartida.VITORIA));
        assertEquals(PoliticaDeRegistroNoRanking.Destino.GLOBAL,
                PoliticaDeRegistroNoRanking.destino(false, true, EstadoPartida.VITORIA));
        assertEquals(PoliticaDeRegistroNoRanking.Destino.NENHUM,
                PoliticaDeRegistroNoRanking.destino(false, false, EstadoPartida.DERROTA));
        assertEquals(PoliticaDeRegistroNoRanking.Destino.GLOBAL,
                PoliticaDeRegistroNoRanking.destino(false, true, EstadoPartida.DERROTA));
    }
}
