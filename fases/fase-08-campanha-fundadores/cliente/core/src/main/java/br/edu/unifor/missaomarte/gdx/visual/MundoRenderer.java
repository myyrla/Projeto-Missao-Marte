package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.gdx.MapaGeometria;
import br.edu.unifor.missaomarte.gdx.MissaoMarteGame;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.ImagemDoMapa;
import br.edu.unifor.missaomarte.gdx.recursos.Recursos;
import br.edu.unifor.missaomarte.gdx.recursos.SimboloParaImagem;
import br.edu.unifor.missaomarte.gdx.ui.EstiloDaInterface;
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
import java.util.function.Predicate;

/**
 * Desenha o mundo do jogo: terreno repetido, grade, limites, objetos, nave, efeitos e textos flutuantes.
 * <p>O sprite de cada objeto vem do simbolo que ele informa ({@link SimboloParaImagem}); nenhuma classe de dominio conhece texturas.
 * <p>SOLID: SRP (so desenho do mundo). Nao contem regra de jogo.
 */
public class MundoRenderer {
    private static final Color VERDE = new Color(0.45f, 1f, 0.6f, 1f);
    private static final Color VERMELHO = new Color(1f, 0.4f, 0.4f, 1f);
    private static final Color CIANO = Color.valueOf("4CC9F0");
    private static final Color AMBAR = Color.valueOf("FFB000");
    private static final Color VERDE_PASSAGEIRO = Color.valueOf("9EF01A");
    private static final Color VERMELHO_INIMIGO = Color.valueOf("FF3B5C");
    private static final Color LARANJA_PERSEGUIDOR = Color.valueOf("FF7B00");
    private static final Color FORA_DO_MAPA = new Color(Color.valueOf("0A0612")).mul(1f, 1f, 1f, 0.55f);
    private static final float REPETICAO_DO_TERRENO = 512f;
    private static final float TAMANHO_DO_OBJETO = 0.9f;
    private static final float ESPACAMENTO_DO_RASTRO = 10f;
    private static final float DURACAO_DO_CICLO_ESPECIAL = 0.5f;
    private static final float ALFA_DA_POEIRA = 0.45f;
    private static final Color ESCURIDAO_DA_TEMPESTADE = new Color(Color.valueOf("0A0612")).mul(1f, 1f, 1f, 0.82f);

    private final Recursos recursos;
    private final SpriteBatch batch;
    private final ShapeRenderer formas;
    private final BitmapFont fonte;
    private final BitmapFont fonteRotulo;
    private final EstiloDaInterface estilo;
    private MapaGeometria mapa;
    private final GlyphLayout layout = new GlyphLayout();
    private final Animation<TextureRegion> animacaoBase;
    private final Animation<TextureRegion> animacaoInimigo;
    private final Animation<TextureRegion> animacaoPerseguidor;
    private final Animation<TextureRegion> animacaoPropulsor;
    private final Animation<TextureRegion> animacaoNami;
    private final Animation<TextureRegion> animacaoParadaDoRover;

