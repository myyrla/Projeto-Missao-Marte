package br.edu.unifor.missaomarte.gdx.campanha;

import java.util.ArrayList;
import java.util.List;

/** Texto integral do cartao; a tela mede e quebra cada linha sem inserir reticencias. */
public record ConteudoDoCartaoDaCampanha(String titulo, List<String> linhas) {
    public ConteudoDoCartaoDaCampanha {
        linhas = List.copyOf(linhas);
    }

    public static ConteudoDoCartaoDaCampanha de(ResumoVisualDaCampanha resumo, boolean demonstracao) {
        if (resumo.conclusao().isEmpty()) {
            ResumoVisualDaCampanha.ProximaMissao proxima = resumo.proximaMissao();
            return new ConteudoDoCartaoDaCampanha("PRÓXIMA MISSÃO", List.of(
                    proxima.cenario() + " · " + proxima.fundador() + " · " + proxima.mecanica()));
        }
        ResumoVisualDaCampanha.Conclusao conclusao = resumo.conclusao().orElseThrow();
        List<String> linhas = new ArrayList<>();
        if (!demonstracao) {
            linhas.add("Pontuação total: " + formatarPontuacao(conclusao.pontuacaoTotal()));
        }
        linhas.add("Construções erguidas: " + conclusao.construcoesErguidas()
                + "/" + conclusao.totalDeConstrucoes());
        linhas.add(conclusao.frase());
        return new ConteudoDoCartaoDaCampanha("CAMPANHA CONCLUÍDA", linhas);
    }

    private static String formatarPontuacao(int pontos) {
        return String.format("%,d", pontos).replace(',', '.');
    }
}
