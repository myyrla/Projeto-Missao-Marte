package br.edu.unifor.missaomarte.gdx.campanha;

import br.edu.unifor.missaomarte.application.campanha.Construcao;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CampusLayout {
    public record Area(Construcao construcao, String rotulo, RetanguloDeTela retangulo) {
    }

    public record Decoracao(String rotulo, RetanguloDeTela retangulo) {
    }

    private final RetanguloDeTela mapa;
    private final List<Area> areas;
    private final List<Decoracao> decoracoes;

    private CampusLayout(RetanguloDeTela mapa, List<Area> areas, List<Decoracao> decoracoes) {
        this.mapa = mapa;
        this.areas = Collections.unmodifiableList(areas);
        this.decoracoes = Collections.unmodifiableList(decoracoes);
    }

    public static CampusLayout padrao() {
        RetanguloDeTela mapa = new RetanguloDeTela(24f, 130f, 520f, 400f);
        List<Area> areas = new ArrayList<>();
        for (int acesso = 0; acesso < 7; acesso++) {
            areas.add(new Area(Construcao.ZONAS_DE_POUSO, "A" + (acesso + 1),
                    new RetanguloDeTela(48f + acesso * 68f, 488f, 36f, 24f)));
        }
        areas.add(new Area(Construcao.TORRE_DE_COMUNICACAO, "Torre", new RetanguloDeTela(44f, 390f, 52f, 76f)));
        areas.add(new Area(Construcao.BLOCOS_DE_AULA, "Blocos A-E", new RetanguloDeTela(180f, 330f, 154f, 92f)));
        areas.add(new Area(Construcao.BLOCOS_DE_AULA, "Blocos N-R", new RetanguloDeTela(180f, 190f, 170f, 72f)));
        areas.add(new Area(Construcao.BIBLIOTECA, "Biblioteca", new RetanguloDeTela(370f, 352f, 96f, 62f)));
        areas.add(new Area(Construcao.REITORIA, "Reitoria", new RetanguloDeTela(464f, 278f, 64f, 112f)));
        areas.add(new Area(Construcao.PREFEITURA, "Prefeitura", new RetanguloDeTela(358f, 268f, 86f, 58f)));
        areas.add(new Area(Construcao.NAMI, "NAMI", new RetanguloDeTela(362f, 148f, 66f, 54f)));
        areas.add(new Area(Construcao.REDE_DO_ROVER, "Rover", new RetanguloDeTela(448f, 168f, 76f, 54f)));

        List<Decoracao> decoracoes = List.of(
                new Decoracao("Estádio", new RetanguloDeTela(44f, 232f, 104f, 82f)),
                new Decoracao("Ginásio", new RetanguloDeTela(62f, 152f, 84f, 56f)),
                new Decoracao("Gelo de água", new RetanguloDeTela(112f, 360f, 48f, 98f)));
        return new CampusLayout(mapa, areas, decoracoes);
    }

    public RetanguloDeTela mapa() {
        return mapa;
    }

    public List<Area> areas() {
        return areas;
    }

    public List<Decoracao> decoracoes() {
        return decoracoes;
    }
}
