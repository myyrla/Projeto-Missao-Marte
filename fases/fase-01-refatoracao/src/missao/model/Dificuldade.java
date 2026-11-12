package missao.model;

public enum Dificuldade {

    FACIL(4, 1, 1, 30),
    MEDIO(5, 2, 2, 20),
    DIFICIL(5, 3, 3, 45);

    private final int qtdPassageiros;
    private final int qtdAsteroides;
    private final int qtdInimigos;
    private final int pontuacaoInicial;

    Dificuldade(int qtdPassageiros, int qtdAsteroides, int qtdInimigos, int pontuacaoInicial) {
        this.qtdPassageiros = qtdPassageiros;
        this.qtdAsteroides = qtdAsteroides;
        this.qtdInimigos = qtdInimigos;
        this.pontuacaoInicial = pontuacaoInicial;
    }

    public int getQtdPassageiros() { return qtdPassageiros; }
    public int getQtdAsteroides() { return qtdAsteroides; }
    public int getQtdInimigos() { return qtdInimigos; }
    public int getPontuacaoInicial() { return pontuacaoInicial; }
}