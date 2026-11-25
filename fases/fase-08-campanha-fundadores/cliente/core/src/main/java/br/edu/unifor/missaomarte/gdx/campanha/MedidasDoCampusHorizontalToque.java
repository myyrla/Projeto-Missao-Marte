package br.edu.unifor.missaomarte.gdx.campanha;

import java.util.List;

/** Métricas tipográficas ativas do campus horizontal de toque. */
public record MedidasDoCampusHorizontalToque(MedidasDoCampus conteudo,
                                              List<LargurasDaEtapa> largurasDasEtapas,
                                              float larguraMinimaDoPainel,
                                              float alturaDoTitulo,
                                              float alturaDoSubtitulo,
                                              float alturaDoBotao,
                                              float alvoMinimo) {
    public record LargurasDaEtapa(float cenario, float fundador) {
    }

    public MedidasDoCampusHorizontalToque {
        largurasDasEtapas = List.copyOf(largurasDasEtapas);
        if (largurasDasEtapas.size() != 3) {
            throw new IllegalArgumentException("São esperadas três etapas.");
        }
    }
}
