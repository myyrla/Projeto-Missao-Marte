package br.edu.unifor.missaomarte.application.pontuacao;

import br.edu.unifor.missaomarte.domain.Passageiro;
import java.util.Objects;

/** Decorator que deriva os valores da regra recebida e acrescenta somente os bonus da campanha. */
public final class RegraDePontuacaoDaCampanha implements RegraDePontuacao {
    private static final int MULTIPLICADOR = 10;
    private static final int BONUS_POR_ENTREGA = 50;
    private static final int BONUS_POR_VIDA_NA_VITORIA = 100;

    private final RegraDePontuacao base;

    public RegraDePontuacaoDaCampanha(RegraDePontuacao base) {
        this.base = Objects.requireNonNull(base, "base");
    }

    @Override
    public int pontuacaoInicial() {
        return base.pontuacaoInicial() * MULTIPLICADOR;
    }

    @Override
    public int pontosDoResgate(Passageiro passageiro) {
        return base.pontosDoResgate(passageiro) * MULTIPLICADOR;
    }

    @Override
    public int pontosDaEntrega(int passageiros) {
        return base.pontosDaEntrega(passageiros) * MULTIPLICADOR + BONUS_POR_ENTREGA * passageiros;
    }

    @Override
    public int custoDoMovimento() {
        return base.custoDoMovimento() * MULTIPLICADOR;
    }

    @Override
    public int custoPorSegundo() {
        return base.custoPorSegundo() * MULTIPLICADOR;
    }

    @Override
    public int custoDoTiro() {
        return base.custoDoTiro() * MULTIPLICADOR;
    }

    @Override
    public int custoDaColisao(int perigos) {
        return base.custoDaColisao(perigos) * MULTIPLICADOR;
    }

    @Override
    public int bonusDaVitoria(int vidasRestantes) {
        return base.bonusDaVitoria(vidasRestantes) * MULTIPLICADOR
                + BONUS_POR_VIDA_NA_VITORIA * vidasRestantes;
    }
}
