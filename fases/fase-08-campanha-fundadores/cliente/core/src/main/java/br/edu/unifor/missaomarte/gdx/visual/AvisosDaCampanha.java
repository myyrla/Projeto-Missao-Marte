package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.application.EventoJogo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ParadaDoRover;

/** Traduz eventos ja decididos pela aplicacao em avisos breves da interface. */
public final class AvisosDaCampanha {
    private AvisosDaCampanha() {
    }

    public static String texto(EventoJogo evento, Missao missao) {
        if (evento == EventoJogo.NAMI_ATIVADA) {
            return "+1 casco";
        }
        if (evento != EventoJogo.ROVER_UTILIZADO) {
            return null;
        }
        return missao.getParadasDoRover().stream()
                .filter(parada -> parada.getPosicao().equals(missao.getNave().getPosicao()))
                .map(ParadaDoRover::getPar)
                .findFirst()
                .map(par -> "Rover: parada " + par)
                .orElse("Rover utilizado");
    }
}
