package br.edu.unifor.missaomarte.gdx.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.campanha.Construcao;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class CampusLayoutTest {
    @Test
    void todasAsConstrucoesPossuemAreaDentroDoMapa() {
        CampusLayout layout = CampusLayout.padrao();

        for (Construcao construcao : Construcao.values()) {
            assertTrue(layout.areas().stream().anyMatch(area -> area.construcao() == construcao),
                    "construção sem área: " + construcao);
        }
        layout.areas().forEach(area -> assertTrue(area.retangulo().estaDentroDe(layout.mapa()),
                "área fora do mapa: " + area.rotulo()));
    }

    @Test
    void campusRepresentaOsSeteAcessosDePousoEOsBlocosPrincipais() {
        CampusLayout layout = CampusLayout.padrao();

        assertEquals(7, layout.areas().stream()
                .filter(area -> area.construcao() == Construcao.ZONAS_DE_POUSO).count());
        assertTrue(layout.areas().stream().anyMatch(area -> "Blocos A-E".equals(area.rotulo())));
        assertTrue(layout.areas().stream().anyMatch(area -> "Blocos N-R".equals(area.rotulo())));
        assertEquals(Arrays.asList("Estádio", "Ginásio", "Gelo de água"),
                layout.decoracoes().stream().map(CampusLayout.Decoracao::rotulo).toList());
    }
}
