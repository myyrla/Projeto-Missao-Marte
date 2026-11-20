package br.edu.unifor.missaomarte.gdx.campanha;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.campanha.Campanha;
import org.junit.jupiter.api.Test;

class ConteudoDoCartaoDaCampanhaTest {
    @Test
    void proximaMissaoMantemDescricaoCompletaSemReticencias() {
        ConteudoDoCartaoDaCampanha conteudo = ConteudoDoCartaoDaCampanha.de(
                ResumoVisualDaCampanha.de(Campanha.nova()), false);

        assertTrue(conteudo.linhas().contains("Planície Utopia · Astronauta · Aprendizado"));
        assertFalse(conteudo.linhas().stream().anyMatch(linha -> linha.contains("...")));
    }

    @Test
    void conclusaoNormalMostraPontuacaoEConclusaoDeDemonstracaoNaoMostra() {
        Campanha campanha = Campanha.nova();
        campanha.registrarResultado(1, EstadoPartida.VITORIA, 10);
        campanha.registrarResultado(2, EstadoPartida.VITORIA, 20);
        campanha.registrarResultado(3, EstadoPartida.VITORIA, 30);
        ResumoVisualDaCampanha resumo = ResumoVisualDaCampanha.de(campanha);

        ConteudoDoCartaoDaCampanha normal = ConteudoDoCartaoDaCampanha.de(resumo, false);
        ConteudoDoCartaoDaCampanha demonstracao = ConteudoDoCartaoDaCampanha.de(resumo, true);

        assertTrue(normal.linhas().stream().anyMatch(linha -> linha.startsWith("Pontuação total:")));
        assertFalse(demonstracao.linhas().stream().anyMatch(linha -> linha.startsWith("Pontuação total:")));
        assertTrue(demonstracao.linhas().contains("Construções erguidas: 7/7"));
        assertTrue(demonstracao.linhas().contains(
                "O campus UNIFOR Marte está pronto para a inauguração."));
    }
}
