package br.edu.unifor.missaomarte.gdx.layout;

import br.edu.unifor.missaomarte.gdx.plataforma.OrientacaoPorTamanhoDeTela;
import br.edu.unifor.missaomarte.gdx.plataforma.Plataforma;

/**
 * Converte medidas fisicas (dp) em unidades do mundo e decide a escala do texto.
 * <p>Uma unidade do mundo vale {@code pixelsPorUnidade} pixels da tela; um dp vale {@code densidade} pixels
 * (densidade 1 = 160 dpi, como no Android). Sem libGDX, para poder ser testada.
 */
public final class EscalaDeTela {
    /** Tamanho minimo recomendado para um alvo de toque, em dp. */
    public static final float ALVO_MINIMO_DP = 48f;
    /** Altura do texto padrao do jogo, em unidades do mundo (a fonte padrao do libGDX tem 15 px). */
    public static final float ALTURA_DO_TEXTO = 15f;
    /** Tamanho minimo do texto, em dp. */
    public static final float TEXTO_MINIMO_DP = 12f;
    public static final float FATOR_MAXIMO_DE_TEXTO = 1.6f;

    /**
     * Fator de texto para tablets. No notebook a 110 PPI, 15 unidades x 1.33 ppu = 20 px = 4,6 mm.
     * No tablet a ~300 PPI (2560x1600 densidade 2), 15 un. x 2.67 ppu = 40 px = 3,4 mm.
     * Com fator 1.3: 40 x 1.3 = 52 px = 4,4 mm, igualando o tamanho fisico do notebook.
     */
    public static final float FATOR_DE_TEXTO_PARA_TABLET = 1.3f;

    private EscalaDeTela() {
    }

    /** Menor lado de um alvo de toque, em unidades do mundo. */
    public static float alvoMinimoNoMundo(float densidade, float pixelsPorUnidade) {
        if (pixelsPorUnidade <= 0f || densidade <= 0f) {
            return 0f;
        }
        return ALVO_MINIMO_DP * densidade / pixelsPorUnidade;
    }

    /** Multiplicador do texto para que ele nunca fique menor que o tamanho minimo em dp (entre 1 e o maximo). */
    public static float fatorDeTexto(float densidade, float pixelsPorUnidade) {
        if (pixelsPorUnidade <= 0f || densidade <= 0f) {
            return 1f;
        }
        float necessario = TEXTO_MINIMO_DP * densidade / (ALTURA_DO_TEXTO * pixelsPorUnidade);
        return Math.max(1f, Math.min(FATOR_MAXIMO_DE_TEXTO, necessario));
    }

    /** No desktop a escala tipografica permanece exatamente 1, inclusive na simulacao de toque. */
    public static float fatorDeTexto(Plataforma plataforma, float densidade, float pixelsPorUnidade) {
        return plataforma.usaDensidadeDaTela()
                ? fatorDeTexto(densidade, pixelsPorUnidade) : 1f;
    }

    /**
     * Variante que aplica o piso de tablet quando {@code menorLarguraDp} atinge o limiar
     * de {@link OrientacaoPorTamanhoDeTela#MENOR_LARGURA_DE_TABLET_DP}.
     */
    public static float fatorDeTexto(Plataforma plataforma, float densidade,
            float pixelsPorUnidade, int menorLarguraDp) {
        float fator = fatorDeTexto(plataforma, densidade, pixelsPorUnidade);
        if (plataforma.usaDensidadeDaTela()
                && OrientacaoPorTamanhoDeTela.eTablet(menorLarguraDp)) {
            fator = Math.max(fator, FATOR_DE_TEXTO_PARA_TABLET);
        }
        return Math.min(FATOR_MAXIMO_DE_TEXTO, fator);
    }

    public static float pixelsParaMundo(float pixels, float pixelsPorUnidade) {
        return pixelsPorUnidade <= 0f ? 0f : pixels / pixelsPorUnidade;
    }
}
