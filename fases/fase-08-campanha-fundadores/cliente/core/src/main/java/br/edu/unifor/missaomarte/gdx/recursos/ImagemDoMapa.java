package br.edu.unifor.missaomarte.gdx.recursos;

/** Catálogo dos recursos próprios do mapa em resolução dobrada. */
public enum ImagemDoMapa {
    TERRENO("terreno_mapa.png", 512f, 512f, true),
    ALCANCE("alcance.png", 128f, 128f, false),
    PULSO("pulso.png", 96f, 96f, false),
    SOMBRA("sombra.png", 64f, 24f, false),
    RASTRO_PONTO("rastro_ponto.png", 8f, 8f, false);

    private static final String PASTA = "imagens/mapa/";

    private final String arquivo;
    private final float larguraLogica;
    private final float alturaLogica;
    private final boolean repetivel;

    ImagemDoMapa(String arquivo, float larguraLogica, float alturaLogica, boolean repetivel) {
        this.arquivo = arquivo;
        this.larguraLogica = larguraLogica;
        this.alturaLogica = alturaLogica;
        this.repetivel = repetivel;
    }

    public String caminho() {
        return PASTA + arquivo;
    }

    public float larguraLogica() {
        return larguraLogica;
    }

    public float alturaLogica() {
        return alturaLogica;
    }

    public boolean repetivel() {
        return repetivel;
    }
}
