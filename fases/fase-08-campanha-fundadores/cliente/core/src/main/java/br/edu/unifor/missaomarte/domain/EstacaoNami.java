package br.edu.unifor.missaomarte.domain;

public final class EstacaoNami implements ObjetoRenderizavel {
    private final Posicao posicao;
    private boolean usada;

    public EstacaoNami(int x, int y) {
        this.posicao = new Posicao(x, y);
    }

    public boolean ativar(Nave nave) {
        return tentarAtivar(nave) == ResultadoDaAtivacao.REPAROU;
    }

    public ResultadoDaAtivacao tentarAtivar(Nave nave) {
        if (usada || !posicao.equals(nave.getPosicao())) {
            return ResultadoDaAtivacao.SEM_EFEITO;
        }
        if (!nave.recuperarVida()) {
            return ResultadoDaAtivacao.CASCO_COMPLETO;
        }
        usada = true;
        return ResultadoDaAtivacao.REPAROU;
    }

    public boolean isUsada() {
        return usada;
    }

    @Override
    public Posicao getPosicao() {
        return posicao;
    }

    @Override
    public char getSimbolo() {
        return 'H';
    }

    public enum ResultadoDaAtivacao {
        REPAROU,
        CASCO_COMPLETO,
        SEM_EFEITO
    }
}
