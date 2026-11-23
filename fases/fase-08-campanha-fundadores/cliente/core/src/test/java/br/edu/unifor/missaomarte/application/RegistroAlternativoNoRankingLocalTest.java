package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import br.edu.unifor.missaomarte.application.ranking.RankingRepository;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Professor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class RegistroAlternativoNoRankingLocalTest {
    private static final class RepositorioEmMemoria implements RankingRepository {
        private List<RankingEntry> dados = new ArrayList<>();

        @Override
        public List<RankingEntry> carregarTop() {
            return new ArrayList<>(dados);
        }

        @Override
        public void salvarTop(List<RankingEntry> ranking) {
            dados = new ArrayList<>(ranking);
        }
    }

    private final RankingService ranking = new RankingService(new RepositorioEmMemoria());
    private final RegistroAlternativoNoRankingLocal registro = new RegistroAlternativoNoRankingLocal();

    @Test
    void falhaDeConexaoSalvaVitoriaLocalUmaVez() {
        GameSession sessao = sessaoVencedora("Myrla");

        assertTrue(registro.registrarSeNecessario(false, sessao,
                Resposta.erroDeConexao("sem conexão"), ranking));
        assertFalse(registro.registrarSeNecessario(false, sessao,
                Resposta.erroDeConexao("sem conexão"), ranking));

        assertEquals(1, ranking.top().size());
        assertEquals("Myrla", ranking.top().get(0).getNome());
    }

    @Test
    void envioBemSucedidoNaoSalvaLocalmente() {
        GameSession sessao = sessaoVencedora("Cristiano");

        assertFalse(registro.registrarSeNecessario(false, sessao, Resposta.sucesso(null), ranking));

        assertTrue(ranking.top().isEmpty());
    }

    @Test
    void reenvioBemSucedidoDepoisDaFalhaNaoDuplica() {
        GameSession sessao = sessaoVencedora("Iandeyara");

        assertTrue(registro.registrarSeNecessario(false, sessao,
                Resposta.erroDeConexao("sem conexão"), ranking));
        assertFalse(registro.registrarSeNecessario(false, sessao, Resposta.sucesso(null), ranking));

        assertEquals(1, ranking.top().size());
        assertEquals("Iandeyara", ranking.top().get(0).getNome());
    }

    @Test
    void campanhaEErroNaoAutorizadoNaoUsamORankingLocal() {
        GameSession sessao = sessaoVencedora("Convidado");

        assertFalse(registro.registrarSeNecessario(true, sessao,
                Resposta.erroDeConexao("sem conexão"), ranking));
        assertFalse(registro.registrarSeNecessario(false, sessao,
                Resposta.erroNaoAutorizado("sessão expirada"), ranking));

        assertTrue(ranking.top().isEmpty());
    }

    private static GameSession sessaoVencedora(String piloto) {
        Nave nave = new Nave(0, 0, 3, 2);
        Missao missao = new Missao(nave);
        missao.adicionarPassageiro(new Professor("P1", 1, 0));
        GameSession sessao = new GameSession(piloto, Dificuldade.MEDIO, missao, 100, -5, 5, new Random(1));
        sessao.processarAcao(Acao.DIREITA);
        sessao.processarAcao(Acao.EMBARCAR);
        sessao.processarAcao(Acao.ESQUERDA);
        assertEquals(EstadoPartida.VITORIA, sessao.getEstado());
        return sessao;
    }
}
