package br.edu.unifor.missaomarte.gdx.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.campanha.Campanha;
import org.junit.jupiter.api.Test;

class ControleDoCampusTest {
    @Test
    void campanhaEmAndamentoIniciaMissaoSemAbrirConfirmacao() {
        ControleDoCampus controle = new ControleDoCampus();

        assertEquals(ControleDoCampus.AcaoPrincipal.INICIAR_MISSAO,
                controle.acionarPrincipal(Campanha.nova()));
        assertFalse(controle.confirmandoNovaCampanha());
    }

    @Test
    void campanhaConcluidaExigeConfirmacaoAntesDeApagarOProgresso() {
        Campanha campanha = campanhaConcluida();
        ControleDoCampus controle = new ControleDoCampus();

        assertEquals(ControleDoCampus.AcaoPrincipal.PEDIR_CONFIRMACAO,
                controle.acionarPrincipal(campanha));
        assertTrue(controle.confirmandoNovaCampanha());
        assertTrue(campanha.concluida());

        controle.confirmarNovaCampanha(campanha);

        assertFalse(controle.confirmandoNovaCampanha());
        assertEquals(1, campanha.etapaAtual().orElseThrow().numero());
    }

    @Test
    void cancelarConfirmacaoPreservaCampanhaConcluida() {
        Campanha campanha = campanhaConcluida();
        ControleDoCampus controle = new ControleDoCampus();
        controle.acionarPrincipal(campanha);

        controle.cancelarNovaCampanha();

        assertFalse(controle.confirmandoNovaCampanha());
        assertTrue(campanha.concluida());
    }

    private static Campanha campanhaConcluida() {
        Campanha campanha = Campanha.nova();
        campanha.registrarResultado(1, EstadoPartida.VITORIA, 10);
        campanha.registrarResultado(2, EstadoPartida.VITORIA, 20);
        campanha.registrarResultado(3, EstadoPartida.VITORIA, 30);
        return campanha;
    }
}
