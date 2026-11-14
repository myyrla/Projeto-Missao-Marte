package br.edu.unifor.missaomarte.application.campanha;

public final class Campus {
    private final ProgressoDaCampanha progresso;

    Campus(ProgressoDaCampanha progresso) {
        this.progresso = progresso;
    }

    public EstadoDaConstrucao estado(Construcao construcao) {
        if (construcao == Construcao.REITORIA) {
            return progresso.concluido() ? EstadoDaConstrucao.INAUGURADA : EstadoDaConstrucao.PLANTA;
        }
        int etapaDoFundador = CatalogoDaCampanha.etapaDoFundador(construcao.responsavel());
        return progresso.etapaAtual() > etapaDoFundador
                ? EstadoDaConstrucao.CONSTRUIDA : EstadoDaConstrucao.PLANTA;
    }
}
