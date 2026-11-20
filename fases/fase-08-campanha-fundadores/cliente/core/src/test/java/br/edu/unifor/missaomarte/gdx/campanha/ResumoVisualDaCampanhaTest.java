package br.edu.unifor.missaomarte.gdx.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.campanha.Campanha;
import org.junit.jupiter.api.Test;

class ResumoVisualDaCampanhaTest {
    @Test
    void campanhaNovaMostraPrimeiraEtapaAtualEDemaisPendentes() {
        ResumoVisualDaCampanha resumo = ResumoVisualDaCampanha.de(Campanha.nova());

        assertEquals(0, resumo.fundadoresResgatados());
        assertEquals("Iniciar missão", resumo.rotuloDaAcao());
        assertEquals(ResumoVisualDaCampanha.SituacaoDaEtapa.ATUAL, resumo.etapas().get(0).situacao());
        assertEquals(ResumoVisualDaCampanha.SituacaoDaEtapa.PENDENTE, resumo.etapas().get(1).situacao());
        assertEquals("Planície Utopia", resumo.etapas().get(0).local());
        assertEquals("Planície Utopia", resumo.proximaMissao().cenario());
        assertEquals("Astronauta", resumo.proximaMissao().fundador());
        assertEquals("Aprendizado", resumo.proximaMissao().mecanica());
        assertTrue(resumo.conclusao().isEmpty());
        assertTrue(resumo.exibePontuacaoNoCabecalho());
    }

    @Test
    void vitoriaMarcaFundadorResgatadoEAvancaODestaque() {
        Campanha campanha = Campanha.nova();
        campanha.registrarResultado(1, EstadoPartida.VITORIA, 33);

        ResumoVisualDaCampanha resumo = ResumoVisualDaCampanha.de(campanha);

        assertEquals(1, resumo.fundadoresResgatados());
        assertEquals(33, resumo.pontuacaoTotal());
        assertEquals(ResumoVisualDaCampanha.SituacaoDaEtapa.RESGATADA, resumo.etapas().get(0).situacao());
        assertEquals(ResumoVisualDaCampanha.SituacaoDaEtapa.ATUAL, resumo.etapas().get(1).situacao());
        assertEquals("Cratera Jezero", resumo.proximaMissao().cenario());
        assertEquals("Professor", resumo.proximaMissao().fundador());
        assertEquals("NAMI", resumo.proximaMissao().mecanica());
    }

    @Test
    void campanhaConcluidaOfereceNovaCampanha() {
        Campanha campanha = Campanha.nova();
        campanha.registrarResultado(1, EstadoPartida.VITORIA, 10);
        campanha.registrarResultado(2, EstadoPartida.VITORIA, 20);
        campanha.registrarResultado(3, EstadoPartida.VITORIA, 30);

        ResumoVisualDaCampanha resumo = ResumoVisualDaCampanha.de(campanha);

        assertEquals(3, resumo.fundadoresResgatados());
        assertEquals("Nova campanha", resumo.rotuloDaAcao());
        assertEquals(3, resumo.etapas().stream()
                .filter(etapa -> etapa.situacao() == ResumoVisualDaCampanha.SituacaoDaEtapa.RESGATADA).count());
        ResumoVisualDaCampanha.Conclusao conclusao = resumo.conclusao().orElseThrow();
        assertEquals(60, conclusao.pontuacaoTotal());
        assertEquals(7, conclusao.construcoesErguidas());
        assertEquals(7, conclusao.totalDeConstrucoes());
        assertEquals("O campus UNIFOR Marte está pronto para a inauguração.", conclusao.frase());
        assertFalse(resumo.exibePontuacaoNoCabecalho());
    }
}
