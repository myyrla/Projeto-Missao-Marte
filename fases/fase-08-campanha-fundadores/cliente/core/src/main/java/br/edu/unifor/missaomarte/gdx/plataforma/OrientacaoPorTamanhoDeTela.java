package br.edu.unifor.missaomarte.gdx.plataforma;

/** Orientacao do Android escolhida pela menor largura declarada pelo aparelho. */
public enum OrientacaoPorTamanhoDeTela {
    RETRATO,
    PAISAGEM;

    private static final int MENOR_LARGURA_DE_TABLET_DP = 600;

    public static OrientacaoPorTamanhoDeTela paraMenorLarguraDp(int menorLarguraDp) {
        return menorLarguraDp >= MENOR_LARGURA_DE_TABLET_DP ? PAISAGEM : RETRATO;
    }
}
