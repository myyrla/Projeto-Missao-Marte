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
import br.edu.unifor.missaomarte.gdx.visual.VisibilidadeDaTempestade;
import br.edu.unifor.missaomarte.gdx.ui.EstiloDaInterface;
import br.edu.unifor.missaomarte.gdx.ui.IconeDaInterface;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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

    public void desenhar(GameSession session, List<String> mensagens, boolean mudo, long segundos,
            RetanguloDeTela janelaVisivel, boolean tempestadeAtiva) {
        Nave nave = session.getMissao().getNave();
        vidasMaximas = Math.max(vidasMaximas, nave.getVidas());
        RetanguloDeTela rodapeEsquerdo = areaOcupadaDoRodape(session, mudo, segundos);
        fontes.comEscalaTemporaria(layout.escala(), () -> {
            try {
                desenharPecas(session, nave, rodapeEsquerdo, tempestadeAtiva);
                desenharRadar(session, janelaVisivel);
                desenharTextos(session, nave, mensagens, mudo, segundos, rodapeEsquerdo, tempestadeAtiva);
            } finally {
                for (FonteDoJogo tipo : FonteDoJogo.values()) {
                    fontes.obter(tipo).setColor(Color.WHITE);
                }
            }
        });
    }

    private void desenharPecas(GameSession session, Nave nave, RetanguloDeTela rodapeEsquerdo,
            boolean tempestadeAtiva) {
        lote(() -> {
            estilo.painel(batch, layout.piloto());
            estilo.painel(batch, layout.objetivo());
            if (layout.exibeRadar()) {
                estilo.painel(batch, layout.radar());
            }
            estilo.painel(batch, rodapeEsquerdo);
            if (layout.exibeAtalhos()) {
                estilo.painel(batch, layout.rodapeDireito());
            }
            if (tempestadeAtiva) {
                estilo.painel(batch, layout.seloDaTempestade());
            }
            for (int i = 0; i < 3; i++) {
                Color borda = campanha == null ? null : corDaBordaDoFundador(session, Fundador.values()[i]);
                estilo.ficha(batch, layout.fichaDoObjetivo(i), borda);
            }
            float s = layout.escala();
            for (int i = 0; i < vidasMaximas; i++) {
                estilo.segmento(batch, new RetanguloDeTela(layout.piloto().x() + (88f + i * 40f) * s,
                        layout.piloto().y() + 61f * s, 34f * s, 12f * s),
                        i < nave.getVidas() ? CIANO : VIDA_PERDIDA);
            }
        });
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
            VisibilidadeDaTempestade.filtrar(session.getMissao().getParadasDoRover(), session::estaVisivel)
                    .forEach(p -> ponto(radar, p.getPosicao(), VERDE, 3f));
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
            RetanguloDeTela rodapeEsquerdo, boolean tempestadeAtiva) {
        lote(() -> {
            desenharPiloto(session);
            desenharObjetivo(session, nave);
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

    private void desenharPiloto(GameSession session) {
        float s = layout.escala();
        RetanguloDeTela painel = layout.piloto();
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

    private void desenharObjetivo(GameSession session, Nave nave) {
        float s = layout.escala();
        RetanguloDeTela painel = layout.objetivo();
        rotulo("OBJETIVO", painel.x() + 16f * s, painel.topo() - 17f * s);
        BitmapFont texto = fontes.obter(FonteDoJogo.TEXTO);
        texto.setColor(TEXTO);
        String frase = campanha == null ? "Resgate os passageiros e retorne à base"
                : "Resgate os fundadores do campus";
        texto.draw(batch, limitar(frase, texto, painel.largura() - 118f * s),
                painel.x() + 106f * s, painel.topo() - 16f * s);

        if (campanha == null) {
            desenharObjetivoRapido(session, nave);
            return;
        }
        for (int i = 0; i < Fundador.values().length; i++) {
            desenharFundador(session, Fundador.values()[i], layout.fichaDoObjetivo(i));
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

    private void desenharFundador(GameSession session, Fundador fundador, RetanguloDeTela ficha) {
        EstadoVisual estado = estadoDoFundador(session, fundador);
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
        nomeDaFicha.setText(texto, limitar(nome, texto, largura));
        estadoDaFicha.setText(pequena, limitar(estado, pequena, largura));
        float espaco = 3f * s;
        float altura = nomeDaFicha.height + espaco + estadoDaFicha.height;
        float yDoNome = ficha.centroY() + altura / 2f;
        texto.draw(batch, nomeDaFicha, x, yDoNome);
        pequena.draw(batch, estadoDaFicha, x, yDoNome - nomeDaFicha.height - espaco);
    }

    private EstadoVisual estadoDoFundador(GameSession session, Fundador fundador) {
        int numero = fundador.ordinal() + 1;
        if (campanha.progresso().etapaAtual() > numero) {
            return new EstadoVisual("Resgatado", true);
        }
        if (etapa == null || etapa.fundador() != fundador) {
            return new EstadoVisual("Sem contato", false);
        }
        Passageiro noMapa = fundadorEm(session.getMissao().getPassageiros());
        Passageiro aBordo = fundadorEm(session.getMissao().getNave().getPassageiros());
        if (aBordo != null) {
            return new EstadoVisual("A bordo", true);
        }
        if (noMapa == null) {
            return new EstadoVisual("Resgatado", true);
        }
        Posicao nave = session.getMissao().getNave().getPosicao();
        int distancia = Math.abs(nave.getX() - noMapa.getX()) + Math.abs(nave.getY() - noMapa.getY());
        return new EstadoVisual("Sinal a " + distancia + " casas", false);
    }

    private static Passageiro fundadorEm(List<Passageiro> passageiros) {
        return passageiros.stream().filter(p -> p.getNome().startsWith("Fundador ")).findFirst().orElse(null);
    }

    private Color corDaBordaDoFundador(GameSession session, Fundador fundador) {
        return estadoDoFundador(session, fundador).marcado() ? cor(fundador) : null;
    }

    private void desenharRodape(GameSession session, boolean mudo, long segundos, RetanguloDeTela esquerdo) {
        float s = layout.escala();
        BitmapFont rotulo = fontes.obter(FonteDoJogo.ROTULO);
        rotulo.setColor(SECUNDARIO);
        String informacoes = informacoesDaPartida(session, segundos);
        if (mudo) {
            informacoes += " · SOM DESLIGADO";
        }
        String limitado = limitar(informacoes, rotulo, esquerdo.largura() - 32f * s);
        medidor.setText(rotulo, limitado);
        rotulo.draw(batch, medidor, esquerdo.x() + 16f * s, esquerdo.centroY() + medidor.height / 2f);
        if (!layout.exibeAtalhos()) {
            return;
        }
        RetanguloDeTela direito = layout.rodapeDireito();
        float x = direito.x() + 12f * s;
        float y = direito.centroY() - EstiloDaInterface.ALTURA_DA_TECLA * s / 2f;
        x = atalho("WASD", "Mover", x, y);
        if (session.getModo() == ModoDeJogo.DINAMICO) {
            x = atalho("F", "Atirar", x, y);
            x = atalho("P", "Pausa", x, y);
        } else {
            x = atalho("E", "Embarcar", x, y);
        }
        x = atalho("M", "Som", x, y);
        atalho("Esc", "Abortar", x, y);
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
        BitmapFont fonte = fontes.obter(FonteDoJogo.ROTULO);
        String texto = informacoesDaPartida(session, segundos) + (mudo ? " · SOM DESLIGADO" : "");
        medidor.setText(fonte, texto);
        return layout.rodapeEsquerdo(medidor.width * layout.escala());
    }

    private void desenharAviso(List<String> mensagens) {
        RetanguloDeTela aviso = layout.aviso();
        estilo.painel(batch, aviso);
        String texto = String.join(" ", mensagens);
        BitmapFont fonte = fontes.obter(FonteDoJogo.TEXTO);
        fonte.setColor(corDoAviso(texto));
        medidor.setText(fonte, limitar(texto, fonte, aviso.largura() - 24f * layout.escala()));
        fonte.draw(batch, medidor, aviso.centroX() - medidor.width / 2f, aviso.centroY() + medidor.height / 2f);
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

    private static String nomeDaDificuldade(br.edu.unifor.missaomarte.domain.Dificuldade dificuldade) {
        switch (dificuldade) {
            case MEDIO:
                return "MÉDIO";
            case DIFICIL:
                return "DIFÍCIL";
            default:
                return "FÁCIL";
        }
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

    private record EstadoVisual(String texto, boolean marcado) {
    }
}
