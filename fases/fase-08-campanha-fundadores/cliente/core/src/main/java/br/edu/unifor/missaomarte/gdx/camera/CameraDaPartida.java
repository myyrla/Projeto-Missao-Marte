package br.edu.unifor.missaomarte.gdx.camera;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Centro suavizado e limitado da câmera de mundo. Objeto de estado sem dependência do libGDX. */
public final class CameraDaPartida {
    private static final float SUAVIZACAO = 0.12f;

    private float x;
    private float y;
    private boolean inicializada;

    public Centro atualizar(float larguraDoMapa, float alturaDoMapa, float larguraVisivel, float alturaVisivel,
            float alvoX, float alvoY) {
        return atualizar(larguraDoMapa, alturaDoMapa, larguraVisivel, alturaVisivel,
                new RetanguloDeTela(0f, 0f, larguraVisivel, alturaVisivel), alvoX, alvoY);
    }

    public Centro atualizar(float larguraDoMapa, float alturaDoMapa, float larguraDaJanela, float alturaDaJanela,
            RetanguloDeTela areaUtil, float alvoX, float alvoY) {
        float desejadoX = limitar(alvoX, larguraDoMapa, larguraDaJanela, areaUtil.x(), areaUtil.largura());
        float desejadoY = limitar(alvoY, alturaDoMapa, alturaDaJanela, areaUtil.y(), areaUtil.altura());
        if (!inicializada) {
            x = desejadoX;
            y = desejadoY;
            inicializada = true;
        } else {
            x += (desejadoX - x) * SUAVIZACAO;
            y += (desejadoY - y) * SUAVIZACAO;
        }
        return new Centro(x, y);
    }

    private static float limitar(float alvo, float tamanhoDoMapa, float tamanhoDaJanela,
            float inicioUtil, float tamanhoUtil) {
        float centroUtil = inicioUtil + tamanhoUtil / 2f;
        float deslocamento = tamanhoDaJanela / 2f - centroUtil;
        if (tamanhoDoMapa <= tamanhoUtil) {
            return tamanhoDoMapa / 2f + deslocamento;
        }
        float centroNoInicio = tamanhoDaJanela / 2f - inicioUtil;
        float centroNoFim = tamanhoDoMapa + tamanhoDaJanela / 2f - inicioUtil - tamanhoUtil;
        return Math.max(centroNoInicio, Math.min(centroNoFim, alvo + deslocamento));
    }

    public record Centro(float x, float y) {
    }
}
