package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.gdx.MapaGeometria;
import br.edu.unifor.missaomarte.gdx.MissaoMarteGame;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.ImagemDoMapa;
import br.edu.unifor.missaomarte.gdx.recursos.Recursos;
import br.edu.unifor.missaomarte.gdx.recursos.SimboloParaImagem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.Collections;
import java.util.List;

/**
 * Desenha o mundo do jogo: terreno repetido, grade, limites, objetos, nave, efeitos e textos flutuantes.
 * <p>O sprite de cada objeto vem do simbolo que ele informa ({@link SimboloParaImagem}); nenhuma classe de dominio conhece texturas.
 * <p>SOLID: SRP (so desenho do mundo). Nao contem regra de jogo.
 */
public class MundoRenderer {
    private static final Color VERDE = new Color(0.45f, 1f, 0.6f, 1f);
    private static final Color VERMELHO = new Color(1f, 0.4f, 0.4f, 1f);
    private static final Color FORA_DO_MAPA = new Color(Color.valueOf("0A0612")).mul(1f, 1f, 1f, 0.55f);
    private static final float REPETICAO_DO_TERRENO = 512f;

    private final Recursos recursos;
    private final SpriteBatch batch;
    private final ShapeRenderer formas;
    private final BitmapFont fonte;
    private MapaGeometria mapa;
    private final GlyphLayout layout = new GlyphLayout();
    private final Animation<TextureRegion> animacaoBase;
    private final Animation<TextureRegion> animacaoInimigo;
    private final Animation<TextureRegion> animacaoPerseguidor;
    private final Animation<TextureRegion> animacaoPropulsor;

    public MundoRenderer(Recursos recursos, SpriteBatch batch, ShapeRenderer formas, BitmapFont fonte,
            MapaGeometria mapa) {
        this.recursos = recursos;
        this.batch = batch;
        this.formas = formas;
        this.fonte = fonte;
        this.mapa = mapa;
        this.animacaoBase = recursos.animacao(Imagem.BASE, 0.25f, PlayMode.LOOP);
        this.animacaoInimigo = recursos.animacao(Imagem.INIMIGO, 0.3f, PlayMode.LOOP);
        this.animacaoPerseguidor = recursos.animacao(Imagem.PERSEGUIDOR, 0.2f, PlayMode.LOOP);
        this.animacaoPropulsor = recursos.animacao(Imagem.PROPULSOR, 0.08f, PlayMode.LOOP);
    }

    /** A geometria muda quando a tela muda de tamanho ou de orientacao; o desenho passa a usar a nova no proximo quadro. */
    public void definirMapa(MapaGeometria novoMapa) {
        this.mapa = novoMapa;
    }

    /**
     * @param anguloNave graus no sentido anti-horario (0 aponta para cima)
     * @param naveVisivel false em quadros alternados durante o piscar apos uma colisao
     * @param deslocX deslocamento do tremor da tela
     */
    public void desenhar(Missao missao, PosicoesVisuais posicoes, EfeitosVisuais efeitos, MensagensFlutuantes flutuantes,
            float tempo, float anguloNave, boolean naveVisivel, float deslocX, float deslocY,
            RetanguloDeTela areaVisivel) {
        desenharTerreno(areaVisivel);
        desenharGradeELimites(areaVisivel);

        batch.begin();
        desenharObjetos(Collections.singletonList(missao.getBase()), posicoes, tempo, deslocX, deslocY);
        desenharObjetos(missao.getAsteroides(), posicoes, tempo, deslocX, deslocY);
        desenharObjetos(missao.getPassageiros(), posicoes, tempo, deslocX, deslocY);
        desenharObjetos(missao.getInimigos(), posicoes, tempo, deslocX, deslocY);

        if (naveVisivel) {
            desenharNave(missao, posicoes, tempo, anguloNave, deslocX, deslocY);
        }

        desenharEfeitos(efeitos, deslocX, deslocY);
        desenharFlutuantes(flutuantes, deslocX, deslocY);

        batch.end();
    }

    private void desenharTerreno(RetanguloDeTela areaVisivel) {
        Texture terreno = recursos.textura(ImagemDoMapa.TERRENO);
        float u = areaVisivel.x() / REPETICAO_DO_TERRENO;
        float v = areaVisivel.y() / REPETICAO_DO_TERRENO;
        float u2 = areaVisivel.direita() / REPETICAO_DO_TERRENO;
        float v2 = areaVisivel.topo() / REPETICAO_DO_TERRENO;
        batch.begin();
        batch.draw(terreno, areaVisivel.x(), areaVisivel.y(), areaVisivel.largura(), areaVisivel.altura(),
                u, v, u2, v2);
        batch.end();
    }

