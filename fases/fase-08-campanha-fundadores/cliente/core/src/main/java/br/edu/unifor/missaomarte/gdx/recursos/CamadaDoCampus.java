package br.edu.unifor.missaomarte.gdx.recursos;

import br.edu.unifor.missaomarte.application.campanha.Construcao;
import br.edu.unifor.missaomarte.application.campanha.EstadoDaConstrucao;
import java.util.EnumMap;
import java.util.Map;

/** Catalogo das camadas do campus, na ordem em que devem ser desenhadas. */
public enum CamadaDoCampus {
    ZONAS_DE_POUSO(Construcao.ZONAS_DE_POUSO, "zonas_de_pouso"),
    TORRE_DE_COMUNICACAO(Construcao.TORRE_DE_COMUNICACAO, "torre_de_comunicacao"),
    BLOCOS_DE_AULA(Construcao.BLOCOS_DE_AULA, "blocos_de_aula"),
    BIBLIOTECA(Construcao.BIBLIOTECA, "biblioteca"),
    PREFEITURA(Construcao.PREFEITURA, "prefeitura"),
    NAMI(Construcao.NAMI, "nami"),
    REITORIA(Construcao.REITORIA, "reitoria");

    private static final String PASTA = "imagens/campus/";
    private static final String CAMINHO_DA_BASE = PASTA + "base.png";
    private static final Map<Construcao, CamadaDoCampus> POR_CONSTRUCAO = criarIndice();

    private final Construcao construcao;
    private final String caminhoPlanta;
    private final String caminhoConstruido;

    CamadaDoCampus(Construcao construcao, String nomeDoArquivo) {
        this.construcao = construcao;
        this.caminhoPlanta = PASTA + nomeDoArquivo + "_planta.png";
        this.caminhoConstruido = PASTA + nomeDoArquivo + "_construido.png";
    }

    public Construcao construcao() {
        return construcao;
    }

    public String caminhoPlanta() {
        return caminhoPlanta;
    }

    public String caminhoConstruido() {
        return caminhoConstruido;
    }

    public String caminho(EstadoDaConstrucao estado) {
        return estado == EstadoDaConstrucao.PLANTA ? caminhoPlanta : caminhoConstruido;
    }

    public static String caminhoDaBase() {
        return CAMINHO_DA_BASE;
    }

    public static CamadaDoCampus da(Construcao construcao) {
        CamadaDoCampus camada = POR_CONSTRUCAO.get(construcao);
        if (camada == null) {
            throw new IllegalArgumentException("Construcao sem camada visual: " + construcao);
        }
        return camada;
    }

    private static Map<Construcao, CamadaDoCampus> criarIndice() {
        Map<Construcao, CamadaDoCampus> indice = new EnumMap<>(Construcao.class);
        for (CamadaDoCampus camada : values()) {
            indice.put(camada.construcao, camada);
        }
        return indice;
    }
}
