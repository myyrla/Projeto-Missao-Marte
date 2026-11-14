package br.edu.unifor.missaomarte.application.campanha;

import br.edu.unifor.missaomarte.domain.Posicao;

public final class TempestadeDeAreia {
    private static final double TOLERANCIA = 1e-6;

    private final float atrasoInicial;
    private final float duracao;
    private final float periodo;
    private final int raio;
    private double tempo;

    public TempestadeDeAreia(float atrasoInicial, float duracao, float periodo, int raio) {
        if (atrasoInicial < 0f || duracao <= 0f || periodo <= duracao || raio < 0) {
            throw new IllegalArgumentException("Configuração de tempestade inválida.");
        }
        this.atrasoInicial = atrasoInicial;
        this.duracao = duracao;
        this.periodo = periodo;
        this.raio = raio;
    }

    public static TempestadeDeAreia padrao() {
        return new TempestadeDeAreia(10f, 12f, 30f, 2);
    }

    public void avancar(float delta) {
        if (delta > 0f) {
            tempo += delta;
        }
    }

    public boolean estaAtiva() {
        if (tempo + TOLERANCIA < atrasoInicial) {
            return false;
        }
        double fase = (tempo - atrasoInicial) % periodo;
        return fase + TOLERANCIA < duracao;
    }

    public boolean estaVisivel(Posicao nave, Posicao alvo) {
        if (!estaAtiva()) {
            return true;
        }
        int dx = Math.abs(nave.getX() - alvo.getX());
        int dy = Math.abs(nave.getY() - alvo.getY());
        return Math.max(dx, dy) <= raio;
    }
}
