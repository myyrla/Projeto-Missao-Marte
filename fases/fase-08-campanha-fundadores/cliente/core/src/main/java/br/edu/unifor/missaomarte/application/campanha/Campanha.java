package br.edu.unifor.missaomarte.application.campanha;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import java.util.Optional;

public final class Campanha {
    private final RepositorioDeProgresso repositorio;
    private ProgressoDaCampanha progresso;

    private Campanha(ProgressoDaCampanha progresso, RepositorioDeProgresso repositorio) {
        this.progresso = progresso;
        this.repositorio = repositorio;
    }

    public static Campanha nova() {
        return new Campanha(ProgressoDaCampanha.novo(), RepositorioDeProgresso.nenhum());
    }

    public static Campanha novaNaEtapa(int etapa) {
        return new Campanha(ProgressoDaCampanha.iniciarNaEtapa(etapa), RepositorioDeProgresso.nenhum());
    }

    public static Campanha carregar(RepositorioDeProgresso repositorio) {
        return new Campanha(repositorio.carregar().orElseGet(ProgressoDaCampanha::novo), repositorio);
    }

    public Optional<EtapaDaCampanha> etapaAtual() {
        return concluida() ? Optional.empty() : Optional.of(CatalogoDaCampanha.etapa(progresso.etapaAtual()));
    }

    public void registrarResultado(int etapa, EstadoPartida estado, int pontos) {
        if (estado == EstadoPartida.VITORIA) {
            ProgressoDaCampanha anterior = progresso;
            progresso = progresso.registrarVitoria(etapa, pontos);
            if (progresso != anterior) {
                repositorio.salvar(progresso);
            }
        }
    }

    public boolean concluida() {
        return progresso.concluido();
    }

    public int pontuacaoTotal() {
        return progresso.pontuacaoTotal();
    }

    public Campus campus() {
        return new Campus(progresso);
    }

    public void reiniciar() {
        progresso = ProgressoDaCampanha.novo();
        repositorio.apagar();
    }

    public ProgressoDaCampanha progresso() {
        return progresso;
    }
}
