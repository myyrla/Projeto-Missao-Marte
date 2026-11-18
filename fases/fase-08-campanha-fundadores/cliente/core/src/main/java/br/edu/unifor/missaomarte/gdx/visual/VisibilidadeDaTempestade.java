package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Projeta a visibilidade fornecida pela aplicacao sem duplicar o relogio ou o raio da tempestade. */
public final class VisibilidadeDaTempestade {
    private VisibilidadeDaTempestade() {
    }

    public static boolean estaAtiva(Predicate<Posicao> estaVisivel, int minimo, int maximo) {
        return !estaVisivel.test(new Posicao(minimo, minimo))
                || !estaVisivel.test(new Posicao(minimo, maximo))
                || !estaVisivel.test(new Posicao(maximo, minimo))
                || !estaVisivel.test(new Posicao(maximo, maximo));
    }

    public static <T extends ObjetoRenderizavel> List<T> filtrar(
            List<T> objetos, Predicate<Posicao> estaVisivel) {
        List<T> visiveis = new ArrayList<>();
        for (T objeto : objetos) {
            if (estaVisivel.test(objeto.getPosicao())) {
                visiveis.add(objeto);
            }
        }
        return List.copyOf(visiveis);
    }
}
