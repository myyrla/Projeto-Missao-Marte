package br.edu.unifor.missaomarte.gdx.campanha;

import java.util.List;

/** Medidas tipograficas que mantem o calculo do painel independente do libGDX. */
public record MedidasDoCampus(float alturaDosFundadores, float alturaDaPontuacao,
                              float alturaDaDemonstracao, boolean exibePontuacao,
                              boolean exibeDemonstracao, List<MedidasDaEtapa> etapas,
                              float alturaDoCartao) {
    public record MedidasDaEtapa(float alturaDoTitulo, float alturaDoCenario,
                                 float alturaDoFundador) {
    }

    public MedidasDoCampus {
        etapas = List.copyOf(etapas);
        if (etapas.size() != 3) {
            throw new IllegalArgumentException("São esperadas três etapas.");
        }
    }

    public static MedidasDoCampus padrao() {
        return padraoComCartao(82f, true, false);
    }

    public static MedidasDoCampus padraoComCartao(float alturaDoCartao,
            boolean exibePontuacao, boolean exibeDemonstracao) {
        MedidasDaEtapa etapa = new MedidasDaEtapa(21f, 17f, 17f);
        return new MedidasDoCampus(29f, 21f, 17f, exibePontuacao, exibeDemonstracao,
                List.of(etapa, etapa, etapa), alturaDoCartao);
    }
}
