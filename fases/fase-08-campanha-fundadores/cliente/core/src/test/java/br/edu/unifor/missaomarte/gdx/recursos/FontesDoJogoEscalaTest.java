package br.edu.unifor.missaomarte.gdx.recursos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

class FontesDoJogoEscalaTest {
    @Test
    void restauraEscalaPadraoDepoisDosDesenhosDoMenuEDasOpcoes() {
        BitmapFont titulo = fonteComEscala(0.5f);
        BitmapFont texto = fonteComEscala(0.5f);
        List<BitmapFont> fontes = List.of(titulo, texto);

        FontesDoJogo.comEscalaTemporaria(fontes, 0.8f, () -> {
            assertEquals(0.4f, titulo.getData().scaleX, 0.0001f);
            assertEquals(0.4f, texto.getData().scaleY, 0.0001f);
        });
        assertEscalaPadrao(titulo, texto);

        assertThrows(IllegalStateException.class,
                () -> FontesDoJogo.comEscalaTemporaria(fontes, 0.7f, () -> {
                    throw new IllegalStateException("falha simulada no desenho");
                }));
        assertEscalaPadrao(titulo, texto);
    }

    private static BitmapFont fonteComEscala(float escala) {
        BitmapFont.BitmapFontData dados = new BitmapFont.BitmapFontData();
        dados.setScale(escala);
        return new BitmapFont(dados, new TextureRegion(), false);
    }

    private static void assertEscalaPadrao(BitmapFont... fontes) {
        for (BitmapFont fonte : fontes) {
            assertEquals(0.5f, fonte.getData().scaleX, 0.0001f);
            assertEquals(0.5f, fonte.getData().scaleY, 0.0001f);
        }
    }
}
