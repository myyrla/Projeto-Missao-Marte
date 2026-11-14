package br.edu.unifor.missaomarte.domain;

public final class ParadaDoRover implements ObjetoRenderizavel {
    private final String par;
    private final Posicao posicao;
    private final Posicao destino;

    public ParadaDoRover(String par, Posicao posicao, Posicao destino) {
        if (par == null || par.isBlank() || posicao == null || destino == null || posicao.equals(destino)) {
            throw new IllegalArgumentException("A parada precisa de par, posição e destino distintos.");
        }
        this.par = par;
        this.posicao = posicao;
        this.destino = destino;
    }

    public String getPar() {
        return par;
    }

    public Posicao getDestino() {
        return destino;
    }

    @Override
    public Posicao getPosicao() {
        return posicao;
    }

    @Override
    public char getSimbolo() {
        return 'R';
    }
}
