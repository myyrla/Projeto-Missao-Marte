package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.ModoDeJogo;
import java.util.List;
import org.junit.jupiter.api.Test;

class LinhasDoStatusTest {
    @Test
    void separaCadaInformacaoDoModoDinamicoEmUmaLinha() {
        assertEquals(List.of("MUNIÇÃO 5", "MIRA DIREITA", "TEMPO 01:05", "SOL 21",
                        "VALLES MARINERIS", "SOM DESLIGADO"),
                LinhasDoStatus.para(ModoDeJogo.DINAMICO, 5, Acao.DIREITA, 0, 65,
                        21, "Valles Marineris", true));
    }

    @Test
    void separaCadaInformacaoDoModoClassicoEmUmaLinha() {
        assertEquals(List.of("MOVIMENTOS 12", "TEMPO 00:09"),
                LinhasDoStatus.para(ModoDeJogo.CLASSICO, 0, Acao.CIMA, 12, 9,
                        null, null, false));
    }
}
