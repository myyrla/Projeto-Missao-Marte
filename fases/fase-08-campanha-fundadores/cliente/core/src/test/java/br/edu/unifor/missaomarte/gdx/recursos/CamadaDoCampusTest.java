package br.edu.unifor.missaomarte.gdx.recursos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import br.edu.unifor.missaomarte.application.campanha.Construcao;
import br.edu.unifor.missaomarte.application.campanha.EstadoDaConstrucao;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CamadaDoCampusTest {
    private static final Map<Construcao, String> NOMES_ESPERADOS = Map.of(
            Construcao.ZONAS_DE_POUSO, "zonas_de_pouso",
            Construcao.TORRE_DE_COMUNICACAO, "torre_de_comunicacao",
            Construcao.BLOCOS_DE_AULA, "blocos_de_aula",
            Construcao.BIBLIOTECA, "biblioteca",
            Construcao.PREFEITURA, "prefeitura",
            Construcao.NAMI, "nami",
            Construcao.REITORIA, "reitoria");

    @Test
    void mapeiaTodasAsConstrucoesNaOrdemDeDesenho() {
        List<Construcao> ordemEsperada = List.of(
                Construcao.ZONAS_DE_POUSO,
                Construcao.TORRE_DE_COMUNICACAO,
                Construcao.BLOCOS_DE_AULA,
                Construcao.BIBLIOTECA,
                Construcao.PREFEITURA,
                Construcao.NAMI,
                Construcao.REITORIA);

        assertEquals(ordemEsperada,
                Arrays.stream(CamadaDoCampus.values()).map(CamadaDoCampus::construcao).toList());
        for (Construcao construcao : Construcao.values()) {
            assertSame(construcao, CamadaDoCampus.da(construcao).construcao());
        }
    }

    @Test
    void escolhePlantaOuConstruidoConformeOEstadoDoDominio() {
        for (CamadaDoCampus camada : CamadaDoCampus.values()) {
            String nome = NOMES_ESPERADOS.get(camada.construcao());
            assertEquals("imagens/campus/" + nome + "_planta.png",
                    camada.caminho(EstadoDaConstrucao.PLANTA));
            assertEquals("imagens/campus/" + nome + "_construido.png",
                    camada.caminho(EstadoDaConstrucao.CONSTRUIDA));
            assertEquals("imagens/campus/" + nome + "_construido.png",
                    camada.caminho(EstadoDaConstrucao.INAUGURADA));
        }
    }

    @Test
    void informaOCaminhoDaBase() {
        assertEquals("imagens/campus/base.png", CamadaDoCampus.caminhoDaBase());
    }
}
