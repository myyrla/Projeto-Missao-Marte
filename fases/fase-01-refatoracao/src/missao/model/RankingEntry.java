package missao.model;
public record RankingEntry(String nome, int score, String dificuldade,
        int passageirosColetados, String dataHora, long tempoJogo) {
    public RankingEntry(String nome, int score, String dificuldade, int passageirosColetados, String dataHora) {
        this(nome, score, dificuldade, passageirosColetados, dataHora, 0);
    }
}
