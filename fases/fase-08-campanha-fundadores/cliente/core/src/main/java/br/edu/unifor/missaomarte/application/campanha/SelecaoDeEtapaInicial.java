package br.edu.unifor.missaomarte.application.campanha;

import java.util.OptionalInt;

public final class SelecaoDeEtapaInicial {
    private SelecaoDeEtapaInicial() {
    }

    public static OptionalInt interpretar(String valor) {
        if (valor == null) {
            return OptionalInt.empty();
        }
        try {
            int etapa = Integer.parseInt(valor.trim());
            return etapa >= 1 && etapa <= 3 ? OptionalInt.of(etapa) : OptionalInt.empty();
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
}
