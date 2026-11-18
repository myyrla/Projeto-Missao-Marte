package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RastroDaNaveTest {
    @Test
    void guardaSomenteAsDozeCasasMaisRecentes() {
        RastroDaNave rastro = new RastroDaNave();

        for (int x = 0; x <= 12; x++) {
            rastro.registrar(x, 4);
        }

        assertEquals(12, rastro.casas().size());
        assertEquals(new RastroDaNave.Casa(1, 4), rastro.casas().get(0));
        assertEquals(new RastroDaNave.Casa(12, 4), rastro.casas().get(11));
    }

    @Test
    void naoRepeteAMesmaCasaEmSequencia() {
        RastroDaNave rastro = new RastroDaNave();

        rastro.registrar(2, 3);
        rastro.registrar(2, 3);
        rastro.registrar(3, 3);

        assertEquals(2, rastro.casas().size());
        assertEquals(new RastroDaNave.Casa(2, 3), rastro.casas().get(0));
        assertEquals(new RastroDaNave.Casa(3, 3), rastro.casas().get(1));
    }
}
