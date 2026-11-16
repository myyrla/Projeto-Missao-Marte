package br.edu.unifor.missaomarte.gdx.ui;

/** Estado de foco de uma lista: navegacao circular e selecao direta por clique ou toque. */
public final class ListaDeBotoes {
    private final int quantidade;
    private int foco;

    public ListaDeBotoes(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A lista precisa ter pelo menos um botão.");
        }
        this.quantidade = quantidade;
    }

    public int foco() {
        return foco;
    }

    public void subir() {
        foco = (foco - 1 + quantidade) % quantidade;
    }

    public void descer() {
        foco = (foco + 1) % quantidade;
    }

    public boolean focar(int indice) {
        if (indice < 0 || indice >= quantidade) {
            return false;
        }
        foco = indice;
        return true;
    }

    public boolean emFoco(int indice) {
        return foco == indice;
    }
}
