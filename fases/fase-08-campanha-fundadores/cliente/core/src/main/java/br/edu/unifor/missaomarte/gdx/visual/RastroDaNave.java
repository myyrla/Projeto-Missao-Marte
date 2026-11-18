package br.edu.unifor.missaomarte.gdx.visual;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Historico visual das ultimas casas ocupadas pela nave, sem alterar a missao. */
public final class RastroDaNave {
    private static final int LIMITE = 12;

    private final Deque<Casa> casas = new ArrayDeque<>();

    public void registrar(int x, int y) {
        Casa atual = new Casa(x, y);
        if (atual.equals(casas.peekLast())) {
            return;
        }
        casas.addLast(atual);
        if (casas.size() > LIMITE) {
            casas.removeFirst();
        }
    }

    public List<Casa> casas() {
        return List.copyOf(new ArrayList<>(casas));
    }

    public record Casa(int x, int y) {
    }
}
