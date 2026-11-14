package br.edu.unifor.missaomarte.domain;

public final class EstacaoNami implements ObjetoRenderizavel {
    private final Posicao posicao;
    private boolean usada;

    public EstacaoNami(int x, int y) {
        this.posicao = new Posicao(x, y);
    }

    public boolean ativar(Nave nave) {
        if (usada || !posicao.equals(nave.getPosicao()) || !nave.recuperarVida()) {
            return false;
        }
        usada = true;
        return true;
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
}
