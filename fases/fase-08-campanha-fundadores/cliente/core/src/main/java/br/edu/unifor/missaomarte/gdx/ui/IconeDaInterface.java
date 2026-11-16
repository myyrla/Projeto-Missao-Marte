package br.edu.unifor.missaomarte.gdx.ui;

/** Icones da interface: arquivos de 48 pixels desenhados com 24 unidades logicas. */
public enum IconeDaInterface {
    PROFESSOR("icone_professor.png"),
    ENGENHEIRO("icone_engenheiro.png"),
    ASTRONAUTA("icone_astronauta.png"),
    CHECK("icone_check.png"),
    CADEADO("icone_cadeado.png"),
    SETA("icone_seta.png");

    private static final String PASTA = "imagens/ui/";
    private static final float TAMANHO_LOGICO = 24f;

    private final String arquivo;

    IconeDaInterface(String arquivo) {
        this.arquivo = arquivo;
    }

    public String caminho() {
        return PASTA + arquivo;
    }

    public float tamanhoLogico() {
        return TAMANHO_LOGICO;
    }
}