    public MundoRenderer(Recursos recursos, SpriteBatch batch, ShapeRenderer formas, BitmapFont fonte,
            BitmapFont fonteRotulo, EstiloDaInterface estilo, MapaGeometria mapa) {
        this.recursos = recursos;
        this.batch = batch;
        this.formas = formas;
        this.fonte = fonte;
        this.fonteRotulo = fonteRotulo;
        this.estilo = estilo;
        this.mapa = mapa;
        this.animacaoBase = recursos.animacao(Imagem.BASE, 0.25f, PlayMode.LOOP);
        this.animacaoInimigo = recursos.animacao(Imagem.INIMIGO, 0.3f, PlayMode.LOOP);
        this.animacaoPerseguidor = recursos.animacao(Imagem.PERSEGUIDOR, 0.2f, PlayMode.LOOP);
        this.animacaoPropulsor = recursos.animacao(Imagem.PROPULSOR, 0.08f, PlayMode.LOOP);
        this.animacaoNami = recursos.animacao(Imagem.NAMI, duracaoDoQuadroEspecial(), PlayMode.LOOP);
        this.animacaoParadaDoRover = recursos.animacao(Imagem.PARADA_ROVER, duracaoDoQuadroEspecial(), PlayMode.LOOP);
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
    public void desenhar(Missao missao, PosicoesVisuais posicoes, RastroDaNave rastro,
            EfeitosVisuais efeitos, MensagensFlutuantes flutuantes,
            float tempo, float anguloNave, boolean naveVisivel, float deslocX, float deslocY,
            RetanguloDeTela areaVisivel, Predicate<Posicao> estaVisivel, boolean tempestadeAtiva) {
        desenharTerreno(areaVisivel);
        desenharGradeELimites(areaVisivel);

        Color corAnterior = new Color(batch.getColor());
        batch.begin();
        try {
            desenharRastro(rastro, deslocX, deslocY);
            desenharAlcances(missao.getInimigos(), posicoes, deslocX, deslocY);
            desenharRotasDoRover(missao, deslocX, deslocY);
            desenharObjetos(Collections.singletonList(missao.getBase()), posicoes, tempo, deslocX, deslocY);
            desenharObjetos(missao.getEstacoesNami(), posicoes, tempo, deslocX, deslocY);
            desenharObjetos(missao.getParadasDoRover(), posicoes, tempo, deslocX, deslocY);
            desenharPassageiros(missao.getPassageiros(), posicoes, tempo, deslocX, deslocY);
            desenharObjetos(missao.getAsteroides(), posicoes, tempo, deslocX, deslocY);
            desenharObjetos(missao.getInimigos(), posicoes, tempo, deslocX, deslocY);

            if (naveVisivel) {
                desenharNave(missao, posicoes, tempo, anguloNave, deslocX, deslocY);
            }

            desenharEfeitos(efeitos, deslocX, deslocY);
        } finally {
            batch.setColor(corAnterior);
            batch.end();
        }
        if (tempestadeAtiva) {
            desenharEscuridao(estaVisivel);
            desenharPoeira(tempo);
        }
        batch.begin();
        try {
            desenharEtiquetas(missao, posicoes, deslocX, deslocY, estaVisivel);
            desenharFlutuantes(flutuantes, deslocX, deslocY);
        } finally {
            batch.setColor(corAnterior);
            batch.end();
        }
    }

    private void desenharRotasDoRover(Missao missao, float dx, float dy) {
        Texture ponto = recursos.textura(ImagemDoMapa.RASTRO_PONTO);
        float tamanho = 6f;
        batch.setColor(VERDE_PASSAGEIRO.r, VERDE_PASSAGEIRO.g, VERDE_PASSAGEIRO.b, 0.65f);
        for (RotasDoRover.Rota rota : RotasDoRover.agrupar(missao.getParadasDoRover())) {
            List<RastroDaNave.Casa> casas = List.of(
                    new RastroDaNave.Casa(rota.origem().getX(), rota.origem().getY()),
                    new RastroDaNave.Casa(rota.destino().getX(), rota.destino().getY()));
            for (GeometriaDoRastro.Ponto amostra : GeometriaDoRastro.calcular(casas,
                    MissaoMarteGame.MIN, MissaoMarteGame.MAX, mapa.celula(), mapa.origemX(), mapa.origemY(), 14f)) {
                batch.draw(ponto, amostra.x() - tamanho / 2f + dx, amostra.y() - tamanho / 2f + dy,
                        tamanho, tamanho);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void desenharEscuridao(Predicate<Posicao> estaVisivel) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        try {
            formas.setColor(ESCURIDAO_DA_TEMPESTADE);
            for (int y = MissaoMarteGame.MIN; y <= MissaoMarteGame.MAX; y++) {
                for (int x = MissaoMarteGame.MIN; x <= MissaoMarteGame.MAX; x++) {
                    if (!estaVisivel.test(new Posicao(x, y))) {
                        formas.rect(mapa.xCentro(x) - mapa.celula() / 2f,
                                mapa.yCentro(y) - mapa.celula() / 2f, mapa.celula(), mapa.celula());
                    }
                }
            }
        } finally {
            formas.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private void desenharPoeira(float tempo) {
        Texture poeira = recursos.textura(Imagem.POEIRA);
        float deslocamentoU = tempo * 0.012f;
        float deslocamentoV = tempo * 0.004f;
        float repeticoesX = mapa.largura() / Imagem.POEIRA.larguraQuadro();
        float repeticoesY = mapa.altura() / Imagem.POEIRA.alturaQuadro();
        batch.begin();
        try {
            batch.setColor(1f, 1f, 1f, ALFA_DA_POEIRA);
            batch.draw(poeira, mapa.origemX(), mapa.origemY(), mapa.largura(), mapa.altura(),
                    deslocamentoU, deslocamentoV, deslocamentoU + repeticoesX, deslocamentoV + repeticoesY);
        } finally {
            batch.setColor(Color.WHITE);
            batch.end();
        }
    }

    private void desenharRastro(RastroDaNave rastro, float dx, float dy) {
        Texture ponto = recursos.textura(ImagemDoMapa.RASTRO_PONTO);
        float tamanho = ImagemDoMapa.RASTRO_PONTO.larguraLogica();
        for (GeometriaDoRastro.Ponto amostra : GeometriaDoRastro.calcular(rastro.casas(),
                MissaoMarteGame.MIN, MissaoMarteGame.MAX, mapa.celula(), mapa.origemX(), mapa.origemY(),
                ESPACAMENTO_DO_RASTRO)) {
            batch.setColor(CIANO.r, CIANO.g, CIANO.b, amostra.alfa());
            batch.draw(ponto, amostra.x() - tamanho / 2f + dx, amostra.y() - tamanho / 2f + dy,
                    tamanho, tamanho);
        }
        batch.setColor(Color.WHITE);
    }

    private void desenharAlcances(List<Inimigo> inimigos, PosicoesVisuais posicoes, float dx, float dy) {
        Texture alcance = recursos.textura(ImagemDoMapa.ALCANCE);
        float tamanho = mapa.celula() * 3f;
        for (Inimigo inimigo : inimigos) {
            PosicaoVisual visual = posicoes.de(inimigo);
            Color cor = inimigo.persegue() ? LARANJA_PERSEGUIDOR : VERMELHO_INIMIGO;
            batch.setColor(cor);
            batch.draw(alcance, mapa.xCentro(visual.x()) - tamanho / 2f + dx,
                    mapa.yCentro(visual.y()) - tamanho / 2f + dy, tamanho, tamanho);
        }
        batch.setColor(Color.WHITE);
    }

    private void desenharPassageiros(List<Passageiro> passageiros, PosicoesVisuais posicoes,
            float tempo, float dx, float dy) {
        Texture pulso = recursos.textura(ImagemDoMapa.PULSO);
        for (Passageiro passageiro : passageiros) {
            PosicaoVisual visual = posicoes.de(passageiro);
            Color cor = cor(MarcadoresDePassageiros.Tipo.de(passageiro));
            desenharPulso(pulso, visual, cor, AnimacaoDoPulso.calcular(tempo, 0f), dx, dy);
            desenharPulso(pulso, visual, cor, AnimacaoDoPulso.calcular(tempo, 0.8f), dx, dy);
        }
        batch.setColor(Color.WHITE);
        desenharObjetos(passageiros, posicoes, tempo, dx, dy);
    }

    private void desenharPulso(Texture pulso, PosicaoVisual visual, Color cor,
            AnimacaoDoPulso.Estado estado, float dx, float dy) {
        float tamanho = ImagemDoMapa.PULSO.larguraLogica() * estado.escala();
        batch.setColor(cor.r, cor.g, cor.b, estado.alfa());
        batch.draw(pulso, mapa.xCentro(visual.x()) - tamanho / 2f + dx,
                mapa.yCentro(visual.y()) - tamanho / 2f + dy, tamanho, tamanho);
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
        float tamanho = tamanhoDoObjeto(mapa.celula());
        for (ObjetoRenderizavel objeto : objetos) {
            PosicaoVisual visual = posicoes.de(objeto);
            batch.draw(regiao(objeto, tempo), mapa.xCentro(visual.x()) - tamanho / 2f + dx,
                    mapa.yCentro(visual.y()) - tamanho / 2f + dy, tamanho, tamanho);
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
            case NAMI:
                return animacaoNami.getKeyFrame(tempo, true);
            case PARADA_ROVER:
                return animacaoParadaDoRover.getKeyFrame(tempo, true);
            default:
                return recursos.quadro(imagem);
        }
    }

    private void desenharNave(Missao missao, PosicoesVisuais posicoes, float tempo, float angulo, float dx, float dy) {
        float celula = mapa.celula();
        float tamanho = tamanhoDoObjeto(celula);
        PosicaoVisual visual = posicoes.de(missao.getNave());
        float centroX = mapa.xCentro(visual.x()) + dx;
        float centroY = mapa.yCentro(visual.y()) + dy;

        float larguraDaSombra = ImagemDoMapa.SOMBRA.larguraLogica();
        float alturaDaSombra = ImagemDoMapa.SOMBRA.alturaLogica();
        batch.draw(recursos.textura(ImagemDoMapa.SOMBRA),
                centroX - larguraDaSombra / 2f + celula * 0.08f,
                centroY - alturaDaSombra / 2f - celula * 0.12f,
                larguraDaSombra, alturaDaSombra);

        float x = centroX - tamanho / 2f;
        float y = centroY - tamanho / 2f;
        batch.draw(animacaoPropulsor.getKeyFrame(tempo, true), x, y,
                tamanho / 2f, tamanho / 2f, tamanho, tamanho, 1f, 1f, angulo);
        batch.draw(recursos.quadro(Imagem.NAVE), x, y,
                tamanho / 2f, tamanho / 2f, tamanho, tamanho, 1f, 1f, angulo);
    }

    private void desenharEfeitos(EfeitosVisuais efeitos, float dx, float dy) {
        for (EfeitosVisuais.Efeito efeito : efeitos.ativos()) {
            TextureRegion[] quadros = recursos.quadros(efeito.imagem());
            float tamanho = mapa.celula() * (efeito.imagem() == Imagem.EXPLOSAO ? 1.6f : 1.2f);
            batch.draw(quadros[efeito.quadro(quadros.length)],
                    mapa.xCentro(efeito.x()) - tamanho / 2f + dx, mapa.yCentro(efeito.y()) - tamanho / 2f + dy, tamanho, tamanho);
        }
    }

    private void desenharEtiquetas(Missao missao, PosicoesVisuais posicoes, float dx, float dy,
            Predicate<Posicao> estaVisivel) {
        Color corAnterior = new Color(fonteRotulo.getColor());
        try {
            fonteRotulo.setColor(Color.WHITE);
            for (MarcadoresDePassageiros.Marcador marcador : MarcadoresDePassageiros.selecionar(
                    missao.getNave().getPosicao(),
                    VisibilidadeDaTempestade.filtrar(missao.getPassageiros(), estaVisivel))) {
                PosicaoVisual visual = posicoes.de(marcador.passageiro());
                layout.setText(fonteRotulo, marcador.texto());
                float largura = layout.width + 24f;
                float altura = Math.max(28f, layout.height + 12f);
                float centroX = mapa.xCentro(visual.x()) + dx;
                float base = mapa.yCentro(visual.y()) + dy
                        + mapa.celula() * TAMANHO_DO_OBJETO / 2f + mapa.celula() / 2f;
                RetanguloDeTela area = new RetanguloDeTela(centroX - largura / 2f, base, largura, altura);
                estilo.ficha(batch, area, cor(marcador.tipo()));
                fonteRotulo.draw(batch, layout, area.centroX() - layout.width / 2f,
                        area.centroY() + layout.height / 2f);
            }
        } finally {
            fonteRotulo.setColor(corAnterior);
        }
    }

    private static Color cor(MarcadoresDePassageiros.Tipo tipo) {
        if (tipo == MarcadoresDePassageiros.Tipo.ASTRONAUTA) {
            return CIANO;
        }
        if (tipo == MarcadoresDePassageiros.Tipo.PROFESSOR) {
            return AMBAR;
        }
        return VERDE_PASSAGEIRO;
    }

    static float tamanhoDoObjeto(float celula) {
        return celula * TAMANHO_DO_OBJETO;
    }

    static float duracaoDoQuadroEspecial() {
        return DURACAO_DO_CICLO_ESPECIAL / 2f;
    }

    private void desenharFlutuantes(MensagensFlutuantes flutuantes, float dx, float dy) {
        Color corAnterior = new Color(fonte.getColor());
        try {
            for (MensagensFlutuantes.Mensagem mensagem : flutuantes.ativas()) {
                Color base = mensagem.positiva() ? VERDE : VERMELHO;
                fonte.setColor(base.r, base.g, base.b, mensagem.alfa());
                layout.setText(fonte, mensagem.texto());
                fonte.draw(batch, layout, mapa.xCentro(mensagem.x()) - layout.width / 2f + dx,
                        mapa.yCentro(mensagem.y()) + layout.height / 2f + dy);
            }
        } finally {
            fonte.setColor(corAnterior);
        }
    }
}
