package missao.model;

public class Professor extends Passageiro {

    public Professor(String nome, int x, int y) {
        super(nome, x, y);
    }

    @Override
    public int getPontuacao() {
        return 10;
    }

    @Override
    public char getSimbolo() {
        return 'P';
    }
}
