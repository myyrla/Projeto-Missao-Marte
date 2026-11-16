package br.edu.unifor.missaomarte.gdx.layout;

import java.util.Objects;
import java.util.function.ToDoubleFunction;

/** Ajustes de texto guiados pela largura real fornecida pelo renderizador. */
public final class TextoResponsivo {
    private static final String RETICENCIAS = "...";

    private TextoResponsivo() {
    }

    public static String limitarComReticencias(String texto, float larguraMaxima, ToDoubleFunction<String> medir) {
        Objects.requireNonNull(texto, "texto");
        Objects.requireNonNull(medir, "medir");
        if (larguraMaxima <= 0f) {
            return "";
        }
        if (medir.applyAsDouble(texto) <= larguraMaxima) {
            return texto;
        }
        if (medir.applyAsDouble(RETICENCIAS) > larguraMaxima) {
            return "";
        }
        int fim = texto.length();
        while (fim > 0 && medir.applyAsDouble(texto.substring(0, fim).stripTrailing() + RETICENCIAS) > larguraMaxima) {
            fim--;
        }
        return texto.substring(0, fim).stripTrailing() + RETICENCIAS;
    }
}
