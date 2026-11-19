package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.EstacaoNami;

/** Projeta o estado da NAMI em propriedades visuais, sem alterar a estacao. */
public final class AparenciaDaNami {
    private static final float OPACIDADE_USADA = 0.30f;

    private AparenciaDaNami() {
    }

    public static float opacidade(EstacaoNami nami) {
        return nami.isUsada() ? OPACIDADE_USADA : 1f;
    }
}
