package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.layout.TextoResponsivo;
import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.FontesDoJogo;
import br.edu.unifor.missaomarte.gdx.ui.ControleDoFimDaPartida;
import br.edu.unifor.missaomarte.gdx.ui.EstiloDaInterface;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;

/** Mede e desenha o modal de resultado sem conhecer sessão, ranking ou navegação. */
public final class ResultadoDaPartidaRenderer {
    private static final Color VITORIA = Color.valueOf("9EF01A");
    private static final Color DERROTA = Color.valueOf("FF3B5C");
    private static final Color TEXTO = Color.valueOf("F2EEF7");
    private static final Color TEXTO_SECUNDARIO = Color.valueOf("B9B2C6");
    private static final Color TEXTO_DO_BOTAO_EM_FOCO = Color.valueOf("0A0612");

    private final SpriteBatch batch;
    private final ShapeRenderer formas;
    private final FontesDoJogo fontes;
    private final EstiloDaInterface estilo;
    private final GlyphLayout medidor = new GlyphLayout();

    public ResultadoDaPartidaRenderer(SpriteBatch batch, ShapeRenderer formas,
            FontesDoJogo fontes, EstiloDaInterface estilo) {
        this.batch = batch;
        this.formas = formas;
        this.fontes = fontes;
        this.estilo = estilo;
    }

    public LayoutDoResultadoDaPartida medirLayout(float largura, float altura,
            MargensSeguras margens, float alvoMinimo, ApresentacaoDoResultadoDaPartida apresentacao) {
        BitmapFont fonteDoTitulo = fonte(FonteDoJogo.TITULO);
        BitmapFont fonteDoTexto = fonte(FonteDoJogo.TEXTO);
        BitmapFont fonteDoRotulo = fonte(FonteDoJogo.ROTULO);
        BitmapFont fontePequena = fonte(FonteDoJogo.PEQUENO);
        BitmapFont fonteDoBotao = fonte(FonteDoJogo.SUBTITULO);
        float larguraDoTitulo = medirLargura(fonteDoTitulo, apresentacao.titulo());
        float larguraDoPrimeiroBotao = medirLargura(fonteDoBotao, "Tentar de novo");
        float larguraDoSegundoBotao = medirLargura(fonteDoBotao, apresentacao.rotuloDeSaida());
        MedidasDoResultadoDaPartida preliminares = new MedidasDoResultadoDaPartida(
                larguraDoTitulo, fonteDoTitulo.getLineHeight(), fonteDoTexto.getLineHeight(),
                fonteDoRotulo.getLineHeight(), fonteDoTitulo.getLineHeight(),
                fontePequena.getLineHeight(), larguraDoPrimeiroBotao,
                larguraDoSegundoBotao, fonteDoBotao.getLineHeight(), alvoMinimo);
        LayoutDoResultadoDaPartida preliminar = LayoutDoResultadoDaPartida.calcular(
                largura, altura, margens, preliminares);
        float alturaDoMotivo = medirAlturaQuebrada(
                fonteDoTexto, apresentacao.motivo(), preliminar.motivo().largura());
        MedidasDoResultadoDaPartida medidas = new MedidasDoResultadoDaPartida(
                larguraDoTitulo, fonteDoTitulo.getLineHeight(), alturaDoMotivo,
                fonteDoRotulo.getLineHeight(), fonteDoTitulo.getLineHeight(),
                fontePequena.getLineHeight(), larguraDoPrimeiroBotao,
                larguraDoSegundoBotao, fonteDoBotao.getLineHeight(), alvoMinimo);
        return LayoutDoResultadoDaPartida.calcular(largura, altura, margens, medidas);
    }

    public void desenhar(ApresentacaoDoResultadoDaPartida apresentacao, LayoutDoHud layout,
            LayoutDoResultadoDaPartida layoutDoToque, ControleDoFimDaPartida controle) {
        desenharFundo(layout);
        if (layoutDoToque == null) {
            fontes.comEscalaTemporaria(layout.escala(),
                    () -> desenharHorizontal(apresentacao, layout, controle));
        } else {
            desenharNoToque(apresentacao, layout, layoutDoToque, controle);
        }
    }

    private void desenharFundo(LayoutDoHud layout) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(0.02f, 0.01f, 0.05f, 0.78f);
        formas.rect(0f, 0f, layout.largura(), layout.altura());
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void desenharHorizontal(ApresentacaoDoResultadoDaPartida apresentacao,
            LayoutDoHud layout, ControleDoFimDaPartida controle) {
        RetanguloDeTela caixa = layout.painelDoFim();
        float escala = layout.escala();
        batch.begin();
        try {
            desenharPainelEBotoes(caixa, layout.botaoDoFim(0), layout.botaoDoFim(1), controle);
            textoCentralizado(FonteDoJogo.TITULO, apresentacao.vitoria() ? VITORIA : DERROTA,
                    apresentacao.titulo(), caixa.centroX(), caixa.topo() - 48f * escala);
            textoCentralizado(FonteDoJogo.TEXTO, TEXTO,
                    textoLimitado(FonteDoJogo.TEXTO, apresentacao.motivo(), caixa.largura() - 60f * escala),
                    caixa.centroX(), caixa.topo() - 91f * escala);
            textoCentralizado(FonteDoJogo.ROTULO, TEXTO_SECUNDARIO, "PONTOS",
                    caixa.centroX(), caixa.topo() - 132f * escala);
            textoCentralizado(FonteDoJogo.TITULO, TEXTO, apresentacao.pontos(),
                    caixa.centroX(), caixa.topo() - 164f * escala);
            desenharSituacao(apresentacao, caixa.centroX(), caixa.y() + 92f * escala, escala);
            desenharRotuloDoBotao("Tentar de novo", layout.botaoDoFim(0),
                    controle.emFoco(ControleDoFimDaPartida.Acao.TENTAR_DE_NOVO));
            desenharRotuloDoBotao(apresentacao.rotuloDeSaida(), layout.botaoDoFim(1),
                    controle.emFoco(ControleDoFimDaPartida.Acao.SAIR));
        } finally {
            batch.end();
            restaurarCores();
        }
    }

