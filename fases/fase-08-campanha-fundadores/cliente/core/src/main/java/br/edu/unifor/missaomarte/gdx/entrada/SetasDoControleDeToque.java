package br.edu.unifor.missaomarte.gdx.entrada;

import java.util.Optional;

/** Rotacao do icone de seta, cuja arte original aponta para a direita. */
public final class SetasDoControleDeToque {
    private SetasDoControleDeToque() {
    }

    public static Optional<Float> rotacao(String idDoBotao) {
        return switch (idDoBotao) {
            case ControleDeToque.CIMA -> Optional.of(90f);
            case ControleDeToque.ESQUERDA -> Optional.of(180f);
            case ControleDeToque.DIREITA -> Optional.of(0f);
            case ControleDeToque.BAIXO -> Optional.of(270f);
            default -> Optional.empty();
        };
    }
}
