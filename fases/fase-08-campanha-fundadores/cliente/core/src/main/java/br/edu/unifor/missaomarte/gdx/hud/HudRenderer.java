package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.ModoDeJogo;
import br.edu.unifor.missaomarte.application.campanha.Campanha;
import br.edu.unifor.missaomarte.application.campanha.EtapaDaCampanha;
import br.edu.unifor.missaomarte.application.campanha.Fundador;
import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import br.edu.unifor.missaomarte.gdx.MissaoMarteGame;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.layout.TextoResponsivo;
import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.FontesDoJogo;
import br.edu.unifor.missaomarte.gdx.visual.PoliticaDeDestaque;
import br.edu.unifor.missaomarte.gdx.visual.VisibilidadeDaTempestade;
import br.edu.unifor.missaomarte.gdx.ui.EstiloDaInterface;
import br.edu.unifor.missaomarte.gdx.ui.IconeDaInterface;
import br.edu.unifor.missaomarte.gdx.ui.RotulosDaInterface;
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

/** HUD no estilo Switch. Apenas projeta o estado da sessao, sem alterar regras da partida. */
public final class HudRenderer {
    private static final Color FUNDO_DO_RADAR = Color.valueOf("2A1410");
    private static final Color TEXTO = Color.valueOf("F2EEF7");
    private static final Color SECUNDARIO = Color.valueOf("B9B2C6");
    private static final Color CIANO = Color.valueOf("4CC9F0");
    private static final Color AMBAR = Color.valueOf("FFB000");
    private static final Color VERDE = Color.valueOf("9EF01A");
    private static final Color VERMELHO = Color.valueOf("FF3B5C");
    private static final Color LARANJA = Color.valueOf("FF7A00");
    private static final Color CINZA_DO_ASTEROIDE = Color.valueOf("6B5A55");
    private static final Color VIDA_PERDIDA = Color.valueOf("3A3448");
    private static final Color FUNDO_DA_FAIXA = Color.valueOf("0C0814");

    private final SpriteBatch batch;
    private final ShapeRenderer formas;
    private final FontesDoJogo fontes;
    private final EstiloDaInterface estilo;
    private final Campanha campanha;
    private final EtapaDaCampanha etapa;
    private final GlyphLayout medidor = new GlyphLayout();
    private final GlyphLayout nomeDaFicha = new GlyphLayout();
    private final GlyphLayout estadoDaFicha = new GlyphLayout();
    private LayoutDoHud layout;
    private int vidasMaximas;

    public HudRenderer(SpriteBatch batch, ShapeRenderer formas, FontesDoJogo fontes, EstiloDaInterface estilo,
            Campanha campanha, EtapaDaCampanha etapa) {
        this.batch = batch;
        this.formas = formas;
        this.fontes = fontes;
        this.estilo = estilo;
        this.campanha = campanha;
        this.etapa = etapa;
    }

    public void definirLayout(LayoutDoHud layout) {
        this.layout = layout;
    }

