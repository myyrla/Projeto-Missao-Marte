package br.edu.unifor.missaomarte.gdx.visual;

/** Controla a dica visual da NAMI durante uma unica partida. */
public final class DicaDaNami {
    private static final String TEXTO = "NAMI: passe por aqui para reparar o casco";
    private boolean mostrada;

    public String proxima(boolean temNami) {
        if (!temNami || mostrada) {
            return null;
        }
        mostrada = true;
        return TEXTO;
    }
}
