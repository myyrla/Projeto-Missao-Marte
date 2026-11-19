package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.application.ModoDeJogo;
import java.util.List;

/** Define os atalhos exibidos no rodape horizontal sem depender da renderizacao. */
final class AtalhosDoRodape {
    private AtalhosDoRodape() {
    }

    static List<Atalho> para(ModoDeJogo modo) {
        if (modo == ModoDeJogo.DINAMICO) {
            return List.of(
                    new Atalho("WASD", "Mover"),
                    new Atalho("F", "Atirar"),
                    new Atalho("P", "Pausa"),
                    new Atalho("M", "Som"),
                    new Atalho("Esc", "Abortar"));
        }
        return List.of(
                new Atalho("WASD", "Mover"),
                new Atalho("E", "Embarcar"),
                new Atalho("M", "Som"),
                new Atalho("Esc", "Abortar"));
    }

    record Atalho(String tecla, String acao) {
        String texto() {
            return tecla + " " + acao;
        }
    }
}
