package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.EstacaoNami;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.List;
import org.junit.jupiter.api.Test;

class VisibilidadeDaTempestadeTest {
    @Test
    void consideraAtivaQuandoAlgumCantoDoMapaEstaOculto() {
        assertFalse(VisibilidadeDaTempestade.estaAtiva(posicao -> true, -5, 5));
        assertTrue(VisibilidadeDaTempestade.estaAtiva(
                posicao -> !posicao.equals(new Posicao(5, 5)), -5, 5));
    }

    @Test
    void filtraObjetosComOMesmoPredicadoUsadoPeloMundo() {
        EstacaoNami visivel = new EstacaoNami(0, 0);
        EstacaoNami oculta = new EstacaoNami(4, 4);

        List<EstacaoNami> resultado = VisibilidadeDaTempestade.filtrar(
                List.of(visivel, oculta), posicao -> posicao.getX() <= 2);

        assertEquals(List.of(visivel), resultado);
    }
}
