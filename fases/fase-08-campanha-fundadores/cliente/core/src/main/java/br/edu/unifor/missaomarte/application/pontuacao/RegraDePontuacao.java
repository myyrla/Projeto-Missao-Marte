package br.edu.unifor.missaomarte.application.pontuacao;

import br.edu.unifor.missaomarte.domain.Passageiro;

/** Strategy com todos os ganhos, custos e bonus que alteram a pontuacao de uma partida. */
public interface RegraDePontuacao {
    int pontuacaoInicial();

    int pontosDoResgate(Passageiro passageiro);

    int pontosDaEntrega(int passageiros);

    int custoDoMovimento();

    int custoPorSegundo();

    int custoDoTiro();

    int custoDaColisao(int perigos);

    int bonusDaVitoria(int vidasRestantes);
}
