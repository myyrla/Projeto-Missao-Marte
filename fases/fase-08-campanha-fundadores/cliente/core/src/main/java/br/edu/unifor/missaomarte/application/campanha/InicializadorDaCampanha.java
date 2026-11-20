package br.edu.unifor.missaomarte.application.campanha;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import java.util.OptionalInt;

public final class InicializadorDaCampanha {
    public static final String DEMONSTRACAO_CONCLUIDA = "concluida";

    private InicializadorDaCampanha() {
    }

    public static PreparacaoInicialDaCampanha preparar(String etapaDeDemonstracao,
                                                       RepositorioDeProgresso repositorio) {
        return new PreparacaoInicialDaCampanha(etapaDeDemonstracao, repositorio);
    }

    public static InicioDaCampanha iniciar(String etapaDeDemonstracao, RepositorioDeProgresso repositorio) {
        if (DEMONSTRACAO_CONCLUIDA.equalsIgnoreCase(etapaDeDemonstracao)) {
            Campanha campanha = Campanha.nova();
            for (int etapa = 1; etapa <= 3; etapa++) {
                campanha.registrarResultado(etapa, EstadoPartida.VITORIA, 0);
            }
            return new InicioDaCampanha(campanha, true, true);
        }
        OptionalInt etapa = SelecaoDeEtapaInicial.interpretar(etapaDeDemonstracao);
        if (etapa.isPresent()) {
            return new InicioDaCampanha(Campanha.novaNaEtapa(etapa.getAsInt()), true, true);
        }
        return new InicioDaCampanha(Campanha.carregar(repositorio), false, false);
    }
}
