package br.edu.unifor.missaomarte.gdx.ui;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.FontesDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.Recursos;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.EnumMap;
import java.util.Map;

/**
 * Fachada das pecas no estilo Switch. Os carregadores compartilhados continuam donos das texturas e fontes;
 * esta classe apenas monta NinePatch na escala logica e oferece operacoes de desenho consistentes.
 */
public final class EstiloDaInterface {
    public static final float ALTURA_DA_TECLA = 24f;

    private static final float MARGEM_HORIZONTAL_DA_TECLA = 8f;
    private static final float LARGURA_MINIMA_DA_TECLA = 32f;
    private static final Color TEXTO_DA_TECLA = Color.valueOf("0A0612");

    private final Map<PecaDaInterface, NinePatch> pecas = new EnumMap<>(PecaDaInterface.class);
    private final Recursos recursos;
    private final FontesDoJogo fontes;
    private final GlyphLayout medidor = new GlyphLayout();

    public EstiloDaInterface(Recursos recursos, FontesDoJogo fontes) {
        this.recursos = recursos;
        this.fontes = fontes;
        for (PecaDaInterface peca : PecaDaInterface.values()) {
            int corte = peca.corte();
            NinePatch patch = new NinePatch(recursos.textura(peca), corte, corte, corte, corte);
            patch.scale(0.5f, 0.5f);
            pecas.put(peca, patch);
        }
    }

    public BitmapFont fonte(FonteDoJogo tipo) {
        return fontes.obter(tipo);
    }

    public TextureRegion icone(IconeDaInterface icone) {
        return recursos.icone(icone);
    }

    public void painel(SpriteBatch batch, RetanguloDeTela area) {
        desenhar(PecaDaInterface.PAINEL, batch, area, Color.WHITE);
    }

    public void botao(SpriteBatch batch, RetanguloDeTela area, boolean foco) {
        desenhar(foco ? PecaDaInterface.BOTAO_FOCO : PecaDaInterface.BOTAO, batch, area, Color.WHITE);
    }

    public void ficha(SpriteBatch batch, RetanguloDeTela area, Color corDaBorda) {
        desenhar(PecaDaInterface.CHIP, batch, area, Color.WHITE);
        if (corDaBorda != null) {
            desenhar(PecaDaInterface.CHIP_BORDA, batch, area, corDaBorda);
        }
    }

    public float tecla(SpriteBatch batch, String rotulo, float x, float y) {
        BitmapFont fonte = fonte(FonteDoJogo.ROTULO);
        medidor.setText(fonte, rotulo);
        float largura = larguraDaTecla(medidor.width);
        desenhar(PecaDaInterface.TECLA, batch,
                new RetanguloDeTela(x, y, largura, ALTURA_DA_TECLA), Color.WHITE);

        Color corAnterior = new Color(fonte.getColor());
        fonte.setColor(TEXTO_DA_TECLA);
        fonte.draw(batch, medidor, x + (largura - medidor.width) / 2f,
                y + (ALTURA_DA_TECLA + medidor.height) / 2f);
        fonte.setColor(corAnterior);
        return largura;
    }

    public void segmento(SpriteBatch batch, RetanguloDeTela area, Color cor) {
        desenhar(PecaDaInterface.SEGMENTO, batch, area, cor);
    }

    public void barraFundo(SpriteBatch batch, RetanguloDeTela area) {
        desenhar(PecaDaInterface.BARRA_FUNDO, batch, area, Color.WHITE);
    }

    static float larguraDaTecla(float larguraDoTexto) {
        return Math.max(LARGURA_MINIMA_DA_TECLA, larguraDoTexto + MARGEM_HORIZONTAL_DA_TECLA * 2f);
    }

    private void desenhar(PecaDaInterface peca, SpriteBatch batch, RetanguloDeTela area, Color cor) {
        NinePatch patch = pecas.get(peca);
        patch.setColor(cor);
        patch.draw(batch, area.x(), area.y(), area.largura(), area.altura());
        patch.setColor(Color.WHITE);
    }
}
