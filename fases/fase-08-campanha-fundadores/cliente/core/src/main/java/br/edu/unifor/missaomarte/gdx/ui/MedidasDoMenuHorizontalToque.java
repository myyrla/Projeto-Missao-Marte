package br.edu.unifor.missaomarte.gdx.ui;

import java.util.List;

/** Métricas tipográficas ativas usadas pelo menu horizontal de toque. */
public record MedidasDoMenuHorizontalToque(List<Float> largurasDasOpcoes,
                                            float larguraDoDetalhe,
                                            float alturaDaOpcao,
                                            float alturaDoDetalhe,
                                            float alturaDoRodape,
                                            float larguraDaConta,
                                            float larguraDaInstrucao,
                                            float alvoMinimo) {
    public MedidasDoMenuHorizontalToque {
        largurasDasOpcoes = List.copyOf(largurasDasOpcoes);
        if (largurasDasOpcoes.size() != 4) {
            throw new IllegalArgumentException("São esperadas quatro opções.");
        }
    }
}
