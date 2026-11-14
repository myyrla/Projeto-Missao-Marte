package br.edu.unifor.missaomarte.application.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.servidor.ArmazemDeTexto;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RepositorioDeProgressoTest {
    @Test
    void salvaECarregaEtapaEPontuacoesEmUmUnicoValorVersionado() {
        ArmazemEmMemoria armazem = new ArmazemEmMemoria();
        RepositorioDeProgressoEmTexto repositorio = new RepositorioDeProgressoEmTexto(armazem);
        Campanha campanha = Campanha.carregar(repositorio);
        campanha.registrarResultado(1, EstadoPartida.VITORIA, 32);
        campanha.registrarResultado(2, EstadoPartida.VITORIA, 41);

        Campanha recarregada = Campanha.carregar(repositorio);

        assertEquals(3, recarregada.etapaAtual().orElseThrow().numero());
        assertEquals(73, recarregada.pontuacaoTotal());
        assertTrue(armazem.dados.get("campanha.progresso").startsWith("1|3|32|41|0"));
    }

    @Test
    void valorAusenteOuCorrompidoAbreCampanhaNova() {
        ArmazemEmMemoria armazem = new ArmazemEmMemoria();
        RepositorioDeProgressoEmTexto repositorio = new RepositorioDeProgressoEmTexto(armazem);
        assertEquals(1, Campanha.carregar(repositorio).etapaAtual().orElseThrow().numero());

        armazem.gravar("campanha.progresso", "1|9|texto|-1|0");

        Campanha recuperada = Campanha.carregar(repositorio);
        assertEquals(1, recuperada.etapaAtual().orElseThrow().numero());
        assertEquals(0, recuperada.pontuacaoTotal());
    }

    @Test
    void novaCampanhaApagaOValorPersistido() {
        ArmazemEmMemoria armazem = new ArmazemEmMemoria();
        RepositorioDeProgressoEmTexto repositorio = new RepositorioDeProgressoEmTexto(armazem);
        Campanha campanha = Campanha.carregar(repositorio);
        campanha.registrarResultado(1, EstadoPartida.VITORIA, 30);
        assertTrue(armazem.dados.containsKey("campanha.progresso"));

        campanha.reiniciar();

        assertFalse(armazem.dados.containsKey("campanha.progresso"));
        assertEquals(1, campanha.etapaAtual().orElseThrow().numero());
    }

    private static final class ArmazemEmMemoria implements ArmazemDeTexto {
        private final Map<String, String> dados = new HashMap<>();

        @Override
        public String ler(String chave) {
            return dados.get(chave);
        }

        @Override
        public void gravar(String chave, String valor) {
            dados.put(chave, valor);
        }

        @Override
        public void apagar(String chave) {
            dados.remove(chave);
        }
    }
}
