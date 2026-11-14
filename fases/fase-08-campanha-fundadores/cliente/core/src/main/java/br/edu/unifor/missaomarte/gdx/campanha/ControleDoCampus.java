package br.edu.unifor.missaomarte.gdx.campanha;

import br.edu.unifor.missaomarte.application.campanha.Campanha;

public final class ControleDoCampus {
    public enum AcaoPrincipal { INICIAR_MISSAO, PEDIR_CONFIRMACAO }

    private boolean confirmandoNovaCampanha;

    public AcaoPrincipal acionarPrincipal(Campanha campanha) {
        if (campanha.concluida()) {
            confirmandoNovaCampanha = true;
            return AcaoPrincipal.PEDIR_CONFIRMACAO;
        }
        return AcaoPrincipal.INICIAR_MISSAO;
    }

    public boolean confirmandoNovaCampanha() {
        return confirmandoNovaCampanha;
    }

    public void confirmarNovaCampanha(Campanha campanha) {
        if (confirmandoNovaCampanha) {
            campanha.reiniciar();
            confirmandoNovaCampanha = false;
        }
    }

    public void cancelarNovaCampanha() {
        confirmandoNovaCampanha = false;
    }
}
