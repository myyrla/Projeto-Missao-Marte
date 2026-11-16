package br.edu.unifor.missaomarte.gdx.ui;

/** Catalogo das pecas redimensionaveis da interface e de seus cortes fisicos para NinePatch. */
public enum PecaDaInterface {
    PAINEL("painel.png", 26),
    BOTAO("botao.png", 22),
    BOTAO_FOCO("botao_foco.png", 22),
    CHIP("chip.png", 22),
    CHIP_BORDA("chip_borda.png", 22),
    TECLA("tecla.png", 16),
    SEGMENTO("segmento.png", 8),
    BARRA_FUNDO("barra_fundo.png", 10);

    private static final String PASTA = "imagens/ui/";

    private final String arquivo;
    private final int corte;

    PecaDaInterface(String arquivo, int corte) {
        this.arquivo = arquivo;
        this.corte = corte;
    }

    public String caminho() {
        return PASTA + arquivo;
    }

    /** Corte em pixels do arquivo em resolucao dobrada, aplicado antes da escala de 0,5. */
    public int corte() {
        return corte;
    }
}
