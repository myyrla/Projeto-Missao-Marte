package br.edu.unifor.missaomarte.gdx.recursos;

/** Catálogo dos recursos próprios do mapa em resolução dobrada. */
public enum ImagemDoMapa {
    TERRENO("terreno_mapa.png", 512f, true);

    private static final String PASTA = "imagens/mapa/";

    private final String arquivo;
    private final float larguraLogica;
    private final boolean repetivel;

    ImagemDoMapa(String arquivo, float larguraLogica, boolean repetivel) {
        this.arquivo = arquivo;
        this.larguraLogica = larguraLogica;
        this.repetivel = repetivel;
    }

    public String caminho() {
        return PASTA + arquivo;
    }

    public float larguraLogica() {
        return larguraLogica;
    }

    public boolean repetivel() {
        return repetivel;
    }
}
