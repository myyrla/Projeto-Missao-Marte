package br.edu.unifor.missaomarte.application.campanha;

import java.util.OptionalInt;

public final class InicializadorDaCampanha {
    private InicializadorDaCampanha() {
    }

    public static PreparacaoInicialDaCampanha preparar(String etapaDeDemonstracao,
                                                       RepositorioDeProgresso repositorio) {
        return new PreparacaoInicialDaCampanha(etapaDeDemonstracao, repositorio);
    }

    public static InicioDaCampanha iniciar(String etapaDeDemonstracao, RepositorioDeProgresso repositorio) {
        OptionalInt etapa = SelecaoDeEtapaInicial.interpretar(etapaDeDemonstracao);
        if (etapa.isPresent()) {
            return new InicioDaCampanha(Campanha.novaNaEtapa(etapa.getAsInt()), true, true);
        }
        return new InicioDaCampanha(Campanha.carregar(repositorio), false, false);
    }
}
