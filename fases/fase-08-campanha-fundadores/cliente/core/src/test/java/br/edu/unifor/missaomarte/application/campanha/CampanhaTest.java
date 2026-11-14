package br.edu.unifor.missaomarte.application.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.ModoDeJogo;
import org.junit.jupiter.api.Test;

class CampanhaTest {
    @Test
    void comecaNaPlanicieUtopiaComAstronauta() {
        Campanha campanha = Campanha.nova();

        EtapaDaCampanha etapa = campanha.etapaAtual().orElseThrow();
        assertEquals(1, etapa.numero());
        assertEquals("Planície Utopia", etapa.local());
        assertEquals(Fundador.ASTRONAUTA, etapa.fundador());
        assertFalse(campanha.concluida());
        assertEquals(0, campanha.pontuacaoTotal());
    }

    @Test
    void todasAsEtapasUsamOModoDinamico() {
        for (int numero = 1; numero <= 3; numero++) {
            assertEquals(ModoDeJogo.DINAMICO, CatalogoDaCampanha.etapa(numero).modo());
        }
    }

    @Test
    void derrotaEAbandonoMantemAEtapaSemPontuar() {
        Campanha campanha = Campanha.nova();

        campanha.registrarResultado(1, EstadoPartida.DERROTA, 80);
        campanha.registrarResultado(1, EstadoPartida.ABORTADA, 90);

        assertEquals(1, campanha.etapaAtual().orElseThrow().numero());
        assertEquals(0, campanha.pontuacaoTotal());
    }

    @Test
    void tresVitoriasEmOrdemConcluemACampanhaESomamOsPontos() {
        Campanha campanha = Campanha.nova();

        campanha.registrarResultado(1, EstadoPartida.VITORIA, 30);
        campanha.registrarResultado(2, EstadoPartida.VITORIA, 45);
        campanha.registrarResultado(3, EstadoPartida.VITORIA, 60);

        assertTrue(campanha.concluida());
        assertTrue(campanha.etapaAtual().isEmpty());
        assertEquals(135, campanha.pontuacaoTotal());
    }

    @Test
    void resultadoRepetidoDeEtapaAnteriorNaoPulaEtapaNemTrocaPontuacao() {
        Campanha campanha = Campanha.nova();
        campanha.registrarResultado(1, EstadoPartida.VITORIA, 30);

        campanha.registrarResultado(1, EstadoPartida.VITORIA, 99);

        assertEquals(2, campanha.etapaAtual().orElseThrow().numero());
        assertEquals(30, campanha.pontuacaoTotal());
    }

    @Test
    void podeIniciarDiretoEmUmaEtapaParaDemonstracao() {
        Campanha campanha = Campanha.novaNaEtapa(3);

        assertEquals(3, campanha.etapaAtual().orElseThrow().numero());
        assertEquals(0, campanha.pontuacaoTotal());
        assertEquals(EstadoDaConstrucao.CONSTRUIDA, campanha.campus().estado(Construcao.BIBLIOTECA));
        assertEquals(EstadoDaConstrucao.PLANTA, campanha.campus().estado(Construcao.NAMI));
    }

    @Test
    void etapaDeDemonstracaoAceitaSomenteUmATres() {
        assertThrows(IllegalArgumentException.class, () -> Campanha.novaNaEtapa(0));
        assertThrows(IllegalArgumentException.class, () -> Campanha.novaNaEtapa(4));
    }

    @Test
    void novaCampanhaApagaPontuacaoEVoltaAEtapaUm() {
        Campanha campanha = Campanha.novaNaEtapa(3);
        campanha.registrarResultado(3, EstadoPartida.VITORIA, 70);

        campanha.reiniciar();

        assertEquals(1, campanha.etapaAtual().orElseThrow().numero());
        assertEquals(0, campanha.pontuacaoTotal());
        assertFalse(campanha.concluida());
    }
}
