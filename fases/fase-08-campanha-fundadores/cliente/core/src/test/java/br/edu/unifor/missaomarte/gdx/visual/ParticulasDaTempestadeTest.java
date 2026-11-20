package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.List;
import org.junit.jupiter.api.Test;

class ParticulasDaTempestadeTest {
    @Test
    void mesmasEntradasGeramAsMesmasParticulas() {
        RetanguloDeTela area = new RetanguloDeTela(10f, 20f, 300f, 180f);

        List<ParticulasDaTempestade.Particula> primeira = ParticulasDaTempestade.calcular(area, 2.5f);
        List<ParticulasDaTempestade.Particula> segunda = ParticulasDaTempestade.calcular(area, 2.5f);

        assertEquals(primeira, segunda);
    }

    @Test
    void particulasSeMovemComOTempoEPermanecemDentroDoMapa() {
        RetanguloDeTela area = new RetanguloDeTela(10f, 20f, 300f, 180f);
        List<ParticulasDaTempestade.Particula> inicio = ParticulasDaTempestade.calcular(area, 0f);
        List<ParticulasDaTempestade.Particula> depois = ParticulasDaTempestade.calcular(area, 1f);

        assertNotEquals(inicio.get(0), depois.get(0));
        for (ParticulasDaTempestade.Particula particula : depois) {
            assertTrue(particula.inicioX() >= area.x());
            assertTrue(particula.fimX() <= area.direita());
            assertTrue(particula.inicioY() >= area.y());
            assertTrue(particula.fimY() <= area.topo());
            assertTrue(particula.alfa() > 0f && particula.alfa() < 0.3f);
        }
    }

    @Test
    void limitaAQuantidadeEmAreasGrandes() {
        RetanguloDeTela area = new RetanguloDeTela(0f, 0f, 10_000f, 10_000f);

        assertTrue(ParticulasDaTempestade.calcular(area, 0f).size() <= 48);
    }
}
