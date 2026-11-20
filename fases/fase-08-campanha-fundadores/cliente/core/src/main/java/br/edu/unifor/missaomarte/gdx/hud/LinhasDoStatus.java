package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.ModoDeJogo;
import java.util.ArrayList;
import java.util.List;

/** Monta as informacoes independentes do painel de status, sem estado de renderizacao. */
public final class LinhasDoStatus {
    private LinhasDoStatus() {
    }

    public static List<String> para(ModoDeJogo modo, int municao, Acao mira, int movimentos, long segundos,
            Integer sol, String local, boolean mudo) {
        List<String> linhas = new ArrayList<>();
        if (modo == ModoDeJogo.DINAMICO) {
            linhas.add("MUNIÇÃO " + municao);
            linhas.add("MIRA " + nomeDaMira(mira));
        } else {
            linhas.add("MOVIMENTOS " + movimentos);
        }
        linhas.add(String.format("TEMPO %02d:%02d", segundos / 60, segundos % 60));
        if (sol != null && local != null) {
            linhas.add("SOL " + sol);
            linhas.add(local.toUpperCase());
        }
        if (mudo) {
            linhas.add("SOM DESLIGADO");
        }
        return List.copyOf(linhas);
    }

    private static String nomeDaMira(Acao mira) {
        if (mira == Acao.CIMA) return "CIMA";
        if (mira == Acao.BAIXO) return "BAIXO";
        if (mira == Acao.ESQUERDA) return "ESQUERDA";
        return "DIREITA";
    }
}
