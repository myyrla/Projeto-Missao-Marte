package br.edu.unifor.missaomarte.gdx.ui;

import br.edu.unifor.missaomarte.domain.Dificuldade;

/** Formata valores técnicos para exibição sem alterar enums, DTOs ou o protocolo do servidor. */
public final class RotulosDaInterface {
    private RotulosDaInterface() {
    }

    public static String dificuldade(Dificuldade dificuldade) {
        return dificuldade(dificuldade.name());
    }

    public static String dificuldade(String dificuldade) {
        switch (dificuldade) {
            case "FACIL":
                return "FÁCIL";
            case "MEDIO":
                return "MÉDIO";
            case "DIFICIL":
                return "DIFÍCIL";
            default:
                return dificuldade;
        }
    }
}
