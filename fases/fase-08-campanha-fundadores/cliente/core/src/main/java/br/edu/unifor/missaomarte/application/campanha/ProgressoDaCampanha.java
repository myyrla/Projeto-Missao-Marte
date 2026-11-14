package br.edu.unifor.missaomarte.application.campanha;

import java.util.Arrays;

public final class ProgressoDaCampanha {
    private final int etapaAtual;
    private final int[] melhoresPontuacoes;

    private ProgressoDaCampanha(int etapaAtual, int[] melhoresPontuacoes) {
        if (etapaAtual < 1 || etapaAtual > 4) {
            throw new IllegalArgumentException("A etapa atual deve estar entre 1 e 4.");
        }
        if (melhoresPontuacoes.length != 3) {
            throw new IllegalArgumentException("São esperadas três pontuações.");
        }
        this.etapaAtual = etapaAtual;
        this.melhoresPontuacoes = Arrays.copyOf(melhoresPontuacoes, melhoresPontuacoes.length);
    }

    public static ProgressoDaCampanha novo() {
        return iniciarNaEtapa(1);
    }

    public static ProgressoDaCampanha iniciarNaEtapa(int etapa) {
        if (etapa < 1 || etapa > 3) {
            throw new IllegalArgumentException("A etapa inicial deve estar entre 1 e 3.");
        }
        return new ProgressoDaCampanha(etapa, new int[3]);
    }

    static ProgressoDaCampanha restaurar(int etapaAtual, int etapa1, int etapa2, int etapa3) {
        if (etapa1 < 0 || etapa2 < 0 || etapa3 < 0) {
            throw new IllegalArgumentException("Pontuações não podem ser negativas.");
        }
        return new ProgressoDaCampanha(etapaAtual, new int[]{etapa1, etapa2, etapa3});
    }

    public int etapaAtual() {
        return etapaAtual;
    }

    public boolean concluido() {
        return etapaAtual == 4;
    }

    public int melhorPontuacao(int etapa) {
        return melhoresPontuacoes[etapa - 1];
    }

    public int pontuacaoTotal() {
        return Arrays.stream(melhoresPontuacoes).sum();
    }

    public ProgressoDaCampanha registrarVitoria(int etapa, int pontos) {
        if (etapa != etapaAtual || concluido()) {
            return this;
        }
        int[] novas = Arrays.copyOf(melhoresPontuacoes, melhoresPontuacoes.length);
        novas[etapa - 1] = Math.max(novas[etapa - 1], Math.max(0, pontos));
        return new ProgressoDaCampanha(etapaAtual + 1, novas);
    }
}
