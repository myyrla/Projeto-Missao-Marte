package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import java.util.List;
import org.junit.jupiter.api.Test;

class MarcadoresDePassageirosTest {
    @Test
    void escolheSomenteOMaisProximoDeCadaTipo() {
        Professor professorDistante = new Professor("Ada", 5, 5);
        Professor professorProximo = new Professor("Lia", 2, 1);
        Astronauta astronauta = new Astronauta("Bia", 0, 1);
        Engenheiro engenheiro = new Engenheiro("Caio", 4, 0);

        List<MarcadoresDePassageiros.Marcador> marcadores = MarcadoresDePassageiros.selecionar(
                new Posicao(0, 0), List.of(professorDistante, professorProximo, astronauta, engenheiro));

        assertEquals(3, marcadores.size());
        assertSame(astronauta, marcador(marcadores, MarcadoresDePassageiros.Tipo.ASTRONAUTA).passageiro());
        assertSame(professorProximo, marcador(marcadores, MarcadoresDePassageiros.Tipo.PROFESSOR).passageiro());
        assertSame(engenheiro, marcador(marcadores, MarcadoresDePassageiros.Tipo.ENGENHEIRO).passageiro());
    }

    @Test
    void montaDistanciaManhattanComSingularEPlural() {
        Passageiro astronauta = new Astronauta("Bia", 2, 3);
        Passageiro professor = new Professor("Lia", 5, 7);

        List<MarcadoresDePassageiros.Marcador> marcadores = MarcadoresDePassageiros.selecionar(
                new Posicao(2, 2), List.of(astronauta, professor));

        assertEquals("ASTRONAUTA · 1 CASA",
                marcador(marcadores, MarcadoresDePassageiros.Tipo.ASTRONAUTA).texto());
        assertEquals("PROFESSOR · 8 CASAS",
                marcador(marcadores, MarcadoresDePassageiros.Tipo.PROFESSOR).texto());
    }

    private static MarcadoresDePassageiros.Marcador marcador(
            List<MarcadoresDePassageiros.Marcador> marcadores, MarcadoresDePassageiros.Tipo tipo) {
        return marcadores.stream().filter(m -> m.tipo() == tipo).findFirst().orElseThrow();
    }
}
