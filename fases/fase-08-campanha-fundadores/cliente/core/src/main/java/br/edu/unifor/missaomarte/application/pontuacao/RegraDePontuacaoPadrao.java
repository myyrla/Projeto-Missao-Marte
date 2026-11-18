package br.edu.unifor.missaomarte.application.pontuacao;

import br.edu.unifor.missaomarte.domain.Passageiro;
import java.util.Objects;

/** Regra original da partida rapida, preservada como estrategia padrao. */
public final class RegraDePontuacaoPadrao implements RegraDePontuacao {
    private static final int CUSTO_DO_MOVIMENTO = 1;
    private static final int CUSTO_POR_SEGUNDO = 1;
    private static final int CUSTO_DO_TIRO = 2;
    private static final int CUSTO_POR_PERIGO = 5;

    private final int pontuacaoInicial;

    public RegraDePontuacaoPadrao(int pontuacaoInicial) {
        this.pontuacaoInicial = pontuacaoInicial;
    }

    @Override
    public int pontuacaoInicial() {
        return pontuacaoInicial;
    }

    @Override
    public int pontosDoResgate(Passageiro passageiro) {
        return Objects.requireNonNull(passageiro, "passageiro").getPontuacao();
    }

    @Override
    public int pontosDaEntrega(int passageiros) {
        return 0;
    }

    @Override
    public int custoDoMovimento() {
        return CUSTO_DO_MOVIMENTO;
    }

    @Override
    public int custoPorSegundo() {
        return CUSTO_POR_SEGUNDO;
    }

    @Override
    public int custoDoTiro() {
        return CUSTO_DO_TIRO;
    }

    @Override
    public int custoDaColisao(int perigos) {
        return CUSTO_POR_PERIGO * Math.max(1, perigos);
    }

    @Override
    public int bonusDaVitoria(int vidasRestantes) {
        return 0;
    }
}
