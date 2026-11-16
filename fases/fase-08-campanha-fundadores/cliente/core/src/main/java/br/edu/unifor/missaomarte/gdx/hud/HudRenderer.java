package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.ModoDeJogo;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.gdx.layout.LayoutDaPartida;
import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.layout.TextoResponsivo;
import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.FontesDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.Recursos;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;
import java.util.List;

/**
 * HUD da partida: barra superior (vidas, pontuacao, tempo, som), painel de informacoes e rodape.
 * <p>As posicoes vem do {@link LayoutDaPartida}: em paisagem o painel fica ao lado do mapa (como nas fases anteriores); em retrato
 * vira uma faixa abaixo do mapa. O fator recebido ajusta os espacamentos responsivos sem redimensionar as fontes.
 * <p>E desenhado depois do mundo, para ficar por cima. Apenas le o estado da {@link GameSession}; nao altera nada.
 * <p>SOLID: SRP (so apresentacao do HUD).
 */
public class HudRenderer {
    private static final float BARRA_ALTURA = 12f;
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);
    private static final Color CINZA = new Color(0.75f, 0.75f, 0.8f, 1f);

    private final Recursos recursos;
    private final SpriteBatch batch;
    private final ShapeRenderer formas;
    private final FontesDoJogo fontes;
    private final GlyphLayout medidor = new GlyphLayout();
    private int vidasMaximas;
    private LayoutDaPartida layout;
    private MargensSeguras margens = MargensSeguras.NENHUMA;
    private boolean modoToque;
    private float fator = 1f;

    public HudRenderer(Recursos recursos, SpriteBatch batch, ShapeRenderer formas, FontesDoJogo fontes) {
        this.recursos = recursos;
        this.batch = batch;
        this.formas = formas;
        this.fontes = fontes;
    }

    /** Layout do quadro atual, margens seguras, se os controles sao por toque e o multiplicador do texto. */
    public void definirLayout(LayoutDaPartida layout, MargensSeguras margens, boolean modoToque, float fatorDeTexto) {
        this.layout = layout;
        this.margens = margens;
        this.modoToque = modoToque;
        this.fator = Math.max(1f, fatorDeTexto);
    }

    public void desenhar(GameSession session, List<String> mensagens, boolean mudo, long segundos) {
        Nave nave = session.getMissao().getNave();
        vidasMaximas = Math.max(vidasMaximas, nave.getVidas());

        desenharFundos(session);

        batch.begin();
        desenharBarraSuperior(session, nave, mudo, segundos);
        if (layout.emRetrato()) {
            desenharFaixa(session, nave, mensagens);
        } else {
            desenharPainel(session, nave, mensagens);
        }
        desenharRodape(session);
        batch.end();
    }

    private void desenharFundos(GameSession session) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(0f, 0f, 0f, 0.55f);
        retangulo(layout.barraSuperior());
        retangulo(layout.barraInferior());
        formas.setColor(0f, 0f, 0f, 0.45f);
        retangulo(layout.painel());

        float progresso = session.getTotalPassageiros() == 0 ? 0f : (float) session.getResgatados() / session.getTotalPassageiros();
        RetanguloDeTela barra = areaDaBarraDeProgresso();
        formas.setColor(0.2f, 0.2f, 0.28f, 1f);
        formas.rect(barra.x(), barra.y(), barra.largura(), barra.altura());
        formas.setColor(0.3f, 0.85f, 0.5f, 1f);
        formas.rect(barra.x(), barra.y(), barra.largura() * progresso, barra.altura());
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void retangulo(RetanguloDeTela r) {
        formas.rect(r.x(), r.y(), r.largura(), r.altura());
    }

    // Em paisagem a barra fica abaixo da linha "Entregues"; em retrato, no pe da faixa.
    private RetanguloDeTela areaDaBarraDeProgresso() {
        RetanguloDeTela painel = layout.painel();
        if (layout.emRetrato()) {
            return new RetanguloDeTela(painel.x() + 8f, painel.y() + 6f, painel.largura() - 16f, 8f);
        }
        BitmapFont fonte = fontes.obter(FonteDoJogo.PEQUENO);
        float y = yDasEntregas() - fonte.getLineHeight() - 6f * fator - BARRA_ALTURA;
        return new RetanguloDeTela(painel.x() + 10f, y, larguraDoTextoNoPainel(), BARRA_ALTURA);
    }

    // Linha de base do texto "Entregues" no painel lateral, calculada pelas metricas reais das fontes.
    private float yDasEntregas() {
        float y = layout.painel().topo() - 12f;
        y = linhaAbaixo(y, fontes.obter(FonteDoJogo.SUBTITULO), 4f);
        y = linhaAbaixo(y, fontes.obter(FonteDoJogo.PEQUENO), 3f);
        y = linhaAbaixo(y, fontes.obter(FonteDoJogo.PEQUENO), 8f);
        y = linhaAbaixo(y, fontes.obter(FonteDoJogo.TEXTO), 4f);
        return y - 24f - 8f * fator;
    }

    private void desenharBarraSuperior(GameSession session, Nave nave, boolean mudo, long segundos) {
        RetanguloDeTela barra = layout.barraSuperior();
        float x0 = 24f + margens.esquerda();
        for (int i = 0; i < vidasMaximas; i++) {
            Imagem icone = i < nave.getVidas() ? Imagem.ICONE_VIDA : Imagem.ICONE_VIDA_VAZIA;
            batch.draw(recursos.quadro(icone), x0 + i * 28f, barra.y() + 6f, 24f, 24f);
        }

        BitmapFont fonte = fontes.obter(FonteDoJogo.PEQUENO);
        float baseline = barra.y() + 26f;
        float xPontos = Math.max(220f, x0 + vidasMaximas * 28f + 16f);
        float xTempo = xPontos + 200f;
        float xSom = xTempo + 200f + 20f;
        fonte.setColor(Color.WHITE);
        fonte.draw(batch, limitar("Pontuação: " + session.getPontuacao(), fonte, 180f), xPontos, baseline);
        fonte.draw(batch, limitar(String.format("Tempo: %02d:%02d", segundos / 60, segundos % 60), fonte, 180f),
                xTempo, baseline);
        fonte.setColor(mudo ? CINZA : Color.WHITE);
        String som = modoToque ? "Som: " + (mudo ? "off" : "on") : "Som: " + (mudo ? "desligado" : "ligado") + " (M)";
        fonte.draw(batch, limitar(som, fonte, 170f), xSom, baseline);
        fonte.setColor(Color.WHITE);
    }

    private void desenharPainel(GameSession session, Nave nave, List<String> mensagens) {
        RetanguloDeTela painel = layout.painel();
        float x = painel.x() + 10f;
        float y = painel.topo() - 12f;
        float largura = larguraDoTextoNoPainel();

        BitmapFont cabecalho = fontes.obter(FonteDoJogo.SUBTITULO);
        cabecalho.setColor(DESTAQUE);
        cabecalho.draw(batch, limitar("MISSÃO MARTE", cabecalho, largura), x, y);

        BitmapFont corpo = fontes.obter(FonteDoJogo.PEQUENO);
        corpo.setColor(Color.WHITE);
        y = linhaAbaixo(y, cabecalho, 4f);
        corpo.draw(batch, limitar("Piloto: " + session.getPiloto(), corpo, largura), x, y);
        y = linhaAbaixo(y, corpo, 3f);
        corpo.draw(batch, limitar(formatarDificuldade(session.getDificuldade()), corpo, largura), x, y);

        int aBordo = nave.getPassageiros().size();
        BitmapFont destaque = fontes.obter(FonteDoJogo.TEXTO);
        destaque.setColor(Color.WHITE);
        y = linhaAbaixo(y, corpo, 8f);
        destaque.draw(batch, limitar("A bordo: " + aBordo + "/" + nave.getCapacidade(), destaque, largura), x, y);
        y = linhaAbaixo(y, destaque, 4f) - 24f;
        for (int i = 0; i < nave.getCapacidade(); i++) {
            Imagem icone = i < aBordo ? Imagem.ICONE_PASSAGEIRO : Imagem.ICONE_VAGA;
            batch.draw(recursos.quadro(icone), x + i * 25f, y, 24f, 24f);
        }

        y -= 8f * fator;
        corpo.draw(batch, limitar("Entregues: " + session.getResgatados() + "/" + session.getTotalPassageiros(), corpo,
                largura), x, y);
        RetanguloDeTela barra = areaDaBarraDeProgresso();
        y = barra.y() - 8f * fator;
        corpo.draw(batch, limitar("No mapa: " + session.getMissao().getPassageiros().size(), corpo, largura), x, y);
        y = linhaAbaixo(y, corpo, 3f);
        if (session.getModo() == ModoDeJogo.DINAMICO) {
            corpo.draw(batch, limitar(formatarMunicaoEMira(session.getMunicao(), session.getMira()), corpo, largura), x, y);
        } else {
            corpo.draw(batch, limitar("Mov.: " + session.getMovimentos(), corpo, largura), x, y);
        }

        if (!mensagens.isEmpty()) {
            y = linhaAbaixo(y, corpo, 8f);
            corpo.setColor(DESTAQUE);
            corpo.draw(batch, String.join("\n", mensagens), x, y, largura, Align.left, true);
            corpo.setColor(Color.WHITE);
        }
    }

    // Faixa do modo retrato: tres linhas de texto e a barra de progresso no pe.
    private void desenharFaixa(GameSession session, Nave nave, List<String> mensagens) {
        RetanguloDeTela faixa = layout.painel();
        float x = faixa.x() + 8f;
        float y = faixa.topo() - 8f;

        BitmapFont fonte = fontes.obter(FonteDoJogo.PEQUENO);
        float linha = fonte.getLineHeight() + 4f * fator;
        fonte.setColor(Color.WHITE);
        float larguraDaLinha = faixa.largura() - 16f;
        fonte.draw(batch, limitar(formatarDificuldade(session.getDificuldade()) + "   Piloto: " + session.getPiloto(), fonte,
                larguraDaLinha), x, y);
        y -= linha;
        fonte.draw(batch, limitar("Entregues: " + session.getResgatados() + "/" + session.getTotalPassageiros()
                + "   A bordo: " + nave.getPassageiros().size() + "/" + nave.getCapacidade(), fonte, larguraDaLinha), x, y);
        y -= linha;
        if (session.getModo() == ModoDeJogo.DINAMICO) {
            fonte.draw(batch, formatarMunicaoEMira(session.getMunicao(), session.getMira()), x, y);
        } else {
            fonte.draw(batch, "Mov.: " + session.getMovimentos(), x, y);
        }

        if (!mensagens.isEmpty()) {
            fonte.setColor(DESTAQUE);
            float xMensagem = faixa.x() + faixa.largura() * 0.56f;
            fonte.draw(batch, String.join(" ", mensagens), xMensagem, faixa.topo() - 8f, faixa.largura() * 0.42f, Align.left, true);
            fonte.setColor(Color.WHITE);
        }
    }

    private void desenharRodape(GameSession session) {
        RetanguloDeTela barra = layout.barraInferior();
        BitmapFont fonte = fontes.obter(FonteDoJogo.PEQUENO);
        fonte.setColor(CINZA);
        String texto;
        if (modoToque) {
            texto = session.getModo() == ModoDeJogo.DINAMICO
                    ? "Segure as setas para mover. Atirar usa munição."
                    : "Toque nas setas para mover e em Embarcar para pegar ou entregar passageiros";
        } else {
            texto = session.getModo() == ModoDeJogo.DINAMICO
                    ? "W/A/S/D ou setas (segure): mover   F ou Espaço: atirar   P: pausa   M: som   Q ou Esc: abortar"
                    : "W/A/S/D ou setas: mover   E: embarcar   M: som   Q ou Esc: abortar";
        }
        fonte.draw(batch, limitar(texto, fonte, barra.largura() - 48f - margens.esquerda() - margens.direita()),
                24f + margens.esquerda(), barra.topo() - 10f);
        fonte.setColor(Color.WHITE);
    }

    private String limitar(String texto, BitmapFont fonte, float larguraMaxima) {
        return TextoResponsivo.limitarComReticencias(texto, larguraMaxima, candidato -> {
            medidor.setText(fonte, candidato);
            return medidor.width;
        });
    }

    private float larguraDoTextoNoPainel() {
        return Math.max(1f, layout.painel().largura() - 20f);
    }

    private float linhaAbaixo(float linhaAtual, BitmapFont fonte, float respiro) {
        return linhaAtual - fonte.getLineHeight() - respiro * fator;
    }

    static String formatarDificuldade(Dificuldade dificuldade) {
        return "Dif.: " + dificuldade;
    }

    static String formatarMunicaoEMira(int municao, Acao mira) {
        return "Mun. " + municao + "   Mira " + simboloDaMira(mira);
    }

    private static String simboloDaMira(Acao mira) {
        switch (mira) {
            case ESQUERDA:
                return "<";
            case CIMA:
                return "^";
            case BAIXO:
                return "v";
            default:
                return ">";
        }
    }
}
