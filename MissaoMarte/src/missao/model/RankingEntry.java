package missao.model;

public record RankingEntry(
        String nome,
        int score,
        String dificuldade,
        int passageirosColetados,
        String dataHora
) { }
