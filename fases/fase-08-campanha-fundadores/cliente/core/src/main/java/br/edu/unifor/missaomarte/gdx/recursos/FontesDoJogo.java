package br.edu.unifor.missaomarte.gdx.recursos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Carrega, fornece e libera as quatro fontes compartilhadas pela interface. */
public final class FontesDoJogo implements Disposable {
    private static final float ESCALA_DE_USO = 0.5f;

    private final Map<FonteDoJogo, BitmapFont> fontes = new EnumMap<>(FonteDoJogo.class);

    public void carregar() {
        for (FonteDoJogo tipo : FonteDoJogo.values()) {
            BitmapFont fonte = new BitmapFont(Gdx.files.internal(tipo.caminho()));
            fonte.getData().setScale(ESCALA_DE_USO);
            fonte.setUseIntegerPositions(false);
            for (TextureRegion regiao : fonte.getRegions()) {
                regiao.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            }
            fontes.put(tipo, fonte);
        }
    }

    public BitmapFont obter(FonteDoJogo tipo) {
        return fontes.get(tipo);
    }

    /** Executa um desenho com escala relativa e sempre restaura as metricas anteriores. */
    public void comEscalaTemporaria(float fator, Runnable desenho) {
        comEscalaTemporaria(fontes.values(), fator, desenho);
    }

    static void comEscalaTemporaria(Iterable<BitmapFont> fontes, float fator, Runnable desenho) {
        List<EscalaOriginal> originais = new ArrayList<>();
        for (BitmapFont fonte : fontes) {
            originais.add(new EscalaOriginal(fonte, fonte.getData().scaleX, fonte.getData().scaleY));
        }
        try {
            for (EscalaOriginal original : originais) {
                original.fonte().getData().setScale(original.x() * fator, original.y() * fator);
            }
            desenho.run();
        } finally {
            for (EscalaOriginal original : originais) {
                original.fonte().getData().setScale(original.x(), original.y());
            }
        }
    }

    @Override
    public void dispose() {
        for (BitmapFont fonte : fontes.values()) {
            fonte.dispose();
        }
        fontes.clear();
    }

    private record EscalaOriginal(BitmapFont fonte, float x, float y) {
    }
}