    private void desenharGradeELimites(RetanguloDeTela areaVisivel) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Line);
        formas.setColor(1f, 1f, 1f, 0.05f);
        for (int i = 0; i <= MissaoMarteGame.MAX - MissaoMarteGame.MIN + 1; i++) {
            float deslocamento = i * mapa.celula();
            formas.line(mapa.origemX() + deslocamento, mapa.origemY(),
                    mapa.origemX() + deslocamento, mapa.origemY() + mapa.altura());
            formas.line(mapa.origemX(), mapa.origemY() + deslocamento,
                    mapa.origemX() + mapa.largura(), mapa.origemY() + deslocamento);
        }
        formas.end();

        formas.begin(ShapeType.Filled);
        formas.setColor(FORA_DO_MAPA);
        desenharVeuExterno(areaVisivel);
        formas.setColor(1f, 1f, 1f, 0.30f);
        linhaTracejadaHorizontal(mapa.origemX(), mapa.origemX() + mapa.largura(), mapa.origemY());
        linhaTracejadaHorizontal(mapa.origemX(), mapa.origemX() + mapa.largura(), mapa.origemY() + mapa.altura());
        linhaTracejadaVertical(mapa.origemY(), mapa.origemY() + mapa.altura(), mapa.origemX());
        linhaTracejadaVertical(mapa.origemY(), mapa.origemY() + mapa.altura(), mapa.origemX() + mapa.largura());
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void desenharVeuExterno(RetanguloDeTela area) {
        float mapaEsquerda = mapa.origemX();
        float mapaDireita = mapaEsquerda + mapa.largura();
        float mapaBase = mapa.origemY();
        float mapaTopo = mapaBase + mapa.altura();
        if (area.x() < mapaEsquerda) {
            formas.rect(area.x(), area.y(), mapaEsquerda - area.x(), area.altura());
        }
        if (area.direita() > mapaDireita) {
            formas.rect(mapaDireita, area.y(), area.direita() - mapaDireita, area.altura());
        }
        float inicioX = Math.max(area.x(), mapaEsquerda);
        float fimX = Math.min(area.direita(), mapaDireita);
        float largura = Math.max(0f, fimX - inicioX);
        if (area.y() < mapaBase && largura > 0f) {
            formas.rect(inicioX, area.y(), largura, mapaBase - area.y());
        }
        if (area.topo() > mapaTopo && largura > 0f) {
            formas.rect(inicioX, mapaTopo, largura, area.topo() - mapaTopo);
        }
    }

    private void linhaTracejadaHorizontal(float inicio, float fim, float y) {
        for (float x = inicio; x < fim; x += 24f) {
            formas.rect(x, y - 0.75f, Math.min(14f, fim - x), 1.5f);
        }
    }

    private void linhaTracejadaVertical(float inicio, float fim, float x) {
        for (float y = inicio; y < fim; y += 24f) {
            formas.rect(x - 0.75f, y, 1.5f, Math.min(14f, fim - y));
        }
    }

    private void desenharObjetos(List<? extends ObjetoRenderizavel> objetos, PosicoesVisuais posicoes, float tempo, float dx, float dy) {
        float celula = mapa.celula();
        for (ObjetoRenderizavel objeto : objetos) {
            PosicaoVisual visual = posicoes.de(objeto);
            batch.draw(regiao(objeto, tempo), mapa.xCanto(visual.x()) + dx, mapa.yCanto(visual.y()) + dy, celula, celula);
        }
    }

    private TextureRegion regiao(ObjetoRenderizavel objeto, float tempo) {
        Imagem imagem = SimboloParaImagem.de(objeto.getSimbolo());
        switch (imagem) {
            case BASE:
                return animacaoBase.getKeyFrame(tempo, true);
            case INIMIGO:
                return animacaoInimigo.getKeyFrame(tempo, true);
            case PERSEGUIDOR:
                return animacaoPerseguidor.getKeyFrame(tempo, true);
            default:
                return recursos.quadro(imagem);
        }
    }

    private void desenharNave(Missao missao, PosicoesVisuais posicoes, float tempo, float angulo, float dx, float dy) {
        float celula = mapa.celula();
        PosicaoVisual visual = posicoes.de(missao.getNave());
        float x = mapa.xCanto(visual.x()) + dx;
        float y = mapa.yCanto(visual.y()) + dy;
        batch.draw(animacaoPropulsor.getKeyFrame(tempo, true), x, y, celula / 2f, celula / 2f, celula, celula, 1f, 1f, angulo);
        batch.draw(recursos.quadro(Imagem.NAVE), x, y, celula / 2f, celula / 2f, celula, celula, 1f, 1f, angulo);
    }

    private void desenharEfeitos(EfeitosVisuais efeitos, float dx, float dy) {
        for (EfeitosVisuais.Efeito efeito : efeitos.ativos()) {
            TextureRegion[] quadros = recursos.quadros(efeito.imagem());
            float tamanho = mapa.celula() * (efeito.imagem() == Imagem.EXPLOSAO ? 1.6f : 1.2f);
            batch.draw(quadros[efeito.quadro(quadros.length)],
                    mapa.xCentro(efeito.x()) - tamanho / 2f + dx, mapa.yCentro(efeito.y()) - tamanho / 2f + dy, tamanho, tamanho);
        }
    }

    private void desenharFlutuantes(MensagensFlutuantes flutuantes, float dx, float dy) {
        for (MensagensFlutuantes.Mensagem mensagem : flutuantes.ativas()) {
            Color base = mensagem.positiva() ? VERDE : VERMELHO;
            fonte.setColor(base.r, base.g, base.b, mensagem.alfa());
            layout.setText(fonte, mensagem.texto());
            fonte.draw(batch, layout, mapa.xCentro(mensagem.x()) - layout.width / 2f + dx,
                    mapa.yCentro(mensagem.y()) + layout.height / 2f + dy);
        }
        fonte.setColor(Color.WHITE);
    }
}
