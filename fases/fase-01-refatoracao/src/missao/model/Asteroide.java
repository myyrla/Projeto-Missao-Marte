package missao.model;

public class Asteroide implements EntidadeMapa {

    private int x;
    private int y;

    public Asteroide(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public char getSimbolo() {
        return '#';
    }
}
