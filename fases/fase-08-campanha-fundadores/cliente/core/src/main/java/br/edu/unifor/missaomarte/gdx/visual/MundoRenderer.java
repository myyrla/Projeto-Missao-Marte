package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.EstacaoNami;
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
    private static final Color COR_DAS_PARTICULAS = Color.valueOf("F4B071");

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
            RetanguloDeTela areaVisivel, Predicate<Posicao> estaVisivel, boolean tempestadeAtiva,
            PoliticaDeDestaque.Resultado destaques) {
        desenharTerreno(areaVisivel);
        desenharGradeELimites(areaVisivel);
        CenaVisivelDaTempestade cena = CenaVisivelDaTempestade.projetar(
                missao, tempestadeAtiva, estaVisivel);
        Predicate<Posicao> visibilidadeVisual = posicao -> !tempestadeAtiva || estaVisivel.test(posicao);

        Color corAnterior = new Color(batch.getColor());
        batch.begin();
        try {
            desenharRastro(rastro, deslocX, deslocY, visibilidadeVisual);
            desenharAlcances(cena.inimigos(), posicoes, deslocX, deslocY, destaques);
            if (!tempestadeAtiva) {
                desenharObjetos(Collections.singletonList(cena.base()), posicoes, tempo, deslocX, deslocY);
            }
            desenharEstacoesNami(cena.estacoesNami(), posicoes, tempo, deslocX, deslocY);
            desenharPassageiros(cena.passageiros(), posicoes, tempo, deslocX, deslocY, destaques);
            desenharObjetos(cena.asteroides(), posicoes, tempo, deslocX, deslocY);
            desenharObjetos(cena.inimigos(), posicoes, tempo, deslocX, deslocY);

            if (naveVisivel) {
                desenharNave(missao, posicoes, tempo, anguloNave, deslocX, deslocY);
            }

            desenharEfeitos(efeitos, deslocX, deslocY, visibilidadeVisual);
        } finally {
            batch.setColor(corAnterior);
            batch.end();
        }
        if (tempestadeAtiva) {
            desenharEscuridao(estaVisivel);
            batch.begin();
            try {
                desenharObjetos(Collections.singletonList(cena.base()), posicoes, tempo, deslocX, deslocY);
            } finally {
                batch.setColor(corAnterior);
                batch.end();
            }
            desenharPoeira(tempo);
            desenharParticulas(tempo);
        }
        batch.begin();
        try {
            desenharEtiqueta(destaques);
            desenharFlutuantes(flutuantes, deslocX, deslocY, visibilidadeVisual);
        } finally {
            batch.setColor(corAnterior);
            batch.end();
        }
    }

    private void desenharEscuridao(Predicate<Posicao> estaVisivel) {
        MascaraDaTempestade.Campo campo = MascaraDaTempestade.calcular(
                mapa, estaVisivel, MissaoMarteGame.MIN, MissaoMarteGame.MAX,
                ESCURIDAO_DA_TEMPESTADE.a);
        float passo = mapa.celula() / 2f;
        float direita = mapa.origemX() + mapa.largura();
        float topo = mapa.origemY() + mapa.altura();
        Color inferiorEsquerda = new Color(ESCURIDAO_DA_TEMPESTADE);
        Color inferiorDireita = new Color(ESCURIDAO_DA_TEMPESTADE);
        Color superiorDireita = new Color(ESCURIDAO_DA_TEMPESTADE);
        Color superiorEsquerda = new Color(ESCURIDAO_DA_TEMPESTADE);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        try {
            for (float y = mapa.origemY(); y < topo; y += passo) {
                float altura = Math.min(passo, topo - y);
                for (float x = mapa.origemX(); x < direita; x += passo) {
                    float largura = Math.min(passo, direita - x);
                    inferiorEsquerda.a = campo.alfa(x, y);
                    inferiorDireita.a = campo.alfa(x + largura, y);
                    superiorDireita.a = campo.alfa(x + largura, y + altura);
                    superiorEsquerda.a = campo.alfa(x, y + altura);
                    formas.triangle(x, y, x + largura, y, x + largura, y + altura,
                            inferiorEsquerda, inferiorDireita, superiorDireita);
                    formas.triangle(x, y, x + largura, y + altura, x, y + altura,
                            inferiorEsquerda, superiorDireita, superiorEsquerda);
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
        Color corAnterior = new Color(batch.getColor());
        batch.begin();
        try {
            batch.setColor(1f, 1f, 1f, ALFA_DA_POEIRA);
            batch.draw(poeira, mapa.origemX(), mapa.origemY(), mapa.largura(), mapa.altura(),
                    deslocamentoU, deslocamentoV, deslocamentoU + repeticoesX, deslocamentoV + repeticoesY);
        } finally {
            batch.setColor(corAnterior);
            batch.end();
        }
    }

    private void desenharParticulas(float tempo) {
        RetanguloDeTela areaDoMapa = new RetanguloDeTela(
                mapa.origemX(), mapa.origemY(), mapa.largura(), mapa.altura());
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        try {
            for (ParticulasDaTempestade.Particula particula
                    : ParticulasDaTempestade.calcular(areaDoMapa, tempo)) {
                formas.setColor(COR_DAS_PARTICULAS.r, COR_DAS_PARTICULAS.g,
                        COR_DAS_PARTICULAS.b, particula.alfa());
                formas.rectLine(particula.inicioX(), particula.inicioY(),
                        particula.fimX(), particula.fimY(), 1.4f);
            }
        } finally {
            formas.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private void desenharRastro(RastroDaNave rastro, float dx, float dy,
            Predicate<Posicao> estaVisivel) {
        Texture ponto = recursos.textura(ImagemDoMapa.RASTRO_PONTO);
        float tamanho = ImagemDoMapa.RASTRO_PONTO.larguraLogica();
        for (GeometriaDoRastro.Ponto amostra : GeometriaDoRastro.calcular(rastro.casas(),
                MissaoMarteGame.MIN, MissaoMarteGame.MAX, mapa.celula(), mapa.origemX(), mapa.origemY(),
                ESPACAMENTO_DO_RASTRO)) {
            int casaX = Math.round((amostra.x() - mapa.origemX()) / mapa.celula() - 0.5f)
                    + MissaoMarteGame.MIN;
            int casaY = MissaoMarteGame.MAX
                    - Math.round((amostra.y() - mapa.origemY()) / mapa.celula() - 0.5f);
            if (!estaVisivel.test(new Posicao(casaX, casaY))) {
                continue;
            }
            batch.setColor(CIANO.r, CIANO.g, CIANO.b, amostra.alfa());
            batch.draw(ponto, amostra.x() - tamanho / 2f + dx, amostra.y() - tamanho / 2f + dy,
                    tamanho, tamanho);
        }
        batch.setColor(Color.WHITE);
    }

    private void desenharAlcances(List<Inimigo> inimigos, PosicoesVisuais posicoes, float dx, float dy,
            PoliticaDeDestaque.Resultado destaques) {
        Texture alcance = recursos.textura(ImagemDoMapa.ALCANCE);
        float tamanho = mapa.celula() * 3f;
        for (Inimigo inimigo : inimigos) {
            float opacidade = destaques.opacidadeDaZona(inimigo);
            if (opacidade <= 0f) {
                continue;
            }
            PosicaoVisual visual = posicoes.de(inimigo);
            Color cor = inimigo.persegue() ? LARANJA_PERSEGUIDOR : VERMELHO_INIMIGO;
            batch.setColor(cor.r, cor.g, cor.b, opacidade);
            batch.draw(alcance, mapa.xCentro(visual.x()) - tamanho / 2f + dx,
                    mapa.yCentro(visual.y()) - tamanho / 2f + dy, tamanho, tamanho);
        }
        batch.setColor(Color.WHITE);
    }

    private void desenharPassageiros(List<Passageiro> passageiros, PosicoesVisuais posicoes,
            float tempo, float dx, float dy, PoliticaDeDestaque.Resultado destaques) {
        Texture pulso = recursos.textura(ImagemDoMapa.PULSO);
        destaques.alvo().filter(passageiros::contains).ifPresent(passageiro -> {
            PosicaoVisual visual = posicoes.de(passageiro);
            Color cor = cor(PoliticaDeDestaque.Tipo.de(passageiro));
            desenharPulso(pulso, visual, cor, AnimacaoDoPulso.calcular(tempo, 0f), dx, dy);
            desenharPulso(pulso, visual, cor, AnimacaoDoPulso.calcular(tempo, 0.8f), dx, dy);
        });
        for (Passageiro passageiro : passageiros) {
            PosicaoVisual visual = posicoes.de(passageiro);
            float opacidade = destaques.opacidade(passageiro);
            batch.setColor(1f, 1f, 1f, opacidade);
            float tamanho = tamanhoDoObjeto(mapa.celula());
            batch.draw(regiao(passageiro, tempo), mapa.xCentro(visual.x()) - tamanho / 2f + dx,
                    mapa.yCentro(visual.y()) - tamanho / 2f + dy, tamanho, tamanho);
        }
        batch.setColor(Color.WHITE);
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

    private void desenharEstacoesNami(List<EstacaoNami> estacoes, PosicoesVisuais posicoes,
            float tempo, float dx, float dy) {
        float tamanho = tamanhoDoObjeto(mapa.celula());
        Color corAnterior = new Color(batch.getColor());
        try {
            for (EstacaoNami nami : estacoes) {
                PosicaoVisual visual = posicoes.de(nami);
                batch.setColor(corAnterior.r, corAnterior.g, corAnterior.b,
                        corAnterior.a * AparenciaDaNami.opacidade(nami));
                batch.draw(regiao(nami, tempo), mapa.xCentro(visual.x()) - tamanho / 2f + dx,
                        mapa.yCentro(visual.y()) - tamanho / 2f + dy, tamanho, tamanho);
            }
        } finally {
            batch.setColor(corAnterior);
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

    private void desenharEfeitos(EfeitosVisuais efeitos, float dx, float dy,
            Predicate<Posicao> estaVisivel) {
        for (EfeitosVisuais.Efeito efeito : efeitos.ativos()) {
            if (!estaVisivel.test(new Posicao(Math.round(efeito.x()), Math.round(efeito.y())))) {
                continue;
            }
            TextureRegion[] quadros = recursos.quadros(efeito.imagem());
            float tamanho = mapa.celula() * (efeito.imagem() == Imagem.EXPLOSAO ? 1.6f : 1.2f);
            batch.draw(quadros[efeito.quadro(quadros.length)],
                    mapa.xCentro(efeito.x()) - tamanho / 2f + dx, mapa.yCentro(efeito.y()) - tamanho / 2f + dy, tamanho, tamanho);
        }
    }

    private void desenharEtiqueta(PoliticaDeDestaque.Resultado destaques) {
        if (destaques.alvo().isEmpty() || destaques.etiqueta().isEmpty()) {
            return;
        }
        Color corAnterior = new Color(fonteRotulo.getColor());
        try {
            fonteRotulo.setColor(Color.WHITE);
            RetanguloDeTela area = destaques.etiqueta().orElseThrow();
            layout.setText(fonteRotulo, destaques.textoDaEtiqueta());
            PoliticaDeDestaque.Tipo tipo = PoliticaDeDestaque.Tipo.de(destaques.alvo().orElseThrow());
            estilo.ficha(batch, area, cor(tipo));
            fonteRotulo.draw(batch, layout, area.centroX() - layout.width / 2f,
                    area.centroY() + layout.height / 2f);
        } finally {
            fonteRotulo.setColor(corAnterior);
        }
    }

    private static Color cor(PoliticaDeDestaque.Tipo tipo) {
        if (tipo == PoliticaDeDestaque.Tipo.ASTRONAUTA) {
            return CIANO;
        }
        if (tipo == PoliticaDeDestaque.Tipo.PROFESSOR) {
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

    private void desenharFlutuantes(MensagensFlutuantes flutuantes, float dx, float dy,
            Predicate<Posicao> estaVisivel) {
        Color corAnterior = new Color(fonte.getColor());
        try {
            for (MensagensFlutuantes.Mensagem mensagem : flutuantes.ativas()) {
                if (!estaVisivel.test(new Posicao(Math.round(mensagem.x()), Math.round(mensagem.y())))) {
                    continue;
                }
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