    private void desenharNoToque(ApresentacaoDoResultadoDaPartida apresentacao, LayoutDoHud layout,
            LayoutDoResultadoDaPartida resultado, ControleDoFimDaPartida controle) {
        batch.begin();
        try {
            desenharPainelEBotoes(resultado.painel(), resultado.botao(0), resultado.botao(1), controle);
            textoCentralizado(FonteDoJogo.TITULO, apresentacao.vitoria() ? VITORIA : DERROTA,
                    apresentacao.titulo(), resultado.titulo().centroX(), resultado.titulo().centroY());
            desenharTextoQuebradoCentralizado(apresentacao.motivo(), resultado.motivo());
            textoCentralizado(FonteDoJogo.ROTULO, TEXTO_SECUNDARIO, "PONTOS",
                    resultado.rotuloDosPontos().centroX(), resultado.rotuloDosPontos().centroY());
            textoCentralizado(FonteDoJogo.TITULO, TEXTO, apresentacao.pontos(),
                    resultado.pontos().centroX(), resultado.pontos().centroY());
            desenharSituacao(apresentacao, resultado.situacao().centroX(),
                    resultado.situacao().centroY(), layout.escala());
            desenharRotuloDoBotao("Tentar de novo", resultado.botao(0),
                    controle.emFoco(ControleDoFimDaPartida.Acao.TENTAR_DE_NOVO));
            desenharRotuloDoBotao(apresentacao.rotuloDeSaida(), resultado.botao(1),
                    controle.emFoco(ControleDoFimDaPartida.Acao.SAIR));
        } finally {
            batch.end();
            restaurarCores();
        }
    }

    private void desenharPainelEBotoes(RetanguloDeTela painel, RetanguloDeTela primeiro,
            RetanguloDeTela segundo, ControleDoFimDaPartida controle) {
        estilo.painel(batch, painel);
        estilo.botao(batch, primeiro,
                controle.emFoco(ControleDoFimDaPartida.Acao.TENTAR_DE_NOVO));
        estilo.botao(batch, segundo, controle.emFoco(ControleDoFimDaPartida.Acao.SAIR));
    }

    private void desenharTextoQuebradoCentralizado(String texto, RetanguloDeTela area) {
        BitmapFont fonte = fonte(FonteDoJogo.TEXTO);
        fonte.setColor(TEXTO);
        medidor.setText(fonte, texto, fonte.getColor(), area.largura(), Align.center, true);
        fonte.draw(batch, medidor, area.x(), area.topo());
    }

    private void desenharRotuloDoBotao(String rotulo, RetanguloDeTela botao, boolean foco) {
        textoCentralizado(FonteDoJogo.SUBTITULO, foco ? TEXTO_DO_BOTAO_EM_FOCO : TEXTO,
                rotulo, botao.centroX(), botao.centroY());
    }

    private void desenharSituacao(ApresentacaoDoResultadoDaPartida apresentacao,
            float centroX, float y, float escala) {
        Color cor = switch (apresentacao.envio()) {
            case ENVIANDO -> Color.LIGHT_GRAY;
            case ENVIADO -> VITORIA;
            case FALHOU -> DERROTA;
            case NAO_ENVIA -> null;
        };
        if (cor == null) {
            return;
        }
        textoCentralizado(FonteDoJogo.PEQUENO, cor,
                apresentacao.textoDoEnvio(), centroX, y);
        if (apresentacao.exibeAtalhoDeReenvio()) {
            textoCentralizado(FonteDoJogo.PEQUENO, cor,
                    "R: reenviar resultado", centroX, y - 16f * escala);
        }
    }

    private void textoCentralizado(FonteDoJogo tipo, Color cor, String texto,
            float centroX, float centroY) {
        BitmapFont fonte = fonte(tipo);
        fonte.setColor(cor);
        medidor.setText(fonte, texto);
        fonte.draw(batch, medidor, centroX - medidor.width / 2f, centroY + medidor.height / 2f);
    }

    private String textoLimitado(FonteDoJogo tipo, String texto, float larguraMaxima) {
        BitmapFont fonte = fonte(tipo);
        return TextoResponsivo.limitarComReticencias(texto, larguraMaxima, candidato -> {
            medidor.setText(fonte, candidato);
            return medidor.width;
        });
    }

    private float medirLargura(BitmapFont fonte, String texto) {
        medidor.setText(fonte, texto);
        return medidor.width;
    }

    private float medirAlturaQuebrada(BitmapFont fonte, String texto, float largura) {
        medidor.setText(fonte, texto, Color.WHITE, largura, Align.center, true);
        if (medidor.height <= 0f) {
            return 0f;
        }
        int linhas = Math.max(1,
                Math.round((medidor.height - fonte.getCapHeight()) / fonte.getLineHeight()) + 1);
        return linhas * fonte.getLineHeight();
    }

    private BitmapFont fonte(FonteDoJogo tipo) {
        return fontes.obter(tipo);
    }

    private void restaurarCores() {
        for (FonteDoJogo tipo : FonteDoJogo.values()) {
            fonte(tipo).setColor(Color.WHITE);
        }
    }
}
