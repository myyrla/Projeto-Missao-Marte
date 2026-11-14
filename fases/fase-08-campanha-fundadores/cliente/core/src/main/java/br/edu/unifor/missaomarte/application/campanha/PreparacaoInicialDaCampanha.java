package br.edu.unifor.missaomarte.application.campanha;

public record PreparacaoInicialDaCampanha(String etapaDeDemonstracao, RepositorioDeProgresso repositorio) {
    public PreparacaoInicialDaCampanha {
        if (repositorio == null) {
            throw new IllegalArgumentException("O repositório de progresso é obrigatório.");
        }
    }

    public InicioDaCampanha iniciar() {
        return InicializadorDaCampanha.iniciar(etapaDeDemonstracao, repositorio);
    }
}
