package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.application.EventoJogo;

/** Traduz eventos ja decididos pela aplicacao em avisos breves da interface. */
public final class AvisosDaCampanha {
    private AvisosDaCampanha() {
    }

    public static String texto(EventoJogo evento) {
        return evento == EventoJogo.NAMI_ATIVADA ? "+1 casco" : null;
    }
}
