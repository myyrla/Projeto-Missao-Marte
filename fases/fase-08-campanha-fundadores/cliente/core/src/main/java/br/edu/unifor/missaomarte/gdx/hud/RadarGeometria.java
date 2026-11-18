package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Projeta coordenadas do grid no retangulo do radar, invertendo o eixo vertical do dominio. */
public final class RadarGeometria {
    private final RetanguloDeTela area;
    private final int min;
    private final int max;

    public RadarGeometria(RetanguloDeTela area, int min, int max) {
        if (max <= min) {
            throw new IllegalArgumentException("O limite máximo deve ser maior que o mínimo.");
        }
        this.area = area;
        this.min = min;
        this.max = max;
    }

    public float x(int x) {
        return area.x() + (x - min) * area.largura() / (max - min);
    }

    public float y(int y) {
        return area.y() + (max - y) * area.altura() / (max - min);
    }
}
