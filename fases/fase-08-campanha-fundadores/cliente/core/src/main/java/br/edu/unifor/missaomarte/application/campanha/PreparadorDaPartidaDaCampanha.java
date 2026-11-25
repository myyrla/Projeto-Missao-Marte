package br.edu.unifor.missaomarte.application.campanha;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.pontuacao.RegraDePontuacaoDaCampanha;
import br.edu.unifor.missaomarte.application.pontuacao.RegraDePontuacaoPadrao;
import java.util.Random;

/** Monta uma sessão com a missão, a pontuação e as mecânicas definidas pela etapa da campanha. */
public final class PreparadorDaPartidaDaCampanha {
    private final MissaoFactory missaoFactory;

    public PreparadorDaPartidaDaCampanha(MissaoFactory missaoFactory) {
        this.missaoFactory = missaoFactory;
    }

    public GameSession preparar(EtapaDaCampanha etapa, String piloto,
            int min, int max, Random random) {
        PreparacaoDaEtapa preparacao = missaoFactory.criarEtapaDaCampanha(
                etapa, min, max, random);
        RegraDePontuacaoDaCampanha pontuacao = new RegraDePontuacaoDaCampanha(
                new RegraDePontuacaoPadrao(missaoFactory.pontuacaoInicial(etapa.dificuldade())));
        return new GameSession(piloto, etapa.dificuldade(), preparacao.missao(),
                pontuacao, min, max, random, etapa.modo(), preparacao.mecanicas());
    }
}