    public MedidasDoHud medir(GameSession session, List<String> mensagens, boolean mudo, long segundos,
            boolean tempestadeAtiva, LayoutDoHud referencia) {
        MedidasDoHud[] resultado = new MedidasDoHud[1];
        fontes.comEscalaTemporaria(referencia.escala(), () -> {
            BitmapFont rotulo = fontes.obter(FonteDoJogo.ROTULO);
            BitmapFont texto = fontes.obter(FonteDoJogo.TEXTO);
            BitmapFont pequena = fontes.obter(FonteDoJogo.PEQUENO);
            float larguraDoJogador = Math.max(1f,
                    referencia.jogador().largura() - 32f * referencia.escala());
            medidor.setText(rotulo,
                    tituloDoPiloto(session.getPiloto(), session.getDificuldade()),
                    TEXTO, larguraDoJogador, Align.left, true);
            float alturaDoTituloDoJogador = alturaEmLinhas(
                    medidor.height, rotulo.getCapHeight(), rotulo.getLineHeight());
            medidor.setText(rotulo, "CASCO");
            MedidasDoJogador medidasDoJogador = new MedidasDoJogador(
                    alturaDoTituloDoJogador, rotulo.getLineHeight(), medidor.width);
            float margemHorizontal = 32f * referencia.escala();
            medidor.setText(texto, fraseDoObjetivo(), TEXTO,
                    Math.max(1f, referencia.objetivo().largura() - margemHorizontal), Align.left, true);
            float alturaDoObjetivo = medidor.height;
            List<String> linhas = linhasDoStatus(session, mudo, segundos);
            float alturaDoStatus;
            if (referencia.compacto()) {
                alturaDoStatus = 0f;
            } else {
                alturaDoStatus = linhas.size() * rotulo.getLineHeight();
            }
            float alturaDoAviso = 0f;
            if (!mensagens.isEmpty()) {
                float larguraDoAviso = referencia.aviso().largura() > 0f
                        ? referencia.aviso().largura()
                        : referencia.emRetrato() ? referencia.piloto().largura() * 0.4f
                        : referencia.radar().largura();
                medidor.setText(texto, String.join(" ", mensagens), TEXTO,
                        Math.max(1f, larguraDoAviso - 24f * referencia.escala()),
                        Align.center, true);
                alturaDoAviso = medidor.height;
            }
            resultado[0] = new MedidasDoHud(rotulo.getLineHeight(), alturaDoObjetivo, alturaDoStatus,
                    AtalhosDoRodape.para(session.getModo()).size(), alturaDoAviso,
                    !mensagens.isEmpty(), tempestadeAtiva,
                    texto.getLineHeight(), pequena.getLineHeight(), 0f)
                    .comJogador(medidasDoJogador);
        });
        return resultado[0];
    }

    public void desenhar(GameSession session, List<String> mensagens, boolean mudo, long segundos,
            RetanguloDeTela janelaVisivel, boolean tempestadeAtiva,
            PoliticaDeDestaque.Resultado destaques) {
        Nave nave = session.getMissao().getNave();
        vidasMaximas = Math.max(vidasMaximas, nave.getVidas());
        RetanguloDeTela rodapeEsquerdo = areaOcupadaDoRodape(session, mudo, segundos);
        fontes.comEscalaTemporaria(layout.escala(), () -> {
            try {
                desenharPecas(session, nave, rodapeEsquerdo, tempestadeAtiva, destaques);
                desenharRadar(session, janelaVisivel);
                desenharTextos(session, nave, mensagens, mudo, segundos, rodapeEsquerdo, tempestadeAtiva,
                        destaques);
            } finally {
                for (FonteDoJogo tipo : FonteDoJogo.values()) {
                    fontes.obter(tipo).setColor(Color.WHITE);
                }
            }
        });
    }

    private void desenharPecas(GameSession session, Nave nave, RetanguloDeTela rodapeEsquerdo,
            boolean tempestadeAtiva, PoliticaDeDestaque.Resultado destaques) {
        desenharFaixasOpacas();
        lote(() -> {
            estilo.painel(batch, layout.piloto());
            estilo.painel(batch, layout.objetivo());
            if (layout.exibeRadar()) {
                estilo.painel(batch, layout.radar());
            }
            if (!layout.statusIntegradoAoJogador()) {
                estilo.painel(batch, rodapeEsquerdo);
            }
            if (layout.exibeAtalhos()) {
                estilo.painel(batch, layout.rodapeDireito());
            }
            if (tempestadeAtiva) {
                estilo.painel(batch, layout.seloDaTempestade());
            }
            for (int i = 0; i < 3; i++) {
                Color borda = campanha == null ? null
                        : corDaBordaDoFundador(session, Fundador.values()[i], destaques);
                estilo.ficha(batch, layout.fichaDoObjetivo(i), borda);
            }
            float s = layout.escala();
            RetanguloDeTela jogador = layout.jogador();
            for (int i = 0; i < vidasMaximas; i++) {
                float x;
                float y;
                float largura;
                float altura;
                if (layout.toqueHorizontal()) {
                    RetanguloDeTela segmento = layout.segmentoDoCasco(i, vidasMaximas);
                    x = segmento.x();
                    y = segmento.y();
                    largura = segmento.largura();
                    altura = segmento.altura();
                } else if (layout.compacto()) {
                    BitmapFont rotulo = fontes.obter(FonteDoJogo.ROTULO);
                    medidor.setText(rotulo, "CASCO");
                    RetanguloDeTela linha = layout.inicioDaLinhaSecundariaDoJogador();
                    x = linha.x() + medidor.width + 8f * s + i * 40f * s;
                    y = linha.centroY() - 6f * s;
                    largura = 34f * s;
                    altura = 12f * s;
                } else {
                    x = jogador.x() + (88f + i * 40f) * s;
                    y = jogador.y() + 61f * s;
                    largura = 34f * s;
                    altura = 12f * s;
                }
                estilo.segmento(batch, new RetanguloDeTela(x, y, largura, altura),
                        i < nave.getVidas() ? CIANO : VIDA_PERDIDA);
            }
        });
    }

