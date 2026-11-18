package br.edu.unifor.missaomarte.gdx.camera;

/** Centro suavizado e limitado da câmera de mundo. Objeto de estado sem dependência do libGDX. */
public final class CameraDaPartida {
    private static final float SUAVIZACAO = 0.12f;

    private float x;
    private float y;
    private boolean inicializada;

    public Centro atualizar(float larguraDoMapa, float alturaDoMapa, float larguraVisivel, float alturaVisivel,
            float alvoX, float alvoY) {
        float desejadoX = limitar(alvoX, larguraDoMapa, larguraVisivel);
        float desejadoY = limitar(alvoY, alturaDoMapa, alturaVisivel);
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

    private static float limitar(float alvo, float tamanhoDoMapa, float tamanhoVisivel) {
        if (tamanhoDoMapa <= tamanhoVisivel) {
            return tamanhoDoMapa / 2f;
        }
        float metadeVisivel = tamanhoVisivel / 2f;
        return Math.max(metadeVisivel, Math.min(tamanhoDoMapa - metadeVisivel, alvo));
    }

    public record Centro(float x, float y) {
    }
}
