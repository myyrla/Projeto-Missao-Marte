package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.gdx.MapaGeometria;
import java.util.function.Predicate;

/** Calcula a opacidade da tempestade no espaço da tela sem depender de OpenGL. */
public final class MascaraDaTempestade {
    private MascaraDaTempestade() {
    }

    public static Campo calcular(MapaGeometria mapa, Predicate<Posicao> estaVisivel,
            int minimo, int maximo, float alfaMaxima) {
        float esquerda = Float.POSITIVE_INFINITY;
        float direita = Float.NEGATIVE_INFINITY;
        float base = Float.POSITIVE_INFINITY;
        float topo = Float.NEGATIVE_INFINITY;
        float meiaCelula = mapa.celula() / 2f;

        for (int y = minimo; y <= maximo; y++) {
            for (int x = minimo; x <= maximo; x++) {
                if (!estaVisivel.test(new Posicao(x, y))) {
                    continue;
                }
                esquerda = Math.min(esquerda, mapa.xCentro(x) - meiaCelula);
                direita = Math.max(direita, mapa.xCentro(x) + meiaCelula);
                base = Math.min(base, mapa.yCentro(y) - meiaCelula);
                topo = Math.max(topo, mapa.yCentro(y) + meiaCelula);
            }
        }

        if (esquerda == Float.POSITIVE_INFINITY) {
            return Campo.total(alfaMaxima);
        }
        return new Campo(esquerda, direita, base, topo, mapa.celula(), alfaMaxima, false);
    }

    public static final class Campo {
        private final float esquerda;
        private final float direita;
        private final float base;
        private final float topo;
        private final float larguraDaTransicao;
        private final float alfaMaxima;
        private final boolean totalmenteEscuro;

        private Campo(float esquerda, float direita, float base, float topo,
                float larguraDaTransicao, float alfaMaxima, boolean totalmenteEscuro) {
            this.esquerda = esquerda;
            this.direita = direita;
            this.base = base;
            this.topo = topo;
            this.larguraDaTransicao = larguraDaTransicao;
            this.alfaMaxima = alfaMaxima;
            this.totalmenteEscuro = totalmenteEscuro;
        }

        private static Campo total(float alfaMaxima) {
            return new Campo(0f, 0f, 0f, 0f, 1f, alfaMaxima, true);
        }

        public float alfa(float x, float y) {
            if (totalmenteEscuro) {
                return alfaMaxima;
            }
            float distanciaX = Math.max(Math.max(esquerda - x, 0f), x - direita);
            float distanciaY = Math.max(Math.max(base - y, 0f), y - topo);
            float distancia = (float) Math.sqrt(distanciaX * distanciaX + distanciaY * distanciaY);
            float proporcao = Math.min(1f, distancia / larguraDaTransicao);
            float suave = proporcao * proporcao * (3f - 2f * proporcao);
            return alfaMaxima * suave;
        }
    }
}