    private void desenharFaixasOpacas() {
        if (!layout.compacto()) {
            return;
        }
        formas.begin(ShapeType.Filled);
        try {
            formas.setColor(FUNDO_DA_FAIXA);
            RetanguloDeTela superior = layout.faixaSuperior();
            RetanguloDeTela inferior = layout.faixaInferior();
            formas.rect(superior.x(), superior.y(), superior.largura(), superior.altura());
            formas.rect(inferior.x(), inferior.y(), inferior.largura(), inferior.altura());
        } finally {
            formas.end();
        }
    }

    private void desenharRadar(GameSession session, RetanguloDeTela janelaVisivel) {
        if (!layout.exibeRadar()) {
            return;
        }
        RetanguloDeTela area = layout.radarInterno();
        RadarGeometria radar = new RadarGeometria(area, MissaoMarteGame.MIN, MissaoMarteGame.MAX);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        try {
            formas.setColor(FUNDO_DO_RADAR);
            formas.rect(area.x(), area.y(), area.largura(), area.altura());
            VisibilidadeDaTempestade.filtrar(session.getMissao().getAsteroides(), session::estaVisivel)
                    .forEach(a -> quadrado(radar, a.getPosicao(), CINZA_DO_ASTEROIDE, 4f));
            if (session.estaVisivel(session.getMissao().getBase().getPosicao())) {
                ponto(radar, session.getMissao().getBase().getPosicao(), AMBAR, 4f);
            }
            ponto(radar, session.getMissao().getNave().getPosicao(), CIANO, 4f);
            for (Passageiro passageiro : VisibilidadeDaTempestade.filtrar(
                    session.getMissao().getPassageiros(), session::estaVisivel)) {
                ponto(radar, passageiro.getPosicao(), corDoPassageiro(passageiro), 4f);
            }
            for (Inimigo inimigo : VisibilidadeDaTempestade.filtrar(
                    session.getMissao().getInimigos(), session::estaVisivel)) {
                ponto(radar, inimigo.getPosicao(), inimigo.persegue() ? LARANJA : VERMELHO, 4f);
            }
            VisibilidadeDaTempestade.filtrar(session.getMissao().getEstacoesNami(), session::estaVisivel)
                    .forEach(e -> ponto(radar, e.getPosicao(), VERDE, 4f));
            desenharJanelaVisivel(janelaVisivel);
        } finally {
            formas.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private void ponto(RadarGeometria radar, Posicao posicao, Color cor, float raioDeReferencia) {
        formas.setColor(cor);
        formas.circle(radar.x(posicao.getX()), radar.y(posicao.getY()), raioDeReferencia * layout.escala(), 16);
    }

    private void quadrado(RadarGeometria radar, Posicao posicao, Color cor, float tamanhoDeReferencia) {
        float tamanho = tamanhoDeReferencia * layout.escala();
        formas.setColor(cor);
        formas.rect(radar.x(posicao.getX()) - tamanho / 2f, radar.y(posicao.getY()) - tamanho / 2f,
                tamanho, tamanho);
    }

    private void desenharJanelaVisivel(RetanguloDeTela janela) {
        if (janela == null || janela.largura() <= 0f || janela.altura() <= 0f) {
            return;
        }
        formas.setColor(1f, 1f, 1f, 0.5f);
        linhaTracejadaHorizontal(janela.x(), janela.direita(), janela.y());
        linhaTracejadaHorizontal(janela.x(), janela.direita(), janela.topo());
        linhaTracejadaVertical(janela.y(), janela.topo(), janela.x());
        linhaTracejadaVertical(janela.y(), janela.topo(), janela.direita());
    }

    private void linhaTracejadaHorizontal(float inicio, float fim, float y) {
        float passo = 7f * layout.escala();
        float traco = 4f * layout.escala();
        float espessura = Math.max(0.6f, layout.escala());
        for (float x = inicio; x < fim; x += passo) {
            formas.rect(x, y - espessura / 2f, Math.min(traco, fim - x), espessura);
        }
    }

    private void linhaTracejadaVertical(float inicio, float fim, float x) {
        float passo = 7f * layout.escala();
        float traco = 4f * layout.escala();
        float espessura = Math.max(0.6f, layout.escala());
        for (float y = inicio; y < fim; y += passo) {
            formas.rect(x - espessura / 2f, y, espessura, Math.min(traco, fim - y));
        }
    }

    private void desenharTextos(GameSession session, Nave nave, List<String> mensagens, boolean mudo, long segundos,
            RetanguloDeTela rodapeEsquerdo, boolean tempestadeAtiva,
            PoliticaDeDestaque.Resultado destaques) {
        lote(() -> {
            desenharPiloto(session, segundos);
            desenharObjetivo(session, nave, destaques);
            if (layout.exibeRadar()) {
                rotulo("RADAR", layout.radar().x() + 14f * layout.escala(),
                        layout.radar().topo() - 16f * layout.escala());
            }
            desenharRodape(session, mudo, segundos, rodapeEsquerdo);
            if (tempestadeAtiva) {
                desenharSeloDaTempestade();
            }
            if (!mensagens.isEmpty()) {
                desenharAviso(mensagens);
            }
        });
    }

    private void desenharSeloDaTempestade() {
        RetanguloDeTela selo = layout.seloDaTempestade();
        BitmapFont fonte = fontes.obter(FonteDoJogo.ROTULO);
        fonte.setColor(AMBAR);
        medidor.setText(fonte, textoDaTempestade());
        fonte.draw(batch, medidor, selo.centroX() - medidor.width / 2f,
                selo.centroY() + medidor.height / 2f);
    }

    private void desenharPiloto(GameSession session, long segundos) {
        float s = layout.escala();
        RetanguloDeTela painel = layout.jogador();
        if (layout.compacto()) {
            desenharPilotoCompacto(session, segundos);
            return;
        }
        if (layout.toqueHorizontal()) {
            desenharPilotoHorizontalDeToque(session);
            return;
        }
        rotulo(tituloDoPiloto(session.getPiloto(), session.getDificuldade()),
                painel.x() + 16f * s, painel.topo() - 17f * s);
        rotulo("CASCO", painel.x() + 16f * s, painel.topo() - 51f * s);
        rotulo("PONTOS", painel.x() + 16f * s, painel.y() + 27f * s);

        BitmapFont titulo = fontes.obter(FonteDoJogo.TITULO);
        titulo.setColor(TEXTO);
        String pontos = String.format("%,d", session.getPontuacao()).replace(',', '.');
        medidor.setText(titulo, pontos);
        titulo.draw(batch, medidor, painel.direita() - 16f * s - medidor.width,
                painel.y() + 15f * s + medidor.height);
    }

    private void desenharPilotoHorizontalDeToque(GameSession session) {
        BitmapFont rotulo = fontes.obter(FonteDoJogo.ROTULO);
        rotulo.setColor(TEXTO);
        RetanguloDeTela titulo = layout.tituloDoJogador();
        medidor.setText(rotulo,
                tituloDoPiloto(session.getPiloto(), session.getDificuldade()),
                TEXTO, titulo.largura(), Align.left, true);
        rotulo.draw(batch, medidor, titulo.x(), titulo.topo());

        RetanguloDeTela casco = layout.linhaDoCasco();
        medidor.setText(rotulo, "CASCO");
        rotulo.draw(batch, medidor, casco.x(), casco.centroY() + medidor.height / 2f);

        RetanguloDeTela pontos = layout.linhaDosPontos();
        medidor.setText(rotulo, "PONTOS");
        rotulo.draw(batch, medidor, pontos.x(), pontos.centroY() + medidor.height / 2f);
        String valor = String.format("%,d", session.getPontuacao()).replace(',', '.');
        medidor.setText(rotulo, valor);
        rotulo.draw(batch, medidor, pontos.direita() - medidor.width,
                pontos.centroY() + medidor.height / 2f);
    }

    private void desenharPilotoCompacto(GameSession session, long segundos) {
        BitmapFont rotulo = fontes.obter(FonteDoJogo.ROTULO);
        rotulo.setColor(TEXTO);
        RetanguloDeTela principal = layout.inicioDaLinhaPrincipalDoJogador();
        RetanguloDeTela pontosNaLinha = layout.fimDaLinhaPrincipalDoJogador();
        RetanguloDeTela secundaria = layout.inicioDaLinhaSecundariaDoJogador();
        RetanguloDeTela statusNaLinha = layout.fimDaLinhaSecundariaDoJogador();
        float yPrincipal = principal.centroY() + rotulo.getCapHeight() / 2f;
        float ySecundaria = secundaria.centroY() + rotulo.getCapHeight() / 2f;
        String piloto = tituloDoPiloto(session.getPiloto(), session.getDificuldade());
        rotulo.draw(batch, limitar(piloto, rotulo, principal.largura()), principal.x(), yPrincipal);

        String pontos = String.format("%,d", session.getPontuacao()).replace(',', '.');
        String textoDosPontos = "PONTOS " + pontos;
        medidor.setText(rotulo, limitar(textoDosPontos, rotulo, pontosNaLinha.largura()));
        rotulo.draw(batch, medidor, pontosNaLinha.direita() - medidor.width, yPrincipal);

        rotulo.draw(batch, "CASCO", secundaria.x(), ySecundaria);
        String informacoes = String.format("TEMPO %02d:%02d", segundos / 60, segundos % 60)
                + (session.getModo() == ModoDeJogo.DINAMICO
                        ? " · MUNIÇÃO " + session.getMunicao()
                        : " · MOVIMENTOS " + session.getMovimentos());
        medidor.setText(rotulo, limitar(informacoes, rotulo, statusNaLinha.largura()));
        rotulo.draw(batch, medidor, statusNaLinha.direita() - medidor.width, ySecundaria);
    }

    private void desenharObjetivo(GameSession session, Nave nave, PoliticaDeDestaque.Resultado destaques) {
        float s = layout.escala();
        RetanguloDeTela painel = layout.objetivo();
        rotulo("OBJETIVO", painel.x() + 16f * s, painel.topo() - 17f * s);
        BitmapFont texto = fontes.obter(FonteDoJogo.TEXTO);
        texto.setColor(TEXTO);
        String frase = fraseDoObjetivo();
        if (!layout.usaReserva()) {
            RetanguloDeTela area = layout.textoDoObjetivo();
            medidor.setText(texto, frase, TEXTO, area.largura(), Align.left, true);
            texto.draw(batch, medidor, area.x(), area.topo());
        } else {
            texto.draw(batch, limitar(frase, texto, painel.largura() - 118f * s),
                    painel.x() + 106f * s, painel.topo() - 16f * s);
        }

        if (campanha == null) {
            desenharObjetivoRapido(session, nave);
            return;
        }
        for (int i = 0; i < Fundador.values().length; i++) {
            desenharFundador(session, Fundador.values()[i], layout.fichaDoObjetivo(i), destaques);
        }
    }

    private void desenharObjetivoRapido(GameSession session, Nave nave) {
        String[] nomes = {"A bordo", "Entregues", "No mapa"};
        String[] valores = {nave.getPassageiros().size() + "/" + nave.getCapacidade(),
                session.getResgatados() + "/" + session.getTotalPassageiros(),
                String.valueOf(session.getMissao().getPassageiros().size())};
        for (int i = 0; i < 3; i++) {
            textoDaFicha(nomes[i], valores[i], layout.fichaDoObjetivo(i), iconeDaPartidaRapida(i), null);
        }
    }

    private void desenharFundador(GameSession session, Fundador fundador, RetanguloDeTela ficha,
            PoliticaDeDestaque.Resultado destaques) {
        EstadoVisual estado = estadoDoFundador(session, fundador, destaques);
        IconeDaInterface icone = fundador == Fundador.ASTRONAUTA ? IconeDaInterface.ASTRONAUTA
                : fundador == Fundador.PROFESSOR ? IconeDaInterface.PROFESSOR : IconeDaInterface.ENGENHEIRO;
        textoDaFicha(nome(fundador), estado.texto(), ficha, icone, estado.marcado() ? cor(fundador) : null);
        if (estado.marcado()) {
            RetanguloDeTela check = layout.checkDoFundador(fundador.ordinal());
            batch.draw(estilo.icone(IconeDaInterface.CHECK), check.x(), check.y(),
                    check.largura(), check.altura());
        }
    }

    private void textoDaFicha(String nome, String estado, RetanguloDeTela ficha, IconeDaInterface icone,
            Color corDoEstado) {
        float s = layout.escala();
        float x = ficha.x() + 12f * s;
        batch.draw(estilo.icone(icone), x, ficha.centroY() - 12f * s, 24f * s, 24f * s);
        x += 32f * s;
        BitmapFont texto = fontes.obter(FonteDoJogo.TEXTO);
        texto.setColor(TEXTO);
        BitmapFont pequena = fontes.obter(FonteDoJogo.PEQUENO);
        pequena.setColor(corDoEstado == null ? SECUNDARIO : corDoEstado);
        float largura = ficha.direita() - x - 12f * s;
        String nomeVisivel = layout.usaReserva() ? limitar(nome, texto, largura) : nome;
        String estadoVisivel = layout.usaReserva() ? limitar(estado, pequena, largura) : estado;
        nomeDaFicha.setText(texto, nomeVisivel);
        estadoDaFicha.setText(pequena, estadoVisivel);
        float espaco = 3f * s;
        float altura = nomeDaFicha.height + espaco + estadoDaFicha.height;
        float yDoNome = ficha.centroY() + altura / 2f;
        texto.draw(batch, nomeDaFicha, x, yDoNome);
        pequena.draw(batch, estadoDaFicha, x, yDoNome - nomeDaFicha.height - espaco);
    }

    private EstadoVisual estadoDoFundador(GameSession session, Fundador fundador,
            PoliticaDeDestaque.Resultado destaques) {
        int numero = fundador.ordinal() + 1;
        if (campanha.progresso().etapaAtual() > numero) {
            return new EstadoVisual("Resgatado", true);
        }
        if (etapa == null || etapa.fundador() != fundador) {
            return new EstadoVisual("Sem contato", false);
        }
        Passageiro aBordo = fundadorEm(session.getMissao().getNave().getPassageiros(), fundador);
        if (aBordo != null) {
            return new EstadoVisual("A bordo", true);
        }
        PoliticaDeDestaque.AlvoDoObjetivo alvo = destaques.alvoDoObjetivo()
                .filter(candidato -> ehFundador(candidato.passageiro(), fundador))
                .orElse(null);
        if (alvo == null) {
            return new EstadoVisual("Resgatado", true);
        }
        return new EstadoVisual(textoDoSinal(alvo), false);
    }

    private static Passageiro fundadorEm(List<Passageiro> passageiros, Fundador fundador) {
        return passageiros.stream().filter(p -> ehFundador(p, fundador)).findFirst().orElse(null);
    }

    private static boolean ehFundador(Passageiro passageiro, Fundador fundador) {
        return passageiro.getNome().equals("Fundador " + nome(fundador));
    }

    private Color corDaBordaDoFundador(GameSession session, Fundador fundador,
            PoliticaDeDestaque.Resultado destaques) {
        return estadoDoFundador(session, fundador, destaques).marcado() ? cor(fundador) : null;
    }

    private void desenharRodape(GameSession session, boolean mudo, long segundos, RetanguloDeTela esquerdo) {
        if (layout.compacto()) {
            return;
        }
        float s = layout.escala();
        BitmapFont rotulo = fontes.obter(FonteDoJogo.ROTULO);
        rotulo.setColor(SECUNDARIO);
        if (layout.usaReserva()) {
            String informacoes = informacoesDaPartida(session, segundos);
            if (mudo) {
                informacoes += " · SOM DESLIGADO";
            }
            String limitado = limitar(informacoes, rotulo, esquerdo.largura() - 32f * s);
            medidor.setText(rotulo, limitado);
            rotulo.draw(batch, medidor, esquerdo.x() + 16f * s,
                    esquerdo.centroY() + medidor.height / 2f);
        } else {
            desenharLinhasDoStatus(session, mudo, segundos, esquerdo, rotulo);
        }
        if (!layout.exibeAtalhos()) {
            return;
        }
        RetanguloDeTela direito = layout.rodapeDireito();
        if (layout.usaReserva()) {
            float x = direito.x() + 12f * s;
            float y = direito.centroY() - EstiloDaInterface.ALTURA_DA_TECLA * s / 2f;
            for (AtalhosDoRodape.Atalho atalho : AtalhosDoRodape.para(session.getModo())) {
                x = atalho(atalho.tecla(), atalho.acao(), x, y);
            }
            return;
        }
        float y = direito.topo() - 12f * s - EstiloDaInterface.ALTURA_DA_TECLA * s;
        for (AtalhosDoRodape.Atalho atalho : AtalhosDoRodape.para(session.getModo())) {
            atalho(atalho.tecla(), atalho.acao(), direito.x() + 12f * s, y);
            y -= 38f * s;
        }
    }

    private void desenharLinhasDoStatus(GameSession session, boolean mudo, long segundos,
            RetanguloDeTela painel, BitmapFont fonte) {
        List<String> linhas = linhasDoStatus(session, mudo, segundos);
        float x = painel.x() + 16f * layout.escala();
        float y = painel.topo();
        if (layout.compacto()) {
            medidor.setText(fonte, String.join(" · ", linhas), SECUNDARIO,
                    painel.largura() - 32f * layout.escala(), Align.left, true);
            fonte.draw(batch, medidor, x, y);
            return;
        }
        for (String linha : linhas) {
            fonte.draw(batch, linha, x, y);
            y -= fonte.getLineHeight();
        }
    }

    private List<String> linhasDoStatus(GameSession session, boolean mudo, long segundos) {
        Integer sol = etapa == null ? null : etapa.numero() * 7;
        String local = etapa == null ? null : etapa.local();
        return LinhasDoStatus.para(session.getModo(), session.getMunicao(), session.getMira(),
                session.getMovimentos(), segundos, sol, local, mudo);
    }

    private String fraseDoObjetivo() {
        return campanha == null ? "Resgate os passageiros e retorne à base"
                : "Resgate os fundadores do campus";
    }

    private float atalho(String tecla, String acao, float x, float y) {
        float s = layout.escala();
        float largura = estilo.tecla(batch, tecla, x, y, s);
        BitmapFont fonte = fontes.obter(FonteDoJogo.PEQUENO);
        fonte.setColor(TEXTO);
        medidor.setText(fonte, acao);
        fonte.draw(batch, medidor, x + largura + 8f * s, y + (24f * s + medidor.height) / 2f);
        return x + largura + medidor.width + 22f * s;
    }

    private String informacoesDaPartida(GameSession session, long segundos) {
        String inicio = session.getModo() == ModoDeJogo.DINAMICO
                ? "MUNIÇÃO " + session.getMunicao() + " · MIRA " + nomeDaMira(session.getMira())
                : "MOVIMENTOS " + session.getMovimentos();
        String texto = inicio + String.format(" · TEMPO %02d:%02d", segundos / 60, segundos % 60);
        if (etapa != null) {
            texto += " · SOL " + etapa.numero() * 7 + " · " + etapa.local().toUpperCase();
        }
        return texto;
    }

    public RetanguloDeTela areaOcupadaDoRodape(GameSession session, boolean mudo, long segundos) {
        if (!layout.usaReserva()) {
            return layout.rodapeEsquerdo();
        }
        BitmapFont fonte = fontes.obter(FonteDoJogo.ROTULO);
        String texto = informacoesDaPartida(session, segundos) + (mudo ? " · SOM DESLIGADO" : "");
        medidor.setText(fonte, texto);
        return layout.rodapeEsquerdo(medidor.width * layout.escala());
    }

    private void desenharAviso(List<String> mensagens) {
        RetanguloDeTela aviso = layout.aviso();
        RetanguloDeTela areaDoTexto = areaDoTextoDoAviso(aviso, layout.escala());
        estilo.painel(batch, aviso);
        String texto = String.join(" ", mensagens);
        BitmapFont fonte = fontes.obter(FonteDoJogo.TEXTO);
        fonte.setColor(corDoAviso(texto));
        if (layout.usaReserva()) {
            medidor.setText(fonte, limitar(texto, fonte, areaDoTexto.largura()));
            fonte.draw(batch, medidor, areaDoTexto.centroX() - medidor.width / 2f,
                    aviso.centroY() + medidor.height / 2f);
        } else {
            medidor.setText(fonte, texto, corDoAviso(texto),
                    areaDoTexto.largura(), Align.center, true);
            fonte.draw(batch, medidor, areaDoTexto.x(), aviso.centroY() + medidor.height / 2f);
        }
    }

    static RetanguloDeTela areaDoTextoDoAviso(RetanguloDeTela aviso, float escala) {
        float margem = 12f * escala;
        return new RetanguloDeTela(aviso.x() + margem, aviso.y(),
                Math.max(0f, aviso.largura() - 2f * margem), aviso.altura());
    }

    private void rotulo(String texto, float x, float y) {
        BitmapFont fonte = fontes.obter(FonteDoJogo.ROTULO);
        fonte.setColor(SECUNDARIO);
        fonte.draw(batch, texto, x, y);
    }

    private String limitar(String texto, BitmapFont fonte, float largura) {
        return TextoResponsivo.limitarComReticencias(texto, largura, candidato -> {
            medidor.setText(fonte, candidato);
            return medidor.width;
        });
    }

    private void lote(Runnable desenho) {
        batch.begin();
        try {
            desenho.run();
        } finally {
            batch.end();
        }
    }

    private static Color corDoPassageiro(Passageiro passageiro) {
        if (passageiro instanceof Astronauta) {
            return CIANO;
        }
        if (passageiro instanceof Engenheiro) {
            return VERDE;
        }
        if (passageiro instanceof Professor) {
            return AMBAR;
        }
        return TEXTO;
    }

    private static Color cor(Fundador fundador) {
        return fundador == Fundador.ASTRONAUTA ? CIANO : fundador == Fundador.PROFESSOR ? AMBAR : VERDE;
    }

    private static String nome(Fundador fundador) {
        return fundador == Fundador.ASTRONAUTA ? "Astronauta"
                : fundador == Fundador.PROFESSOR ? "Professor" : "Engenheiro";
    }

    private static String nomeDaMira(Acao mira) {
        return mira == Acao.CIMA ? "CIMA" : mira == Acao.BAIXO ? "BAIXO"
                : mira == Acao.ESQUERDA ? "ESQUERDA" : "DIREITA";
    }

    private static Color corDoAviso(String texto) {
        String minusculo = texto.toLowerCase();
        if (minusculo.contains("colis") || minusculo.contains("falhou") || minusculo.contains("fatal")) {
            return VERMELHO;
        }
        if (minusculo.contains("base") || minusculo.contains("entreg")) {
            return VERDE;
        }
        return CIANO;
    }

    static String formatarDificuldade(br.edu.unifor.missaomarte.domain.Dificuldade dificuldade) {
        return "Dif.: " + nomeDaDificuldade(dificuldade);
    }

    static String tituloDoPiloto(String piloto, br.edu.unifor.missaomarte.domain.Dificuldade dificuldade) {
        String nome = "Piloto".equalsIgnoreCase(piloto) ? "CONVIDADO" : "PILOTO " + piloto.toUpperCase();
        return nome + " · " + nomeDaDificuldade(dificuldade);
    }

    private static float alturaEmLinhas(float alturaDoLayout, float alturaDaMaiuscula,
            float alturaDaLinha) {
        if (alturaDoLayout <= 0f || alturaDaLinha <= 0f) {
            return 0f;
        }
        int linhas = Math.max(1,
                Math.round((alturaDoLayout - alturaDaMaiuscula) / alturaDaLinha) + 1);
        return linhas * alturaDaLinha;
    }

    private static String nomeDaDificuldade(br.edu.unifor.missaomarte.domain.Dificuldade dificuldade) {
        return RotulosDaInterface.dificuldade(dificuldade);
    }

    static String formatarMunicaoEMira(int municao, Acao mira) {
        String simbolo = mira == Acao.ESQUERDA ? "<" : mira == Acao.CIMA ? "^" : mira == Acao.BAIXO ? "v" : ">";
        return "Mun. " + municao + "   Mira " + simbolo;
    }

    static IconeDaInterface iconeDaPartidaRapida(int indice) {
        switch (indice) {
            case 0:
                return IconeDaInterface.ASTRONAUTA;
            case 1:
                return IconeDaInterface.CHECK;
            case 2:
                return IconeDaInterface.SETA;
            default:
                throw new IllegalArgumentException("Ficha de objetivo inválida: " + indice);
        }
    }

    static String textoDaTempestade() {
        return "TEMPESTADE DE AREIA";
    }

    static String textoDoSinal(PoliticaDeDestaque.AlvoDoObjetivo alvo) {
        return "Sinal a " + alvo.distancia() + " casas";
    }

    private record EstadoVisual(String texto, boolean marcado) {
    }
}
