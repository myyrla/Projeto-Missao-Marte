package br.edu.unifor.missaomarte.gdx.entrada;

import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.FontesDoJogo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.HashMap;
import java.util.Map;

/** Desenha o joystick e os botoes circulares; toda decisao de entrada fica nas classes puras. */
public final class ControleDeToqueRenderer {
    private static final Color BASE = Color.valueOf("161225CC");
    private static final Color ZONA_MORTA = Color.valueOf("34284C99");
    private static final Color BOTAO_DO_JOYSTICK = Color.valueOf("4CC9F0DD");
    private static final Color PRINCIPAL = Color.valueOf("FFB000EE");
    private static final Color SECUNDARIO = Color.valueOf("241A35EE");
    private static final Color BORDA = Color.valueOf("B388FFFF");
    private static final Color TEXTO_ESCURO = Color.valueOf("0A0612");

    private final SpriteBatch batch;
    private final ShapeRenderer formas;
    private final FontesDoJogo fontes;
    private final GlyphLayout medidor = new GlyphLayout();

    public ControleDeToqueRenderer(SpriteBatch batch, ShapeRenderer formas, FontesDoJogo fontes) {
        this.batch = batch;
        this.formas = formas;
        this.fontes = fontes;
    }

    /** Mede os mesmos rotulos e a mesma fonte usados no desenho, com a escala atualmente ativa. */
    public ControleDeToque.MedidasDosRotulos medirRotulos(boolean dinamico) {
        BitmapFont fonte = fontes.obter(FonteDoJogo.PEQUENO);
        Map<String, ControleDeToque.MedidaDoRotulo> medidas = new HashMap<>();
        medir(medidas, fonte, ControleDeToque.ABORTAR, "Abortar");
        medir(medidas, fonte, ControleDeToque.SOM, "Som");
        if (dinamico) {
            medir(medidas, fonte, ControleDeToque.ATIRAR, "Atirar");
            medir(medidas, fonte, ControleDeToque.PAUSA, "Pausa");
        } else {
            medir(medidas, fonte, ControleDeToque.EMBARCAR, "Embarcar");
        }
        return new ControleDeToque.MedidasDosRotulos(medidas);
    }

    private void medir(Map<String, ControleDeToque.MedidaDoRotulo> medidas,
            BitmapFont fonte, String id, String rotulo) {
        medidor.setText(fonte, rotulo);
        medidas.put(id, new ControleDeToque.MedidaDoRotulo(medidor.width, medidor.height));
    }

    public void desenhar(ControleDeToque controle) {
        JoystickVirtual joystick = controle.joystick().orElse(null);
        if (joystick == null) {
            return;
        }
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        try {
            formas.setColor(BASE);
            formas.circle(joystick.centroX(), joystick.centroY(), joystick.raio(), 48);
            formas.setColor(ZONA_MORTA);
            formas.circle(joystick.centroX(), joystick.centroY(), joystick.raio() * 0.25f, 32);
            formas.setColor(BOTAO_DO_JOYSTICK);
            formas.circle(joystick.botaoX(), joystick.botaoY(), joystick.raioDoBotao(), 40);
            for (BotaoVirtual botao : controle.getBotoes()) {
                formas.setColor(ControleDeToque.ATIRAR.equals(botao.getId())
                        || ControleDeToque.EMBARCAR.equals(botao.getId()) ? PRINCIPAL : SECUNDARIO);
                formas.circle(centroX(botao), centroY(botao), botao.getLargura() / 2f, 40);
            }
        } finally {
            formas.end();
        }
        formas.begin(ShapeType.Line);
        try {
            formas.setColor(BORDA);
            formas.circle(joystick.centroX(), joystick.centroY(), joystick.raio(), 48);
            for (BotaoVirtual botao : controle.getBotoes()) {
                formas.circle(centroX(botao), centroY(botao), botao.getLargura() / 2f, 40);
            }
        } finally {
            formas.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        batch.begin();
        try {
            for (BotaoVirtual botao : controle.getBotoes()) {
                desenharRotulo(controle, botao);
            }
        } finally {
            batch.end();
            for (FonteDoJogo tipo : FonteDoJogo.values()) {
                fontes.obter(tipo).setColor(Color.WHITE);
            }
            batch.setColor(Color.WHITE);
        }
    }

    private void desenharRotulo(ControleDeToque controle, BotaoVirtual botao) {
        boolean principal = ControleDeToque.ATIRAR.equals(botao.getId())
                || ControleDeToque.EMBARCAR.equals(botao.getId());
        BitmapFont fonte = fontes.obter(FonteDoJogo.PEQUENO);
        float escalaX = fonte.getData().scaleX;
        float escalaY = fonte.getData().scaleY;
        try {
            float fator = controle.escalaDoRotulo(botao.getId());
            fonte.getData().setScale(escalaX * fator, escalaY * fator);
            fonte.setColor(principal ? TEXTO_ESCURO : Color.WHITE);
            medidor.setText(fonte, botao.getRotulo());
            fonte.draw(batch, medidor, centroX(botao) - medidor.width / 2f,
                    centroY(botao) + medidor.height / 2f);
        } finally {
            fonte.getData().setScale(escalaX, escalaY);
        }
    }

    private static float centroX(BotaoVirtual botao) {
        return botao.getX() + botao.getLargura() / 2f;
    }

    private static float centroY(BotaoVirtual botao) {
        return botao.getY() + botao.getAltura() / 2f;
    }
}
