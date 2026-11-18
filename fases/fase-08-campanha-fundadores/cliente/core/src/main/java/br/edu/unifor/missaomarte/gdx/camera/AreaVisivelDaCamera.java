package br.edu.unifor.missaomarte.gdx.camera;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Converte a area livre do layout virtual para a janela fisica usada pela camera do mundo. */
public final class AreaVisivelDaCamera {
    private AreaVisivelDaCamera() {
    }

    public static RetanguloDeTela converter(RetanguloDeTela areaVirtual,
            float larguraVirtual, float alturaVirtual,
            int xDaViewport, int yDaViewport, int larguraDaViewport, int alturaDaViewport) {
        if (larguraVirtual <= 0f || alturaVirtual <= 0f || larguraDaViewport <= 0 || alturaDaViewport <= 0) {
            throw new IllegalArgumentException("As dimensoes da area visivel precisam ser positivas.");
        }
        float escalaX = larguraDaViewport / larguraVirtual;
        float escalaY = alturaDaViewport / alturaVirtual;
        return new RetanguloDeTela(
                xDaViewport + areaVirtual.x() * escalaX,
                yDaViewport + areaVirtual.y() * escalaY,
                areaVirtual.largura() * escalaX,
                areaVirtual.altura() * escalaY);
    }
}
