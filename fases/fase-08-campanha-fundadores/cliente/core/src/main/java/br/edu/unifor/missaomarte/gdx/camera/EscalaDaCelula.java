package br.edu.unifor.missaomarte.gdx.camera;

/** Calcula a escala visual do grid sem alterar o tamanho logico do mapa no dominio. */
public final class EscalaDaCelula {
    public static final float MINIMA = 48f;
    public static final float MAXIMA = 72f;

    private EscalaDaCelula() {
    }

    public static float calcular(float alturaDaJanela, int linhas) {
        if (alturaDaJanela <= 0f || linhas <= 0) {
            throw new IllegalArgumentException("A altura da janela e as linhas precisam ser positivas.");
        }
        float desejada = alturaDaJanela / linhas;
        return Math.max(MINIMA, Math.min(MAXIMA, desejada));
    }

    /**
     * Escala o limite maximo pela densidade da tela, para que o mapa ocupe a mesma fracao
     * da tela em dp que ocupa no desktop em pixels. No desktop (densidade 1) o resultado e
     * identico ao de {@link #calcular(float, int)}.
     */
    public static float calcularComDensidade(float alturaDaJanela, int linhas, float densidade) {
        if (alturaDaJanela <= 0f || linhas <= 0 || densidade <= 0f) {
            throw new IllegalArgumentException(
                    "A altura da janela, as linhas e a densidade precisam ser positivas.");
        }
        float maxima = MAXIMA * Math.max(1f, densidade);
        float desejada = alturaDaJanela / linhas;
        return Math.max(MINIMA, Math.min(maxima, desejada));
    }

    /** No toque em retrato, o mapa usa toda a largura livre e a camera resolve somente a falta de altura. */
    public static float calcularNoToqueVertical(float larguraUtil, int colunas) {
        if (larguraUtil <= 0f || colunas <= 0) {
            throw new IllegalArgumentException("A largura util e as colunas precisam ser positivas.");
        }
        return larguraUtil / colunas;
    }
}
