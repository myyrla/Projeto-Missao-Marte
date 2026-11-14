package br.edu.unifor.missaomarte.application.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InicializadorDaCampanhaTest {
    @Test
    void semEtapaDeDemonstracaoCarregaOProgressoPersistido() {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        Campanha campanhaSalva = Campanha.carregar(repositorio);
        campanhaSalva.registrarResultado(1, EstadoPartida.VITORIA, 35);

        InicioDaCampanha inicio = InicializadorDaCampanha.iniciar(null, repositorio);

        assertEquals(2, inicio.campanha().etapaAtual().orElseThrow().numero());
        assertFalse(inicio.abrirCampusAoIniciar());
        assertFalse(inicio.demonstracao());
    }

    @Test
    void etapaValidaIniciaDemonstracaoDiretamenteSemAlterarOProgressoPersistido() {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        Campanha campanhaSalva = Campanha.carregar(repositorio);
        campanhaSalva.registrarResultado(1, EstadoPartida.VITORIA, 35);

        InicioDaCampanha inicio = InicializadorDaCampanha.iniciar("3", repositorio);

        assertEquals(3, inicio.campanha().etapaAtual().orElseThrow().numero());
        assertTrue(inicio.abrirCampusAoIniciar());
        assertTrue(inicio.demonstracao());
        assertEquals(2, Campanha.carregar(repositorio).etapaAtual().orElseThrow().numero());
    }

    @Test
    void etapaInvalidaIgnoraADemonstracaoECarregaOProgressoPersistido() {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        Campanha campanhaSalva = Campanha.carregar(repositorio);
        campanhaSalva.registrarResultado(1, EstadoPartida.VITORIA, 27);

        InicioDaCampanha inicio = InicializadorDaCampanha.iniciar("4", repositorio);

        assertEquals(2, inicio.campanha().etapaAtual().orElseThrow().numero());
        assertFalse(inicio.abrirCampusAoIniciar());
        assertFalse(inicio.demonstracao());
    }

    @Test
    void preparacaoAdiaALeituraDoRepositorioAteAInicializacaoDoJogo() {
        RepositorioControlado repositorio = new RepositorioControlado();

        PreparacaoInicialDaCampanha preparacao = InicializadorDaCampanha.preparar(null, repositorio);

        assertFalse(repositorio.consultado);
        repositorio.permitirLeitura = true;
        InicioDaCampanha inicio = preparacao.iniciar();
        assertTrue(repositorio.consultado);
        assertEquals(1, inicio.campanha().etapaAtual().orElseThrow().numero());
    }

    private static final class RepositorioEmMemoria implements RepositorioDeProgresso {
        private ProgressoDaCampanha progresso;

        @Override
        public Optional<ProgressoDaCampanha> carregar() {
            return Optional.ofNullable(progresso);
        }

        @Override
        public void salvar(ProgressoDaCampanha progresso) {
            this.progresso = progresso;
        }

        @Override
        public void apagar() {
            progresso = null;
        }
    }

    private static final class RepositorioControlado implements RepositorioDeProgresso {
        private boolean permitirLeitura;
        private boolean consultado;

        @Override
        public Optional<ProgressoDaCampanha> carregar() {
            if (!permitirLeitura) {
                throw new IllegalStateException("O repositório ainda não pode ser lido.");
            }
            consultado = true;
            return Optional.empty();
        }

        @Override
        public void salvar(ProgressoDaCampanha progresso) {
        }

        @Override
        public void apagar() {
        }
    }
}
