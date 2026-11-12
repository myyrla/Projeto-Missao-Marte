package missao.model;

public abstract class Passageiro {
    private String nome;
    private int x;
    private int y;

    protected Passageiro(String nome, int x, int y) {
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    protected Passageiro() {
    }

    public String getNome() { return nome; }

    public int getX() { return x; }

    public int getY() { return y; }

    public abstract int getPontuacao();

    public abstract char getSimbolo();
}
