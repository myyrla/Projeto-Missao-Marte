package br.edu.unifor.missaomarte.gdx.ui;

import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** Mede e desenha uma sequencia de teclas e descricoes no rodape. */
public final class RodapeDeTeclas {
    private static final float ESPACO_APOS_TECLA = 8f;
    private static final float ESPACO_ENTRE_ATALHOS = 20f;
    private static final Color SECUNDARIO = Color.valueOf("F2EEF7");

    private final GlyphLayout medidor = new GlyphLayout();

    public float desenhar(SpriteBatch batch, EstiloDaInterface estilo, float x, float y, Atalho... atalhos) {
        BitmapFont descricao = estilo.fonte(FonteDoJogo.PEQUENO);
        Color corAnterior = new Color(descricao.getColor());
        descricao.setColor(SECUNDARIO);
        float cursor = x;
        for (Atalho atalho : atalhos) {
            float larguraDaTecla = estilo.tecla(batch, atalho.tecla(), cursor, y);
            cursor += larguraDaTecla + ESPACO_APOS_TECLA;
            medidor.setText(descricao, atalho.descricao());
            descricao.draw(batch, medidor, cursor,
                    y + (EstiloDaInterface.ALTURA_DA_TECLA + medidor.height) / 2f);
            cursor += medidor.width + ESPACO_ENTRE_ATALHOS;
        }
        descricao.setColor(corAnterior);
        return cursor - x;
    }

    public record Atalho(String tecla, String descricao) {
    }
}
