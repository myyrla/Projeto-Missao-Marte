package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Projeta no radar a parte do mapa contida na janela da câmera de mundo. */
public final class JanelaDoRadar {
    private JanelaDoRadar() {
    }

    public static RetanguloDeTela projetar(RetanguloDeTela radar, float larguraDoMapa, float alturaDoMapa,
            float centroX, float centroY, float larguraVisivel, float alturaVisivel) {
        float esquerda = Math.max(0f, centroX - larguraVisivel / 2f);
        float direita = Math.min(larguraDoMapa, centroX + larguraVisivel / 2f);
        float base = Math.max(0f, centroY - alturaVisivel / 2f);
        float topo = Math.min(alturaDoMapa, centroY + alturaVisivel / 2f);
        return new RetanguloDeTela(
                radar.x() + esquerda / larguraDoMapa * radar.largura(),
                radar.y() + base / alturaDoMapa * radar.altura(),
                Math.max(0f, direita - esquerda) / larguraDoMapa * radar.largura(),
                Math.max(0f, topo - base) / alturaDoMapa * radar.altura());
    }
}
