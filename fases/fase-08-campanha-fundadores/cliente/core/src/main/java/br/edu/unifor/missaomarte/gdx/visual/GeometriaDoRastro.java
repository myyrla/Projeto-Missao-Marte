package br.edu.unifor.missaomarte.gdx.visual;

import java.util.ArrayList;
import java.util.List;

/** Amostra o caminho entre centros das casas e calcula o desvanecimento do rastro. */
public final class GeometriaDoRastro {
    private static final float ALFA_ANTIGO = 0.15f;
    private static final float ALFA_NOVO = 0.70f;

    private GeometriaDoRastro() {
    }

    public static List<Ponto> calcular(List<RastroDaNave.Casa> casas, int minimo, int maximo,
            float celula, float origemX, float origemY, float espacamento) {
        if (casas.isEmpty()) {
            return List.of();
        }
        if (celula <= 0f || espacamento <= 0f) {
            throw new IllegalArgumentException("Celula e espacamento precisam ser positivos.");
        }
        List<Coordenada> amostras = new ArrayList<>();
        Coordenada anterior = centro(casas.get(0), minimo, maximo, celula, origemX, origemY);
        amostras.add(anterior);
        for (int i = 1; i < casas.size(); i++) {
            Coordenada proxima = centro(casas.get(i), minimo, maximo, celula, origemX, origemY);
            float dx = proxima.x() - anterior.x();
            float dy = proxima.y() - anterior.y();
            float distancia = (float) Math.sqrt(dx * dx + dy * dy);
            for (float percorrida = espacamento; percorrida < distancia; percorrida += espacamento) {
                float proporcao = percorrida / distancia;
                amostras.add(new Coordenada(anterior.x() + dx * proporcao, anterior.y() + dy * proporcao));
            }
            if (!proxima.equals(amostras.get(amostras.size() - 1))) {
                amostras.add(proxima);
            }
            anterior = proxima;
        }

        List<Ponto> resultado = new ArrayList<>();
        for (int i = 0; i < amostras.size(); i++) {
            float proporcao = amostras.size() == 1 ? 1f : (float) i / (amostras.size() - 1);
            Coordenada ponto = amostras.get(i);
            resultado.add(new Ponto(ponto.x(), ponto.y(), ALFA_ANTIGO + (ALFA_NOVO - ALFA_ANTIGO) * proporcao));
        }
        return List.copyOf(resultado);
    }

    private static Coordenada centro(RastroDaNave.Casa casa, int minimo, int maximo,
            float celula, float origemX, float origemY) {
        return new Coordenada(origemX + (casa.x() - minimo + 0.5f) * celula,
                origemY + (maximo - casa.y() + 0.5f) * celula);
    }

    private record Coordenada(float x, float y) {
    }

    public record Ponto(float x, float y, float alfa) {
    }
}
