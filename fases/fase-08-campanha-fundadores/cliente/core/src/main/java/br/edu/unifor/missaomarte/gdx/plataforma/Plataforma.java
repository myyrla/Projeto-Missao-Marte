package br.edu.unifor.missaomarte.gdx.plataforma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.OnscreenKeyboardType;
import com.badlogic.gdx.Input.TextInputListener;
import java.util.function.Consumer;

/**
 * O que muda entre desktop e celular, num lugar so: se o modo toque esta ligado e se existe digitacao nativa.
 * <p>No celular nao ha teclado fisico, entao a tela abre a caixa de texto do sistema ({@code Gdx.input.getTextInput}).
 * No desktop, mesmo no modo toque (mouse no lugar do dedo), o teclado fisico continua valendo.
 */
public final class Plataforma {
    public enum TipoDeTexto { NORMAL, SENHA, NUMERO }

    private final boolean toque;
    private final boolean textoNativo;
    private final boolean usaDensidadeDaTela;
    private final float fatorDeTextoForcado;

    public Plataforma(boolean toque, boolean textoNativo) {
        this(toque, textoNativo, false, 0f);
    }

    private Plataforma(boolean toque, boolean textoNativo, boolean usaDensidadeDaTela,
            float fatorDeTextoForcado) {
        this.toque = toque;
        this.textoNativo = textoNativo;
        this.usaDensidadeDaTela = usaDensidadeDaTela;
        this.fatorDeTextoForcado = fatorDeTextoForcado;
    }

    /** Desktop com teclado e mouse (comportamento das fases anteriores). */
    public static Plataforma desktop() {
        return desktop(false);
    }

    /** Desktop opcionalmente simulando controles por toque, sem aplicar densidade fisica. */
    public static Plataforma desktop(boolean toque) {
        return new Plataforma(toque, false, false, 0f);
    }

    /** Desktop com uma escala tipografica explicita, usada para reproduzir telas densas durante o desenvolvimento. */
    public static Plataforma desktop(boolean toque, float fatorDeTexto) {
        if (!Float.isFinite(fatorDeTexto) || fatorDeTexto <= 0f) {
            throw new IllegalArgumentException("O fator de texto precisa ser positivo.");
        }
        return new Plataforma(toque, false, false, fatorDeTexto);
    }

    /** Android com toque, teclado nativo e escala pela densidade fisica da tela. */
    public static Plataforma android() {
        return new Plataforma(true, true, true, 0f);
    }

    public boolean usaToque() {
        return toque;
    }

    public boolean temTextoNativo() {
        return textoNativo;
    }

    public boolean usaDensidadeDaTela() {
        return usaDensidadeDaTela;
    }

    public float fatorDeTextoForcado() {
        return fatorDeTextoForcado;
    }

    /** Abre a caixa de texto do sistema; se o jogador cancelar, nada e devolvido. */
    public void pedirTexto(String titulo, String atual, TipoDeTexto tipo, Consumer<String> aoConfirmar) {
        if (!textoNativo) {
            return;
        }
        OnscreenKeyboardType teclado = tipo == TipoDeTexto.SENHA ? OnscreenKeyboardType.Password
                : tipo == TipoDeTexto.NUMERO ? OnscreenKeyboardType.NumberPad : OnscreenKeyboardType.Default;
        Gdx.input.getTextInput(new TextInputListener() {
            @Override
            public void input(String texto) {
                // A caixa de texto responde fora da thread do jogo; a tela so pode ser alterada na thread principal.
                Gdx.app.postRunnable(() -> aoConfirmar.accept(texto));
            }

            @Override
            public void canceled() {
            }
        }, titulo, atual, "", teclado);
    }
}
