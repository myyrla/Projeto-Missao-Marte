package br.edu.unifor.missaomarte.gdx.plataforma;

/** Orientacao do Android escolhida pela menor largura declarada pelo aparelho. */
public enum OrientacaoPorTamanhoDeTela {
    RETRATO,
    PAISAGEM;

    public static final int MENOR_LARGURA_DE_TABLET_DP = 600;

    public static OrientacaoPorTamanhoDeTela paraMenorLarguraDp(int menorLarguraDp) {
        return eTablet(menorLarguraDp) ? PAISAGEM : RETRATO;
    }

    /** Fonte de verdade unica para o criterio "é tablet" (smallestScreenWidthDp >= 600). */
    public static boolean eTablet(int menorLarguraDp) {
        return menorLarguraDp >= MENOR_LARGURA_DE_TABLET_DP;
    }
}
