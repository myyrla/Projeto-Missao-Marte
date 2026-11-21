package br.edu.unifor.missaomarte.gdx.hud;

/** Decide se a interface pode mostrar instrucoes para teclado fisico. */
public final class PoliticaDeAtalhos {
    private PoliticaDeAtalhos() {
    }

    public static boolean exibeRodapeDeTeclado(boolean modoToque) {
        return !modoToque;
    }
}
