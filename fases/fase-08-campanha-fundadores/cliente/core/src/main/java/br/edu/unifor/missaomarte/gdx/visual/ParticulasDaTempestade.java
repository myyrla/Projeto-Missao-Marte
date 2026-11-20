package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.ArrayList;
import java.util.List;

/** Gera riscos de poeira determinísticos e limitados à área do mapa. */
public final class ParticulasDaTempestade {
    private static final int QUANTIDADE_MINIMA = 20;
    private static final int QUANTIDADE_MAXIMA = 48;
    private static final float AREA_POR_PARTICULA = 18_000f;

    private ParticulasDaTempestade() {
    }

    public static List<Particula> calcular(RetanguloDeTela area, float tempo) {
        int quantidade = Math.min(QUANTIDADE_MAXIMA, Math.max(QUANTIDADE_MINIMA,
                Math.round(area.largura() * area.altura() / AREA_POR_PARTICULA)));
        List<Particula> particulas = new ArrayList<>(quantidade);
        for (int indice = 0; indice < quantidade; indice++) {
            float comprimento = 8f + indice % 5 * 3f;
            float subida = comprimento * 0.28f;
            float larguraUtil = Math.max(1f, area.largura() - comprimento);
            float alturaUtil = Math.max(1f, area.altura() - subida);
            float velocidade = 52f + indice % 7 * 9f;
            float sementeX = fracao((indice + 1) * 0.61803398875f);
            float sementeY = fracao((indice + 1) * 0.41421356237f + 0.17f);
            float x = area.x() + modulo(sementeX * larguraUtil + tempo * velocidade, larguraUtil);
            float y = area.y() + modulo(sementeY * alturaUtil - tempo * velocidade * 0.22f, alturaUtil);
            float alfa = 0.12f + indice % 4 * 0.03f;
            particulas.add(new Particula(x, y, x + comprimento, y + subida, alfa));
        }
        return List.copyOf(particulas);
    }

    private static float fracao(float valor) {
        return valor - (float) Math.floor(valor);
    }

    private static float modulo(float valor, float divisor) {
        float resultado = valor % divisor;
        return resultado < 0f ? resultado + divisor : resultado;
    }

    public record Particula(float inicioX, float inicioY, float fimX, float fimY, float alfa) {
    }
}
