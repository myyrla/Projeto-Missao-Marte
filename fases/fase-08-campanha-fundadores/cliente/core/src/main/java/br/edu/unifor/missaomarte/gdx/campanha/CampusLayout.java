package br.edu.unifor.missaomarte.gdx.campanha;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

public final class CampusLayout {
    private final RetanguloDeTela mapa;

    private CampusLayout(RetanguloDeTela mapa) {
        this.mapa = mapa;
    }

    public static CampusLayout padrao() {
        float alturaDoMapa = 400f;
        float larguraDoMapa = alturaDoMapa * 1056f / 864f;
        RetanguloDeTela mapa = new RetanguloDeTela(284f - larguraDoMapa / 2f, 130f, larguraDoMapa, alturaDoMapa);
        return new CampusLayout(mapa);
    }

    public RetanguloDeTela mapa() {
        return mapa;
    }
}
