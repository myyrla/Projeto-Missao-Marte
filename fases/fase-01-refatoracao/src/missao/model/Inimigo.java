package missao.model;

import java.util.Random;

public class Inimigo implements EntidadeMapa {
    private int x;
    private int y;

    public Inimigo(int x, int y) {
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
        return 'X';
    }

    public void mover(Random random, int minX, int maxX, int minY, int maxY) {
        int direcao = random.nextInt(4);

        switch (direcao) {
            case 0 -> { if (x < maxX) x++; }
            case 1 -> { if (x > minX) x--; }
            case 2 -> { if (y < maxY) y++; }
            case 3 -> { if (y > minY) y--; }
        }
    }
}
