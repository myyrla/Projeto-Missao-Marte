package br.edu.unifor.missaomarte.gdx.camera;

/** Calcula a escala visual do grid sem alterar o tamanho logico do mapa no dominio. */
public final class EscalaDaCelula {
    public static final float MINIMA = 64f;
    public static final float MAXIMA = 96f;

    private EscalaDaCelula() {
    }

    public static float calcular(float larguraVisivel, float alturaVisivel, int colunas, int linhas) {
        if (larguraVisivel <= 0f || alturaVisivel <= 0f || colunas <= 0 || linhas <= 0) {
            throw new IllegalArgumentException("A área visível e o grid precisam ser positivos.");
        }
        float desejada = Math.max(larguraVisivel / colunas, alturaVisivel / linhas);
        return Math.max(MINIMA, Math.min(MAXIMA, desejada));
    }
}
